# SPRING PLUS

## Challenge 13. Large Data Processing

### User Bulk Insert

대용량 데이터 처리 실습을 위해 `JdbcTemplate.batchUpdate()`로 `users` 테이블에 유저 1,000,000건을 생성했다.

| 항목 | 결과 |
| --- | ---: |
| 생성 데이터 수 | 1,000,000건 |
| 생성 방식 | JDBC Bulk Insert |
| Batch Size | 5,000 |
| Insert 소요 시간 | 70,998ms |
| Gradle 전체 실행 시간 | 1m 15s |
| 샘플 nickname | `bulk-20260616095005-0500000` |

실행 결과:

```text
Inserted 1,000,000 users in 70,998 ms. sampleNickname=bulk-20260616095005-0500000
BUILD SUCCESSFUL in 1m 15s
```

### User Search Performance

다음 단계에서는 위에서 생성된 샘플 nickname을 검색 조건으로 사용해 nickname 정확 일치 조회 속도를 측정한다.

예상 검색 조건:

```text
nickname=bulk-20260616095005-0500000
```

비교 결과:

| 단계 | 방법 | 조회 조건 | 조회 시간 (ms) | 조회 시간 (μs, 원본 로그) | EXPLAIN type | key | Extra | rows | 비고 |
| --- | --- | --- | ---: | ---: | --- | --- | --- | ---: | --- |
| 1 | 인덱스 없음 | nickname 정확 일치 | 1,670.435 | 1,670,435 | ALL | NULL | - | 1,014,254 | 최초 조회 (풀스캔) |
| 2 | nickname 단일 인덱스 + `SELECT *` | nickname 정확 일치 | 427.497 | 427,497 | ref | idx_users_nickname | NULL | 1 | 인덱스 조회 후 테이블 액세스 발생 |
| 3 | Projection + Redis 캐시 미스 | nickname 정확 일치 | 1,177.574 | 1,177,574 | ref | idx_users_nickname | NULL | 1 | 첫 요청 오버헤드와 DB 조회 포함 |
| 4 | Redis 캐시 히트 | nickname 정확 일치 (재조회) | 72.216 | 72,216 | - | - | - | - | DB 우회, 캐시에서 반환 |

> 3·4단계는 같은 테스트 메서드에서 연속 조회한 결과다. 1회차는 캐시 미스라 DB 조회가 발생하고, 2회차는 Redis 캐시 히트라 DB 조회 없이 반환된다.

### 3·4단계 재현 방법

**3단계 — Projection + 캐시 미스**

- `UserService.searchUsersByNickname`는 `UserRepository.searchByNickname`(JPQL constructor expression)로 `id·email·nickname`만 조회한다.
- 현재 실측에서는 MySQL이 `idx_users_nickname` 인덱스를 선택했고, `type=ref`, `rows=1`로 조회 범위를 줄였다.
- 커버링 인덱스 여부는 아래 SQL로 별도 확인할 수 있다. `Extra`가 `Using index`로 표시되면 테이블 본체 접근 없이 인덱스만으로 조회된 것이다.

  ```sql
  EXPLAIN SELECT id, email, nickname FROM users WHERE nickname = '<sample-nickname>';
  ```

**4단계 — Redis 캐시 히트**

- 사전 준비: 로컬에 Redis 기동 (`docker run -d --name redis -p 6379:6379 redis`, `redis-cli ping` → `PONG` 확인).
- 동일 nickname을 두 번 조회하면 1회차는 캐시 미스(DB 조회), 2회차는 캐시 히트(Redis 반환)입니다.
- 캐시 적재 확인:

  ```bash
  redis-cli keys 'usersByNickname*'
  ```

검색 API:

```http
GET /users?nickname=bulk-20260616095005-0500000
```

실행 시 주의사항:

- `BULK_INSERT_USERS=true`를 설정하면 실제 MySQL `users` 테이블에 1,000,000건이 추가된다.
- 대량 데이터 생성 테스트는 반복 실행할 때마다 새 데이터가 누적되므로 필요한 경우에만 실행한다.
- 검색 성능 테스트는 `USER_SEARCH_PERFORMANCE_TEST=true`를 설정했을 때만 실행된다.
- 테스트 실행 시 `JWT_SECRET_KEY`, `MYSQL_USERNAME`, `MYSQL_PASSWORD` 환경변수가 필요하다.

성능 개선 해석:

- 인덱스 추가 전에는 `type=ALL`, `key=NULL`로 전체 테이블 스캔이 발생했고, MySQL이 약 1,014,254 rows를 확인할 것으로 예상했다.
- `nickname` 컬럼에 `idx_users_nickname` 인덱스를 추가한 뒤에는 `type=ref`, `key=idx_users_nickname`, `rows=1`로 바뀌었다.
- 같은 nickname 정확 일치 조건에서 조회 시간이 1,670.435ms에서 427.497ms로 줄었다.
- Redis 캐시를 적용한 뒤 같은 nickname을 재조회하면 DB를 거치지 않아 1,177.574ms에서 72.216ms로 줄었다. 캐시 히트는 캐시 미스 대비 약 16.3배 빠르다.
- 다만 3단계의 1,177.574ms는 Spring Boot 테스트 컨텍스트에서 MockMvc 첫 요청으로 측정한 값이므로 DB 순수 조회 시간만 의미하지는 않는다.

인덱스 없는 상태의 검색 성능 테스트 실행 결과:

```text
User nickname search without index took 1,670,435 microseconds. nickname=bulk-20260616095005-0500000
EXPLAIN users nickname search: type=ALL, key=null, rows=1014254
BUILD SUCCESSFUL in 20s
```

인덱스 추가 후 실행 결과:

```text
Hibernate:
    create index idx_users_nickname
       on users (nickname)
User nickname search took 427,497 microseconds. nickname=bulk-20260616095005-0500000
EXPLAIN users nickname search: type=ref, key=idx_users_nickname, rows=1
BUILD SUCCESSFUL in 32s
```

Redis 캐시 적용 후 실행 결과:

```text
User nickname search: [cache miss] 1,177,574 us, [cache hit] 72,216 us. nickname=bulk-20260616094304-0000001, response=[{"id":9006,"email":"bulk-20260616094304-0000001@example.com","nickname":"bulk-20260616094304-0000001"}]
EXPLAIN(covering projection): type=ref, key=idx_users_nickname, Extra=null, rows=1
BUILD SUCCESSFUL in 22s
```

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

| 단계 | 방법 | 조회 조건 | 조회 시간 | EXPLAIN type | key | rows | 비고 |
| --- | --- | --- | ---: | --- | --- | ---: | --- |
| 1 | 인덱스 없음 | nickname 정확 일치 | 1,670.435ms | ALL | NULL | 1,014,254 | 최초 조회 |
| 2 | nickname 인덱스 추가 | nickname 정확 일치 | 427.497ms | ref | idx_users_nickname | 1 | 개선 후 조회 |

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

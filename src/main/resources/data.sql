INSERT
IGNORE INTO users (id, created_at, modified_at, email, password, user_role, nickname)
VALUES
    (1001, '2026-06-01 09:00:00', '2026-06-01 09:00:00', 'seed-user1@example.com', 'dummy-password', 'USER', '검색테스터1'),
    (1002, '2026-06-01 09:05:00', '2026-06-01 09:05:00', 'seed-user2@example.com', 'dummy-password', 'USER', '검색테스터2');

INSERT
IGNORE INTO todos (id, created_at, modified_at, title, contents, weather, user_id)
VALUES
    (2001, '2026-06-10 09:00:00', '2026-06-10 10:00:00', '맑은 날 할 일', 'weather 조건 단독 검색 확인용', 'Sunny', 1001),
    (2002, '2026-06-11 09:00:00', '2026-06-11 12:00:00', '흐린 날 할 일', 'start 조건 검색 확인용', 'Cloudy', 1001),
    (2003, '2026-06-12 09:00:00', '2026-06-12 15:00:00', '더운 날 할 일', 'weather와 기간 조건 동시 검색 확인용', 'Hot and Humid', 1002),
    (2004, '2026-06-13 09:00:00', '2026-06-13 18:00:00', '비 오는 날 할 일', 'end 조건 검색 확인용', 'Rainy', 1002),
    (2005, '2026-06-14 09:00:00', '2026-06-14 21:00:00', '또 다른 더운 날 할 일', 'weather 조건 결과가 여러 개인지 확인용', 'Hot and Humid', 1001);

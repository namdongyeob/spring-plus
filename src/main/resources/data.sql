INSERT INTO users (id, created_at, modified_at, email, password, user_role, nickname)
SELECT 9001, '2026-06-01 09:00:00', '2026-06-01 09:00:00', 'todo-owner@example.com', '$2a$10$yU3HXXGsw0q6naTIQD1jS.nmkdRYuLYFRhzG19UvbR7PKXSfk8M9a', 'USER', 'todo-owner'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 9001);

INSERT INTO users (id, created_at, modified_at, email, password, user_role, nickname)
SELECT 9002, '2026-06-01 09:05:00', '2026-06-01 09:05:00', 'todo-manager@example.com', '$2a$10$yU3HXXGsw0q6naTIQD1jS.nmkdRYuLYFRhzG19UvbR7PKXSfk8M9a', 'USER', 'todo-manager'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 9002);

INSERT INTO users (id, created_at, modified_at, email, password, user_role, nickname)
SELECT 9003, '2026-06-01 09:10:00', '2026-06-01 09:10:00', 'todo-commenter@example.com', '$2a$10$yU3HXXGsw0q6naTIQD1jS.nmkdRYuLYFRhzG19UvbR7PKXSfk8M9a', 'USER', 'todo-commenter'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 9003);

INSERT INTO todos (id, created_at, modified_at, title, contents, weather, user_id)
SELECT 9101, '2026-06-10 09:00:00', '2026-06-10 10:00:00', 'QueryDSL projection practice', 'Sunny search result with two managers and two comments.', 'Sunny', 9001
WHERE NOT EXISTS (SELECT 1 FROM todos WHERE id = 9101);

INSERT INTO todos (id, created_at, modified_at, title, contents, weather, user_id)
SELECT 9102, '2026-06-11 09:00:00', '2026-06-11 12:00:00', 'Paging practice first row', 'Cloudy search result for paging test.', 'Cloudy', 9001
WHERE NOT EXISTS (SELECT 1 FROM todos WHERE id = 9102);

INSERT INTO todos (id, created_at, modified_at, title, contents, weather, user_id)
SELECT 9103, '2026-06-12 09:00:00', '2026-06-12 15:00:00', 'Paging practice second row', 'Sunny search result for sort and page test.', 'Sunny', 9002
WHERE NOT EXISTS (SELECT 1 FROM todos WHERE id = 9103);

INSERT INTO managers (id, user_id, todo_id)
SELECT 9201, 9001, 9101
WHERE NOT EXISTS (SELECT 1 FROM managers WHERE id = 9201);

INSERT INTO managers (id, user_id, todo_id)
SELECT 9202, 9002, 9101
WHERE NOT EXISTS (SELECT 1 FROM managers WHERE id = 9202);

INSERT INTO managers (id, user_id, todo_id)
SELECT 9203, 9002, 9102
WHERE NOT EXISTS (SELECT 1 FROM managers WHERE id = 9203);

INSERT INTO managers (id, user_id, todo_id)
SELECT 9204, 9003, 9103
WHERE NOT EXISTS (SELECT 1 FROM managers WHERE id = 9204);

INSERT INTO comments (id, created_at, modified_at, contents, user_id, todo_id)
SELECT 9301, '2026-06-10 10:10:00', '2026-06-10 10:10:00', 'First projection comment.', 9002, 9101
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE id = 9301);

INSERT INTO comments (id, created_at, modified_at, contents, user_id, todo_id)
SELECT 9302, '2026-06-10 10:20:00', '2026-06-10 10:20:00', 'Second projection comment.', 9003, 9101
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE id = 9302);

INSERT INTO comments (id, created_at, modified_at, contents, user_id, todo_id)
SELECT 9303, '2026-06-11 12:10:00', '2026-06-11 12:10:00', 'Paging comment.', 9003, 9102
WHERE NOT EXISTS (SELECT 1 FROM comments WHERE id = 9303);

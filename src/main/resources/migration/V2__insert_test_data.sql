INSERT INTO users (id, login, email, password) VALUES (1, 'test', 'test@test.com', '123');
INSERT INTO roles (id, role) VALUES (1, 'USER');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
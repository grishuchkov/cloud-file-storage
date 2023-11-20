CREATE TABLE users(
    id bigserial primary key,
    login varchar(50) not null unique,
    email varchar(50) not null unique,
    password varchar(256) not null
);

CREATE TABLE user_roles(
    user_id bigint references users(id),
    role varchar(50) not null,
    primary key (user_id, role)
);
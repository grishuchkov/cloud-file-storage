CREATE TABLE users(
    id bigserial primary key,
    login varchar(50) not null unique,
    email varchar(50) not null unique,
    password varchar(256) not null
);

CREATE TABLE roles(
    id serial primary key,
    role varchar(50) not null unique
);

CREATE TABLE user_roles(
    user_id bigint not null references users(id),
    role_id int not null references roles(id),
    primary key (user_id, role_id)
)

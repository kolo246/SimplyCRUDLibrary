CREATE TABLE users
(
    id           serial primary key,
    name         varchar(50) not null,
    email        varchar(50) not null,
    phone_number varchar(12) not null,
    age          integer,
    deleted      boolean default false
);
CREATE TABLE books
(
    id_books serial primary key,
    title    varchar(50) not null,
    author   varchar(50) not null,
    pages    integer,
    user_id  integer,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);
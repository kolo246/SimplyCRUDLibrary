CREATE TABLE users(
    id serial primary key,
    name varchar(50) not null,
    email varchar(50) not null,
    phoneNumber varchar(12) not null,
    age integer,
    isDeleted boolean default false
);
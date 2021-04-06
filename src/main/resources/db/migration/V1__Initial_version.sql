CREATE TABLE users(
    id serial primary key,
    name varchar(50) not null,
    email varchar(50) not null,
    phoneNumber varchar(12) not null,
    age integer,
    isDeleted boolean default false
);
insert into users (id, name, email, phoneNumber, age) values (1, 'Heather', 'hcrippill0@sfgate.com', '724-813-9979', 30);
insert into users (id, name, email, phoneNumber, age) values (2, 'Gottfried', 'glepick1@hud.gov', '333-146-2225', 67);
insert into users (id, name, email, phoneNumber, age) values (3, 'Virgina', 'vbromley2@naver.com', '858-576-7154', 59);
insert into users (id, name, email, phoneNumber, age) values (4, 'Amby', 'aennals3@devhub.com', '438-222-7530', 4);
insert into users (id, name, email, phoneNumber, age) values (5, 'Floris', 'fkellock4@techcrunch.com', '171-991-9651', 41);
insert into users (id, name, email, phoneNumber, age) values (6, 'Dru', 'dbye5@mysql.com', '576-724-3782', 22);
insert into users (id, name, email, phoneNumber, age) values (7, 'Davide', 'dbold6@dailymail.co.uk', '186-528-3208', 19);
insert into users (id, name, email, phoneNumber, age) values (8, 'Ogdon', 'okellough7@github.com', '697-143-1651', 69);
insert into users (id, name, email, phoneNumber, age) values (9, 'Kipp', 'kboggas8@google.cn', '170-837-1005', 81);
insert into users (id, name, email, phoneNumber, age) values (10, 'Ricki', 'rhartup9@rambler.ru', '391-674-9207', 14);

alter sequence users_id_seq restart with 11;
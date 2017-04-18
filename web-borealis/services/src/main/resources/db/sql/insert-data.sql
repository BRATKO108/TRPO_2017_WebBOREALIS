insert into users(username, password) values ('admin', '1');
insert into users(username, password) values ('user', '2');
insert into users(username, password) values ('ivan', '3');

insert into roles(user_id, role) values(1, 'ROLE_ADMIN');
insert into roles(user_id, role) values(2, 'ROLE_USER');
insert into roles(user_id, role) values(3, 'ROLE_USER');


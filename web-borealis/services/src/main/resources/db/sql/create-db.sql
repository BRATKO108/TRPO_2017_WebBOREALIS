create table users(
                id bigint not null auto_increment primary key,
                username varchar(255) not null unique,
                password varchar(255) not null);


create table roles(
                id bigint not null auto_increment,
                user_id bigint not null,
                role varchar(255),
                foreign key(user_id) references users(id) on update cascade);

alter table roles add foreign key (user_id) references users(id);


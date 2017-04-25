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

create table projects(
                id bigint not null auto_increment primary key,
                title varchar(255),
                description varchar(255),
                owner varchar(255) not null,
                repo varchar(255) not null,
                commit varchar(255),
                branch varchar(255),
                path varchar(255),
                userid bigint,
                foreign key(user_id) references users(id) on update cascade
);

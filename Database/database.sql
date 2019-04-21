SET timezone TO 'UTC';
create extension if not exists "uuid-ossp";
-- user manager
create table if not exists app_user
(
  user_id               uuid default uuid_generate_v4() primary key,
  user_name             varchar(36)  not null unique,
  encrypted_password    varchar(128) not null,
  token_active_after    timestamp not null default now()
);
create table if not exists app_role
(
  role_id   bigint      not null primary key,
  role_name varchar(30) not null unique
);
create table if not exists user_role
(
  user_id uuid not null,
  role_id bigint not null,
  primary key (user_id, role_id),
  constraint user_role_fk1 foreign key (user_id) references app_user (user_id),
  constraint user_role_fk2 foreign key (role_id) references app_role (role_id)
);
--
insert into app_user (user_id, user_name, encrypted_password, token_active_after)
values ('44260380-cc98-4296-acac-1d57b88b2535', 'duc010298', '$2a$10$hjBz774Yg4Fff44DYseK4.w4p27w2enR0W.QxSxlIXA.TcxS2bYV.', default);
--
insert into app_role (role_id, role_name)
values (1, 'ROLE_ADMIN');

insert into app_role (role_id, role_name)
values (2, 'ROLE_MEMBER');
--
insert into user_role
values ('44260380-cc98-4296-acac-1d57b88b2535', 1);

insert into user_role
values ('44260380-cc98-4296-acac-1d57b88b2535', 2);
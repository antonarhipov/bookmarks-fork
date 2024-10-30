drop table if exists bookmarks;
drop sequence if exists bookmark_id_seq;

create sequence bookmark_id_seq start with 1 increment by 1;
create table bookmarks
(
    id         bigint       not null default nextval('bookmark_id_seq'),
    title      varchar(200) not null,
    url        varchar(500) not null,
    created_at timestamp    not null default now(),
    updated_at timestamp,
    primary key (id)
);

insert into bookmarks(title, url, created_at)
values ('IntelliJ IDEA documentation', 'https://www.jetbrains.com/help/idea/getting-started.html', '2021-06-26'),
       ('IntelliJ IDEA YouTube channel', 'https://www.youtube.com/intellijidea', '2021-10-10'),
       ('JetBrains Guide', 'https://www.jetbrains.com/guide/', '2023-12-05'),
       ('Java Guide', 'https://www.jetbrains.com/guide/java/', '2024-08-15');


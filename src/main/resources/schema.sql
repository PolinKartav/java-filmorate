create table if not exists  PUBLIC.GENRE
(
    GENRE_ID   INTEGER auto_increment
        primary key,
    GENRE_NAME CHARACTER VARYING(30) not null
);

create table if not exists RATING
(
    RATING_ID   INTEGER auto_increment
        primary key,
    RATING_NAME CHARACTER VARYING(10) not null
);

create table if not exists PUBLIC.FILMS
(
    FILM_ID           INTEGER auto_increment
        primary key,
    FILM_NAME         CHARACTER VARYING(30) not null,
    FILM_DESCRIPTION  CHARACTER VARYING(200),
    FILM_DURATION     INTEGER,
    FILM_RELEASE_DATE TIMESTAMP,
    RATING_ID         INTEGER,
    constraint FILMS_RATING_FK
        foreign key (RATING_ID) references PUBLIC.RATING
);

create table PUBLIC.USERS
(
    USER_ID       BIGINT auto_increment
        primary key,
    USER_NAME     CHARACTER VARYING(30),
    USER_LOGIN    CHARACTER VARYING(50)  not null,
    USER_EMAIL    CHARACTER VARYING(100) not null,
    USER_BIRTHDAY TIMESTAMP
);

create table PUBLIC.FILM_LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint PK_FILM_LIKES
        primary key (FILM_ID, USER_ID)
);

create table PUBLIC.USER_FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint PK_USER_FRIENDS
        primary key (USER_ID, FRIEND_ID)
);

create table PUBLIC.FILMS_GENRES
(
    FILM_ID  BIGINT  not null,
    GENRE_ID INTEGER not null,
    constraint PK_FILMS_GENRES
        primary key (FILM_ID, GENRE_ID)
);
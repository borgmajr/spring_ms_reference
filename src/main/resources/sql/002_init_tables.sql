CREATE TABLE users
(
    id SERIAL PRIMARY KEY,
    username character varying(100) UNIQUE,
    password character varying(64),
    email character varying(100),
    deleted boolean default false
);

CREATE TABLE roles
(
    id SERIAL PRIMARY KEY,
    name character varying(100) UNIQUE
);

CREATE TABLE user_roles
(
    userid int not null REFERENCES users(id) ON DELETE CASCADE,
    roleid int not null REFERENCES roles(id)
);

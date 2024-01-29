CREATE TABLE users (
    id RAW(16) NOT NULL PRIMARY KEY,
    nick VARCHAR2(32 CHAR),
    name VARCHAR2(255 char) NOT NULL UNIQUE,
    birth_date TIMESTAMP(6) NOT NULL
);

CREATE TABLE user_stack (
    stack VARCHAR2(32 CHAR),
    user_id RAW(16) NOT NULL CONSTRAINT fk_users REFERENCES users
);
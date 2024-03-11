CREATE TABLE user (
    email VARCHAR(255) NOT NULL PRIMARY KEY
);

CREATE TABLE post (
    id VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL UNIQUE,
    content TEXT NOT NULL
);
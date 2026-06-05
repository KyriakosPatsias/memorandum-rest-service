CREATE TABLE IF NOT EXISTS note  (
  noteId SERIAL PRIMARY KEY,
  title VARCHAR(250) NULL,
  content VARCHAR NULL,
  category VARCHAR(50) NULL,
  is_favorite BOOLEAN DEFAULT FALSE,
  userId SERIAL REFERENCES users(userId),
  created_on TIMESTAMP,
  updated_on TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category  (
    categoryId SERIAL PRIMARY KEY,
    name VARCHAR(50) NULL,
    color VARCHAR(20) NULL
);


CREATE TABLE IF NOT EXISTS users  (
    userId INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    timezone VARCHAR(20) NULL,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
    );

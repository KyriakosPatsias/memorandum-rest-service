CREATE SCHEMA memorandum;

CREATE TABLE IF NOT EXISTS memorandum.users  (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cognito_id UUID NULL,
    username VARCHAR(50) NOT NULL,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    timezone VARCHAR(20) NULL,
    status VARCHAR(20) NOT NULL,
    created_on TIMESTAMP,
    updated_on TIMESTAMP
);

CREATE TABLE IF NOT EXISTS memorandum.categories  (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NULL,
    color VARCHAR(20) NULL,
    user_id INTEGER REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS memorandum.notes  (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title VARCHAR(250) NULL,
  content VARCHAR NULL,
  category INT REFERENCES categories(id),
  is_favorite BOOLEAN DEFAULT FALSE,
  user_id INT REFERENCES users(id) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_on TIMESTAMP,
  updated_on TIMESTAMP
);
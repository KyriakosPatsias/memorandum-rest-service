CREATE TABLE IF NOT EXISTS note  (
  noteId SERIAL PRIMARY KEY,
  title VARCHAR(250) NULL,
  content VARCHAR NULL,
  category VARCHAR(50) NULL,
  is_favorite BOOLEAN DEFAULT FALSE,
  created_on TIMESTAMP,
  updated_on TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category  (
    categoryId SERIAL PRIMARY KEY,
    name VARCHAR(50) NULL,
    color VARCHAR(20) NULL
);
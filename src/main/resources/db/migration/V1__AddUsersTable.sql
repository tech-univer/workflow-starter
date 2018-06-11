CREATE TABLE users (
  id       INT AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX username
  ON users (username);


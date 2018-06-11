CREATE TABLE flips (
  request_id VARCHAR(36) NOT NULL,
  id VARCHAR(36) PRIMARY KEY,
  outcome VARCHAR(5) NOT NULL,
  currency VARCHAR(3) NOT NULL,
  denomination INT NOT NULL,
  PRIMARY KEY (id)
);

CREATE INDEX request_id
  ON flips (request_id);


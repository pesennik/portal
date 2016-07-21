DROP TABLE verification_record;
DROP TABLE users;

CREATE TABLE users (
  id                INTEGER AUTO_INCREMENT PRIMARY KEY,

  uid               CHAR(36)      NOT NULL UNIQUE,
  email             VARCHAR(50)   NOT NULL UNIQUE,
  password_hash     CHAR(32)      NOT NULL,

  registration_date TIMESTAMP     NOT NULL,
  termination_date  TIMESTAMP     NULL,

  last_login_date   TIMESTAMP     NOT NULL,

  settings          VARCHAR(2048) NOT NULL,
  personal_info     VARCHAR(2048) NOT NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE verification_record (
  id                INTEGER AUTO_INCREMENT PRIMARY KEY,
  hash              CHAR(36)     NOT NULL UNIQUE,
  user_id           INTEGER      NOT NULL REFERENCES users (id),
  type              INTEGER      NOT NULL,
  value             VARCHAR(100) NOT NULL,
  creation_date     TIMESTAMP    NOT NULL,
  verification_date TIMESTAMP    NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


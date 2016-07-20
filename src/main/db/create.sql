CREATE TABLE users (
  id                INTEGER AUTO_INCREMENT PRIMARY KEY,

  login             VARCHAR(50)   NOT NULL UNIQUE,
  password_hash     CHAR(32)      NOT NULL,
  email             VARCHAR(50)   NOT NULL UNIQUE,
  uid               CHAR(32)      NOT NULL UNIQUE,

  registration_date TIMESTAMP     NOT NULL,
  termination_date  TIMESTAMP     NULL,

  last_login_date   TIMESTAMP     NOT NULL,
  email_checked     BOOLEAN       NOT NULL,

  settings          VARCHAR(2048) NOT NULL,
  personal_info     VARCHAR(2048) NOT NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE verification_record (
  id                INTEGER AUTO_INCREMENT PRIMARY KEY,
  hash              CHAR(32)     NOT NULL UNIQUE,
  user_id           INTEGER      NOT NULL REFERENCES users (id),
  type              INTEGER      NOT NULL,
  value             VARCHAR(100) NOT NULL,
  creation_date     TIMESTAMP    NOT NULL,
  verification_date TIMESTAMP    NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


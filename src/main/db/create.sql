DROP TABLE verification_record;
DROP TABLE user_songs;
DROP TABLE users;

# Пользователь сайта
CREATE TABLE users (
  id                INTEGER AUTO_INCREMENT PRIMARY KEY,

  login             VARCHAR(30)   NOT NULL UNIQUE,
  uid               CHAR(36)      NOT NULL UNIQUE,
  email             VARCHAR(50)   NOT NULL UNIQUE,
  password_hash     CHAR(32)      NOT NULL,

  registration_date DATETIME      NOT NULL,
  termination_date  DATETIME      NULL,

  last_login_date   DATETIME      NOT NULL,

  settings          VARCHAR(2048) NOT NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

# Запрос на что-либо: сброс пароля и тп. Определяется типом (type).
CREATE TABLE verification_record (
  id                INTEGER AUTO_INCREMENT PRIMARY KEY,
  hash              CHAR(36)     NOT NULL UNIQUE,
  user_id           INTEGER      NOT NULL REFERENCES users (id),
  type              INTEGER      NOT NULL,
  value             VARCHAR(100) NOT NULL,
  creation_date     DATETIME     NOT NULL,
  verification_date DATETIME     NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

# Пользовательский персональный вариант песни.
CREATE TABLE user_songs (
  id            INTEGER  AUTO_INCREMENT PRIMARY KEY,
  user_id       INTEGER     NOT NULL REFERENCES users (id),
  title         VARCHAR(64) NOT NULL,
  author        VARCHAR(64) NOT NULL,
  text          TEXT        NOT NULL,
  creation_date DATETIME    NOT NULL,
  deletion_date DATETIME DEFAULT NULL, # дата удаления. Мы храним некоторое время после удаления.
  extra         TEXT                   # дополнительная не-индексируемая информация. Хранится в JSON форме
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS posts;


CREATE TABLE users
(
    id              INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(254) NOT NULL UNIQUE, -- RFC5321 メアドの最大長254文字
    hashed_password VARCHAR(255) NOT NULL,        -- BCryptで生成されるエンコード文字列は60文字、アルゴリズム変更を考慮して255とする
    authority       VARCHAR(20)  NOT NULL,        -- read/writeの権限（いたずら防止用として）
    disabled        BOOLEAN      NOT NULL DEFAULT FALSE,
    disabled_at     TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts
(
    id              INT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         INT       NOT NULL,
    image_file_name VARCHAR(255),
    comment         VARCHAR(255),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
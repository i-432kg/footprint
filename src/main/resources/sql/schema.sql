DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS comments;


CREATE TABLE users
(
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(254) NOT NULL UNIQUE, -- RFC5321 メアドの最大長254文字
    hashed_password VARCHAR(255) NOT NULL,        -- BCryptで生成されるエンコード文字列は60文字、アルゴリズム変更を考慮して255とする
    authority       VARCHAR(20)  NOT NULL,        -- read/writeの権限（いたずら防止用として）
    disabled        BOOLEAN      NOT NULL DEFAULT FALSE,
    disabled_at     TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts
(
    id              BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT    NOT NULL,
    image_file_name VARCHAR(255),
    comment         VARCHAR(255),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE replies
(
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    post_id         BIGINT       NOT NULL, -- どの投稿に対するコメントか
    user_id         BIGINT       NOT NULL, -- 誰のコメントか
    parent_reply_id BIGINT,                -- どの返信に対する返信か（投稿に対する返信の場合null）
    content         VARCHAR(255) NOT NULL, -- コメント内容：commentが予約語なのでcontentに変更
    image_file_name VARCHAR(255),          -- 機能拡張：画像添付
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    child_count     INT          NOT NULL DEFAULT 0, -- 次階層の子返信の数
    FOREIGN KEY (post_id) REFERENCES posts (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
    -- depth
    -- is_deleted
);
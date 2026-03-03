DROP TABLE IF EXISTS post_images;
DROP TABLE IF EXISTS replies;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- users テーブル
CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,               -- 主キー
    username      VARCHAR(50)  NOT NULL UNIQUE,                    -- 表示用ユーザ名
    email         VARCHAR(255) NOT NULL UNIQUE,                    -- ログインID
    password_hash VARCHAR(255) NOT NULL,                           -- ハッシュ化パスワード
    birthdate     DATE         NOT NULL,                           -- 生年月日
    is_active     BOOLEAN      NOT NULL DEFAULT FALSE,             -- アカウント認証状態
    disabled      BOOLEAN      NOT NULL DEFAULT FALSE,             -- アカウント削除状態
    disabled_at   TIMESTAMP    NULL,                               -- 削除日時
    last_login_at TIMESTAMP    NULL,                               -- 最終ログイン日時
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 作成日時
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 更新日時
);

-- posts テーブル
CREATE TABLE posts
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 主キー
    user_id      BIGINT        NOT NULL,                           -- 投稿者
    caption      TEXT          NULL,                               -- 投稿本文
    has_location BOOLEAN       NOT NULL DEFAULT FALSE,             -- 位置情報有無
    latitude     DECIMAL(9, 6) NULL,                               -- 緯度
    longitude    DECIMAL(9, 6) NULL,                               -- 経度
    taken_at     TIMESTAMP     NULL,                               -- 撮影日時（EXIF）
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 作成日時
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 更新日時
    CONSTRAINT fk_posts_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

-- タイムライン用（シーク法）
CREATE INDEX idx_posts_timeline ON posts (created_at DESC, id DESC);
-- マイページ用
CREATE INDEX idx_posts_user_timeline ON posts (user_id, created_at DESC);
-- 地図表示用（bbox）
CREATE INDEX idx_posts_location ON posts (has_location, latitude, longitude);

-- post_images テーブル
CREATE TABLE post_images
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 主キー
    post_id        BIGINT        NOT NULL,                           -- 紐づく投稿
    sort_order     INT           NOT NULL DEFAULT 0,                 -- 表示順
    storage_type   VARCHAR(16)   NOT NULL,                           -- LOCAL / S3
    path           VARCHAR(1024) NOT NULL,                           -- 保存パス or オブジェクトキー
    content_type   VARCHAR(128)  NOT NULL,                           -- MIMEタイプ
    size_bytes     BIGINT        NOT NULL,                           -- ファイルサイズ
    width          INT           NULL,                               -- 横幅
    height         INT           NULL,                               -- 高さ
    exif_available BOOLEAN       NOT NULL DEFAULT FALSE,             -- EXIF取得可否
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 作成日時
    CONSTRAINT fk_post_images_post_id FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT uq_post_images_order UNIQUE (post_id, sort_order)
);

-- replies テーブル
CREATE TABLE replies
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,            -- 主キー
    post_id         BIGINT    NOT NULL,                           -- 対象投稿
    user_id         BIGINT    NOT NULL,                           -- 投稿者
    parent_reply_id BIGINT    NULL,                               -- 親返信
    message         TEXT      NOT NULL,                           -- 返信本文
    child_count     INT       NOT NULL DEFAULT 0,                 -- 次階層の返信数
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 作成日時
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 更新日時
    CONSTRAINT fk_replies_post_id FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_replies_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_replies_parent_id FOREIGN KEY (parent_reply_id) REFERENCES replies (id) ON DELETE CASCADE
);

-- 投稿詳細での返信一覧取得用
CREATE INDEX idx_replies_post_id_created ON replies (post_id, created_at);
-- 特定の返信に対するツリー取得用
CREATE INDEX idx_replies_parent_id_created ON replies (parent_reply_id, created_at);
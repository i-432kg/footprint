SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS post_images;
DROP TABLE IF EXISTS replies;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id     CHAR(26)     NOT NULL,
    username      VARCHAR(50)  NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    birthdate     DATE         NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT FALSE,
    disabled      BOOLEAN      NOT NULL DEFAULT FALSE,
    disabled_at   DATETIME     NULL,
    last_login_at DATETIME     NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_users_public_id UNIQUE (public_id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_public_id ON users (public_id);

CREATE TABLE posts
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id    CHAR(26)      NOT NULL,
    user_id      CHAR(26)      NOT NULL,
    caption      TEXT          NULL,
    has_location BOOLEAN       NOT NULL DEFAULT FALSE,
    latitude     DECIMAL(9, 6) NULL,
    longitude    DECIMAL(9, 6) NULL,
    taken_at     DATETIME      NULL,
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_posts_public_id UNIQUE (public_id),
    CONSTRAINT fk_posts_user_id FOREIGN KEY (user_id) REFERENCES users (public_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_posts_public_id ON posts (public_id);
CREATE INDEX idx_posts_timeline ON posts (created_at, id);
CREATE INDEX idx_posts_user_timeline ON posts (user_id, created_at);
CREATE INDEX idx_posts_location ON posts (has_location, latitude, longitude);

CREATE TABLE post_images
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id        CHAR(26)      NOT NULL,
    sort_order     INT           NOT NULL DEFAULT 0,
    storage_type   VARCHAR(16)   NOT NULL,
    object_key     VARCHAR(1024) NOT NULL,
    file_extension VARCHAR(128)  NOT NULL,
    size_bytes     BIGINT        NOT NULL,
    width          INT           NULL,
    height         INT           NULL,
    exif_available BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_images_post_id FOREIGN KEY (post_id) REFERENCES posts (public_id) ON DELETE CASCADE,
    CONSTRAINT uq_post_images_order UNIQUE (post_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE replies
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id   CHAR(26)  NOT NULL,
    post_id     CHAR(26)  NOT NULL,
    user_id     CHAR(26)  NOT NULL,
    parent_id   CHAR(26)  NULL,
    message     TEXT      NOT NULL,
    child_count INT       NOT NULL DEFAULT 0,
    created_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_replies_public_id UNIQUE (public_id),
    CONSTRAINT fk_replies_post_id FOREIGN KEY (post_id) REFERENCES posts (public_id) ON DELETE CASCADE,
    CONSTRAINT fk_replies_user_id FOREIGN KEY (user_id) REFERENCES users (public_id),
    CONSTRAINT fk_replies_parent_id FOREIGN KEY (parent_id) REFERENCES replies (public_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_replies_public_id ON replies (public_id);
CREATE INDEX idx_replies_post_id_created ON replies (post_id, created_at);
CREATE INDEX idx_replies_parent_id_created ON replies (parent_id, created_at);
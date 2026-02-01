INSERT INTO users(name, hashed_password, authority, birthdate)
VALUES ('user', '$2a$10$c/xgJEeLdWay1Qv/mVP1WurNreZ32vuNeRxSTCEQ30c/gY75RQLwe', 'GENERAL', '2000-01-01');

INSERT INTO posts(user_id, image_file_name, comment, latitude, longitude)
VALUES (1, '3f61d8f7-87c8-402f-afa3-6f36a9690864_knowledge01.webp', '美味しいランチを堪能。', 35.6586, 139.7454);
INSERT INTO posts(user_id, image_file_name, comment, latitude, longitude)
VALUES (1, '04c8c6d7-8266-428e-80d7-58b36d4fd419_00.jpg', '素晴らしい景色でした！', 35.6812, 139.7671);

INSERT INTO replies(post_id, user_id, parent_reply_id, content, child_count)
VALUES (1, 1, null, 'いちこめ', 2);
INSERT INTO replies(post_id, user_id, parent_reply_id, content)
VALUES (1, 1, 1, 'にこめ');
INSERT INTO replies(post_id, user_id, parent_reply_id, content)
VALUES (1, 1, 1, 'にこめ2');
INSERT INTO replies(post_id, user_id, parent_reply_id, content, child_count)
VALUES (2, 1, null, '1get', 2);
INSERT INTO replies(post_id, user_id, parent_reply_id, content)
VALUES (2, 1, 4, '2get');
INSERT INTO replies(post_id, user_id, parent_reply_id, content)
VALUES (2, 1, 4, '2get2');
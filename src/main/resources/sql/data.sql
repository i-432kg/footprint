-- ユーザ
INSERT INTO users(username, email, password_hash, birthdate, is_active)
VALUES ('test', 'test@example.com', '$2a$10$c/xgJEeLdWay1Qv/mVP1WurNreZ32vuNeRxSTCEQ30c/gY75RQLwe', '2000-01-01', TRUE);

-- 投稿と画像データの流し込み
INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_025_IMG_1222.jpeg', TRUE, 36.310883, 140.165206, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (1, 0, 'LOCAL', 'test_image_025_IMG_1222.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_007_IMG_0322.jpeg', TRUE, 34.675072, 138.945586, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (2, 0, 'LOCAL', 'test_image_007_IMG_0322.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_042_IMG_3317.jpeg', TRUE, 35.313003, 139.785994, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (3, 0, 'LOCAL', 'test_image_042_IMG_3317.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_011_IMG_0481.jpeg', TRUE, 36.061189, 140.327178, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (4, 0, 'LOCAL', 'test_image_011_IMG_0481.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_023_IMG_1105.jpeg', TRUE, 36.127994, 140.590483, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (5, 0, 'LOCAL', 'test_image_023_IMG_1105.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_002_IMG_0090.jpeg', TRUE, 36.310897, 140.584625, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (6, 0, 'LOCAL', 'test_image_002_IMG_0090.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_046_IMG_4128.jpeg', TRUE, 36.232692, 140.090911, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (7, 0, 'LOCAL', 'test_image_046_IMG_4128.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_035_IMG_2680.jpeg', TRUE, 35.938122, 139.119050, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (8, 0, 'LOCAL', 'test_image_035_IMG_2680.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_052_IMG_5430.jpeg', TRUE, 35.757911, 138.236800, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (9, 0, 'LOCAL', 'test_image_052_IMG_5430.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_022_IMG_1042.jpeg', TRUE, 36.081219, 140.180831, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (10, 0, 'LOCAL', 'test_image_022_IMG_1042.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_054_IMG_5644.jpeg', TRUE, 35.883158, 138.726350, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (11, 0, 'LOCAL', 'test_image_054_IMG_5644.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_036_IMG_2767.jpeg', TRUE, 35.948364, 139.104692, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (12, 0, 'LOCAL', 'test_image_036_IMG_2767.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_043_IMG_3467.jpeg', TRUE, 35.446639, 139.180161, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (13, 0, 'LOCAL', 'test_image_043_IMG_3467.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_047_IMG_4731.jpeg', TRUE, 36.799317, 139.390883, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (14, 0, 'LOCAL', 'test_image_047_IMG_4731.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_040_IMG_3237.jpeg', TRUE, 35.120822, 138.919053, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (15, 0, 'LOCAL', 'test_image_040_IMG_3237.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_024_IMG_1154.jpeg', TRUE, 37.338603, 140.669511, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (16, 0, 'LOCAL', 'test_image_024_IMG_1154.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_048_IMG_5021.jpeg', TRUE, 36.931058, 139.317842, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (17, 0, 'LOCAL', 'test_image_048_IMG_5021.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_003_IMG_0165.jpeg', TRUE, 36.087600, 140.081803, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (18, 0, 'LOCAL', 'test_image_003_IMG_0165.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_041_IMG_3274.jpeg', TRUE, 35.252056, 140.163483, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (19, 0, 'LOCAL', 'test_image_041_IMG_3274.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_050_IMG_5228.jpeg', TRUE, 36.902531, 139.172622, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (20, 0, 'LOCAL', 'test_image_050_IMG_5228.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_006_IMG_0260.jpeg', TRUE, 36.761642, 140.402878, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (21, 0, 'LOCAL', 'test_image_006_IMG_0260.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_012_IMG_0522.jpeg', TRUE, 34.799689, 135.245178, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (22, 0, 'LOCAL', 'test_image_012_IMG_0522.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_029_IMG_1995.jpeg', TRUE, 35.695989, 140.857103, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (23, 0, 'LOCAL', 'test_image_029_IMG_1995.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_026_IMG_1315.jpeg', TRUE, 36.727853, 140.624236, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (24, 0, 'LOCAL', 'test_image_026_IMG_1315.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_005_IMG_0257.jpeg', TRUE, 34.715350, 138.987747, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (25, 0, 'LOCAL', 'test_image_005_IMG_0257.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_053_IMG_5637.jpeg', TRUE, 35.880767, 138.726944, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (26, 0, 'LOCAL', 'test_image_053_IMG_5637.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_044_IMG_3649.jpeg', TRUE, 36.794750, 139.121444, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (27, 0, 'LOCAL', 'test_image_044_IMG_3649.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_027_IMG_1414.jpeg', TRUE, 36.737747, 139.504014, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (28, 0, 'LOCAL', 'test_image_027_IMG_1414.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_028_IMG_1503.jpeg', TRUE, 35.456081, 139.637358, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (29, 0, 'LOCAL', 'test_image_028_IMG_1503.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_019_IMG_0751.jpeg', TRUE, 36.180050, 140.201111, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (30, 0, 'LOCAL', 'test_image_019_IMG_0751.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_021_IMG_0877.jpeg', TRUE, 35.064853, 135.824631, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (31, 0, 'LOCAL', 'test_image_021_IMG_0877.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_045_IMG_3846.jpeg', TRUE, 37.129781, 139.974319, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (32, 0, 'LOCAL', 'test_image_045_IMG_3846.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_004_IMG_0222.jpeg', TRUE, 34.969831, 138.926103, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (33, 0, 'LOCAL', 'test_image_004_IMG_0222.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_049_IMG_5149.jpeg', TRUE, 36.921975, 139.210189, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (34, 0, 'LOCAL', 'test_image_049_IMG_5149.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_010_IMG_0468.jpeg', TRUE, 36.360694, 140.621078, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (35, 0, 'LOCAL', 'test_image_010_IMG_0468.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_014_IMG_0556.jpeg', TRUE, 35.807597, 137.240936, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (36, 0, 'LOCAL', 'test_image_014_IMG_0556.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_032_IMG_2162.jpeg', TRUE, 35.159728, 139.834258, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (37, 0, 'LOCAL', 'test_image_032_IMG_2162.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_015_IMG_0587.jpeg', TRUE, 36.027175, 138.077317, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (38, 0, 'LOCAL', 'test_image_015_IMG_0587.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_020_IMG_0804.jpeg', TRUE, 36.339939, 140.602494, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (39, 0, 'LOCAL', 'test_image_020_IMG_0804.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_013_IMG_0527.jpeg', TRUE, 36.354747, 140.611708, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (40, 0, 'LOCAL', 'test_image_013_IMG_0527.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_018_IMG_0667.jpeg', TRUE, 37.124986, 139.962831, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (41, 0, 'LOCAL', 'test_image_018_IMG_0667.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_038_IMG_2850.jpeg', TRUE, 36.748222, 139.488694, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (42, 0, 'LOCAL', 'test_image_038_IMG_2850.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_033_IMG_2321.jpeg', TRUE, 35.624447, 139.243256, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (43, 0, 'LOCAL', 'test_image_033_IMG_2321.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_051_IMG_5408.jpeg', TRUE, 35.764194, 138.247528, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (44, 0, 'LOCAL', 'test_image_051_IMG_5408.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_016_IMG_0637.jpeg', TRUE, 35.115775, 140.120178, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (45, 0, 'LOCAL', 'test_image_016_IMG_0637.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_030_IMG_2048.jpeg', TRUE, 35.698339, 140.845794, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (46, 0, 'LOCAL', 'test_image_030_IMG_2048.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_001_IMG_0066.jpeg', TRUE, 36.225414, 140.110764, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (47, 0, 'LOCAL', 'test_image_001_IMG_0066.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_009_IMG_0432.jpeg', TRUE, 35.090278, 136.879897, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (48, 0, 'LOCAL', 'test_image_009_IMG_0432.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_039_IMG_2868.jpeg', TRUE, 36.758731, 139.488983, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (49, 0, 'LOCAL', 'test_image_039_IMG_2868.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_008_IMG_0344.jpeg', TRUE, 35.027611, 138.900344, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (50, 0, 'LOCAL', 'test_image_008_IMG_0344.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_017_IMG_0667 2.jpeg', TRUE, 36.195397, 140.216003, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (51, 0, 'LOCAL', 'test_image_017_IMG_0667 2.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_000_IMG_0009.jpeg', TRUE, 36.063225, 140.352828, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (52, 0, 'LOCAL', 'test_image_000_IMG_0009.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_031_IMG_2066.jpeg', TRUE, 35.712578, 140.834794, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (53, 0, 'LOCAL', 'test_image_031_IMG_2066.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_037_IMG_2793.jpeg', TRUE, 35.984506, 139.279647, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (54, 0, 'LOCAL', 'test_image_037_IMG_2793.jpeg', 'image/jpeg', 1024000, TRUE);

INSERT INTO posts (user_id, caption, has_location, latitude, longitude, created_at) VALUES (1, 'Auto generated post for test_image_034_IMG_2383.jpeg', TRUE, 35.652114, 139.166625, CURRENT_TIMESTAMP);
INSERT INTO post_images (post_id, sort_order, storage_type, path, content_type, size_bytes, exif_available) VALUES (55, 0, 'LOCAL', 'test_image_034_IMG_2383.jpeg', 'image/jpeg', 1024000, TRUE);

-- 返信
-- 投稿1への返信
INSERT INTO replies(post_id, user_id, parent_reply_id, body, child_count) VALUES (1, 1, null, 'いちこめ', 2);
INSERT INTO replies(post_id, user_id, parent_reply_id, body, child_count) VALUES (1, 1, 1, 'にこめ', 0);
INSERT INTO replies(post_id, user_id, parent_reply_id, body, child_count) VALUES (1, 1, 1, 'にこめ2', 0);
-- 投稿2へ
INSERT INTO replies(post_id, user_id, parent_reply_id, body, child_count) VALUES (2, 1, null, '1get', 2);
INSERT INTO replies(post_id, user_id, parent_reply_id, body, child_count) VALUES (2, 1, 4, '2get', 0);
INSERT INTO replies(post_id, user_id, parent_reply_id, body, child_count) VALUES (2, 1, 4, '2get2', 0);
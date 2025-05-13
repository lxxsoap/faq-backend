INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 1, '问题行为', '问题行为', null, 0, '2024-12-07 15:57:36'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 1 OR name = '问题行为');

INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 2, '刻板行为/思维', '刻板行为/思维', null, 0, '2024-12-07 15:57:58'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 2 OR name = '刻板行为/思维');

INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 3, '自我刺激', '自我刺激', null, 0, '2024-12-07 15:58:11'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 3 OR name = '自我刺激');

INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 4, '语言行为', '语言行为', null, 0, '2024-12-07 15:58:25'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 4 OR name = '语言行为');

INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 5, '情绪管理', '情绪管理', null, 0, '2024-12-07 15:58:47'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 5 OR name = '情绪管理');

INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 6, '社交融合', '社交融合', null, 0, '2024-12-07 15:59:01'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 6 OR name = '社交融合');

INSERT INTO `tag` (id, name, description, icon, topic_count, in_time)
SELECT 7, '生活自理', '生活自理', null, 0, '2024-12-07 15:59:12'
WHERE NOT EXISTS (SELECT 1 FROM `tag` WHERE id = 7 OR name = '生活自理');
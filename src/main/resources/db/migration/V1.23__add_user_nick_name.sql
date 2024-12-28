SET @exist := (SELECT COUNT(*)
               FROM information_schema.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE()
                 AND TABLE_NAME = 'user'
                 AND COLUMN_NAME = 'nick_name');

SET @query := IF(@exist = 0,
    'ALTER TABLE `user` ADD COLUMN `nick_name` varchar(32) COLLATE utf8mb4_bin NULL COMMENT "用户昵称"',
    'SELECT "Column nick_name already exists"');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 
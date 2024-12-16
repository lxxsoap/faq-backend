SET @exist_career := (SELECT COUNT(*) 
                     FROM INFORMATION_SCHEMA.COLUMNS 
                     WHERE TABLE_NAME = 'user' 
                     AND COLUMN_NAME = 'career'
                     AND TABLE_SCHEMA = DATABASE());

SET @exist_tags := (SELECT COUNT(*) 
                   FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'user' 
                   AND COLUMN_NAME = 'tags'
                   AND TABLE_SCHEMA = DATABASE());

SET @alter_career := IF(@exist_career = 0,
    'ALTER TABLE user ADD career varchar(8) null comment "职业"',
    'SELECT "career column already exists"');

SET @alter_tags := IF(@exist_tags = 0,
    'ALTER TABLE user ADD tags varchar(255) null comment "兴趣标签"',
    'SELECT "tags column already exists"');

PREPARE stmt_career FROM @alter_career;
PREPARE stmt_tags FROM @alter_tags;

EXECUTE stmt_career;
EXECUTE stmt_tags;

DEALLOCATE PREPARE stmt_career;
DEALLOCATE PREPARE stmt_tags;
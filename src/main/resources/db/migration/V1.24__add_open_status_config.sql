INSERT INTO system_config (`key`, value, description, pid, type, `option`, reboot)
SELECT 'open_status', '1', '小程序审核时是否展示内容', 1000, 'number', null, 0
WHERE NOT EXISTS (
    SELECT 1 FROM system_config WHERE `key` = 'open_status'
);
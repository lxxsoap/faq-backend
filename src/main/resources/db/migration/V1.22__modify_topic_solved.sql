ALTER TABLE topic
    modify solved bit default b'0' not null comment '是否已解决';
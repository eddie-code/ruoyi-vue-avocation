CREATE TABLE `test`
(
    `id`       bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `mobile`   varchar(11) DEFAULT NULL COMMENT '手机号',
    `password` varchar(50) DEFAULT NULL COMMENT '密码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `mobile_unique` (`mobile`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='示例';

INSERT INTO `ry-vue`.`test`(`mobile`, `password`) VALUES ('13800138000', '123');
INSERT INTO `ry-vue`.`test`(`mobile`, `password`) VALUES ('13800138001', '456');

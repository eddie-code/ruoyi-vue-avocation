# filetrans 语音识别表
DROP TABLE
    IF
        EXISTS `biz_filetrans`;
CREATE TABLE `biz_filetrans`
(
    `id`                BIGINT       NOT NULL COMMENT 'id',
    `member_id`         BIGINT       NOT NULL COMMENT '会员ID',
    `name`              VARCHAR(200) NOT NULL COMMENT '文件名称',
    `second`            INT COMMENT '音频文件时长|秒',
    `amount`            DECIMAL(6, 2) COMMENT '金额|元，second*单价',
    `audio`             VARCHAR(500) COMMENT '文件链接',
    `file_sign`         CHAR(32) COMMENT '文件签名md5',
    `pay_status`        CHAR(2) COMMENT '支付状态|枚举[FiletransPayStatusEnum];',
    `status`            CHAR(2) COMMENT '识别状态|枚举[FiletransStatusEnum];',
    `lang`              CHAR(16)     NOT NULL COMMENT '音频语言|枚举[FiletransLangEnum]',
    `vod`               CHAR(32) COMMENT 'VOD|videoId',
    `task_id`           CHAR(32) COMMENT '任务ID',
    `trans_status_code` INT COMMENT '转换状态码',
    `trans_status_text` VARCHAR(200) COMMENT '转换状态说明',
    `trans_time`        datetime(3) COMMENT '转换时间|开始转换的时间',
    `solve_time`        datetime(3) COMMENT '完成时间|录音文件识别完成的时间',
    `tenant_id`         VARCHAR(20)           DEFAULT '000000' COMMENT '租户编号',
    `del_flag`          CHAR(1)               DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
    `create_dept`       BIGINT(20) COMMENT '创建部门',
    `create_by`         BIGINT(20) COMMENT '创建者',
    `create_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         BIGINT(20) COMMENT '更新者',
    `update_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  DEFAULT charset = utf8mb4 COMMENT = '语音识别表';

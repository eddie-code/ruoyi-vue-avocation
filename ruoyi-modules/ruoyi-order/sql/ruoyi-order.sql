-- order_info 订单信息表
-- 保存订单 I； 调用支付宝；回调接口更新成S；补偿查询
drop table if exists `order_info`;
create table `order_info`
(
    `id`         bigint        not null comment 'id',
    `order_no`   char(20)      not null comment '订单号',
    `order_at`   datetime(3)   not null comment '下单时间',
    `order_type` char(1)       not null comment '订单类型|枚举[OrderInfoOrderTypeEnum]',
    `info`       varchar(100)  not null comment '订单信息|根据订单类型，存放不同的信息（给程序使用，比如biz_filetrans表的id）',
    `member_id`  bigint        not null comment '会员|id',
    `amount`     decimal(6, 2) not null comment '订单金额(元)',
    `pay_at`     datetime comment '支付时间|本地时间',
    `channel`    char(1) comment '支付通道|枚举[OrderInfoChannelEnum]',
    `channel_at` datetime comment '通道时间|支付通道返回的时间',
    `status`     char(1) comment '交易状态|枚举[OrderInfoStatusEnum]',
    `desc`       varchar(200) comment '订单描述',
    `tenant_id`         VARCHAR(20)           DEFAULT '000000' COMMENT '租户编号',
    `del_flag`          CHAR(1)               DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
    `create_dept`       BIGINT(20) COMMENT '创建部门',
    `create_by`         BIGINT(20) COMMENT '创建者',
    `create_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         BIGINT(20) COMMENT '更新者',
    `update_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    primary key (`id`),
    unique key `order_no` (`order_no`)
) engine = innodb
  default charset = utf8 comment ='订单信息表';

package org.dromara.order.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;

import java.io.Serial;

/**
 * 订单信息对象 order_info
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_info")
public class OrderInfo extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单时间
     */
    private Date orderAt;

    /**
     * 订单类型|枚举[OrderInfoOrderTypeEnum]
     */
    private String orderType;

    /**
     * 订单信息|根据订单类型，存放不同的信息
     */
    private String info;

    /**
     * 会员|id
     */
    private Long memberId;

    /**
     * 订单金额(元)
     */
    private BigDecimal amount;

    /**
     * 支付时间|本地时间
     */
    private Date payAt;

    /**
     * 支付通道|枚举[OrderInfoChannelEnum]
     */
    private String channel;

    /**
     * 通道时间|支付通道返回的时间
     */
    private Date channelAt;

    /**
     * 交易状态|枚举[OrderInfoStatusEnum]
     */
    private String status;

    /**
     * 订单描述
     */
    private String desc;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;


}

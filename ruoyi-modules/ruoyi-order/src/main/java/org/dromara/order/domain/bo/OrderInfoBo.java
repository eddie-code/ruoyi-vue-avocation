package org.dromara.order.domain.bo;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.order.domain.OrderInfo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息业务对象 order_info
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = OrderInfo.class, reverseConvertGenerate = false)
public class OrderInfoBo extends BaseEntity {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderNo;

    /**
     * 下单时间
     */
    @NotNull(message = "下单时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date orderAt;

    /**
     * 订单类型|枚举[OrderInfoOrderTypeEnum]
     */
    @NotBlank(message = "订单类型|枚举[OrderInfoOrderTypeEnum]不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderType;

    /**
     * 订单信息|根据订单类型，存放不同的信息
     */
    @NotBlank(message = "订单信息|根据订单类型，存放不同的信息不能为空", groups = { AddGroup.class, EditGroup.class })
    private String info;

    /**
     * 会员|id
     */
    @NotNull(message = "会员|id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long memberId;

    /**
     * 订单金额(元)
     */
    @NotNull(message = "订单金额(元)不能为空", groups = { AddGroup.class, EditGroup.class })
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


}

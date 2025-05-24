package org.dromara.order.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.order.domain.OrderInfo;

import java.io.Serial;
import java.io.Serializable;



/**
 * 订单信息视图对象 order_info
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = OrderInfo.class)
public class OrderInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private String orderNo;

    /**
     * 下单时间
     */
    @ExcelProperty(value = "下单时间")
    private Date orderAt;

    /**
     * 订单类型|枚举[OrderInfoOrderTypeEnum]
     */
    @ExcelProperty(value = "订单类型|枚举[OrderInfoOrderTypeEnum]")
    private String orderType;

    /**
     * 订单信息|根据订单类型，存放不同的信息
     */
    @ExcelProperty(value = "订单信息|根据订单类型，存放不同的信息")
    private String info;

    /**
     * 会员|id
     */
    @ExcelProperty(value = "会员|id")
    private Long memberId;

    /**
     * 订单金额(元)
     */
    @ExcelProperty(value = "订单金额(元)")
    private BigDecimal amount;

    /**
     * 支付时间|本地时间
     */
    @ExcelProperty(value = "支付时间|本地时间")
    private Date payAt;

    /**
     * 支付通道|枚举[OrderInfoChannelEnum]
     */
    @ExcelProperty(value = "支付通道|枚举[OrderInfoChannelEnum]")
    private String channel;

    /**
     * 通道时间|支付通道返回的时间
     */
    @ExcelProperty(value = "通道时间|支付通道返回的时间")
    private Date channelAt;

    /**
     * 交易状态|枚举[OrderInfoStatusEnum]
     */
    @ExcelProperty(value = "交易状态|枚举[OrderInfoStatusEnum]")
    private String status;

    /**
     * 订单描述
     */
    @ExcelProperty(value = "订单描述")
    private String desc;


}

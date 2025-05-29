package org.dromara.order.alipay;

import java.util.Date;

/**
 * @author lee
 * @description
 */
public interface IAfterPayService {

    /**
     * 在支付成功后调用的方法
     * 该方法主要用于处理支付成功后的后续逻辑，如更新订单状态、发送通知等
     *
     * @param orderNo 订单号，用于标识特定的订单
     * @param channelTime 渠道返回的支付成功时间，可能用于时间验证或日志记录
     */
    void afterPaySuccess(String orderNo, Date channelTime);


}

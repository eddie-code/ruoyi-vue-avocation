package org.dromara.order.alipay;

import com.alipay.easysdk.payment.common.models.AlipayTradeCloseResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;

/**
 * @author lee
 * @description
 */
public interface IAliPayService {

    /**
     * 执行支付宝页面支付
     *
     * @param subject      订单标题
     * @param outTradeNo   商户订单号，确保在商户系统中唯一
     * @param totalAmount  订单总金额
     * @return AlipayTradePagePayResponse 支付响应结果，包含支付信息和状态
     */
    AlipayTradePagePayResponse pay(String subject, String outTradeNo, String totalAmount);

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，用于查询对应的订单信息
     * @return AlipayTradeQueryResponse 查询响应结果，包含订单状态和详细信息
     */
    AlipayTradeQueryResponse query(String outTradeNo);

    /**
     * 关闭进行中的订单
     *
     * @param outTradeNo 商户订单号，用于标识需要关闭的订单
     * @return AlipayTradeCloseResponse 关闭响应结果，指示订单关闭是否成功
     */
    AlipayTradeCloseResponse close(String outTradeNo);

}

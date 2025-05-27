package org.dromara.order.service;

import org.dromara.order.domain.bo.OrderInfoBo;
import org.dromara.order.domain.vo.OrderInfoPayVo;

/**
 * 订单信息服务接口
 * 提供与订单相关的信息和服务，包括订单支付信息和订单状态查询
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
public interface IOrderInfoService {

    /**
     * 根据订单信息请求获取订单支付信息
     *
     * @param req 订单信息请求对象，包含订单的相关信息
     * @return 返回订单支付信息对象，包括支付详情和支付渠道等信息
     */
    OrderInfoPayVo pay(OrderInfoBo req);

    /**
     * 查询本地订单的状态并返回
     *
     * @param orderNo 订单编号，用于标识特定的订单
     * @return 返回订单的当前状态，例如待支付、已支付、取消等
     */
    String queryOrderStatus(String orderNo);

}

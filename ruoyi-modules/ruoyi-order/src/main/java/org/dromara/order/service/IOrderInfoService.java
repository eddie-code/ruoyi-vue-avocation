package org.dromara.order.service;

import org.dromara.order.domain.bo.OrderInfoBo;

/**
 * 订单信息Service接口
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
public interface IOrderInfoService {

    String pay(OrderInfoBo req);

}

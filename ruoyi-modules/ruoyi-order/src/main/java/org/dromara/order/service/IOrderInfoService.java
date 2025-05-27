package org.dromara.order.service;

import org.dromara.order.domain.bo.OrderInfoBo;
import org.dromara.order.domain.vo.OrderInfoPayVo;

/**
 * 订单信息Service接口
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
public interface IOrderInfoService {

    OrderInfoPayVo pay(OrderInfoBo req);

}

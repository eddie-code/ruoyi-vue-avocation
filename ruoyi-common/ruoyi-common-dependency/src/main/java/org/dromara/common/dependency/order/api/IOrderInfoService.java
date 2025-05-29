package org.dromara.common.dependency.order.api;

import org.dromara.common.dependency.order.domain.OrderInfo;
import org.dromara.common.dependency.order.domain.bo.OrderInfoBo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;

import java.util.Date;

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
     * 查询本地订单的状态并返回, 并且提供前端两秒钟查询一次
     *
     * @param orderNo 订单编号，用于标识特定的订单
     * @return 返回订单的当前状态，例如待支付、已支付、取消等
     */
    String queryOrderStatus(String orderNo);

    /**
     * 根据订单号查询订单信息
     *
     * @param orderNo 订单号，用于唯一标识一个订单
     * @return 返回OrderInfo对象，包含订单的详细信息；如果未找到对应订单，则返回null
     */
    OrderInfo selectByOrderNo(String orderNo);

    /**
     * 在支付成功后更新订单状态，将订单更新成S，按状态更新，从I改成S
     * 此方法主要用于在支付系统通知支付成功后，更新订单信息，包括订单状态、渠道时间等
     *
     * @param orderNo 订单号，用于识别特定的订单
     * @param channelTime 渠道时间，表示支付成功的时间
     * @return 返回更新操作影响的行数，通常用于调试和日志记录
     */
    int afterPaySuccess(String orderNo, Date channelTime);
}

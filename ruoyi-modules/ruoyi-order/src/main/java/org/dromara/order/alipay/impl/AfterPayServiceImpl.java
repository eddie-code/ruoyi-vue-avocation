package org.dromara.order.alipay.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.order.alipay.IAfterPayService;
import org.dromara.common.dependency.order.domain.OrderInfo;
import org.dromara.common.dependency.order.enums.OrderInfoOrderTypeEnum;
import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 实现IAfterPayService接口的类，提供支付后处理服务的具体实现
 * 该类负责处理支付完成后的一系列操作，如订单状态更新、库存减少等
 *
 * @author lee
 * @description
 */
@Slf4j
@Service
public class AfterPayServiceImpl implements IAfterPayService {

    @Resource
    private IOrderInfoService orderInfoService;

    @Resource
    private IBizFiletransService filetransService;

    /**
     * 支付成功后的处理方法
     * 在订单支付成功后，更新订单状态和相关记录
     *
     * @param orderNo 订单号
     * @param channelTime 渠道时间
     */
    @Transactional
    @Override
    public void afterPaySuccess(String orderNo, Date channelTime) {
        // 记录支付成功处理开始日志
        log.info("执行支付成功动作开始");

        // 校验订单是否存在
        OrderInfo orderInfo = orderInfoService.selectByOrderNo(orderNo);
        if (orderInfo.equals(new OrderInfo())) {
            // 如果订单不存在，记录错误日志并返回
            log.error("订单不存在，{}", orderNo);
            return;
        }

        // 将订单更新成S
        log.info("更新订单信息开始");
        int i = orderInfoService.afterPaySuccess(orderNo, channelTime);
        if (i == 0) {
            // 如果订单状态不是初始状态，记录错误日志并返回
            log.error("订单状态异常，订单状态非初始，{}，结束", orderNo);
            return;
        }

        // 根据订单类型进行后续处理
        if (orderInfo.getOrderType().equals(OrderInfoOrderTypeEnum.FILETRANS_PAY.getCode())) {
            // 如果是语音识别单次付费订单，将语音识别记录更新成SI
            log.info("语音识别单次付费，更新语音识别表状态");

            String info = orderInfo.getInfo();
            Map<String, Object> infoMap = JsonUtils.parseObject(info, Map.class);
            String idStr = (String) infoMap.get("id"); // 注意类型是否为 String
            Long filetransId = Long.valueOf(idStr);

            filetransService.afterPaySuccess(filetransId);
        }

        // 记录支付成功处理结束日志
        log.info("执行支付成功动作结束");
    }


}

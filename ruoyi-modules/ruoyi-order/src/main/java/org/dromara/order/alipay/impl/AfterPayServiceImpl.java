package org.dromara.order.alipay.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.order.alipay.IAfterPayService;
import org.dromara.common.dependency.order.domain.OrderInfo;
import org.dromara.common.dependency.order.enums.OrderInfoOrderTypeEnum;
import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class AfterPayServiceImpl implements IAfterPayService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource
    private IOrderInfoService orderInfoService;

    // 移除外部的 @Resource，改为延迟获取
//    @Resource
//    private IBizFiletransService filetransService;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // 延迟获取 filetransService
    private IBizFiletransService getFiletransService() {
        return applicationContext.getBean(IBizFiletransService.class);
    }

    // 延迟获取 orderInfoService
    private IOrderInfoService getOrderInfoService() {
        if (this.orderInfoService == null) {
            this.orderInfoService = applicationContext.getBean(IOrderInfoService.class);
        }
        return this.orderInfoService;
    }

    /**
     * 支付成功后的处理方法
     * 在订单支付成功后，更新订单状态和相关记录
     *
     * @param orderNo     订单号
     * @param channelTime 渠道时间
     */
    @Transactional
    @Override
    public void afterPaySuccess(String orderNo, Date channelTime) {
        // 记录支付成功处理开始日志
        log.info("执行支付成功动作开始");

        // 校验订单是否存在
        OrderInfo orderInfo = getOrderInfoService().selectByOrderNo(orderNo);
        if (orderInfo.equals(new OrderInfo())) {
            // 如果订单不存在，记录错误日志并返回
            log.error("订单不存在，{}", orderNo);
            return;
        }

        // 将订单更新成S
        log.info("更新订单信息开始");
        int i = getOrderInfoService().afterPaySuccess(orderNo, channelTime);
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

            // 使用延迟获取的方式， 屏蔽 filetransService.afterPaySuccess(filetransId);
            getFiletransService().afterPaySuccess(filetransId);
        }

        // 记录支付成功处理结束日志
        log.info("执行支付成功动作结束");
    }
}

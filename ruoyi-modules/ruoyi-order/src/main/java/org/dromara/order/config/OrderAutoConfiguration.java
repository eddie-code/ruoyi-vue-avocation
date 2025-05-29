package org.dromara.order.config;

import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.dromara.order.alipay.IAfterPayService;
import org.dromara.order.alipay.IAliPayService;
import org.dromara.order.mapper.OrderInfoMapper;
import org.dromara.order.service.impl.OrderInfoServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(IOrderInfoService.class)
public class OrderAutoConfiguration {

    private final OrderInfoMapper orderInfoMapper;
    private final IAliPayService aliPayService;

    public OrderAutoConfiguration(OrderInfoMapper orderInfoMapper, IAliPayService aliPayService) {
        this.orderInfoMapper = orderInfoMapper;
        this.aliPayService = aliPayService;
    }

    @Bean
    @ConditionalOnMissingBean
    public IOrderInfoService orderInfoService() {
        return new OrderInfoServiceImpl(orderInfoMapper, aliPayService);
    }
}

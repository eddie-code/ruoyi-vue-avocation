package org.dromara.business.config;

import org.dromara.business.mapper.BizFiletransMapper;
import org.dromara.business.service.impl.BizFiletransServiceImpl;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(IBizFiletransService.class)
public class BusinessAutoConfiguration {

    private final BizFiletransMapper filetransMapper;

    private final IOrderInfoService orderInfoService;

    public BusinessAutoConfiguration(BizFiletransMapper filetransMapper, IOrderInfoService orderInfoService) {
        this.filetransMapper = filetransMapper;
        this.orderInfoService = orderInfoService;
    }

    @Bean
    @ConditionalOnMissingBean
    public IBizFiletransService filetransService() {
        return new BizFiletransServiceImpl(filetransMapper, orderInfoService);
    }
}

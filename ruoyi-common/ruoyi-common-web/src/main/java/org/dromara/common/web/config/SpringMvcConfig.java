package org.dromara.common.web.config;

import org.dromara.common.web.interceptor.TraceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lee
 * @description
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TraceInterceptor traceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 日志跟踪
        registry.addInterceptor(traceInterceptor);
    }
}

package org.dromara.snailjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * SnailJob Server 启动程序
 *
 * @author opensnail
 * @date 2024-05-17
 */
@SpringBootApplication
public class SnailJobServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.aizuda.snailjob.server.SnailJobServerApplication.class, args);
    }

}

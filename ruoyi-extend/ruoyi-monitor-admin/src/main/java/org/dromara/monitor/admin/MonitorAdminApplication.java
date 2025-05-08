package org.dromara.monitor.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Admin 监控启动程序
 *
 * @author Lion Li
 */
@Slf4j
@SpringBootApplication
public class MonitorAdminApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MonitorAdminApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(1024));
        ConfigurableApplicationContext applicationContext = application.run(args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        log.info("(♥◠‿◠)ﾉﾞ  Admin 监控启动成功   ლ(´ڡ`ლ)ﾞ");
        log.info("测试地址：http://localhost:{}{}admin"
            , environment.getProperty("server.port")
            , environment.getProperty("server.servlet.context-path"));
    }

}

package org.dromara;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 启动程序
 *
 * @author Lion Li
 */

@Slf4j
@SpringBootApplication
public class DromaraApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DromaraApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        ConfigurableApplicationContext applicationContext = application.run(args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        log.info("(♥◠‿◠)ﾉﾞ  RuoYi-Vue-Plus启动成功   ლ(´ڡ`ლ)ﾞ");
        log.info("测试地址：http://localhost:{}{}hello"
            , environment.getProperty("server.port")
            , environment.getProperty("server.servlet.context-path"));
    }

}

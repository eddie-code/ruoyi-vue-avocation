package org.dromara.business;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lee
 * @description
 */
@SaIgnore // 跳过鉴权
@RestController
public class TestController {

    @GetMapping("/hello")
    private String hello() {
        return "Hello World!";
    }

}

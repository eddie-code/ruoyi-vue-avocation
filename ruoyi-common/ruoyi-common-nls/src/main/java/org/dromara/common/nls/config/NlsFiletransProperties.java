package org.dromara.common.nls.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云智能语音服务文件传输属性配置类
 * 该类使用@Data注解自动生成getter和setter方法，减少boilerplate代码
 * 使用@Component注解将该类标记为Spring组件，使其能够被自动扫描和注册到Spring容器中
 * 使用@ConfigurationProperties注解指定配置属性的前缀，使得该类的字段能够自动绑定到应用配置中对应的属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "nls.filetrans")
public class NlsFiletransProperties {

    /**
     * 阿里云访问密钥ID，用于身份验证
     */
    private String accessKeyId;

    /**
     * 阿里云访问密钥秘密，用于身份验证
     */
    private String accessKeySecret;

    /**
     * 阿里云服务所在的区域ID
     */
    private String regionId;

    /**
     * 阿里云服务接入点名称
     */
    private String endpointName;

    /**
     * 阿里云产品标识
     */
    private String product;

    /**
     * 阿里云服务的域名
     */
    private String domain;

    /**
     * API版本号
     */
    private String version;

    /**
     * 任务版本号
     */
    private String taskVersion;

    /**
     * 回调URL，用于处理任务完成后的通知
     */
    private String callback;

}

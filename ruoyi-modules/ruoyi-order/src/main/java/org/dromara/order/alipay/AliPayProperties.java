package org.dromara.order.alipay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliPayProperties {

    /**
     * HTTPS协议的网关主机地址
     * 用于建立安全的网络通信
     */
    private String gatewayHttpsHost;

    /**
     * 普通协议的网关主机地址
     * 用于网络通信，非加密场景
     */
    private String gatewayHost;

    /**
     * 应用程序ID
     * 唯一标识应用程序，在调用API时使用
     */
    private String appId;

    /**
     * 商户私钥
     * 用于加密签名，确保请求的完整性和安全性
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     * 用于验签，确保支付宝响应的合法性和完整性
     */
    private String alipayPublicKey;

    /**
     * 通知URL
     * 支付宝服务器主动通知商户服务器支付结果的地址
     */
    private String notifyUrl;

    /**
     * 加密密钥
     * 用于加密数据，保证传输过程中的数据安全
     */
    private String encryptKey;

    /**
     * 返回URL
     * 用户支付成功后，页面跳转的地址
     */
    private String returnUrl;

}

package org.dromara.order.controller;

import cn.hutool.core.date.DateUtil;
import com.alipay.api.internal.util.AlipaySignature;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.order.alipay.AliPayProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@RestController
public class AliPayController {

    @Resource
    private AliPayProperties aliPayProperties;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    /**
     * 异步通知
     *
     * https://opendocs.alipay.com/open/0c2c19?pathHash=df12a335
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/alipay/callback")
    public String aliPayCallback(HttpServletRequest request) throws Exception {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        log.info("验签参数：{}", params);

        //调用SDK验证签名

        //公钥验签示
        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayProperties.getAlipayPublicKey(), "UTF-8", "RSA2");

        if (!activeProfile.contains("pro")) {
            log.info("不是生产环境，不参与验签，当前环境：{}", activeProfile);
            signVerified = true;
        }

        if (signVerified) {//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝回调，本地订单号：{}", outTradeNo);

            //支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //交易状态
            String tradeStatus = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

            String gmtPayment = new String(request.getParameter("gmt_payment").getBytes("ISO-8859-1"), "UTF-8");
            Date channelTime = DateUtil.parse(gmtPayment, "yyyy-MM-dd HH:mm:ss");
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。

                log.info("支付成功后的处理");
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            return "success";

            //////////////////////////////////////////////////////////////////////////////////////////
        } else {//验证失败
            return "failure";
        }
    }
}

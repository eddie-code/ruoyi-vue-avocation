package org.dromara.business.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.bo.BizFiletransBo;
import org.dromara.business.service.IBizFiletransService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 语音识别
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
//@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/filetrans")
public class WebBizFiletransController extends BaseController {

    private final IBizFiletransService bizFiletransService;

    @PostMapping("/pay")
    public R<Object> pay(@Valid @RequestBody BizFiletransBo req) throws Exception {
        log.info("语音识别支付开始");
        bizFiletransService.pay(req);
        log.info("语音识别支付结束");
        return R.ok();
    }

}

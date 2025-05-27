package org.dromara.order.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.order.service.IOrderInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.web.core.BaseController;

/**
 * 订单信息
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/web/order-info")
public class OrderInfoController extends BaseController {

    private final IOrderInfoService orderInfoService;

    /**
     * 根据订单号查询订单状态
     *
     * @param orderNo 订单号，用于查询订单状态
     * @return 返回一个封装了订单状态的R对象
     */
    @GetMapping("/query-order-status/{orderNo}")
    public R<Object> sendForRegister(@PathVariable String orderNo) {
        // 调用服务层方法查询订单状态
        String status = orderInfoService.queryOrderStatus(orderNo);
        // 将查询到的状态封装到响应对象R中并返回
        // I("I", "未支付"),
        // P("P", "处理中"),
        // S("S", "支付成功"),
        // F("F", "支付失败");
        return R.ok(status);
    }


}

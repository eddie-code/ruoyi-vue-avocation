package org.dromara.order.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/audio/order")
public class OrderInfoController extends BaseController {

    private final IOrderInfoService orderInfoService;

}

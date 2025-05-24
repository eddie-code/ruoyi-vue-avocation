package org.dromara.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.order.mapper.OrderInfoMapper;
import org.dromara.order.service.IOrderInfoService;
import org.springframework.stereotype.Service;


/**
 * 订单信息Service业务层处理
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@RequiredArgsConstructor
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    private final OrderInfoMapper baseMapper;

}

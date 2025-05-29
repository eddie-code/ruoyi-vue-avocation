package org.dromara.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.common.dependency.order.domain.OrderInfo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 订单信息Mapper接口
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@Mapper
public interface OrderInfoMapper extends BaseMapperPlus<OrderInfo, OrderInfoVo> {

}

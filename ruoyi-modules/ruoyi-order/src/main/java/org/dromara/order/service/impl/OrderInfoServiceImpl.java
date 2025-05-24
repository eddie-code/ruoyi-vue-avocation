package org.dromara.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.enums.BusinessExceptionEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.order.alipay.impl.AliPayServiceImpl;
import org.dromara.order.domain.OrderInfo;
import org.dromara.order.domain.bo.OrderInfoBo;
import org.dromara.order.enums.OrderInfoChannelEnum;
import org.dromara.order.enums.OrderInfoStatusEnum;
import org.dromara.order.mapper.OrderInfoMapper;
import org.dromara.order.service.IOrderInfoService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 订单信息Service业务层处理
 *
 * @author Eddie Lee
 * @date 2025-05-22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    private final OrderInfoMapper baseMapper;

    private final AliPayServiceImpl aliPayServiceImpl;

    @Override
    public String pay(OrderInfoBo req) {
        Date now = new Date();

        OrderInfo orderInfo = new OrderInfo();
        long id = IdUtil.getSnowflakeNextId();
        orderInfo.setId(id);
        String orderNo = genOrderNo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setOrderAt(now);
        orderInfo.setOrderType(req.getOrderType());
        orderInfo.setInfo(req.getInfo());
        orderInfo.setMemberId(LoginHelper.getUserId());
        orderInfo.setAmount(req.getAmount());
        orderInfo.setPayAt(now);
        orderInfo.setChannel(req.getChannel());
        orderInfo.setChannelAt(null);
        orderInfo.setStatus(OrderInfoStatusEnum.I.getCode());
        orderInfo.setDesc(req.getDesc());
//        orderInfo.setDelFlag();
        orderInfo.setTenantId(LoginHelper.getTenantId());
//        orderInfo.setSearchValue();
//        orderInfo.setCreateDept();
//        orderInfo.setCreateBy();
//        orderInfo.setCreateTime();
//        orderInfo.setUpdateBy();
//        orderInfo.setUpdateTime();
//        orderInfo.setParams();

        validEntityBeforeSave(orderInfo);
        boolean flag = baseMapper.insert(orderInfo) > 0;
        if (flag) {
            // 请求支付宝接口
            if (OrderInfoChannelEnum.ALIPAY.getCode().equals(req.getChannel())) {
                // 调用支付宝下单接口
                AlipayTradePagePayResponse response = aliPayServiceImpl.pay(req.getDesc(), orderNo, req.getAmount().toPlainString());
                return response.getBody();
            } else {
                log.warn("支付渠道【{}】不存在", req.getChannel());
                throw new ServiceException(BusinessExceptionEnum.PAY_ERROR.getDesc());
            }
        }
        return null;
    }

    private String genOrderNo() {
        String no = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        int random = (int) (Math.random() * 900 + 100);
        no = no + random;
        return no;
    }


    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OrderInfo entity) {
        //TODO 做一些数据校验,如唯一约束
    }
}

package org.dromara.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.enums.BusinessExceptionEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.dependency.order.domain.OrderInfo;
import org.dromara.common.dependency.order.domain.bo.OrderInfoBo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.order.alipay.IAfterPayService;
import org.dromara.order.alipay.IAliPayService;
import org.dromara.common.dependency.order.enums.OrderInfoChannelEnum;
import org.dromara.common.dependency.order.enums.OrderInfoStatusEnum;
import org.dromara.order.mapper.OrderInfoMapper;
import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private final IAliPayService aliPayService;

    // 移除外部的 @Autowired，改为 setter 注入
    private IAfterPayService afterPayService;

    @Autowired
    public void setAfterPayService(IAfterPayService afterPayService) {
        this.afterPayService = afterPayService;
    }

    @Override
    public OrderInfoPayVo pay(OrderInfoBo req) {
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
        orderInfo.setTenantId(LoginHelper.getTenantId());

        validEntityBeforeSave(orderInfo);
        boolean flag = baseMapper.insert(orderInfo) > 0;
        OrderInfoPayVo orderInfoPayVo = new OrderInfoPayVo();
        // 如果之后需要扩展, 比如商品描述就在orderInfoPayVo增加字段，在这里set..

        if (flag) {
            orderInfoPayVo.setOrderNo(orderNo);
            // 请求支付宝接口
            if (OrderInfoChannelEnum.ALIPAY.getCode().equals(req.getChannel())) {
                // 调用支付宝下单接口
                AlipayTradePagePayResponse response = aliPayService.pay(req.getDesc(), orderNo, req.getAmount().toPlainString());
                orderInfoPayVo.setChannelResult(response.getBody());
                return orderInfoPayVo;
            } else {
                log.warn("支付渠道【{}】不存在", req.getChannel());
                throw new ServiceException(BusinessExceptionEnum.PAY_ERROR.getDesc());
            }
        }
        return orderInfoPayVo;
    }

    @Override
    public String queryOrderStatus(String orderNo) {
        OrderInfo orderInfo = this.selectByOrderNo(orderNo);
        // 全链路查询
        // 检查订单信息状态是否为初始化状态
        if (OrderInfoStatusEnum.I.getCode().equals(orderInfo.getStatus())) {
            // 进一步检查订单信息渠道是否为支付宝
            if (OrderInfoChannelEnum.ALIPAY.getCode().equals(orderInfo.getChannel())) {
                // 调用支付宝服务查询订单状态
                AlipayTradeQueryResponse response = aliPayService.query(orderNo);
                String tradeStatus = response.getTradeStatus();
                // 检查交易状态是否为成功或完成
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    String sendPayDate = response.getSendPayDate();
                    Date date = DateUtil.parse(sendPayDate, "yyyy-MM-dd HH:mm:ss");
                    // 在支付成功后进行后续处理
                    afterPayService.afterPaySuccess(orderNo, date);
                    // 返回订单成功状态码
                    return OrderInfoStatusEnum.S.getCode();
                }
            }
        }
        return orderInfo.getStatus();
    }

    @Override
    public OrderInfo selectByOrderNo(String orderNo) {
        OrderInfoBo orderInfoBo = new OrderInfoBo();
        orderInfoBo.setOrderNo(orderNo);
        LambdaQueryWrapper<OrderInfo> lqw = buildQueryWrapper(orderInfoBo);
        List<OrderInfo> list = baseMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(list)) {
            // 避免传入不存在的订单号或者恶心破坏，导致大量的空指针异常日志，所以返回空对象而不是null
            return new OrderInfo();
        }
        return list.get(0);
    }
    @Override
    public int afterPaySuccess(String orderNo, Date channelTime) {
        // 只更新以下实体字段
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setStatus(OrderInfoStatusEnum.S.getCode());
        // 设置渠道时间，即支付成功的时间
        orderInfo.setChannelAt(channelTime);
        //orderInfo.setUpdateTime(new Date());

        // 符合更新条件
        OrderInfoBo bo = new OrderInfoBo();
        bo.setOrderNo(orderNo);
        bo.setStatus(OrderInfoStatusEnum.I.getCode()); // 在并发场景下，这个状态起到乐观锁作用，因为订单号+I条件才能更新

        // 构建查询包装器，用于查询待更新的订单信息
        LambdaQueryWrapper<OrderInfo> lqw = buildQueryWrapper(bo);
        // 执行更新操作，返回影响的行数
        return baseMapper.update(orderInfo, lqw);
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

    private LambdaQueryWrapper<OrderInfo> buildQueryWrapper(OrderInfoBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<OrderInfo> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(OrderInfo::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getOrderNo()), OrderInfo::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getOrderAt() != null, OrderInfo::getOrderAt, bo.getOrderAt());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderType()), OrderInfo::getOrderType, bo.getOrderType());
        lqw.eq(StringUtils.isNotBlank(bo.getInfo()), OrderInfo::getInfo, bo.getInfo());
        lqw.eq(bo.getMemberId() != null, OrderInfo::getMemberId, bo.getMemberId());
        lqw.eq(bo.getAmount() != null, OrderInfo::getAmount, bo.getAmount());
        lqw.eq(bo.getPayAt() != null, OrderInfo::getPayAt, bo.getPayAt());
        lqw.eq(StringUtils.isNotBlank(bo.getChannel()), OrderInfo::getChannel, bo.getChannel());
        lqw.eq(bo.getChannelAt() != null, OrderInfo::getChannelAt, bo.getChannelAt());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), OrderInfo::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getDesc()), OrderInfo::getDesc, bo.getDesc());
        return lqw;
    }

}

package org.dromara.business.service.impl;

import cn.hutool.core.util.IdUtil;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.mapper.BizFiletransMapper;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.dromara.common.dependency.business.domain.BizFiletrans;
import org.dromara.common.dependency.business.domain.bo.BizFiletransBo;
import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.dromara.common.dependency.order.domain.bo.OrderInfoBo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;
import org.dromara.common.dependency.order.enums.OrderInfoOrderTypeEnum;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.vod.enums.FiletransPayStatusEnum;
import org.dromara.common.vod.enums.FiletransStatusEnum;
import org.dromara.common.vod.util.VodUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 语音识别Service业务层处理
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BizFiletransServiceImpl implements IBizFiletransService {

    private final BizFiletransMapper baseMapper;

    private final IOrderInfoService orderInfoService;

    @Override
    public OrderInfoPayVo pay(BizFiletransBo req) throws Exception {
        // 获取视频信息
        GetVideoInfoResponse videoInfo = VodUtil.getVideoInfo(req.getVod());
        Float duration = videoInfo.getVideo().getDuration();
        log.info("视频：{}，时长：{}", req.getVod(), duration);
        int second = Math.round(duration);

        BizFiletrans bizFiletrans = new BizFiletrans();
        validEntityBeforeSave(bizFiletrans);
        Long userId = LoginHelper.getUserId();
        long id = IdUtil.getSnowflakeNextId();
        bizFiletrans.setId(id);
        bizFiletrans.setMemberId(userId);
        bizFiletrans.setName(req.getName());
        bizFiletrans.setSecond(second);
        bizFiletrans.setAmount(req.getAmount());
        bizFiletrans.setAudio(req.getAudio());
        bizFiletrans.setFileSign(req.getFileSign());
        bizFiletrans.setPayStatus(FiletransPayStatusEnum.I.getCode());
        bizFiletrans.setStatus(FiletransStatusEnum.INIT.getCode());
        bizFiletrans.setLang(req.getLang());
        bizFiletrans.setVod(req.getVod());
        bizFiletrans.setTaskId(req.getTaskId());

        boolean flag = baseMapper.insert(bizFiletrans) > 0;
        if (flag) {
            req.setId(bizFiletrans.getId());
        }

        // 保存订单信息
        OrderInfoBo orderInfoPayReq = new OrderInfoBo();
        orderInfoPayReq.setOrderType(OrderInfoOrderTypeEnum.FILETRANS_PAY.getCode());
        // 订单表的info保存语音识别表的id
//        orderInfoPayReq.setInfo("biz_filetrans:"+ id);
        Map<String, Object> map = new HashMap<>();
        map.put("type", "biz_filetrans"); // 业务标识
        map.put("id", id); // 对应某种业务表的主键id
        String infos = JsonUtils.toJsonString(map);
        orderInfoPayReq.setInfo(infos);
        orderInfoPayReq.setAmount(req.getAmount());
        orderInfoPayReq.setChannel(req.getChannel());
        orderInfoPayReq.setDesc("语音识别付费");

        return orderInfoService.pay(orderInfoPayReq);
    }

    /**
     * 支付成功后处理
     */
    @Override
    public void afterPaySuccess(Long id) {
//        Date now = new Date();
        BizFiletrans filetrans = new BizFiletrans();
        filetrans.setId(id);
        filetrans.setPayStatus(FiletransPayStatusEnum.S.getCode()); // 支付成功
        filetrans.setStatus(FiletransStatusEnum.SUBTITLE_INIT.getCode());
//        filetrans.setUpdatedAt(now);
        baseMapper.updateById(filetrans);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizFiletrans entity) {
        //TODO 做一些数据校验,如唯一约束
    }

}

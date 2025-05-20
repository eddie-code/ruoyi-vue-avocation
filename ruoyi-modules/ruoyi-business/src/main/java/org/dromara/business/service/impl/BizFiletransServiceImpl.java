package org.dromara.business.service.impl;

import cn.hutool.core.util.IdUtil;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.domain.BizFiletrans;
import org.dromara.business.domain.bo.BizFiletransBo;
import org.dromara.business.mapper.BizFiletransMapper;
import org.dromara.business.service.IBizFiletransService;
import org.dromara.common.vod.enums.FiletransPayStatusEnum;
import org.dromara.common.vod.enums.FiletransStatusEnum;
import org.dromara.common.vod.util.VodUtil;
import org.springframework.stereotype.Service;

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

    @Override
    public Boolean pay(BizFiletransBo req) throws Exception {
        // 获取视频信息
        GetVideoInfoResponse videoInfo = VodUtil.getVideoInfo(req.getVod());
        Float duration = videoInfo.getVideo().getDuration();
        log.info("视频：{}，时长：{}", req.getVod(), duration);
        int second = Math.round(duration);

        BizFiletrans BizFiletrans = new BizFiletrans();
        validEntityBeforeSave(BizFiletrans);
        long id = IdUtil.getSnowflakeNextId();
        BizFiletrans.setId(id);
        BizFiletrans.setMemberId(req.getMemberId());
        BizFiletrans.setName(req.getName());
        BizFiletrans.setSecond(second);
        BizFiletrans.setAmount(req.getAmount());
        BizFiletrans.setAudio(req.getAudio());
        BizFiletrans.setFileSign(req.getFileSign());
        BizFiletrans.setPayStatus(FiletransPayStatusEnum.I.getCode());
        BizFiletrans.setStatus(FiletransStatusEnum.INIT.getCode());
        BizFiletrans.setLang(req.getLang());
        BizFiletrans.setVod(req.getVod());
        BizFiletrans.setTaskId(req.getTaskId());
//        BizFiletrans.setTransStatusCode();
//        BizFiletrans.setTransStatusText();
//        BizFiletrans.setTransTime();
//        BizFiletrans.setSolveTime();

//        BizFiletrans.setDelFlag();
//        BizFiletrans.setTenantId();
//        BizFiletrans.setSearchValue();
//        BizFiletrans.setCreateDept();
//        BizFiletrans.setCreateBy();
//        BizFiletrans.setCreateTime();
//        BizFiletrans.setUpdateBy();
//        BizFiletrans.setUpdateTime();
//        BizFiletrans.setParams();

        boolean flag = baseMapper.insert(BizFiletrans) > 0;
        if (flag) {
            req.setId(BizFiletrans.getId());
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizFiletrans entity) {
        //TODO 做一些数据校验,如唯一约束
    }

}

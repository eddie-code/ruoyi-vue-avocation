package org.dromara.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.mapper.BizFiletransMapper;
import org.dromara.common.core.enums.BusinessExceptionEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.dromara.common.dependency.business.api.IBizFiletransSubtitleService;
import org.dromara.common.dependency.business.domain.BizFiletrans;
import org.dromara.common.dependency.business.domain.bo.BizFiletransBo;
import org.dromara.common.dependency.business.domain.bo.BizFiletransQueryBo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransVo;
import org.dromara.common.dependency.order.api.IOrderInfoService;
import org.dromara.common.dependency.order.domain.bo.OrderInfoBo;
import org.dromara.common.dependency.order.domain.vo.OrderInfoPayVo;
import org.dromara.common.dependency.order.enums.OrderInfoOrderTypeEnum;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.nls.util.NlsUtil;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.vod.enums.FiletransPayStatusEnum;
import org.dromara.common.vod.enums.FiletransStatusEnum;
import org.dromara.common.vod.util.VodUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    private final IBizFiletransSubtitleService bizFiletransSubtitleService;

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
        Date now = new Date();
        BizFiletrans filetrans = new BizFiletrans();
        filetrans.setId(id);
        filetrans.setPayStatus(FiletransPayStatusEnum.S.getCode()); // 支付成功
        filetrans.setStatus(FiletransStatusEnum.SUBTITLE_INIT.getCode());
//        filetrans.setUpdatedAt(now);
        baseMapper.updateById(filetrans);

        log.info("发起语音识别任务");
        BizFiletrans filetransDB = baseMapper.selectById(id);
        CommonResponse commonResponse = NlsUtil.trans(filetransDB.getAudio(), filetransDB.getLang());
        if (commonResponse.getHttpStatus() == 200) {
            JSONObject result = JSONObject.parseObject(commonResponse.getData());
            Integer statusCode = result.getInteger("StatusCode");
            String statusText = result.getString("StatusText");
            String taskId = result.getString("TaskId");
            if ("SUCCESS".equals(statusText)) {
                log.info("录音文件识别请求成功响应： " + result.toJSONString());
            } else {
                log.error("录音文件识别请求失败： " + result.toJSONString());
                throw new ServiceException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR.getDesc());
            }
            log.info("更新语音识别状态为：生成字幕中");
            BizFiletrans filetransAfterNls = new BizFiletrans();
            filetransAfterNls.setId(id);
            filetransAfterNls.setStatus(FiletransStatusEnum.SUBTITLE_PENDING.getCode());
            filetransAfterNls.setTaskId(taskId);
            filetransAfterNls.setTransTime(now);
            filetransAfterNls.setTransStatusCode(statusCode);
            filetransAfterNls.setTransStatusText(statusText);
            baseMapper.updateById(filetransAfterNls);
        }
    }

    @Override
    public void afterTrans(JSONObject jsonResult) {
        Date now = new Date();
        String taskId = jsonResult.getString("TaskId");
        Integer statusCode = jsonResult.getInteger("StatusCode");
        String statusText = jsonResult.getString("StatusText");

        BizFiletrans filetrans = new BizFiletrans();
        filetrans.setUpdateTime(now);
        filetrans.setTransStatusCode(statusCode);
        filetrans.setTransStatusText(statusText);

        BizFiletransBo bo = new BizFiletransBo();
        bo.setTaskId(taskId);
        bo.setStatus(FiletransStatusEnum.SUBTITLE_PENDING.getCode());

        // 以2开头状态码为正常状态码，回调方式正常状态只返回“21050000”。
        if ("21050000".equals(statusCode.toString())) {
            // 完成时间|录音文件识别完成的时间
            filetrans.setSolveTime(new Date(jsonResult.getLong("SolveTime")));
            filetrans.setStatus(FiletransStatusEnum.SUBTITLE_SUCCESS.getCode());
        } else {
            filetrans.setStatus(FiletransStatusEnum.SUBTITLE_FAILURE.getCode());
        }

        LambdaQueryWrapper<BizFiletrans> lqw = buildQueryWrapper(bo);
        // 更新内容， 更新条件
        int i = baseMapper.update(filetrans, lqw);

        // 保存字幕结果
        // 判断是否更新成功, 成功才进入保存子表
        if (i == 0) {
            log.info("未更新到taskId={}，状态={}/{}，不保存字幕表",
                taskId,
                FiletransStatusEnum.SUBTITLE_PENDING.getCode(),
                FiletransStatusEnum.SUBTITLE_PENDING.getDesc());
            return;
        }

        // 返回成功才保存到子表
        if ("21050000".equals(statusCode.toString())) {
            BizFiletransBo bo1 = new BizFiletransBo();
            bo1.setTaskId(taskId);
            List<BizFiletrans> bizFiletransList = baseMapper.selectList(buildQueryWrapper(bo1));
            // TaskId 是支付宝返回的唯一的, 所以只取第0条就可以
            BizFiletrans bizFiletransDB = bizFiletransList.get(0);
            JSONObject result = jsonResult.getJSONObject("Result");
            bizFiletransSubtitleService.saveSubtitle(bizFiletransDB.getId(), result);
        }
    }

    @Override
    public TableDataInfo<BizFiletransVo> queryPageList(BizFiletransQueryBo queryBo, PageQuery pageQuery) {
        BizFiletransBo bo = BeanUtil.copyProperties(queryBo, BizFiletransBo.class);
        LambdaQueryWrapper<BizFiletrans> lqw = buildQueryWrapper(bo);
        Page<BizFiletransVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 删除过期的视频点播(VOD)文件。
     * 该方法会删除创建时间在30天前到15天前范围内的媒体文件。
     * 执行流程：
     * 1. 计算时间范围：当前时间前30天作为起始时间，前15天作为结束时间
     * 2. 调用VOD服务查询该时间范围内创建的媒体文件列表
     * 3. 遍历查询结果并逐个删除媒体文件
     * 4. 记录操作日志和异常信息
     */
    public void deleteVodJob() {
        try {
            // 计算查询时间范围：30天前至15天前
            Date date = new Date();
            Date start = DateUtil.offsetDay(date, -30);
            String startStr = DateUtil.format(start, "yyyy-MM-dd") + "T00:00:00Z";
            Date end = DateUtil.offsetDay(date, -15);
            String endStr = DateUtil.format(end, "yyyy-MM-dd") + "T00:00:00Z";
            log.info("删除过期VOD，查询列表日期范围：" + startStr + ", " + endStr);

            // 查询指定时间范围内的媒体文件
            SearchMediaResponse searchMediaResponse = VodUtil.searchByCreationTime(startStr, endStr);
            List<SearchMediaResponse.Media> mediaList = searchMediaResponse.getMediaList();

            // 批量删除媒体文件
            if (!CollectionUtils.isEmpty(mediaList)) {
                for (SearchMediaResponse.Media media : mediaList) {
                    try {
                        // 删除单个媒体文件
                        VodUtil.deleteVideo(media.getMediaId());
                    } catch (Exception e) {
                        log.error("删除过期VOD异常：" + media.getMediaId(), e);
                    }
                }
            }
        } catch (Exception e) {
            // 捕获并记录整个任务执行过程中的异常
            log.error("删除过期VOD异常", e);
        }
    }


    private LambdaQueryWrapper<BizFiletrans> buildQueryWrapper(BizFiletransBo bo) {
        LambdaQueryWrapper<BizFiletrans> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(BizFiletrans::getId); // 最新的排序
        lqw.eq(bo.getMemberId() != null, BizFiletrans::getMemberId, bo.getMemberId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), BizFiletrans::getName, bo.getName()); // 模糊查询
        lqw.eq(bo.getSecond() != null, BizFiletrans::getSecond, bo.getSecond());
        lqw.eq(bo.getAmount() != null, BizFiletrans::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getAudio()), BizFiletrans::getAudio, bo.getAudio());
        lqw.eq(StringUtils.isNotBlank(bo.getFileSign()), BizFiletrans::getFileSign, bo.getFileSign());
        lqw.eq(StringUtils.isNotBlank(bo.getPayStatus()), BizFiletrans::getPayStatus, bo.getPayStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizFiletrans::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getLang()), BizFiletrans::getLang, bo.getLang());
        lqw.eq(StringUtils.isNotBlank(bo.getVod()), BizFiletrans::getVod, bo.getVod());
        lqw.eq(StringUtils.isNotBlank(bo.getTaskId()), BizFiletrans::getTaskId, bo.getTaskId());
        lqw.eq(bo.getTransStatusCode() != null, BizFiletrans::getTransStatusCode, bo.getTransStatusCode());
        lqw.eq(StringUtils.isNotBlank(bo.getTransStatusText()), BizFiletrans::getTransStatusText, bo.getTransStatusText());
        lqw.eq(bo.getTransTime() != null, BizFiletrans::getTransTime, bo.getTransTime());
        lqw.eq(bo.getSolveTime() != null, BizFiletrans::getSolveTime, bo.getSolveTime());
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizFiletrans entity) {
        //TODO 做一些数据校验,如唯一约束
    }

}

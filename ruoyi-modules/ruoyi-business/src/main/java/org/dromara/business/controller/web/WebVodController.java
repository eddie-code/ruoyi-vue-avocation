package org.dromara.business.controller.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetMezzanineInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import jakarta.validation.Valid;
import org.dromara.common.core.domain.R;
import org.dromara.common.dependency.business.domain.bo.GetUploadAuthBo;
import org.dromara.common.dependency.business.domain.vo.GetUploadAuthVo;
import org.dromara.common.vod.util.VodUtil;
import org.dromara.common.web.core.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author lee
 * @description
 */
//@SaIgnore // 跳过鉴
@RestController
@RequestMapping("/web/vod")
public class WebVodController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebVodController.class);

    /**
     * 获取视频上传凭证
     * @param req 获取上传凭证的请求参数，包含文件名和key等信息
     * @return 包含上传凭证或已存在视频信息的响应对象
     * @throws Exception 获取凭证过程中可能抛出的异常
     */
    @PostMapping("/get-upload-auth")
    public R<Object> getUploadAuth(@Valid @RequestBody GetUploadAuthBo req) throws Exception {
        LOGGER.info("获取上传凭证开始");
        DefaultAcsClient client = VodUtil.initVodClient();
        String title = req.getKey() + "-" + req.getName();
        // 按标题搜索视频，检查是否已存在相同文件
        SearchMediaResponse searchMediaResponse = VodUtil.searchByTitle(title);
        Object obj = handleUploadAuth(searchMediaResponse, title, client);
        LOGGER.info("获取上传凭证结束");
        return R.ok(obj);
    }

    /**
     * 计算视频费用
     * @param videoId 视频ID
     * @return 包含视频费用的响应对象
     */
    @GetMapping("/cal-amount/{videoId}")
    public R<BigDecimal> calAmount(@PathVariable String videoId) {
        BigDecimal amount = VodUtil.calAmount(videoId);
        return R.ok(amount);
    }

    /**
     * 处理上传凭证逻辑
     * @param searchMediaResponse 视频搜索响应结果
     * @param title 视频标题
     * @param client 视频点播客户端
     * @return 已存在视频的URL信息或新视频的上传凭证
     * @throws Exception 处理过程中可能抛出的异常
     */
    private Object handleUploadAuth(SearchMediaResponse searchMediaResponse, String title, DefaultAcsClient client) throws Exception {
        // 如果视频已存在，返回现有视频信息
        if (searchMediaResponse.getTotal() > 0 && !searchMediaResponse.getMediaList().isEmpty()) {
            LOGGER.info("该文件已上传过 = {}", title);
            SearchMediaResponse.Media media = searchMediaResponse.getMediaList().get(0);
            String vid = media.getMediaId();
            GetMezzanineInfoResponse getMezzanineInfoResponse = VodUtil.getMezzanineInfo(vid);
            String fileUrl = getMezzanineInfoResponse.getMezzanine().getFileURL();
            // 直接返回原始地址，不带过期时间等参数
            if (fileUrl != null) {
                fileUrl = fileUrl.split("\\?")[0];
            }
            return createJsonResponse(fileUrl, vid);
        } else {
            // 视频不存在，创建新的上传凭证
            try {
                CreateUploadVideoResponse videoResponse = VodUtil.createUploadVideo(client, title);
                GetUploadAuthVo authResp = new GetUploadAuthVo();
                authResp.setUploadAuth(videoResponse.getUploadAuth());
                authResp.setUploadAddress(videoResponse.getUploadAddress());
                authResp.setVideoId(videoResponse.getVideoId());
                LOGGER.debug("授权码 = {}", videoResponse.getUploadAuth());
                LOGGER.debug("地址 = {}", videoResponse.getUploadAddress());
                LOGGER.debug("videoId = {}", videoResponse.getVideoId());
                return authResp;
            } catch (Exception e) {
                LOGGER.error("获取上传凭证错误", e);
                throw new RuntimeException("获取上传凭证失败", e);
            }
        }
    }

    /**
     * 创建包含视频URL和ID的JSON响应
     * @param fileUrl 视频文件URL
     * @param videoId 视频ID
     * @return 包含视频信息的JSON对象
     */
    private JSONObject createJsonResponse(String fileUrl, String videoId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileUrl", fileUrl);
        jsonObject.put("videoId", videoId);
        return jsonObject;
    }

}

package org.dromara.business.controller.web;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetMezzanineInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import jakarta.validation.Valid;
import org.dromara.business.domain.bo.GetUploadAuthBo;
import org.dromara.business.domain.vo.GetUploadAuthVo;
import org.dromara.business.util.VodUtil;
import org.dromara.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lee
 * @description
 */
@SaIgnore // 跳过鉴
@RestController
@RequestMapping("/web/vod")
public class WebVodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebVodController.class);

    @PostMapping("/get-upload-auth")
    public R<Object> getUploadAuth(@Valid @RequestBody GetUploadAuthBo req) throws Exception {
        LOGGER.info("获取上传凭证开始");
        DefaultAcsClient client = VodUtil.initVodClient();
        String title = req.getKey() + "-" + req.getName();
        // 按标题搜索 (查询同一个文件是否存在, 如果存在并且状态是正常的, 就不会上传, 如果状态非正常, 就会继续上传, 导致重覆)
        SearchMediaResponse searchMediaResponse = VodUtil.searchByTitle(title);
        Object obj  = null;
        if (searchMediaResponse.getTotal() > 0) {
            LOGGER.info("该文件已上传过 = {}", title);
            SearchMediaResponse.Media media = searchMediaResponse.getMediaList().get(0);
            String vid = media.getMediaId();
            GetMezzanineInfoResponse getMezzanineInfoResponse = VodUtil.getMezzanineInfo(vid);
            String fileUrl = getMezzanineInfoResponse.getMezzanine().getFileURL();
            // 直接返回原始地址，不带过期时间等参数
            fileUrl = fileUrl.split("\\?")[0];
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fileUrl", fileUrl);
            jsonObject.put("videoId", vid);
            obj = jsonObject;
        } else {
            try {
                CreateUploadVideoResponse videoResponse = VodUtil.createUploadVideo(client, title);
                GetUploadAuthVo authResp = new GetUploadAuthVo();
                authResp.setUploadAuth(videoResponse.getUploadAuth());
                authResp.setUploadAddress(videoResponse.getUploadAddress());
                authResp.setVideoId(videoResponse.getVideoId());
                LOGGER.info("授权码 = {}", videoResponse.getUploadAuth());
                LOGGER.info("地址 = {}", videoResponse.getUploadAddress());
                LOGGER.info("videoId = {}", videoResponse.getVideoId());
                obj = authResp;
            } catch (Exception e) {
                LOGGER.error("获取上传凭证错误", e);
            }
        }
        LOGGER.info("获取上传凭证结束");
        return R.ok(obj);
    }
}

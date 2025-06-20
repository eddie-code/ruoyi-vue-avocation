package org.dromara.business.controller.web;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetMezzanineInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.dependency.business.domain.bo.GetUploadAuthBo;
import org.dromara.common.dependency.business.domain.vo.GetUploadAuthVo;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.vod.properties.DemoProperties;
import org.dromara.common.vod.util.VodUtil;
import org.dromara.common.web.core.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author lee
 * @description
 */
//@SaIgnore // 跳过鉴
@Slf4j
@RestController
@RequestMapping("/web/vod")
public class WebVodController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebVodController.class);

    @Resource
    private DemoProperties demoProperties;

    @Autowired
    private Environment environment;

    /**
     * 获取视频上传凭证
     *
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
     *
     * @param videoId 视频ID
     * @return 包含视频费用的响应对象
     */
    @GetMapping("/cal-amount/{videoId}")
    public R<BigDecimal> calAmount(@PathVariable String videoId) {
        BigDecimal amount = VodUtil.calAmount(videoId);
        return R.ok(amount);
    }

    /**
     * 上传示例音频，如果上传过，直接取上传过的示例音频，并计算出收费金额、时长等
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/upload-demo")
    public R<DemoProperties> uploadDemo() throws Exception {
        String title = demoProperties.getKey() + "-" + demoProperties.getName();
        SearchMediaResponse searchMediaResponse = VodUtil.searchByTitle(title);
        String vid = "";
        // 有就直接从VOD拿，没有就先上传VOD，得到vid
        if (searchMediaResponse.getTotal() > 0) {
            log.info("该文件已上传过 = {}", title);
            SearchMediaResponse.Media media = searchMediaResponse.getMediaList().get(0);
            vid = media.getMediaId();
        } else {
            UploadVideoResponse videoResponse = null;
            if (!isProd()) {
                // 开发环境使用 classpath 资源
                File file = ResourceUtils.getFile("classpath:" + demoProperties.getName());
                videoResponse = VodUtil.uploadLocalFile(title, file.getAbsolutePath());
            } else {
                // 上面两行只在本地起作用，生产打包后，demo.wav会被打进jar包里，导致file.getAbsolutePath()报错
                // 所以修改demo.name配置为全路径，如：demo.name=/Users/temp/nls/demo.wav，并手动放入demo.wav文件，生产也需要手动放入demo.wav文件

                // 生产环境使用全路径
                String path = "/Users/temp/" + demoProperties.getName();
                videoResponse = VodUtil.uploadLocalFile(title, path);
            }
            vid = videoResponse.getVideoId();

            // 需要延迟2秒，才能拿到刚上传的音频的时长，否则金额计算出来是0
            Thread.sleep(2000);
        }
        demoProperties.setVid(vid);

        // 获取音频地址
        GetMezzanineInfoResponse getMezzanineInfoResponse = VodUtil.getMezzanineInfo(vid);
        String fileUrl = getMezzanineInfoResponse.getMezzanine().getFileURL();
        // 直接返回原始地址，不带过期时间等参数
        fileUrl = fileUrl.split("\\?")[0];
        demoProperties.setAudio(fileUrl);

        // 获取音频时长（从阿里云返回的响应中提取）
        String duration = getMezzanineInfoResponse.getMezzanine().getDuration();
        demoProperties.setDuration(duration);

        // 计算收费金额
        BigDecimal amount = VodUtil.calAmount(vid);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            amount = new BigDecimal("0.01"); // 强制最低金额
        }
        demoProperties.setAmount(amount);
        System.out.println("demoProperties ========: " + JsonUtils.toJsonString(demoProperties));
        return R.ok(demoProperties);
    }

    /**
     * 处理上传凭证逻辑
     *
     * @param searchMediaResponse 视频搜索响应结果
     * @param title               视频标题
     * @param client              视频点播客户端
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
     *
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

    /**
     * 检查当前环境是否为生产环境(prod)
     *
     * @return boolean - 返回true表示当前是生产环境，false表示非生产环境
     */
    private boolean isProd() {
        // 获取当前激活的Spring profiles并检查是否包含"prod"
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

}

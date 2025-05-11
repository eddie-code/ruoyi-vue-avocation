package org.dromara.business.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/**
 * 阿里云Vod工具类
 */
@Slf4j
@Component
public class VodUtil {

    private static String accessKeyId;

    private static String accessKeySecret;

    @Value("${vod.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        VodUtil.accessKeyId = accessKeyId;
    }

    @Value("${vod.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        VodUtil.accessKeySecret = accessKeySecret;
    }

    /**
     * 使用AK初始化VOD客户端
     *
     * @return
     * @throws ClientException
     */
    public static DefaultAcsClient initVodClient() {
        // 点播服务接入区域，国内请填cn-shanghai，其他区域请参考文档[点播中心](~~98194~~)
        String regionId = "cn-shanghai";
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    /**
     * 获取视频上传地址和凭证
     *
     * @param vodClient
     * @return
     * @throws ClientException
     */
    public static CreateUploadVideoResponse createUploadVideo(DefaultAcsClient vodClient, String fileName) throws ClientException {
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setFileName(fileName);
        request.setTitle(fileName);
        // request.setDescription("this is desc");
        // request.setTags("tag1,tag2");
        // request.setCoverURL("http://vod.aliyun.com/test_cover_url.jpg");
        // request.setCateId(1000115308L);

        // 这段逻辑放到付费成功后处理
        // // 如果是视频，则增加转码任务：提取音频
        // String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // if ("mp4".equals(suffix) || "mov".equals(suffix)) {
        //     log.info("上传的是视频，需要添加提取音频和标清视频转码模板组ID：{}", audioTemplateGroupId);
        //     request.setTemplateGroupId(audioTemplateGroupId);
        // }

        //request.setWorkflowId("");
        //request.setStorageLocation("");
        //request.setAppId("app-1000000");
        //设置请求超时时间
        request.setSysReadTimeout(1000);
        request.setSysConnectTimeout(1000);
        return vodClient.getAcsResponse(request);
    }

    /**
     * 使用上传凭证和地址初始化OSS客户端（注意需要先Base64解码并Json Decode再传入）
     *
     * @param uploadAuth
     * @param uploadAddress
     * @return
     */
    public static OSSClient initOssClient(JSONObject uploadAuth, JSONObject uploadAddress) {
        String endpoint = uploadAddress.getString("Endpoint");
        String accessKeyId = uploadAuth.getString("AccessKeyId");
        String accessKeySecret = uploadAuth.getString("AccessKeySecret");
        String securityToken = uploadAuth.getString("SecurityToken");
        return new OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken);
    }

    /**
     * 简单上传
     *
     * @param ossClient
     * @param uploadAddress
     * @param inputStream
     */
    public static void uploadLocalFile(OSSClient ossClient, JSONObject uploadAddress, InputStream inputStream) {
        String bucketName = uploadAddress.getString("Bucket");
        String objectName = uploadAddress.getString("FileName");
        // 单文件上传
        ossClient.putObject(bucketName, objectName, inputStream);

        /* 视频点播不支持追加上传
        // 追加上传
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/plain");
        AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName, file, meta);
        request.setPosition(0L);
        ossClient.appendObject(request);*/
    }

    /**
     * 上传本地文件
     *
     * @param ossClient
     * @param uploadAddress
     * @param localFile
     */
    public static void uploadLocalFile(OSSClient ossClient, JSONObject uploadAddress, String localFile) {
        String bucketName = uploadAddress.getString("Bucket");
        String objectName = uploadAddress.getString("FileName");
        log.info("bucketName: {}", bucketName);
        ;
        log.info("objectName: {}", objectName);
        ;
        File file = new File(localFile);
        // 单文件上传
        ossClient.putObject(bucketName, objectName, file);

        /* 视频点播不支持追加上传
        // 追加上传
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/plain");
        AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName, file, meta);
        request.setPosition(0L);
        ossClient.appendObject(request);*/
    }

    /**
     * 刷新上传凭证
     *
     * @param vodClient
     * @return
     * @throws ClientException
     */
    public static RefreshUploadVideoResponse refreshUploadVideo(DefaultAcsClient vodClient) throws ClientException {
        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setVideoId("VideoId");
        //设置请求超时时间
        request.setSysReadTimeout(1000);
        request.setSysConnectTimeout(1000);
        return vodClient.getAcsResponse(request);
    }

    /**
     * 获取源文件信息
     *
     * @return GetMezzanineInfoResponse 获取源文件信息响应数据
     * @throws Exception
     */
    public static GetMezzanineInfoResponse getMezzanineInfo(String videoId) throws Exception {
        GetMezzanineInfoRequest request = new GetMezzanineInfoRequest();
        request.setVideoId(videoId);
        //源片下载地址过期时间
        request.setAuthTimeout(10L);

        log.info("获取VOD源文件信息请求：{}", JSON.toJSONString(request));
        GetMezzanineInfoResponse acsResponse = initVodClient().getAcsResponse(request);
        log.info("获取VOD源文件信息返回：{}", JSON.toJSONString(acsResponse));
        return acsResponse;
    }

    /**
     * 获取播放凭证函数
     *
     * @param client
     * @return
     * @throws Exception
     */
    public static GetVideoPlayAuthResponse getVideoPlayAuth(DefaultAcsClient client, String videoId) throws Exception {
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoId);
        return client.getAcsResponse(request);
    }

    /**
     * 获取视频信息
     *
     * @return GetVideoInfoResponse 获取视频信息响应数据
     * @throws Exception
     */
    public static GetVideoInfoResponse getVideoInfo(String videoId) throws Exception {
        DefaultAcsClient client = VodUtil.initVodClient();
        GetVideoInfoRequest request = new GetVideoInfoRequest();
        request.setVideoId(videoId);
        GetVideoInfoResponse getVideoInfoResponse = client.getAcsResponse(request);
        log.info("获取视频信息: {}", JSON.toJSONString(getVideoInfoResponse));
        return getVideoInfoResponse;
    }

    /**
     * 查询视频转码摘要信息
     */
    public static GetTranscodeSummaryResponse getTranscodeSummary(String videoId) throws Exception {
        DefaultAcsClient client = VodUtil.initVodClient();
        GetTranscodeSummaryRequest request = new GetTranscodeSummaryRequest();
        request.setVideoIds(videoId);
        return client.getAcsResponse(request);
    }

    /**
     * 获取辅助媒资上传地址和凭证
     */
    private static CreateUploadAttachedMediaResponse getUploadAttachedMediaAuthSrt(String fullPath) throws ClientException {
        CreateUploadAttachedMediaRequest request = new CreateUploadAttachedMediaRequest();
        File file = new File(fullPath);
        request.setTitle(file.getName());
        request.setFileName(fullPath);
        request.setMediaExt("srt");
        request.setBusinessType("subtitle");
        return initVodClient().getAcsResponse(request);
    }

    /**
     * 搜索媒资信息
     * <p>
     * https://help.aliyun.com/document_detail/86044.htm?spm=a2c4g.11186623.0.0.7893715cVg2YeH#doc-api-vod-SearchMedia
     *
     * @return SearchMediaResponse 搜索媒资信息响应数据
     * @throws Exception
     */
    public static SearchMediaResponse searchByTitle(String title) throws Exception {
        SearchMediaRequest request = new SearchMediaRequest();
        request.setFields("Title,CoverURL,Status");
        // request.setMatch("Status in ('Normal') and Title = '" + title + "'");
        request.setMatch("Status in ('Normal', 'Transcoding') and Title = '" + title + "'");
        // request.setMatch("Status = ('Normal')");
        request.setPageNo(1);
        request.setPageSize(1);
        // request.setSearchType("video");
        request.setSortBy("CreationTime:Desc");
        return initVodClient().getAcsResponse(request);
    }

    /**
     * 按时间范围搜索
     *
     * @param start '2021-11-01T08:00:00Z'
     * @param end   '2021-11-10T08:00:00Z'
     * @return
     * @throws Exception
     */
    public static SearchMediaResponse searchByCreationTime(String start, String end) throws Exception {
        SearchMediaRequest request = new SearchMediaRequest();
        request.setFields("Title,CoverURL,Status");
        // request.setMatch("Status in ('Normal') and Title = '" + title + "'");
        // request.setMatch("Status = ('Normal')");
        request.setMatch("CreationTime = (" + start + "," + end + ")");
        request.setPageNo(1);
        request.setPageSize(10);
        // request.setSearchType("video");
        request.setSortBy("CreationTime:desc");
        return initVodClient().getAcsResponse(request);
    }

    /**
     * 删除视频
     *
     * @return DeleteVideoResponse 删除视频响应数据
     * @throws Exception
     */
    public static DeleteVideoResponse deleteVideo(String videoIds) throws Exception {
        DeleteVideoRequest request = new DeleteVideoRequest();
        //支持传入多个视频ID，多个用逗号分隔
        request.setVideoIds(videoIds);
        DeleteVideoResponse acsResponse = initVodClient().getAcsResponse(request);
        log.info("删除视频结果: {}", JSON.toJSONString(acsResponse));
        return acsResponse;
    }

    public static String uploadSubtitle(String subtitleFullPath) {
        try {
            CreateUploadAttachedMediaResponse response;

            response = getUploadAttachedMediaAuthSrt(subtitleFullPath);
            log.info("获取辅助媒资地址和凭证：{}", JSON.toJSONString(response));

            JSONObject uploadAuth = decodeBase64(response.getUploadAuth());
            JSONObject uploadAddress = decodeBase64(response.getUploadAddress());

            OSSClient ossClient = initOssClient(uploadAuth, uploadAddress);

            uploadLocalFile(ossClient, uploadAddress, subtitleFullPath);
            log.info("上传辅助媒资成功: {}", response.getFileURL());
            return response.getFileURL();
        } catch (Exception e) {
            throw new ServiceException("GEN 字幕错误");
        }
    }

    /**
     * base64解码
     *
     * @param uploadAuth
     * @return
     */
    private static JSONObject decodeBase64(String uploadAuth) {
        return JSONObject.parseObject(Base64.decodeBase64(uploadAuth), JSONObject.class);
    }

}

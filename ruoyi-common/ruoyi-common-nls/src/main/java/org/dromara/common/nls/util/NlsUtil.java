package org.dromara.common.nls.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.dromara.common.core.enums.BusinessExceptionEnum;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.vod.enums.FiletransLangEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.nls.config.NlsFiletransProperties;
import org.springframework.stereotype.Component;

/**
 * 智能语音交互（NLS） 工具类
 */
@Slf4j
@Component
public class NlsUtil {

    private static NlsFiletransProperties nlsFiletransProperties;

    @Resource
    public void setNlsFiletransProperties(NlsFiletransProperties nlsFiletransProperties) {
        NlsUtil.nlsFiletransProperties = nlsFiletransProperties;
    }

    /**
     * 发送识别请求结果：{"data":"{\"TaskId\":\"28d1d5569ab2418f8f5fb926e752f9b5\",\"RequestId\":\"60323EBA-A0A2-5CCA-912B-174F72BB5160\",\"StatusText\":\"SUCCESS\",\"StatusCode\":21050000}","httpResponse":{"encoding":"UTF-8","headers":{"Keep-Alive":"timeout=25","Access-Control-Expose-Headers":"*","Access-Control-Allow-Origin":"*","ETag":"1jbWNKy3EDS8OFVuTZHWgew1","x-acs-request-id":"60323EBA-A0A2-5CCA-912B-174F72BB5160","Connection":"keep-alive","Content-Length":"141","Date":"Tue, 05 Dec 2023 02:27:47 GMT","Content-Type":"application/json;charset=utf-8","x-acs-trace-id":"4cbf2b0a68c22588840ba72d2304073e"},"httpContent":"eyJUYXNrSWQiOiIyOGQxZDU1NjlhYjI0MThmOGY1ZmI5MjZlNzUyZjliNSIsIlJlcXVlc3RJZCI6IjYwMzIzRUJBLUEwQTItNUNDQS05MTJCLTE3NEY3MkJCNTE2MCIsIlN0YXR1c1RleHQiOiJTVUNDRVNTIiwiU3RhdHVzQ29kZSI6MjEwNTAwMDB9","httpContentString":"{\"TaskId\":\"28d1d5569ab2418f8f5fb926e752f9b5\",\"RequestId\":\"60323EBA-A0A2-5CCA-912B-174F72BB5160\",\"StatusText\":\"SUCCESS\",\"StatusCode\":21050000}","httpContentType":"JSON","ignoreSSLCerts":false,"reasonPhrase":"OK","status":200,"success":true,"sysEncoding":"UTF-8","sysHeaders":{"Keep-Alive":"timeout=25","Access-Control-Expose-Headers":"*","Access-Control-Allow-Origin":"*","ETag":"1jbWNKy3EDS8OFVuTZHWgew1","x-acs-request-id":"60323EBA-A0A2-5CCA-912B-174F72BB5160","Connection":"keep-alive","Content-Length":"141","Date":"Tue, 05 Dec 2023 02:27:47 GMT","Content-Type":"application/json;charset=utf-8","x-acs-trace-id":"4cbf2b0a68c22588840ba72d2304073e"}},"httpStatus":200}
     *
     * @param fileLink 音频地址 对应 biz_filetrans 里面的 audio
     * @param appKey 对应 biz_filetrans 里面的 lang 语言
     * @return
     */
    public static CommonResponse trans(String fileLink, String appKey) {
        try {
            IAcsClient client = getClient();

            /**
             * 创建CommonRequest 设置请求参数
             */
            CommonRequest postRequest = new CommonRequest();
            postRequest.setDomain(nlsFiletransProperties.getDomain()); // 设置域名，固定值。
            postRequest.setVersion(nlsFiletransProperties.getVersion());         // 设置API的版本号，固定值。
            postRequest.setAction("SubmitTask");          // 设置action，固定值。
            postRequest.setProduct(nlsFiletransProperties.getProduct());      // 设置产品名称，固定值。
            // 设置录音文件识别请求参数，以JSON字符串的格式设置到请求Body中。
            JSONObject taskObject = new JSONObject();
            // taskObject.put("appkey", "NPUHwGzguE1HZ463");    // 项目的Appkey
            // taskObject.put("file_link", "https://jiawa-test.oss-cn-shanghai.aliyuncs.com/nls-test/nls-sample-16k.wav");  // 设置录音文件的链接
            // taskObject.put("version", "4.0");  // 新接入请使用4.0版本，已接入（默认2.0）如需维持现状，请注释掉该参数设置。
            taskObject.put("enable_sample_rate_adaptive", true);  // 是否将大于16kHz采样率的音频进行自动降采样，默认为false，开启时需要设置version为“4.0”。
            taskObject.put("appkey", appKey);    // 项目的Appkey
            taskObject.put("file_link", fileLink);  // 设置录音文件的链接
            taskObject.put("version", nlsFiletransProperties.getTaskVersion());  // 新接入请使用4.0版本，已接入（默认2.0）如需维持现状，请注释掉该参数设置。

            // 允许单句话最大结束时间，秒 转 毫秒
            taskObject.put("max_single_segment_time", 15 * 1000);
            // 是否打开ITN，中文数字将转为阿拉伯数字输出
            // 只有中文时可打开，外语不支持，需关闭
            // 英语需要同时打开enable_inverse_text_normalization和enable_punctuation_prediction，才能有大小写
            if (appKey.equals(FiletransLangEnum.LANG0.getCode())
                || appKey.equals(FiletransLangEnum.LANG1.getCode())
                || appKey.equals(FiletransLangEnum.LANG2.getCode())) {
                taskObject.put("enable_inverse_text_normalization", true);
            } else {
                taskObject.put("enable_inverse_text_normalization", false);
            }

            // 是否开启智能分轨
            // taskObject.put("auto_split", true);
            // 暂不开启智能分轨，出现过SAMPLERATE_16K_SPKDIAR_NOT_SUPPORTED 16k音频不支持智能分轨
            taskObject.put("auto_split", "false");

            taskObject.put("enable_callback", true);
            taskObject.put("callback_url", nlsFiletransProperties.getCallback());
            String task = taskObject.toJSONString();
            postRequest.putBodyParameter("Task", task);  // 设置以上JSON字符串为Body参数。
            postRequest.setMethod(MethodType.POST);      // 设置为POST方式请求。
            /**
             * 提交录音文件识别请求
             */
            String taskId = "";   // 获取录音文件识别请求任务的ID，以供识别结果查询使用。
            log.info("发送识别请求参数：{}", JSONObject.toJSONString(postRequest));
            CommonResponse commonResponse = client.getCommonResponse(postRequest);
            log.info("发送识别请求结果：{}", JSONObject.toJSONString(commonResponse));
            return commonResponse;
        } catch (ClientException e) {
            log.error("发送识别请求异常！", e);
            throw new ServiceException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR.getDesc());
        }
    }

    public static CommonResponse query(String taskId) {
        try {
            IAcsClient client = getClient();

            /**
             * 创建CommonRequest，设置任务ID。
             */
            CommonRequest getRequest = new CommonRequest();
//        getRequest.setDomain("filetrans.cn-shanghai.aliyuncs.com");   // 设置域名，固定值。
//        getRequest.setVersion("2018-08-17");             // 设置API版本，固定值。
//        getRequest.setAction("GetTaskResult");           // 设置action，固定值。
//        getRequest.setProduct("nls-filetrans");          // 设置产品名称，固定值。
            getRequest.setDomain(nlsFiletransProperties.getDomain()); // 设置域名，固定值。
            getRequest.setVersion(nlsFiletransProperties.getVersion());         // 设置API的版本号，固定值。
            getRequest.setAction("GetTaskResult");           // 设置action，固定值。
            getRequest.setProduct(nlsFiletransProperties.getProduct());      // 设置产品名称，固定值。
            getRequest.putQueryParameter("TaskId", taskId);  // 设置任务ID为查询参数。
            getRequest.setMethod(MethodType.GET);            // 设置为GET方式的请求。

            /**
             * 提交录音文件识别结果查询请求
             * 以轮询的方式进行识别结果的查询，直到服务端返回的状态描述为“SUCCESS”、“SUCCESS_WITH_NO_VALID_FRAGMENT”，或者为错误描述，则结束轮询。
             */
            String statusText = "";
            log.info("查询识别结果请求参数：{}", JSONObject.toJSONString(getRequest));
            CommonResponse commonResponse = client.getCommonResponse(getRequest);
            log.info("查询识别结果返回结果：{}", JSONObject.toJSONString(commonResponse));
            return commonResponse;
        } catch (ClientException e) {
            log.error("查询识别结果异常！", e);
            throw new ServiceException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR.getDesc());
        }
    }

    private static IAcsClient getClient() throws ClientException {
        final String regionId = nlsFiletransProperties.getRegionId();
        final String endpointName = nlsFiletransProperties.getEndpointName();
        final String product = nlsFiletransProperties.getProduct();
        final String domain = nlsFiletransProperties.getDomain();

        IAcsClient client;
        // 设置endpoint
        DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(regionId, nlsFiletransProperties.getAccessKeyId(), nlsFiletransProperties.getAccessKeySecret());
//        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

        return client;
    }
}

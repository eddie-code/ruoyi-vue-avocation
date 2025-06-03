package org.dromara.business.controller.nls;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 智能语音交互（NLS）
 *
 * @author lee
 * @description
 */
@SaIgnore
@Slf4j
@RestController
@RequestMapping("/nls")
public class NlsFiletransCallBackController {

//    @Resource
//    private IBizFiletransService filetransService;

    // 以4开头的状态码是客户端错误。
    private static final Pattern PATTERN_CLIENT_ERR = Pattern.compile("4105[0-9]*");
    // 以5开头的状态码是服务端错误。
    private static final Pattern PATTERN_SERVER_ERR = Pattern.compile("5105[0-9]*");
    // 必须是post方式
    @PostMapping("/filetrans/callback")
    public void GetResult(HttpServletRequest request) {
        // 关键修复：设置请求字符编码为UTF-8
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            log.error("设置字符编码失败", e);
        }

        int contentLength = request.getContentLength();
        if (contentLength <= 0) {
            log.error("请求体为空或长度不合法，Content-Length: {}", contentLength);
            return;
        }

        byte[] buffer = new byte[contentLength];
        int totalRead = 0;

        try (InputStream in = request.getInputStream()) {
            // 循环读取确保读满整个缓冲区
            while (totalRead < contentLength) {
                int bytesRead = in.read(buffer, totalRead, contentLength - totalRead);
                if (bytesRead == -1) break;
                totalRead += bytesRead;
            }

            // 检查是否读取完整
            if (totalRead != contentLength) {
                log.error("数据读取不完整，期望长度: {}，实际读取: {}", contentLength, totalRead);
                return;
            }

            // 使用UTF-8解码字节数组
            String result = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonResult = JSONObject.parseObject(result);
            String taskId = jsonResult.getString("TaskId");
            String statusCode = jsonResult.getString("StatusCode");
            String statusText = jsonResult.getString("StatusText");
            // 解析并输出相关结果内容。
            log.info("录音转换回调结果2: TaskId：{}，StatusCode：{}，StatusText：{}", taskId, statusCode, statusText);
            Matcher matcherClient = PATTERN_CLIENT_ERR.matcher(jsonResult.getString("StatusCode"));
            Matcher matcherServer = PATTERN_SERVER_ERR.matcher(jsonResult.getString("StatusCode"));

            // 不管成功还是失败，都应该更新状态
//            filetransService.afterTrans(jsonResult);

            // 以2开头状态码为正常状态码，回调方式正常状态只返回“21050000”。
            if("21050000".equals(statusCode)) {

                log.info("录音文件识别成功！taskId：{}", taskId);

                // System.out.println("RequestTime: " + jsonResult.getString("RequestTime"));
                // System.out.println("SolveTime: " + jsonResult.getString("SolveTime"));
                // System.out.println("BizDuration: " + jsonResult.getString("BizDuration"));
                // System.out.println("Result.Sentences.size: " +
                //     jsonResult.getJSONObject("Result").getJSONArray("Sentences").size());
                // for (int i = 0; i < jsonResult.getJSONObject("Result").getJSONArray("Sentences").size(); i++) {
                //     System.out.println("Result.Sentences[" + i + "].BeginTime: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("BeginTime"));
                //     System.out.println("Result.Sentences[" + i + "].EndTime: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("EndTime"));
                //     System.out.println("Result.Sentences[" + i + "].SilenceDuration: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("SilenceDuration"));
                //     System.out.println("Result.Sentences[" + i + "].Text: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("Text"));
                //     System.out.println("Result.Sentences[" + i + "].ChannelId: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("ChannelId"));
                //     System.out.println("Result.Sentences[" + i + "].SpeechRate: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("SpeechRate"));
                //     System.out.println("Result.Sentences[" + i + "].EmotionValue: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("EmotionValue"));
                // }
            }
            else if(matcherClient.matches()) {
                log.error("录音文件识别失败！状态码以4开头表示客户端错误：{}, {}", statusCode, statusText);
            }
            else if(matcherServer.matches()) {
                log.error("录音文件识别失败！状态码以5开头表示服务端错误：{}, {}", statusCode, statusText);
            }
            else {
                log.error("录音文件识别失败！状态码：{}, {}", statusCode, statusText);
            }
        } catch (IOException e) {
            log.error("录音转换回调处理异常！！", e);
        }
    }
}

package org.dromara.job.snailjob;

import cn.hutool.core.util.IdUtil;
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.core.util.JsonUtil;
import com.aizuda.snailjob.common.log.SnailJobLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.dependency.business.api.IBizFiletransService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * @author lee
 * @description 定时删除删除早期视频
 */
@Slf4j
@Component
@JobExecutor(name = "deleteVodJobExecutor")
public class DeleteVodJobExecutor {

    @Resource
    private IBizFiletransService filetransService;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
//        SnailJobLog.LOCAL.info("deleteVodJobExecutor. jobArgs:{}", JsonUtil.toJsonString(jobArgs));
//        SnailJobLog.REMOTE.info("deleteVodJobExecutor. jobArgs:{}", JsonUtil.toJsonString(jobArgs));
        try {
            // 增加日志流水号
            MDC.put("LOG_ID", IdUtil.getSnowflakeNextIdStr());
            log.info("删除VOD跑批开始");
            long start = System.currentTimeMillis();
            // 删除早期视频
            filetransService.deleteVodJob();
            log.info("删除VOD跑批结束，耗时：{}毫秒", System.currentTimeMillis() - start);
            MDC.clear();
        } catch (Exception e) {
            log.error("删除VOD跑批异常", e);
        }
        return ExecuteResult.success("删除早期视频");
    }

}

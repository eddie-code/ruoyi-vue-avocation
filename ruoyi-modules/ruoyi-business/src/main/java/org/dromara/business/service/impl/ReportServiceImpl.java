package org.dromara.business.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.mapper.ReportMapper;
import org.dromara.business.service.IReportService;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.domain.dto.UserOnlineDTO;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.dependency.business.domain.vo.StatisticVo;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class ReportServiceImpl implements IReportService {

    @Resource
    private ReportMapper reportMapper;

    /**
     * 查询系统统计信息
     * <p>
     * 该方法用于获取系统各项关键指标的统计信息，包括在线用户数、注册用户数、
     * 文件传输相关统计以及订单相关统计。
     *
     * @return StatisticVo 统计信息值对象，包含以下字段：
     * - onlineCount: 当前在线用户数（通过Redis在线token数量计算） (会包含同一个账号不同端登录也会计算在内)
     * - registerCount: 系统注册用户总数
     * - filetransCount: 文件传输总次数
     * - filetransSecond: 文件传输总耗时（秒）
     * - orderCount: 订单总数
     * - orderAmount: 订单总金额
     */
    @Override
    public StatisticVo queryStatistic() {
        // 初始化统计值对象
        StatisticVo statisticVo = new StatisticVo();
        // 从Redis获取在线用户数（匹配所有在线token key）
        statisticVo.setOnlineCount(SysUserOnlineList());
        // 从数据库获取各项统计指标
        statisticVo.setRegisterCount(reportMapper.queryRegisterCount());
        statisticVo.setFiletransCount(reportMapper.queryFiletransCount());
        statisticVo.setFiletransSecond(reportMapper.queryFiletransSecond());
        statisticVo.setOrderCount(reportMapper.queryOrderCount());
        statisticVo.setOrderAmount(reportMapper.queryOrderAmount());
        return statisticVo;
    }

    /**
     * 获取系统在线用户数量
     * <p>
     * 本函数通过以下步骤实现：
     * 1. 从Redis中获取所有未过期的用户token
     * 2. 根据token获取对应的在线用户信息
     * 3. 处理结果集并返回在线用户数量
     *
     * @return Integer 当前系统在线用户数量
     */
    private Integer SysUserOnlineList() {
        // 从Redis获取所有未过期的token，键名格式为ONLINE_TOKEN_KEY前缀
        Collection<String> keys = RedisUtils.keys(CacheConstants.ONLINE_TOKEN_KEY + "*");
        List<UserOnlineDTO> userOnlineDTOList = new ArrayList<>();
        // 处理每个token：验证有效性并获取用户信息
        for (String key : keys) {
            String token = StringUtils.substringAfterLast(key, ":");
            // 跳过已过期的token（超时时间小于-1表示已过期）
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                continue;
            }
            userOnlineDTOList.add(RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token));
        }
        // 结果处理：倒序排列并过滤null值
        Collections.reverse(userOnlineDTOList);
        userOnlineDTOList.removeAll(Collections.singleton(null));
        return userOnlineDTOList.size();
    }


}

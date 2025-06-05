package org.dromara.business.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.mapper.BizFiletransSubtitleMapper;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.dependency.business.api.IBizFiletransSubtitleService;
import org.dromara.common.dependency.business.domain.BizFiletransSubtitle;
import org.dromara.common.dependency.business.domain.bo.BizFiletransSubtitleBo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class BizFiletransSubtitleServiceImpl implements IBizFiletransSubtitleService {

    @Resource
    private BizFiletransSubtitleMapper baseMapper;

    @Transactional
    @Override
    public void saveSubtitle(Long filetransId, JSONObject result) {
        // 先清空
        BizFiletransSubtitleBo bo = new BizFiletransSubtitleBo();
        bo.setFiletransId(filetransId);
        LambdaQueryWrapper<BizFiletransSubtitle> lqw = buildQueryWrapper(bo);
        baseMapper.delete(lqw);

        List<BizFiletransSubtitle> subtitleList = new ArrayList<>();
        JSONArray sentences = result.getJSONArray("Sentences");
        for (int i = 0; i < sentences.size(); i++) {
            JSONObject sentence = (JSONObject) sentences.get(i);
            String text = sentence.getString("Text");
            Integer beginTime = sentence.getInteger("BeginTime");
            Integer endTime = sentence.getInteger("EndTime");
            // 后保存
            BizFiletransSubtitle filetransSubtitle = new BizFiletransSubtitle();
            filetransSubtitle.setId(IdUtil.getSnowflakeNextId());
            filetransSubtitle.setFiletransId(filetransId);
            filetransSubtitle.setIndex(i + 1);
            filetransSubtitle.setBegin(beginTime);
            filetransSubtitle.setEnd(endTime);
            filetransSubtitle.setText(text);
            // 添加集合里面
            subtitleList.add(filetransSubtitle);
        }

        // 判断集合是否为空, 非空就把数据批量插入
        if (!subtitleList.isEmpty()) {
            baseMapper.insertBatch(subtitleList);
        }
    }

    private LambdaQueryWrapper<BizFiletransSubtitle> buildQueryWrapper(BizFiletransSubtitleBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizFiletransSubtitle> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizFiletransSubtitle::getId);
        lqw.eq(bo.getFiletransId() != null, BizFiletransSubtitle::getFiletransId, bo.getFiletransId());
        lqw.eq(bo.getIndex() != null, BizFiletransSubtitle::getIndex, bo.getIndex());
        lqw.eq(bo.getBegin() != null, BizFiletransSubtitle::getBegin, bo.getBegin());
        lqw.eq(bo.getEnd() != null, BizFiletransSubtitle::getEnd, bo.getEnd());
        lqw.eq(StringUtils.isNotBlank(bo.getText()), BizFiletransSubtitle::getText, bo.getText());
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizFiletransSubtitle entity) {
    }

}

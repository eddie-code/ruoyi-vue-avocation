package org.dromara.business.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.business.mapper.BizFiletransSubtitleMapper;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.dependency.business.api.IBizFiletransSubtitleService;
import org.dromara.common.dependency.business.domain.BizFiletransSubtitle;
import org.dromara.common.dependency.business.domain.bo.BizFiletransSubtitleBo;
import org.dromara.common.dependency.business.domain.bo.GenSubtitleBo;
import org.dromara.common.dependency.business.domain.bo.GenTextBo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransSubtitleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.vod.util.VodUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author lee
 * @description
 */
@Slf4j
@Service
public class BizFiletransSubtitleServiceImpl implements IBizFiletransSubtitleService {

    @Resource
    private BizFiletransSubtitleMapper baseMapper;

    @Value("${temp.dir}")
    private String tempDir;

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

    @Override
    public TableDataInfo<BizFiletransSubtitleVo> queryPageList(BizFiletransSubtitleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizFiletransSubtitle> lqw = buildQueryWrapper(bo);
        lqw.orderByAsc(BizFiletransSubtitle::getIndex); // 正序，看字幕通常都是 0s 看到最后
        Page<BizFiletransSubtitleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public String genSubtitle(GenSubtitleBo bo) {
        String suffix = ".srt";
        Long filetransId = bo.getFiletransId();
        log.info("获取字幕");
        LambdaQueryWrapper<BizFiletransSubtitle> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizFiletransSubtitle::getId);
        lqw.eq(bo.getFiletransId() != null, BizFiletransSubtitle::getFiletransId, filetransId);
        List<BizFiletransSubtitle> bizFiletransSubtitleList = baseMapper.selectList(lqw);

        log.info("格式化字幕");
        StringBuffer buffer = this.formatSubtitle(bizFiletransSubtitleList);

        return uploadFile(filetransId, buffer, suffix);
    }

    @Override
    public String genText(GenTextBo bo) {
        String suffix = ".txt";
        Long filetransId = bo.getFiletransId();
        log.info("获取文本");
        LambdaQueryWrapper<BizFiletransSubtitle> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizFiletransSubtitle::getId);
        lqw.eq(bo.getFiletransId() != null, BizFiletransSubtitle::getFiletransId, filetransId);
        List<BizFiletransSubtitle> bizFiletransSubtitleList = baseMapper.selectList(lqw);

        log.info("格式化文本");
        StringBuffer buffer = this.formatText(bizFiletransSubtitleList);

        return uploadFile(filetransId, buffer, suffix);
    }

    /**
     * 上传文件方法
     * 该方法负责将给定的字符串缓冲区内容保存到临时目录中，然后上传到视频点播服务，并在上传成功后删除临时文件
     *
     * @param filetransId 文件传输ID，用于生成临时文件名
     * @param buffer      包含文件内容的字符串缓冲区
     * @param suffix      文件后缀名，用于指定文件类型
     * @return 返回上传成功后的文件URL
     */
    private String uploadFile(Long filetransId, StringBuffer buffer, String suffix) {
        // 生成临时文件的全路径
        String fullPath = tempDir + filetransId + suffix;
        log.info("生成临时文件：{}", fullPath);

        // 确保临时目录存在
        FileUtil.mkdir(tempDir);
        // 将缓冲区的内容写入临时文件
        FileUtil.writeBytes(buffer.toString().getBytes(), fullPath);

        // 上传临时文件到视频点播服务
        String url = VodUtil.uploadSubtitle(fullPath);
        log.info("上传临时文件成功：{}", url);

        // 删除临时文件
        log.info("删除临时文件：{}", fullPath);
        FileUtil.del(fullPath);

        // 返回上传后的文件URL
        return url;
    }

    /**
     * 格式化纯文本
     * 该方法将一个包含字幕信息的列表格式化为一个大的字符串缓冲区，每个字幕文本占一行
     *
     * @param list 包含 {@link BizFiletransSubtitle} 对象的列表，这些对象包含要格式化的文本
     * @return 返回包含所有字幕文本的字符串缓冲区
     */
    private StringBuffer formatText(List<BizFiletransSubtitle> list) {
        log.info("拼接纯文本数据，总行数：{}", list.size());
        StringBuffer buffer = new StringBuffer();

        // 遍历字幕列表，将每个字幕文本添加到缓冲区，并在末尾添加换行符
        for (BizFiletransSubtitle item : list) {
            buffer.append(item.getText());
            buffer.append(System.getProperty("line.separator"));
        }
        log.info("拼接纯文本完成，字符数：{}", buffer.length());
        return buffer;
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
     * 格式化字幕内容
     * 此方法将给定的字幕对象列表格式化为一个字符串缓冲区
     * 主要用于将字幕数据拼接成特定格式的字符串，以便后续处理或显示
     *
     * @param list 字幕对象列表，包含每行字幕的开始时间、结束时间和文本内容
     * @return StringBuffer 包含格式化后字幕内容的字符串缓冲区
     */
    private StringBuffer formatSubtitle(List<BizFiletransSubtitle> list) {
        // 记录日志，拼接字幕数据开始，显示总行数
        log.info("拼接字幕数据，总行数：{}", list.size());
        StringBuffer buffer = new StringBuffer();

        // 遍历字幕列表，格式化每行字幕内容
        for (int i = 0, l = list.size(); i < l; i++) {
            BizFiletransSubtitle item = list.get(i);
            // 添加行号
            buffer.append(i);
            buffer.append(System.getProperty("line.separator"));
            // 添加时间戳，将毫秒转换为时分秒格式
            buffer.append(this.convertMs(Long.valueOf(item.getBegin())) + " --> " + this.convertMs(Long.valueOf(item.getEnd())));
            buffer.append(System.getProperty("line.separator"));
            // 添加字幕文本
            buffer.append(item.getText());
            buffer.append(System.getProperty("line.separator"));
            buffer.append(System.getProperty("line.separator"));
        }
        // 记录日志，拼接字幕完成，显示字符数
        log.info("拼接字幕完成，字符数：{}", buffer.length());
        return buffer;
    }

    /**
     * 将毫秒转换为字幕显示所需的时间格式
     * 此方法将毫秒值转换为 HH:mm:ss,SSS 格式的时间字符串，并考虑时区差异
     *
     * @param ms 时间戳，以毫秒为单位
     * @return String 转换后的时分秒格式字符串
     */
    public String convertMs(Long ms) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
        // 设置时区为GMT-8，以适应字幕显示的时区需求
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("ETC/GMT-8"));
        return simpleDateFormat.format(ms);
    }


    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizFiletransSubtitle entity) {
    }

}

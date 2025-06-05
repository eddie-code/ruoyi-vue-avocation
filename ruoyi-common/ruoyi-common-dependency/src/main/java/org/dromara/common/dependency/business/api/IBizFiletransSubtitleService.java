package org.dromara.common.dependency.business.api;

import com.alibaba.fastjson.JSONObject;
import org.dromara.common.dependency.business.domain.bo.BizFiletransSubtitleBo;
import org.dromara.common.dependency.business.domain.vo.BizFiletransSubtitleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 接口IBizFiletransSubtitleService用于定义文件传输字幕相关的业务操作
 * @author lee
 * @description
 */
public interface IBizFiletransSubtitleService {

    /**
     * 保存文件传输的字幕信息
     *
     * @param filetransId 文件传输的唯一标识符，用于关联字幕到特定的文件传输任务
     * @param result 包含字幕信息的JSON对象，用于存储和处理字幕数据
     */
    void saveSubtitle(Long filetransId, JSONObject result);

    /**
     * 分页查询语音识别字幕列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 语音识别字幕分页列表
     */
    TableDataInfo<BizFiletransSubtitleVo> queryPageList(BizFiletransSubtitleBo bo, PageQuery pageQuery);

}

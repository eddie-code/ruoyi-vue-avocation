package org.dromara.business.service;

import org.dromara.business.domain.vo.BizFiletransVo;
import org.dromara.business.domain.bo.BizFiletransBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 语音识别Service接口
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
public interface IBizFiletransService {

    /**
     * 查询语音识别
     *
     * @param id 主键
     * @return 语音识别
     */
    BizFiletransVo queryById(Long id);

    /**
     * 分页查询语音识别列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 语音识别分页列表
     */
    TableDataInfo<BizFiletransVo> queryPageList(BizFiletransBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的语音识别列表
     *
     * @param bo 查询条件
     * @return 语音识别列表
     */
    List<BizFiletransVo> queryList(BizFiletransBo bo);

    /**
     * 新增语音识别
     *
     * @param bo 语音识别
     * @return 是否新增成功
     */
    Boolean insertByBo(BizFiletransBo bo);

    /**
     * 修改语音识别
     *
     * @param bo 语音识别
     * @return 是否修改成功
     */
    Boolean updateByBo(BizFiletransBo bo);

    /**
     * 校验并批量删除语音识别信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}

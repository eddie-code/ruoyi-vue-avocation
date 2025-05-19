package org.dromara.business.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.business.domain.bo.BizFiletransBo;
import org.dromara.business.domain.vo.BizFiletransVo;
import org.dromara.business.domain.BizFiletrans;
import org.dromara.business.mapper.BizFiletransMapper;
import org.dromara.business.service.IBizFiletransService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 语音识别Service业务层处理
 *
 * @author Eddie Lee
 * @date 2025-05-19
 */
@RequiredArgsConstructor
@Service
public class BizFiletransServiceImpl implements IBizFiletransService {

    private final BizFiletransMapper baseMapper;

    /**
     * 查询语音识别
     *
     * @param id 主键
     * @return 语音识别
     */
    @Override
    public BizFiletransVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询语音识别列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 语音识别分页列表
     */
    @Override
    public TableDataInfo<BizFiletransVo> queryPageList(BizFiletransBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizFiletrans> lqw = buildQueryWrapper(bo);
        Page<BizFiletransVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的语音识别列表
     *
     * @param bo 查询条件
     * @return 语音识别列表
     */
    @Override
    public List<BizFiletransVo> queryList(BizFiletransBo bo) {
        LambdaQueryWrapper<BizFiletrans> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizFiletrans> buildQueryWrapper(BizFiletransBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizFiletrans> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizFiletrans::getId);
        lqw.eq(bo.getMemberId() != null, BizFiletrans::getMemberId, bo.getMemberId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), BizFiletrans::getName, bo.getName());
        lqw.eq(bo.getSecond() != null, BizFiletrans::getSecond, bo.getSecond());
        lqw.eq(bo.getAmount() != null, BizFiletrans::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getAudio()), BizFiletrans::getAudio, bo.getAudio());
        lqw.eq(StringUtils.isNotBlank(bo.getFileSign()), BizFiletrans::getFileSign, bo.getFileSign());
        lqw.eq(StringUtils.isNotBlank(bo.getPayStatus()), BizFiletrans::getPayStatus, bo.getPayStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizFiletrans::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getLang()), BizFiletrans::getLang, bo.getLang());
        lqw.eq(StringUtils.isNotBlank(bo.getVod()), BizFiletrans::getVod, bo.getVod());
        lqw.eq(StringUtils.isNotBlank(bo.getTaskId()), BizFiletrans::getTaskId, bo.getTaskId());
        lqw.eq(bo.getTransStatusCode() != null, BizFiletrans::getTransStatusCode, bo.getTransStatusCode());
        lqw.eq(StringUtils.isNotBlank(bo.getTransStatusText()), BizFiletrans::getTransStatusText, bo.getTransStatusText());
        lqw.eq(bo.getTransTime() != null, BizFiletrans::getTransTime, bo.getTransTime());
        lqw.eq(bo.getSolveTime() != null, BizFiletrans::getSolveTime, bo.getSolveTime());
        return lqw;
    }

    /**
     * 新增语音识别
     *
     * @param bo 语音识别
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizFiletransBo bo) {
        BizFiletrans add = MapstructUtils.convert(bo, BizFiletrans.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改语音识别
     *
     * @param bo 语音识别
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizFiletransBo bo) {
        BizFiletrans update = MapstructUtils.convert(bo, BizFiletrans.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizFiletrans entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除语音识别信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}

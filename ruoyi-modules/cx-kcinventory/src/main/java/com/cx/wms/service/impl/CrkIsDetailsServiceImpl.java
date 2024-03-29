package com.cx.wms.service.impl;

import com.cx.wms.domain.CrkIsDetails;
import com.cx.wms.mapper.CrkInventorySheetMapper;
import com.cx.wms.mapper.CrkIsDetailsMapper;
import com.cx.wms.service.ICrkIsDetailsService;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 盘点明细Service业务层处理
 *
 * @author xrc
 * @date 2023-12-14
 */
@Service
public class CrkIsDetailsServiceImpl implements ICrkIsDetailsService {
    @Autowired
    private CrkIsDetailsMapper crkIsDetailsMapper;

    @Autowired
    private CrkInventorySheetMapper crkInventorySheetMapper;

    /**
     * 查询盘点明细
     *
     * @param isdId 盘点明细主键
     * @return 盘点明细
     */
    @Override
    public CrkIsDetails selectCrkIsDetailsByIsdId(Long isdId) {
        return crkIsDetailsMapper.selectCrkIsDetailsByIsdId(isdId);
    }

    /**
     * 查询盘点明细列表
     *
     * @param crkIsDetails 盘点明细
     * @return 盘点明细
     */
    @Override
    public List<CrkIsDetails> selectCrkIsDetailsList(CrkIsDetails crkIsDetails) {
        return crkIsDetailsMapper.selectCrkIsDetailsList(crkIsDetails);
    }

    /**
     * 新增盘点明细
     *
     * @param crkIsDetails 盘点明细
     * @return 结果
     */
    @Override
    public int insertCrkIsDetails(CrkIsDetails crkIsDetails) {
        crkIsDetails.setCreateTime(DateUtils.getNowDate());
        crkIsDetails.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        int isId = 0;
        Integer isResult = 0;
        isId = Math.toIntExact(crkIsDetails.getIsId());
        isResult = crkIsDetails.getIsStatus();
        System.out.println("isId:" + isId + "isResult:" + isResult);
        crkInventorySheetMapper.updateIsResult(isId, isResult);
        return crkIsDetailsMapper.insertCrkIsDetails(crkIsDetails);
    }

    /**
     * 修改盘点明细
     *
     * @param crkIsDetails 盘点明细
     * @return 结果
     */
    @Override
    public int updateCrkIsDetails(CrkIsDetails crkIsDetails) {
        crkIsDetails.setUpdateTime(DateUtils.getNowDate());
        crkIsDetails.setUpdateBy(String.valueOf(SecurityUtils.getUserId()));
        return crkIsDetailsMapper.updateCrkIsDetails(crkIsDetails);
    }

    /**
     * 批量删除盘点明细
     *
     * @param isdIds 需要删除的盘点明细主键
     * @return 结果
     */
    @Override
    public int deleteCrkIsDetailsByIsdIds(Long[] isdIds) {
        return crkIsDetailsMapper.deleteCrkIsDetailsByIsdIds(isdIds);
    }

    /**
     * 删除盘点明细信息
     *
     * @param isdId 盘点明细主键
     * @return 结果
     */
    @Override
    public int deleteCrkIsDetailsByIsdId(Long isdId) {
        return crkIsDetailsMapper.deleteCrkIsDetailsByIsdId(isdId);
    }
}

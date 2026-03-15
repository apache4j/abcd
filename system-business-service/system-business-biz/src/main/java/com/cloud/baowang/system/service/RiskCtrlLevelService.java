package com.cloud.baowang.system.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelAddVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelEditVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.po.risk.RiskCtrlLevelPO;
import com.cloud.baowang.system.repositories.RiskCtrlLevelRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


/**
 * 风控层级service
 */
@Slf4j
@Service
@AllArgsConstructor
public class RiskCtrlLevelService extends ServiceImpl<RiskCtrlLevelRepository, RiskCtrlLevelPO> {

    private final RiskCtrlLevelRepository riskCtrlLevelRepository;


    /**
     * 新增风控层级
     *
     * @param riskLevelAddVO 新增实体
     * @return true
     */
    public ResponseVO<Boolean> insertRiskLevel(RiskLevelAddVO riskLevelAddVO) {

        long currentTimeMillis = System.currentTimeMillis();
        LambdaQueryWrapper<RiskCtrlLevelPO> query = Wrappers.lambdaQuery();
        String siteCode = riskLevelAddVO.getSiteCode();
        String riskControlType = riskLevelAddVO.getRiskControlType();
        String riskControlLevel = riskLevelAddVO.getRiskControlLevel();

        query.eq(RiskCtrlLevelPO::getSiteCode, siteCode)
                .eq(RiskCtrlLevelPO::getRiskControlType, riskControlType)
                .eq(RiskCtrlLevelPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(RiskCtrlLevelPO::getRiskControlLevel, riskControlLevel);
        if (this.count(query) > 0) {
            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
        }
        RiskCtrlLevelPO riskCtrlLevelPO = new RiskCtrlLevelPO();
        BeanUtil.copyProperties(riskLevelAddVO, riskCtrlLevelPO);
        riskCtrlLevelPO.setRiskControlLevelCode(currentTimeMillis);
        riskCtrlLevelPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        riskCtrlLevelPO.setCreator(riskLevelAddVO.getOperator());
        riskCtrlLevelPO.setCreatedTime(currentTimeMillis);
        riskCtrlLevelPO.setUpdater(riskLevelAddVO.getOperator());
        riskCtrlLevelPO.setUpdatedTime(currentTimeMillis);
        save(riskCtrlLevelPO);
        return ResponseVO.success(true);
    }

    /**
     * 分页查询风控层级列表
     *
     * @param riskLevelReqVO 分页参数
     * @return 分页列表
     */
    public ResponseVO<Page<RiskLevelResVO>> selectRiskLevelList(RiskLevelReqVO riskLevelReqVO) {
        // 分页查询
        Page<RiskCtrlLevelPO> page = new Page<>(riskLevelReqVO.getPageNumber(), riskLevelReqVO.getPageSize());
        LambdaQueryWrapper<RiskCtrlLevelPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RiskCtrlLevelPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        String siteCode = riskLevelReqVO.getSiteCode();
        queryWrapper.eq(RiskCtrlLevelPO::getSiteCode, siteCode);

        if (StringUtils.isNotBlank(riskLevelReqVO.getRiskControlType())) {
            queryWrapper.eq(RiskCtrlLevelPO::getRiskControlType, riskLevelReqVO.getRiskControlType());
        }

        // 风控层级传入自增ID
        Long[] codes = riskLevelReqVO.getRiskControlLevelCode();

        if (riskLevelReqVO.getRiskControlLevelCode() != null && codes.length > 0) {
            queryWrapper.in(RiskCtrlLevelPO::getId, Lists.newArrayList(riskLevelReqVO.getRiskControlLevelCode()));
        }

        // 创建人
        String creatorName = riskLevelReqVO.getCreatorName();
        if (StringUtils.isNotBlank(creatorName)) {
            queryWrapper.eq(RiskCtrlLevelPO::getCreator, creatorName);
        }
        // 最近操作人
        String updaterName = riskLevelReqVO.getUpdaterName();
        if (StringUtils.isNotBlank(updaterName)) {
            queryWrapper.eq(RiskCtrlLevelPO::getUpdater, updaterName);
        }

        if (StringUtils.isEmpty(riskLevelReqVO.getOrderField())){
            queryWrapper.orderByDesc(RiskCtrlLevelPO::getUpdatedTime);
        }else{
            applySort(queryWrapper, riskLevelReqVO.getOrderField(), riskLevelReqVO.getOrderType(), field -> {
                switch (field) {
                    case "createdTime": return RiskCtrlLevelPO::getCreatedTime;
                    case "updatedTime": return RiskCtrlLevelPO::getUpdatedTime;
                    default: return RiskCtrlLevelPO::getUpdatedTime;
                }
            });
        }

        Page<RiskCtrlLevelPO> poPage = riskCtrlLevelRepository.selectPage(page, queryWrapper);
        IPage<RiskLevelResVO> convert = poPage.convert(item -> {
            RiskLevelResVO vo = BeanUtil.copyProperties(item, RiskLevelResVO.class);
            vo.setCreatorName(vo.getCreatorName());
            vo.setRecentOperatorName(vo.getUpdater());
            return vo;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }


    public <T> LambdaQueryWrapper<T> applySort(LambdaQueryWrapper<T> wrapper,
                                               String field,
                                               String direction,
                                               Function<String, SFunction<T, ?>> fieldMapper) {
        if (StringUtils.isNotBlank(field)) {
            SFunction<T, ?> column = fieldMapper.apply(field);
            if ("asc".equalsIgnoreCase(direction)) {
                wrapper.orderByAsc(column);
            } else {
                wrapper.orderByDesc(column);
            }
        }
        return wrapper;
    }


    public ResponseVO<Boolean> deleteRiskLevel(IdVO idVO) {

        riskCtrlLevelRepository.update(null, Wrappers.<RiskCtrlLevelPO>lambdaUpdate()
                .eq(RiskCtrlLevelPO::getId, idVO.getId())
                .set(RiskCtrlLevelPO::getStatus, EnableStatusEnum.DISABLE.getCode())
        );
        return ResponseVO.success(true);
    }

    public ResponseVO<Boolean> updateRiskLevel(RiskLevelEditVO riskLevelEditVO) {

        RiskCtrlLevelPO riskCtrlLevelPO = riskCtrlLevelRepository.selectById(riskLevelEditVO.getId());
        if (riskCtrlLevelPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<RiskCtrlLevelPO> query = Wrappers.lambdaQuery();
        String siteCode = riskLevelEditVO.getSiteCode();
        String riskControlType = riskLevelEditVO.getRiskControlType();
        String riskControlLevel = riskLevelEditVO.getRiskControlLevel();

        query.eq(RiskCtrlLevelPO::getSiteCode, siteCode)
                .eq(RiskCtrlLevelPO::getRiskControlType, riskControlType)
                .eq(RiskCtrlLevelPO::getRiskControlLevel, riskControlLevel)
                .eq(RiskCtrlLevelPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .ne(RiskCtrlLevelPO::getId, riskLevelEditVO.getId());
        if (this.count(query) > 0) {
            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
        }

        BeanUtil.copyProperties(riskLevelEditVO, riskCtrlLevelPO, "id");
        riskCtrlLevelPO.setUpdatedTime(System.currentTimeMillis());
        riskCtrlLevelPO.setUpdater(riskLevelEditVO.getOperator());
        updateById(riskCtrlLevelPO);
        return ResponseVO.success(true);
    }
}

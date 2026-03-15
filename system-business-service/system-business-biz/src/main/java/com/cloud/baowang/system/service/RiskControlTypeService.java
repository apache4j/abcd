package com.cloud.baowang.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.po.risk.RiskCtrlLevelPO;
import com.cloud.baowang.system.repositories.RiskCtrlLevelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author aomiao
 */
@Slf4j
@Service
@AllArgsConstructor
public class RiskControlTypeService extends ServiceImpl<RiskCtrlLevelRepository, RiskCtrlLevelPO> {

    private final RiskCtrlLevelRepository riskLevelRepository;

    /**
     * @return 风控层级下拉框
     */
    public ResponseVO<List<RiskLevelResVO>> getRiskLevelList(RiskLevelDownReqVO riskLevelDownReqVO) {
        try {
            //根据站点获取风控层级
            String siteCode = riskLevelDownReqVO.getSiteCode();
            LambdaQueryWrapper<RiskCtrlLevelPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RiskCtrlLevelPO::getSiteCode, siteCode);
            if (StringUtils.isNotEmpty(riskLevelDownReqVO.getRiskControlType())) {
                queryWrapper.eq(StringUtils.isNotBlank(riskLevelDownReqVO.getRiskControlType()),
                        RiskCtrlLevelPO::getRiskControlType, riskLevelDownReqVO.getRiskControlType());
            }
            queryWrapper.eq(RiskCtrlLevelPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            List<RiskCtrlLevelPO> result = riskLevelRepository.selectList(queryWrapper);
            List<RiskLevelResVO> riskLevelList = result.stream().map(po -> {
                RiskLevelResVO vo = new RiskLevelResVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).toList();
            return ResponseVO.success(riskLevelList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaowangDefaultException(ResultCode.RISK_LEVEL_GET);
        }
    }

    public RiskLevelResVO getRiskLevelByLevelCode(String riskControlLevelCode,String siteCode) {
        LambdaQueryWrapper<RiskCtrlLevelPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RiskCtrlLevelPO::getRiskControlLevelCode, riskControlLevelCode).eq(RiskCtrlLevelPO::getSiteCode,siteCode);

        RiskCtrlLevelPO po = riskLevelRepository.selectOne(queryWrapper);
        if (po != null) {
            RiskLevelResVO resVO = new RiskLevelResVO();
            BeanUtils.copyProperties(po, resVO);
            return resVO;
        }

        return null;
    }

    public Map<String, RiskLevelDetailsVO> getByIds(List<String> ids) {
        LambdaQueryWrapper<RiskCtrlLevelPO> in = new LambdaQueryWrapper<RiskCtrlLevelPO>().in(RiskCtrlLevelPO::getId, ids);
        List<RiskCtrlLevelPO> riskCtrlLevelPOS = riskLevelRepository.selectList(in);
        if (CollectionUtil.isNotEmpty(riskCtrlLevelPOS)) {
            return riskCtrlLevelPOS.stream().map(po -> ConvertUtil.entityToModel(po, RiskLevelDetailsVO.class))
                    .filter(e -> e != null && StringUtils.isNotEmpty(e.getId()))
                    .collect(Collectors.toMap(RiskLevelDetailsVO::getId, Function.identity(), (k1, k2) -> k2));
        }
        return new HashMap<>();
    }

}

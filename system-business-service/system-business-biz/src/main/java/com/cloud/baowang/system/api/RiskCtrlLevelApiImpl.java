package com.cloud.baowang.system.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.system.api.api.RiskCtrlLevelApi;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.system.api.vo.risk.*;
import com.cloud.baowang.system.po.risk.RiskCtrlLevelPO;
import com.cloud.baowang.system.service.RiskControlTypeService;
import com.cloud.baowang.system.service.RiskCtrlLevelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class RiskCtrlLevelApiImpl implements RiskCtrlLevelApi {

    private final RiskCtrlLevelService riskCtrlLevelService;

    @Override
    public ResponseVO<Page<RiskLevelResVO>> selectRiskLevelList(RiskLevelReqVO riskLevelReqVO) {
        return riskCtrlLevelService.selectRiskLevelList(riskLevelReqVO);
    }

    @Override
    public ResponseVO<Boolean> insertRiskLevel(RiskLevelAddVO riskLevelAddVO) {
        // 风控类型
        String riskControlType = riskLevelAddVO.getRiskControlType();
        if (StringUtils.isBlank(riskControlType)) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_TYPE);
        }
        //风控层级
        String riskControlLevel = riskLevelAddVO.getRiskControlLevel();
        if (StringUtils.isBlank(riskControlLevel)) {
            return ResponseVO.fail(ResultCode.RISK_LEVEL_IS_NULL);
        }
        if (riskControlLevel.length() < 2 || riskControlLevel.length() > 10) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_LEVEL);
        }
        //风控层级描述
        String riskLevelDescribe = riskLevelAddVO.getRiskControlLevelDescribe();
        if (riskLevelDescribe.isBlank()) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_LEVEL_DESCRIBE);
        }
        if (riskLevelDescribe.length() > 50) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_LEVEL_DESCRIBE_LEN);
        }
        return riskCtrlLevelService.insertRiskLevel(riskLevelAddVO);
    }

    @Override
    public ResponseVO<Boolean> deleteRiskLevel(IdVO idVO) {
        return riskCtrlLevelService.deleteRiskLevel(idVO);
    }

    @Override
    public ResponseVO<Boolean> updateRiskLevel(RiskLevelEditVO riskLevelEditVO) {
        String riskControlLevel = riskLevelEditVO.getRiskControlLevel();
        if (riskControlLevel.length() < 2 || riskControlLevel.length() > 10) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_LEVEL);
        }
        //风控层级描述
        String riskLevelDescribe = riskLevelEditVO.getRiskControlLevelDescribe();
        if (riskLevelDescribe.isBlank()) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_LEVEL_DESCRIBE);
        }
        if (riskLevelDescribe.length() > 50) {
            return ResponseVO.fail(ResultCode.RISK_CONTROL_LEVEL_DESCRIBE_LEN);
        }
        return riskCtrlLevelService.updateRiskLevel(riskLevelEditVO);
    }

    @Override
    public List<RiskLevelResVO> getAllRiskLevelList() {
        List<RiskCtrlLevelPO> list = riskCtrlLevelService.list();
        return ConvertUtil.entityListToModelList(list, RiskLevelResVO.class);
    }


}

package com.cloud.baowang.admin.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskCtrlLevelApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelAddVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelEditVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RiskLevelService {

    private final RiskCtrlLevelApi riskCtrlLevelApi;

    public ResponseVO<Boolean> insertRiskLevel(RiskLevelAddVO riskLevelAddVO, String adminId) {
        return riskCtrlLevelApi.insertRiskLevel(riskLevelAddVO);
    }

    public ResponseVO<Page<RiskLevelResVO>> selectRiskLevelList(RiskLevelReqVO riskLevelReqVO) {
        return riskCtrlLevelApi.selectRiskLevelList(riskLevelReqVO);
    }

    public ResponseVO<Boolean> deleteRiskLevel(IdVO idVO) {
        return riskCtrlLevelApi.deleteRiskLevel(idVO);
    }

    public ResponseVO<Boolean> updateRiskLevel(RiskLevelEditVO riskLevelEditVO, String adminId) {
        return riskCtrlLevelApi.updateRiskLevel(riskLevelEditVO);
    }
}

package com.cloud.baowang.system.api;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskCtrlBlackApi;
import com.cloud.baowang.system.api.enums.RiskBlackTypeEnum;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.service.RiskBlackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class RiskCtrlBlackApiImpl implements RiskCtrlBlackApi {

    private final RiskBlackService riskBlackService;

    @Override
    public ResponseVO<Boolean> addBlackAccount(RiskBlackAccountVO vo) {
        ResponseVO<Boolean> booleanResponseVO = checkBlackAccount(vo);
        if(ObjectUtil.isNotNull(booleanResponseVO)){
            return booleanResponseVO;
        }

        RiskInfoReqVO riskInfoReqVO = new RiskInfoReqVO();
        String riskControlAccount = vo.getRiskControlAccount();
        if (StrUtil.containsAny(riskControlAccount, "～")) {
            String[] split = riskControlAccount.split("～");
            riskControlAccount = String.format("%s%s%s", split[0], "~", split[1]);
        }
        vo.setRiskControlAccount(riskControlAccount);
        vo.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        riskInfoReqVO.setRiskControlAccount(riskControlAccount);
        riskInfoReqVO.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        riskInfoReqVO.setRiskControlAccountName(vo.getRiskControlAccountName());
        RiskBlackAccountVO accountVO = riskBlackService.getRiskByAccount(riskInfoReqVO);
        if (accountVO != null) {
            return ResponseVO.fail(ResultCode.RISK_ACCOUNT_EXIST);
        }
        return riskBlackService.save(vo);
    }
    public ResponseVO<Boolean> checkBlackAccount(RiskBlackAccountVO vo) {
        // 校验入参数
        if (ObjectUtil.isEmpty(vo.getRiskControlAccount())) {
            log.info("riskControlAccount 是空，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if(vo.getRiskControlAccount().contains("|")){
            log.info("riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if(ObjectUtil.isNotEmpty(vo.getRiskControlAccountName())){
            if(vo.getRiskControlAccountName().contains("|")){
                log.info("riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }
        /*if(vo.getRiskControlAccount().contains("|")){
            log.info("riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }*/
        if (ObjectUtil.isEmpty(vo.getRiskControlTypeCode())) {
            log.info("riskControlTypeCode 是空，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return null;
    }

    @Override
    public ResponseVO<Boolean> updateBlackAccount(RiskBlackAccountVO vo) {
        ResponseVO<Boolean> booleanResponseVO = checkBlackAccount(vo);
        if(ObjectUtil.isNotNull(booleanResponseVO)){
            return booleanResponseVO;
        }
        return riskBlackService.updateBlackAccount(vo);
    }

    @Override
    public ResponseVO<Boolean> removeBlackAccount(IdVO vo) {
        return riskBlackService.removeBlackAccount(vo);
    }

    @Override
    public ResponseVO<Page<RiskBlackAccountVO>> getRiskBlackListPage(RiskBlackAccountReqVO reqVO) {
        return riskBlackService.getRiskBlackListPage(reqVO);
    }

    @Override
    public ResponseVO<List<RiskBlackAccountVO>> getRiskBlack(RiskBlackAccountVO queryVO) {
        return riskBlackService.getRiskBlack(queryVO);
    }

    @Override
    public ResponseVO<Boolean> getRiskIpBlack(RiskBlackAccountVO queryVO) {
        return riskBlackService.getRiskIpBlack(queryVO);
    }

}

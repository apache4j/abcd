package com.cloud.baowang.system.api.site.black;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteRiskCtrlBlackApi;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountIsBlackReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.service.site.black.SiteRiskBlackService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteRiskCtrlBlackApiImpl implements SiteRiskCtrlBlackApi {

    private final SiteRiskBlackService siteRiskBlackService;

    @Override
    public ResponseVO<Boolean> addBlackAccount(RiskBlackAccountVO vo) {

        if (vo.getRiskControlTypeCode() == null) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        ResponseVO<Boolean> booleanResponseVO = checkBlackAccount(vo);
        if (ObjectUtil.isNotNull(booleanResponseVO)) {
            return booleanResponseVO;
        }
        RiskInfoReqVO riskInfoReqVO = new RiskInfoReqVO();
        riskInfoReqVO.setRiskControlAccount(vo.getRiskControlAccount());
        riskInfoReqVO.setRiskControlAccountName(vo.getRiskControlAccountName());
        riskInfoReqVO.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        String riskControlAccount = vo.getRiskControlAccount();
        if (StrUtil.containsAny(riskControlAccount, "～")) {
            String[] split = riskControlAccount.split("～");
            riskControlAccount = String.format("%s%s%s", split[0], "~", split[1]);
        }
        vo.setRiskControlAccount(riskControlAccount);
        vo.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        riskInfoReqVO.setRiskControlAccount(riskControlAccount);
        riskInfoReqVO.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        riskInfoReqVO.setSiteCode(vo.getSiteCode());
        RiskBlackAccountVO accountVO = siteRiskBlackService.getRiskByAccount(riskInfoReqVO);
        if (accountVO != null) {
            return ResponseVO.fail(ResultCode.RISK_ACCOUNT_EXIST);
        }
        return siteRiskBlackService.save(vo);
    }

    public ResponseVO<Boolean> checkBlackAccount(RiskBlackAccountVO vo) {
        // 校验入参数
        if (ObjectUtil.isEmpty(vo.getRiskControlAccount())) {
            log.info("站点 riskControlAccount 是空，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        /*if(vo.getRiskControlAccount().contains("|")){
            log.info("站点 riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }*/
        if (ObjectUtil.isNotEmpty(vo.getRiskControlAccountName())) {
            if (vo.getRiskControlAccountName().contains("|")) {
                log.info("站点 riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }
        /*if(vo.getRiskControlAccount().contains("|")){
            log.info("站点 riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }*/
        if (ObjectUtil.isEmpty(vo.getRiskControlTypeCode())) {
            log.info("站点 riskControlTypeCode 是空，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return null;
    }

    @Override
    public ResponseVO<Boolean> updateBlackAccount(RiskBlackAccountVO vo) {
        return siteRiskBlackService.updateBlackAccount(vo);
    }

    @Override
    public ResponseVO<Boolean> removeBlackAccount(IdVO vo) {
        return siteRiskBlackService.removeBlackAccount(vo);
    }

    @Override
    public ResponseVO<Page<RiskBlackAccountVO>> getRiskBlackListPage(RiskBlackAccountReqVO reqVO) {
        return siteRiskBlackService.getRiskBlackListPage(reqVO);
    }

    @Override
    public ResponseVO<List<RiskBlackAccountVO>> getRiskBlack(RiskBlackAccountVO queryVO) {
        return siteRiskBlackService.getRiskBlack(queryVO);
    }

    @Override
    public ResponseVO<Boolean> getRiskIpBlack(RiskBlackAccountVO queryVO) {
        return siteRiskBlackService.getRiskIpBlack(queryVO);
    }

    @Override
    public ResponseVO<Boolean> isRiskBlack(RiskBlackAccountIsBlackReqVO queryVO) {
        return siteRiskBlackService.isRiskBlack(queryVO);
    }

}

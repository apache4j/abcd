package com.cloud.baowang.user.api.vip;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.vo.vip.SiteVipFeeRateVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.user.service.SiteVIPGradeService;
import com.cloud.baowang.user.service.SiteVipOptionService;
import com.cloud.baowang.user.service.UserVipFlowRecordCnService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteVipOptionApiImpl implements SiteVipOptionApi {

    private SiteVipOptionService siteVipOptionService;

    private UserVipFlowRecordCnService userVipFlowRecordCnService;

    private SiteVIPGradeService siteVIPGradeService;

    @Override
    public ResponseVO<Boolean> initVIP(String siteCode, List<String> currency) {
        return ResponseVO.success(siteVipOptionService.initVip(siteCode,currency));
    }

    @Override
    public ResponseVO<Void> updateSiteVipOptionVO(SiteVipOptionVO vo) {
        siteVipOptionService.updateSiteVipOptionVO(vo);
        //批量更改用户VIP当前经验相关设定信息
        userVipFlowRecordCnService.batchUpdateVIPconfigUserVip(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<SiteVipOptionVO>> getList(String siteCode, String currency) {
        return ResponseVO.success(siteVipOptionService.getList(siteCode,currency));
    }

    @Override
    public ResponseVO<List<VIPGradeVO>> getCnVipGradeList() {
        return ResponseVO.success(siteVipOptionService.getInitVIPGrade());
    }

    @Override
    public ResponseVO<Void> cnVipUpDownAllSiteCode(String timezone) {
        userVipFlowRecordCnService.cnVipUpDownAllSiteCode(timezone);
        return ResponseVO.success();
    }
    @Override
    public Map<Integer,String> getCnVipGradeMap() {
        List<VIPGradeVO> list = siteVipOptionService.getInitVIPGrade();
        Map<Integer,String> vipGradeMap = list.stream().collect(Collectors.toMap(VIPGradeVO::getVipGradeCode, VIPGradeVO::getVipGradeName, (k1, k2) -> k2));
        return vipGradeMap;
    }

    @Override
    public SiteVipFeeRateVO getVipGradeSiteCodeAndCurrency(String siteCode, Integer vipGradeCode, String currencyCode, String withdrawWayId) {
        return siteVipOptionService.getVipGradeSiteCodeAndCurrency(siteCode,vipGradeCode,currencyCode,withdrawWayId);
    }

    @Override
    public SiteVipOptionVO getVipGradeInfoByCode(String siteCode, Integer vipGradeCode, String currencyCode) {
        return siteVipOptionService.getVipGradeInfoByCode(siteCode,vipGradeCode,currencyCode);
    }

    @Override
    public ResponseVO<Void> initVIPGrade(String siteCode) {
        siteVIPGradeService.initVIPGrade(siteCode);
        return ResponseVO.success();
    }
}

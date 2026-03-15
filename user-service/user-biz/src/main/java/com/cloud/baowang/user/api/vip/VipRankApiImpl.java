package com.cloud.baowang.user.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.vip.*;
import com.cloud.baowang.user.po.SiteVIPRankPO;
import com.cloud.baowang.user.service.SiteVIPRankService;
import com.cloud.baowang.user.service.SiteVipChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author : 小智
 * @Date : 22/3/24 2:16 PM
 * @Version : 1.0
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class VipRankApiImpl implements VipRankApi {

    private SiteVIPRankService siteVIPRankService;

    private SiteVipChangeRecordService recordService;


    @Override
    public ResponseVO<Page<SiteVIPRankVO>> queryVIPRankPage(PageVO pageVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        return siteVIPRankService.queryVIPRankPage(pageVO, siteCode);
    }

    @Override
    public ResponseVO<Boolean> batchVIPRank(String siteCode, List<String> currency,Integer handicapMode) {
        return ResponseVO.success(siteVIPRankService.batchVIPRank(siteCode, currency,handicapMode)) ;
    }

    @Override
    public ResponseVO<?> updateVIPRank(VIPRankUpdateVO vipRankUpdateVO) {
        return siteVIPRankService.updateVIPRank(vipRankUpdateVO);
    }

    @Override
    public ResponseVO<List<CodeValueNoI18VO>> getVipRank() {
        return ResponseVO.success(siteVIPRankService.getVipRank());
    }

    @Override
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryVIPRankOperation(SiteVipChangeRecordPageQueryVO reqVO) {
        return recordService.queryChangeRecordPage(reqVO);
    }

    @Override
    public ResponseVO<Long> queryOperationCount(SiteVipChangeRecordPageQueryVO reqVO) {
        return recordService.queryOperationCount(reqVO);
    }



    @Override
    public ResponseVO<Long> getTotalCount(SiteVipChangeRecordPageQueryVO reqVO) {
        return recordService.getTotalCount(reqVO);
    }

    @Override
    public ResponseVO<SiteVIPRankVO> queryVIPRankDetailById(String id) {
        return siteVIPRankService.queryVIPRankDetailById(id);
    }

    @Override
    public ResponseVO<List<SiteVIPRankVO>> getVipRankListBySiteCode(String siteCode) {
        return ResponseVO.success(siteVIPRankService.getVipRankListBySiteCode(siteCode));
    }

    @Override
    public SiteVipFeeRateVO getVipRankSiteCodeAndCurrency(String siteCode, Integer vipRankCode, String currencyCode, String withdrawWayId) {
        return siteVIPRankService.getVipRankSiteCodeAndCurrency(siteCode,vipRankCode,currencyCode,withdrawWayId);
    }

    @Override
    public Map<String, List<SiteVIPRankVO>> getVipRankListBySiteCodes(List<String> siteCodeList) {
        return siteVIPRankService.getVipRankListBySiteCodes(siteCodeList);
    }

    @Override
    public ResponseVO<SiteVIPRankVO> getVipRankListBySiteCodeAndCode(String siteCode, Integer vipRankCode) {
        return ResponseVO.success(siteVIPRankService.getVipRankListBySiteCodeAndCode(siteCode, vipRankCode));
    }

    @Override
    public ResponseVO<List<SiteVIPRankVO>> getVipRankListBySiteCodeAndCodes(String siteCode, List<Integer> vipRankCodes) {
        return ResponseVO.success(siteVIPRankService.getVipRankListBySiteCodeAndCodes(siteCode, vipRankCodes));
    }

    /**
     * 获取总台vip段位信息
     *
     * @param vipRankCode
     * @return
     */
    @Override
    public VIPRankVO getVipRankByCode(Integer vipRankCode) {
        return siteVIPRankService.getVipRankByCode(vipRankCode);
    }

    @Override
    public List<VIPRankVO> getVipRankList() {
        return siteVIPRankService.getVipRankList();
    }

    @Override
    public ResponseVO<Boolean> initSystemVipRank() {
        return ResponseVO.success(siteVIPRankService.initSystemVipRank());
    }

    @Override
    public SiteVIPRankVO getFirstVipRankBySiteCode(String siteCode) {
        return siteVIPRankService.getFirstVipRankBySiteCode(siteCode);
    }

    @Override
    public Map<String, List<CodeValueNoI18VO>> getVIPRankGradeList(String siteCode) {
        return siteVIPRankService.getVIPRankGradeList(siteCode);
    }

    @Override
    public Map<String, List<SiteVIPRankVO>> getAllSiteVipRank() {
        return siteVIPRankService.getAllSiteVipRank();
    }

    @Override
    public SiteVIPRankVO getVipRankBySiteCodeAndCode(String siteCode,Integer vipRank) {
        SiteVIPRankPO siteVIPRankPO = siteVIPRankService.getVipRankBySiteCodeAndCode(siteCode,vipRank);

        return ConvertUtil.entityToModel(siteVIPRankPO,SiteVIPRankVO.class);
    }

    @Override
    public List<SiteVIPRankRabateVO> getVipRankBySiteCode(String siteCode) {
        return siteVIPRankService.getVipRankBySiteCode(siteCode);
    }

    @Override
    public ResponseVO<Boolean> initUserWithdrawConfig(String siteCode,List<String> currency,Integer handicapMode) {
        return  ResponseVO.success(siteVIPRankService.initUserWithdrawConfig(siteCode,currency,handicapMode));
    }


}

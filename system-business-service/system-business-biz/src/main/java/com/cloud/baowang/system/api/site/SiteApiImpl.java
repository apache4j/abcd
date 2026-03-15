package com.cloud.baowang.system.api.site;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.SystemParamTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteOptionModelNameEnum;
import com.cloud.baowang.system.api.enums.SiteOptionStatusEnum;
import com.cloud.baowang.system.api.enums.SiteOptionTypeEnum;
import com.cloud.baowang.system.api.vo.language.LanguageManagerVO;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import com.cloud.baowang.system.api.vo.site.SiteAddVO;
import com.cloud.baowang.system.api.vo.site.SiteEnableVO;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordReqVO;
import com.cloud.baowang.system.service.SiteAddService;
import com.cloud.baowang.system.service.SiteService;
import com.cloud.baowang.system.service.SystemParamService;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author : 小智
 * @Date : 2024/7/26 16:13
 * @Version : 1.0
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteApiImpl implements SiteApi {

    private final SiteService siteService;

    private final SiteAddService siteAddService;

    private final LanguageManagerService languageManagerService;

    private final SystemParamService systemParamService;

    private final SiteInfoChangeRecordService siteInfoChangeRecordService;

    @Override
    public ResponseVO<Page<SiteVO>> querySiteInfo(final SiteRequestVO siteRequestVO) {
        return siteService.querySiteInfo(siteRequestVO);
    }

    @Override
    public ResponseVO<List<SiteVO>> allSiteInfo() {
        return siteService.allSiteInfo();
    }

    @Override
    public ResponseVO<List<SiteVO>> siteInfoAllstauts() {
        return siteService.siteInfoAllstauts();
    }

    @Override
    public ResponseVO<?> judgeAndAddSite(SiteAddVO siteAddVO) {
        ResponseVO<?> data=null;
        try {
            data=siteAddService.judgeAndAddSite(siteAddVO);
            if (data.getCode()!= ResultCode.SUCCESS.getCode()){
                SiteInfoChangeRecordReqVO sd=siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(null,null,SiteOptionModelNameEnum.site.getname(),
                        CurrReqUtils.getReqIp(),null,SiteOptionTypeEnum.DataInsert.getCode(),
                        SiteOptionStatusEnum.fail.getCode(),CurrReqUtils.getAccount());
                siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            }
        }catch (BaowangDefaultException e){
            SiteInfoChangeRecordReqVO sd=siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(null,null,SiteOptionModelNameEnum.site.getname(),
                    CurrReqUtils.getReqIp(),null,SiteOptionTypeEnum.DataInsert.getCode(),
                    SiteOptionStatusEnum.fail.getCode(),CurrReqUtils.getAccount());
            siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            throw new BaowangDefaultException(e.getMessage());
        }
        return data;
    }

    @Override
    public ResponseVO<Boolean> updateSiteInfo(SiteAddVO siteAddVO) {
        return siteAddService.updateSiteInfo(siteAddVO);
    }

    @Override
    public ResponseVO<?> isEnable(SiteEnableVO siteEnableVO) {
        return siteService.isEnable(siteEnableVO);
    }

    @Override
    public ResponseVO<?> resetPassword(SiteEnableVO siteEnableVO) {
        return siteService.resetPassword(siteEnableVO);
    }

    @Override
    public ResponseVO<SiteVO> getSiteInfo(String siteCode) {
        return siteService.getSiteInfo(siteCode);
    }

    @Override
    public ResponseVO<SiteVO> getCustomerSiteInfo(String siteCode) {
        ResponseVO<SiteVO> responseVO = siteService.getSiteInfo(siteCode);
        if (responseVO != null && responseVO.getData() != null) {
            SiteVO vo = responseVO.getData();
            ResponseVO<List<LanguageValidListCacheVO>> responseVO1 = languageManagerService.validList(siteCode);
            List<LanguageValidListCacheVO> data = responseVO1.getData();
            if (CollectionUtil.isNotEmpty(data)) {
                List<CodeValueNoI18VO> languageList = data.stream()
                        .map(item -> new CodeValueNoI18VO(item.getCode(), item.getName()))
                        .toList();
                vo.setLanguageList(languageList);
                vo.setLanguageManagerVOS(BeanUtil.copyToList(data, LanguageManagerVO.class));

                Map<String, String> systemParam = systemParamService.getSystemParamMap(SystemParamTypeEnum.CAPTCHA_SWITCH.getType());
                vo.setShowCaptcha(CommonConstant.business_one);
                if (systemParam != null && systemParam.size() > 0) {
                    if ("0".equals(systemParam.get("0"))) {
                        vo.setShowCaptcha(CommonConstant.business_zero);
                    }
                }
                responseVO.setData(vo);
                responseVO.getData().setValidListCacheVOS(data);
            }
        }

        return responseVO;
    }

    @Override
    public ResponseVO<List<SiteVO>> getSiteInfoByName(String siteName) {
        return ResponseVO.success(siteService.getSiteInfoByName(siteName));
    }

    @Override
    public SiteVO getSiteInfoByCode(String siteCode) {
        return siteService.getSiteInfoByCode(siteCode);
    }

    @Override
    public ResponseVO<Map<String, List<CodeValueVO>>> getLanAndCurrencyDownBox() {
        return ResponseVO.success(siteService.getLanAndCurrencyDownBox());
    }

    @Override
    public ResponseVO<List<CodeValueVO>> chooseCurrency(String siteCode) {
        return siteService.chooseCurrency(siteCode);
    }

    @Override
    public Boolean checkSiteIncludesRiskControl(String siteCode) {
        return siteService.checkSiteIncludesRiskControl(siteCode);
    }

    @Override
    public ResponseVO<Boolean> updPlatCurrency(String siteCode, String platCurrencyName, String platCurrencySymbol, String platCurrencyIcon) {
        return ResponseVO.success(siteService.updPlatCurrency(siteCode, platCurrencyName, platCurrencySymbol, platCurrencyIcon));
    }

    @Override
    public ResponseVO<List<CodeValueVO>> getSiteDownBox() {
        return siteService.getSiteDownBox();
    }

    @Override
    public List<SiteVO> getSiteInfoByTimezone(String timeZone) {
        return siteService.getSiteInfoByTimezone(timeZone);
    }

    @Override
    public SiteVO getSiteDetail(String siteCode) {
        return siteService.getSiteDetail(siteCode);
    }

    @Override
    public List<SiteVO> getSiteInfoSByCodes(List<String> totalSiteCodeList) {
        return siteService.getSiteInfoSByCodes(totalSiteCodeList);
    }

    @Override
    public ResponseVO<List<SiteVO>> getSiteList() {
        return siteService.getSiteList();
    }

    @Override
    public ResponseVO updateSiteRebateStatus(Integer status) {
        return siteService.updateSiteRebateStatus(status);
    }

}

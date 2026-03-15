package com.cloud.baowang.system.api.site.agreement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.vo.site.ContactInfoVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicClientVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.api.vo.site.agreement.MediaInfo;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.site.tutorial.OptionTypeConfigVO;
import com.cloud.baowang.system.api.vo.site.tutorial.SiteBasicVo;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowRspVO;
import com.cloud.baowang.system.service.site.config.HelpCenterManageService;
import com.cloud.baowang.system.service.site.config.SiteBusinessBasicInfoService;
import com.cloud.baowang.system.service.site.config.SiteDownloadConfigService;
import com.cloud.baowang.system.service.site.config.SiteMediaInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class HelpCenterManageApiImpl implements HelpCenterManageApi {
    private final HelpCenterManageService  helpCenterManageService;
    private final SiteDownloadConfigService siteDownloadConfigService;

    private final SiteBusinessBasicInfoService businessBasicInfoService;

    private final SiteMediaInfoService siteMediaInfoService;

    @Override
    public ResponseVO<Boolean> addConfig(i18nMessagesVO vo) {
        return helpCenterManageService.addConfig(vo);
    }

    @Override
    public ResponseVO<i18nMessagesVO> getConfig(Integer code) {
        return helpCenterManageService.getConfig(code);
    }

    @Override
    public ResponseVO<i18nMessagesVO> showUnLoginPic() {
        return helpCenterManageService.showUnLoginPic();
    }

//    @Override
//    public ResponseVO<UserAgreementVO> getSingleSpecificInfo(Integer code) {
//        return helpCenterManageService.getSingleSpecificInfo(code);
//    }

    @Override
    public ResponseVO<List<SiteBasicVo>> getSiteBasicInfo() {
        return helpCenterManageService.getSiteBasicInfo();
    }

    @Override
    public Page<OptionTypeConfigVO> getOptionPage(PageVO pageVO) {
        return helpCenterManageService.getOptionPage(pageVO);
    }

    @Override
    public ResponseVO<List<TutorialClientShowRspVO>> getHelpCenterInfo() {
        return helpCenterManageService.getHelpCenterInfo();
    }

    @Override
    public ResponseVO<Boolean> addDownloadInfo(i18nMessagesVO i18nMessagesVO) {
        return siteDownloadConfigService.addDownloadInfo(i18nMessagesVO);
    }

    @Override
    public ResponseVO<i18nMessagesVO> getDownloadInfo() {
        return siteDownloadConfigService.getDownloadInfo();
    }

    @Override
    public ResponseVO<List<ContactInfoVO>> getContactInfo() {
        return helpCenterManageService.getContactInfo();
    }

    @Override
    public ResponseVO<List<BusinessBasicVO>> getBusinessBasicInfos(String siteCode) {
        return ResponseVO.success(businessBasicInfoService.getBusinessChampionInfo(siteCode));
    }

    @Override
    public ResponseVO<Boolean> updateBusinessBasicInfo(BusinessBasicVO reqVO) {
        return ResponseVO.success(businessBasicInfoService.updateBusinessBasicInfo(reqVO));
    }

    @Override
    public ResponseVO<Boolean> addBusinessBasicInfo(BusinessBasicVO reqVO) {
        return ResponseVO.success(businessBasicInfoService.addBusinessChampionInfo(reqVO));
    }

    @Override
    public ResponseVO<List<BusinessBasicClientVO>> getBusinessBasicInfoClient(String siteCode) {
        return ResponseVO.success(businessBasicInfoService.getBusinessBasicInfoClient(siteCode));
    }

    @Override
    public ResponseVO<Boolean> addMediaInfo(List<MediaInfo> reqVO) {
        return ResponseVO.success(siteMediaInfoService.addMediaInfo(reqVO));
    }

    @Override
    public ResponseVO<List<MediaInfo>> getMediaInfo() {
        return ResponseVO.success(siteMediaInfoService.getMediaInfo());
    }

    @Override
    public ResponseVO<Boolean> delMediaInfo(MediaInfo reqVO) {
        return ResponseVO.success(siteMediaInfoService.delMediaInfo(reqVO));
    }

    @Override
    public ResponseVO<Boolean> delBusinessBasicInfo(BusinessBasicVO reqVO) {
        return ResponseVO.success(businessBasicInfoService.delBusinessBasicInfo(reqVO));
    }

    @Override
    public ResponseVO<Boolean> sort(List<BusinessBasicVO> reqVO) {
        return ResponseVO.success(businessBasicInfoService.sort(reqVO));
    }
}

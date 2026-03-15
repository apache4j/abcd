package com.cloud.baowang.site.controller.site;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.business.BusinessStorageMenuRespVO;
import com.cloud.baowang.system.api.vo.param.SystemSiteSaveQuickEntryParam;
import com.cloud.baowang.system.api.vo.param.SystemSiteSelectQuickEntryParam;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.notice.UserNoticeConfigApi;
import com.cloud.baowang.user.api.api.site.SiteHomeStatisticsApi;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.SiteHomeNoticeConfigVO;
import com.cloud.baowang.user.api.vo.site.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qiqi
 */
@Tag(name = "站点首页统计")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/site_home/api")
public class SiteHomeStatisticsController {

    private final SiteHomeStatisticsApi siteHomeStatisticsApi;

    private final SiteApi siteApi;

    private final UserNoticeConfigApi userNoticeConfigApi;

    private final MinioUploadApi minioUploadApi;

    private final BusinessAdminApi businessAdminApi;


    @Operation(summary = "用户数据概览")
    @PostMapping("/userDataOverview")
    public ResponseVO<UserDataOverviewRespVo> userDataOverview(@Valid @RequestBody UserDataOverviewResVo vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return ResponseVO.success(siteHomeStatisticsApi.userDataOverview(vo));
    }

    @Operation(summary = "代理数据概览")
    @PostMapping("/agentDataOverview")
    public ResponseVO<AgentDataOverviewRespVo> agentDataOverview(@Valid @RequestBody UserDataOverviewResVo vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return ResponseVO.success(siteHomeStatisticsApi.agentDataOverview(vo));
    }

    @Operation(summary = "站点线图数据概览")
    @PostMapping("/dataCompareGraph")
    public ResponseVO<SiteDataCompareGraphVO> dataCompareGraph(@Valid @RequestBody SiteDataCompareGraphParam vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        if (StringUtils.isBlank(vo.getCurrencyCode())) {
            vo.setConvertPlatCurrency(true);
        }
        return siteHomeStatisticsApi.dataCompareGraph(vo);
    }

    /**
     * 如果不传主货币，则统计所有，且转换为主货币，否则就是平台币
     */
    @Operation(summary = "站点输赢概览")
    @PostMapping("/siteDataWinLoss")
    public ResponseVO<SiteDataWinLossResVO> siteDataWinLoss(@Valid @RequestBody UserDataOverviewResVo vo) {
        if (StringUtils.isBlank(vo.getCurrencyCode())) {
            vo.setConvertPlatCurrency(true);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteHomeStatisticsApi.siteDataWinLoss(vo);
    }

    /*@Operation(summary = "流量访问数据概览")
    @PostMapping("/siteTrafficData")
    public ResponseVO<SiteTrafficDataResVO> siteTrafficData(@Valid @RequestBody UserDataOverviewResVo vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());

        return siteHomeStatisticsApi.siteTrafficData(vo);
    }*/

    /*@Operation(summary = "流量访问数据线图")
    @PostMapping("/siteTrafficDataCompareGraph")
    public ResponseVO<SiteTrafficDataCompareGraphResVO> siteTrafficDataCompareGraph(@Valid @RequestBody SiteTrafficDataCompareGraphResVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteHomeStatisticsApi.siteTrafficDataCompareGraph(vo);
    }*/

    @Operation(summary = "站点待办")
    @PostMapping("/getSiteTodo")
    public ResponseVO<SiteTodoDataResVO> getSiteTodo() {

        return siteHomeStatisticsApi.getSiteTodo(CurrReqUtils.getSiteCode());
    }


    @Operation(summary = "获取站点详细信息")
    @PostMapping(value = "/getSiteInfo")
    public ResponseVO<SiteVO> getSiteInfo() {
        return siteApi.getSiteInfo(CurrReqUtils.getSiteCode());
    }

    @Operation(summary = "获取该站点已选中币种")
    @PostMapping(value = "/chooseCurrency")
    public ResponseVO<List<CodeValueVO>> chooseCurrency() {
        return siteApi.chooseCurrency(CurrReqUtils.getSiteCode());
    }

    @Operation(summary = "站点首页公告显示")
    @PostMapping("/siteNoticeList")
    public ResponseVO<List<SiteHomeNoticeConfigVO>> siteNoticeLists() {
        return userNoticeConfigApi.siteNoticeLists(CurrReqUtils.getSiteCode());
    }


    @Operation(summary = "查询常用功能")
    @PostMapping(value = "/selectQuickEntry")
    public ResponseVO<List<BusinessStorageMenuRespVO>> selectQuickEntry() {
        SystemSiteSelectQuickEntryParam siteSelectQuickEntryParam = new SystemSiteSelectQuickEntryParam();
        siteSelectQuickEntryParam.setAdminId(CurrReqUtils.getOneId());
        siteSelectQuickEntryParam.setSiteCode(CurrReqUtils.getSiteCode());
        return  ResponseVO.success(businessAdminApi.getQuickButton(siteSelectQuickEntryParam));
    }

    @Operation(summary = "编辑保存常用功能")
    @PostMapping(value = "/saveQuickEntry")
    public ResponseVO<Boolean> saveQuickEntry(@Valid @RequestBody SystemSiteSaveQuickEntryParam vo) {
        vo.setAdminId(CurrReqUtils.getOneId());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(businessAdminApi.updateQuickButton(CurrReqUtils.getOneId(), vo.getQuickEntry()));
    }

    @Operation(summary = "来路分析-显示域名访问排行")
    @PostMapping("/getDomainNameRanking")
    public ResponseVO<List<IPTop10ResVO>> getDomainNameRanking(@RequestBody IPTop10ReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteHomeStatisticsApi.getDomainNameRanking(vo);
    }
//
//    @Operation(summary = "访问来源")
//    @PostMapping("/getVisitFrom")
//    public ResponseVO<List<VisitFromResVO>> getVisitFrom(@Valid @RequestBody IPTop10ReqVO vo) {
//        vo.setSiteCode(CurrReqUtils.getSiteCode());
//        return siteHomeStatisticsApi.getVisitFrom(vo);
//    }

    @Operation(summary = "流量显示-地图-访问ip统计")
    @PostMapping("/getVisitFromByIp")
    ResponseVO<List<IPTop10ResVO>> getVisitFromByIp(@RequestBody IPTop10ReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteHomeStatisticsApi.getVisitFromByIp(vo);
    }

    @Operation(summary = "来路分析-导出")
    @PostMapping("/exportIPTop10")
    public ResponseVO<String> exportIPTop10(@RequestBody IPTop10ReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPageSize(10000);
        ResponseVO<List<IPTop10ResVO>> responseVO = siteHomeStatisticsApi.getDomainNameRanking(vo);
        if (!responseVO.isOk() || responseVO.getData().isEmpty()) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        long size = responseVO.getData().size();
        byte[] byteArray = ExcelUtil.writeForParallel(IPTop10ResVO.class, vo, 1,
                ExcelUtil.getPages(vo.getPageSize(), size),
                param -> ConvertUtil.entityListToModelList(siteHomeStatisticsApi.getDomainNameRanking(param).getData(), IPTop10ResVO.class));

        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.IP_TOP10_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}



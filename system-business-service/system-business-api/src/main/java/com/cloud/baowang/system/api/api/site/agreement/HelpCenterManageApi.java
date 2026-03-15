package com.cloud.baowang.system.api.api.site.agreement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.ContactInfoVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicClientVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.api.vo.site.agreement.MediaInfo;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.site.tutorial.OptionTypeConfigVO;
import com.cloud.baowang.system.api.vo.site.tutorial.SiteBasicVo;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowRspVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(contextId = "remoteHelpCenterManageApi", value = ApiConstants.NAME)
@Tag(name = "用户协议管理 服务 - helpCenterManageApi")
public interface HelpCenterManageApi {

    String PREFIX = ApiConstants.PREFIX + "/helpCenterManage/api/";


    @PostMapping(PREFIX + "addConfig")
    @Operation(summary = "配置添加")
    ResponseVO<Boolean> addConfig(@RequestBody i18nMessagesVO vo);

    @GetMapping(PREFIX + "getConfig")
    @Operation(summary = "配置查询")
    ResponseVO<i18nMessagesVO> getConfig(@RequestParam("code") Integer code);


    @PostMapping(PREFIX + "showUnLoginPic")
    @Operation(summary = "首页未登录图")
    ResponseVO<i18nMessagesVO> showUnLoginPic();

//    @GetMapping("getSingleSpecificInfo")
//    @Operation(summary = "PC获取单个标签具体信息")
//    ResponseVO<UserAgreementVO> getSingleSpecificInfo(@RequestParam("code") Integer code);

    @PostMapping(PREFIX + "getSiteBasicInfo")
    @Operation(summary = "查询站点下基本配置")
    ResponseVO<List<SiteBasicVo>> getSiteBasicInfo();

    @PostMapping(PREFIX + "getOptionPage")
    @Operation(summary = "信息配置分页列表")
    Page<OptionTypeConfigVO> getOptionPage(PageVO pageVO);

    @PostMapping(PREFIX + "getHelpCenterInfo")
    @Operation(summary = "帮助中心")
    ResponseVO<List<TutorialClientShowRspVO>> getHelpCenterInfo();

    @PostMapping(PREFIX+"addDownloadInfo")
    @Operation(summary = "下载页配置")
    ResponseVO<Boolean> addDownloadInfo(@RequestBody i18nMessagesVO i18nMessagesVO);

    @PostMapping(PREFIX+"getDownloadInfo")
    @Operation(summary = "下载页配置回显")
    ResponseVO<i18nMessagesVO> getDownloadInfo();

    @PostMapping(PREFIX+"getContactInfo")
    @Operation(summary = "加入我们")
    ResponseVO<List<ContactInfoVO>> getContactInfo();

    @GetMapping(PREFIX+"getBusinessBasicInfos")
    @Operation(summary = "招商联系方式列表")
    ResponseVO<List<BusinessBasicVO>> getBusinessBasicInfos(@RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX+"updateBusinessBasicInfo")
    @Operation(summary = "修改商务信息")
    ResponseVO<Boolean> updateBusinessBasicInfo(@RequestBody BusinessBasicVO reqVO);

    @PostMapping(PREFIX+"addBusinessBasicInfo")
    @Operation(summary = "添加商务信息")
    ResponseVO<Boolean> addBusinessBasicInfo(@RequestBody BusinessBasicVO reqVO);


    @PostMapping(PREFIX+"delBusinessBasicInfo")
    @Operation(summary = "删除商务信息")
    ResponseVO<Boolean> delBusinessBasicInfo(@RequestBody BusinessBasicVO reqVO);


    @PostMapping(PREFIX+"sort")
    @Operation(summary = "排序")
    ResponseVO<Boolean> sort(@RequestBody List<BusinessBasicVO> reqVO);

    @PostMapping(PREFIX+"getBusinessBasicInfoClient")
    @Operation(summary = "获取商务信息")
    ResponseVO<List<BusinessBasicClientVO>> getBusinessBasicInfoClient(@RequestParam("siteCode") String siteCode);

    @PostMapping("addMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<Boolean> addMediaInfo(@RequestBody List<MediaInfo> reqVO);

    @PostMapping("getMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<List<MediaInfo>> getMediaInfo();


    @PostMapping("delMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<Boolean> delMediaInfo(@RequestBody MediaInfo reqVO);
}

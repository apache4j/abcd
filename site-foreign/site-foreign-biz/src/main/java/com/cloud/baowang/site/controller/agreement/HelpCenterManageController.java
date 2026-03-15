package com.cloud.baowang.site.controller.agreement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.api.vo.site.agreement.MediaInfo;
import com.cloud.baowang.system.api.vo.site.agreement.SortVO;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.site.tutorial.OptionTypeConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "站点-信息配置管理")
@RestController
@RequestMapping("/site-helpCenter/api")
@AllArgsConstructor
public class HelpCenterManageController {
    private final SystemParamApi systemParamApi;
    private final HelpCenterManageApi helpCenterManageApi;
    private final LanguageManagerApi languageManagerApi;

    @PostMapping("getHelpCenterConfigList")
    @Operation(summary = "信息配置分页列表")
    public ResponseVO<Page<OptionTypeConfigVO>> getHelpCenterConfigList(@RequestBody PageVO pageVO) {
        Page<OptionTypeConfigVO> optionPage = helpCenterManageApi.getOptionPage(pageVO);
        return ResponseVO.success(optionPage);
    }

    @PostMapping("getSiteLanguageInfo")
    @Operation(summary = "获取站点语言列表")
    public ResponseVO<List<CodeValueVO>> getSiteLanguageInfo(){
        return ResponseVO.success(getSiteLanguage());
    }


    @PostMapping("addConfig")
    @Operation(summary = "编辑配置")
    public ResponseVO<Boolean> addConfig(@RequestBody @Validated i18nMessagesVO i18nMessagesVO) {
        i18nMessagesVO.setSiteCode(CurrReqUtils.getSiteCode());
        if (i18nMessagesVO.getCode().intValue()==CommonConstant.business_eleven ){
            if (i18nMessagesVO.getI18nFileUrl().isEmpty()){
                throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
            }
            addDownloadInfo(i18nMessagesVO);
        }else {
            if (i18nMessagesVO.getCode().intValue()==CommonConstant.business_ten ){
                if (i18nMessagesVO.getI18nFileUrl().isEmpty()){
                    throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
                }
            }else if (i18nMessagesVO.getI18nMessages().isEmpty()){
                throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
            }

        }
        return helpCenterManageApi.addConfig(i18nMessagesVO);

    }


    public void addDownloadInfo(i18nMessagesVO i18nMessagesVO) {
        helpCenterManageApi.addDownloadInfo(i18nMessagesVO);

    }
//    @GetMapping("getDownloadInfo")
//    @Operation(summary = "下载页配置回显")
//    public ResponseVO<i18nMessagesVO> getDownloadInfo() {
//        return helpCenterManageApi.getDownloadInfo();
//
//    }

    @GetMapping("getConfig")
    @Operation(summary = "配置查询")
    ResponseVO<i18nMessagesVO> getConfig(@RequestParam("code") Integer code) {
        if (code.intValue()==CommonConstant.business_eleven){
            return helpCenterManageApi.getDownloadInfo();
        }
        return helpCenterManageApi.getConfig(code);
    }

    public List<CodeValueVO> getSiteLanguage(){
        List<CodeValueVO> languageInfo = new ArrayList<>();
        ResponseVO<List<LanguageManagerListVO>> list = languageManagerApi.list();
        List<LanguageManagerListVO> data = list.getData();
        data.stream().forEach(e -> {
            languageInfo.add(CodeValueVO.builder().code(e.getCode()).value(e.getName()).build());
        });
        return languageInfo;
    }

    @GetMapping("getBusinessBasicInfos")
    @Operation(summary = "招商联系方式列表 skin4")
    ResponseVO<List<BusinessBasicVO>> getBusinessBasicInfos() {
        return helpCenterManageApi.getBusinessBasicInfos(CurrReqUtils.getSiteCode());
    }

    @PostMapping("updateBusinessBasicInfo")
    @Operation(summary = "修改商务信息 skin4")
    ResponseVO<Boolean> updateBusinessBasicInfo(@RequestBody BusinessBasicVO reqVO) {
        if (StringUtils.isEmpty(reqVO.getId())){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setUpdater(CurrReqUtils.getAccount());

        return helpCenterManageApi.updateBusinessBasicInfo(reqVO);
    }

    @PostMapping("addBusinessBasicInfo")
    @Operation(summary = "保存商务信息 skin4")
    ResponseVO<Boolean> addBusinessBasicInfo(@RequestBody BusinessBasicVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setUpdater(CurrReqUtils.getAccount());
        return helpCenterManageApi.addBusinessBasicInfo(reqVO);
    }


    @PostMapping("delBusinessBasicInfo")
    @Operation(summary = "删除商务信息 skin4")
    ResponseVO<Boolean> delBusinessBasicInfo(@RequestBody BusinessBasicVO reqVO) {
        if (StringUtils.isEmpty(reqVO.getId())){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setUpdater(CurrReqUtils.getAccount());
        return helpCenterManageApi.delBusinessBasicInfo(reqVO);
    }

    @PostMapping("sort")
    @Operation(summary = "商务排序 skin4")
    ResponseVO<Boolean> sort(@RequestBody List<BusinessBasicVO> reqVO) {
        return helpCenterManageApi.sort(reqVO);
    }



    @PostMapping("addMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<Boolean> addMediaInfo(@RequestBody List<MediaInfo> reqVO) {

        return helpCenterManageApi.addMediaInfo(reqVO);
    }



    @PostMapping("getMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<List<MediaInfo>> getMediaInfo() {
        return helpCenterManageApi.getMediaInfo();
    }

    @PostMapping("delMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<Boolean> delMediaInfo(@RequestBody MediaInfo reqVO) {

        return helpCenterManageApi.delMediaInfo(reqVO);
    }

}

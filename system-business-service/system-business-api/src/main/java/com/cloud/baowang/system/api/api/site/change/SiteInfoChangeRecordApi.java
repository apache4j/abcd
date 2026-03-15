package com.cloud.baowang.system.api.api.site.change;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.adminLogin.AdminUpdateVO;
import com.cloud.baowang.system.api.vo.adminLogin.GoogleKeyVO;
import com.cloud.baowang.system.api.vo.adminLogin.PasswordEditVO;
import com.cloud.baowang.system.api.vo.member.AccountSetParamVO;
import com.cloud.baowang.system.api.vo.member.AdminPasswordEditVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.member.NameUniqueVO;
import com.cloud.baowang.system.api.vo.site.SiteInfoChangeRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.admin.*;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteInfoChangeRecordApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - siteInfoChangeRecordApi")
public interface SiteInfoChangeRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/siteInfoChangeRecord/api/";

    @Operation(summary = "查询操作日志站点列表")
    @PostMapping(PREFIX + "querySiteInfo")
    Page<SiteInfoChangeRecordVO> querySiteInfoChangeRecord(@RequestBody SiteInfoChangeRequestVO siteInfoChangeRequestVO);

    @Operation(summary = "新增操作日志列表")
    @PostMapping(PREFIX + "addInfo")
    void addInfo(@RequestBody SiteInfoChangeRecordReqVO siteInfoChangeRecordReqVO);


    @Operation(summary = "获取操作变化列表")
    @PostMapping(PREFIX + "getJsonDifferenceVOInfo")
    List<JsonDifferenceVO> getJsonDifferenceList(@RequestBody SiteInfoChangeBodyVO siteInfoChangeBodyVO);

    @Operation(summary = "获取操作变化为特殊的存款和提款单独的转换列表")
    @PostMapping(PREFIX + "getJsonDifferenceListForRecharger")
    List<JsonDifferenceVO> getJsonDifferenceListForRecharger(@RequestBody SiteInfoChangeBodyVO siteInfoChangeBodyVO);

    @Operation(summary = "获取操作变化列表")
    @PostMapping(PREFIX + "addJsonDifferenceList")
    void addJsonDifferenceList(@RequestBody SiteInfoChangeRecordListReqVO vo);
}

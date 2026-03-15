package com.cloud.baowang.wallet.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogAllRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogPageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 保证金帐变记录相关接口
 */
@FeignClient(contextId = "remoteSiteSecurityChangeLogApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-保证金帐变记录api")
public interface SiteSecurityChangeLogApi {

    String PREFIX = ApiConstants.PREFIX + "/site/SiteSecurityChangeLog/";


    //分页查询接口
    @PostMapping(value = PREFIX + "listPage")
    @Operation(summary = "分页查询接口")
    ResponseVO<SiteSecurityChangeLogAllRespVO> listPage(@RequestBody SiteSecurityChangeLogPageReqVO securityChangeLogPageReqVO);


}

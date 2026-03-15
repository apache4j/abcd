package com.cloud.baowang.user.api.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteSiteMedalOperLogApi", value = ApiConstants.NAME)
@Tag(name = "RPC-勋章变更记录api")
public interface SiteMedalOperLogApi {

    String PREFIX = ApiConstants.PREFIX + "/siteMedalOperLog/api";

    @Operation(summary = "勋章变更记录分页查询")
    @PostMapping(value = PREFIX+"/listPage")
    ResponseVO<Page<SiteMedalOperLogRespVO>> listPage(@RequestBody SiteMedalOperLogReqVO siteMedalOperLogReqVO);

    /**
     * 操作项下拉框
     * @return
     */
    @Operation(summary = "操作项下拉框")
    @PostMapping(value = PREFIX+"/getMedalOperationEnums")
    ResponseVO<List<CodeValueVO>>  getMedalOperationEnums();


}

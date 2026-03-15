package com.cloud.baowang.user.api.api;

import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.SiteUserFbReTemplateReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFbReTemplateRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteUserFeedbackReplyTemplateApi", value = ApiConstants.NAME)
@Tag(name = "RPC 用户意见反馈回复模板 服务")
public interface SiteUserFeedbackReplyTemplateApi {

    String PREFIX = ApiConstants.PREFIX + "/userFeedbackReplayTemplate/api/";

    @Operation(summary = "新增模板")
    @PostMapping(value = PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody SiteUserFbReTemplateReqVO reqVO);


    @Operation(summary = "编辑模板")
    @PostMapping(value = PREFIX + "edit")
    ResponseVO<Boolean> edit(@RequestBody List<SiteUserFbReTemplateReqVO> reqVO);

    @Operation(summary = "新增模板")
    @PostMapping(value = PREFIX + "del")
    ResponseVO<Boolean> del(@RequestBody IdVO idVO);


    @Operation(summary = "模板列表")
    @PostMapping(value = PREFIX + "listTemplate")
    ResponseVO<List<SiteUserFbReTemplateRespVO>> listTemplate();

}

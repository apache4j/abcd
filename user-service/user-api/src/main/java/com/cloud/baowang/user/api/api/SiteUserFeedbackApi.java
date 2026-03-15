package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdSVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAddVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackDetailResVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAppPageReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAppResVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackReplyReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteUserFeedbackApi", value = ApiConstants.NAME)
@Tag(name = "RPC 用户意见反馈 服务")
public interface SiteUserFeedbackApi {

    String PREFIX = ApiConstants.PREFIX + "/userFeedback/api/";

    @Operation(summary = "提交意见建议")
    @PostMapping(value = PREFIX + "submit")
    ResponseVO<Boolean> submit(@RequestBody SiteUserFeedbackAddVO siteUserFeedbackAddVO);


    @Operation(summary = "用户意见建议列表")
    @PostMapping(value = PREFIX + "userPageList")
    ResponseVO<Page<SiteUserFeedbackAppResVO>> userPageList(@RequestBody SiteUserFeedbackAppPageReqVO reqVO);

    @Operation(summary = "根据id获取详情")
    @PostMapping(value = PREFIX + "detail")
    ResponseVO<List<SiteUserFeedbackDetailResVO>> detail(@RequestBody IdVO idVO);


    @Operation(summary = "反馈列表")
    @PostMapping(value = PREFIX + "feedbackList")
    ResponseVO<Page<SiteUserFeedbackSiteRespVO>> feedbackList(@RequestBody SiteUserFeedbackSiteReqVO reqVO);

    @Operation(summary = "锁单")
    @PostMapping(value = PREFIX + "lock")
    ResponseVO<SiteUserFeedbackSiteRespVO> lock(@RequestBody StatusListVO ids);

    @Operation(summary = "回复")
    @PostMapping(value = PREFIX + "reply")
    ResponseVO<Boolean> reply(@RequestBody SiteUserFeedbackReplyReqVO reqVO);

    @Operation(summary = "删除")
    @PostMapping(value = PREFIX + "del")
    ResponseVO<Boolean> del(@RequestBody IdVO idVO);

    @Operation(summary = "批量删除")
    @PostMapping(value = PREFIX + "batchDel")
    ResponseVO<Boolean> batchDel(@RequestBody IdSVO idsVO);


    @Operation(summary = "已读")
    @PostMapping(value = PREFIX + "read")
    void read(@RequestBody IdVO idVO);

    @Operation(summary = "回复")
    @PostMapping(value = PREFIX + "replyAgain")
    ResponseVO<Boolean> replyAgain(@RequestBody SiteUserFeedbackReplyReqVO reqVO);

    @Operation(summary = "site根据id获取详情")
    @PostMapping(value = PREFIX + "history")
    ResponseVO<List<SiteUserFeedbackDetailResVO>> history(@RequestBody IdVO idVO);
}

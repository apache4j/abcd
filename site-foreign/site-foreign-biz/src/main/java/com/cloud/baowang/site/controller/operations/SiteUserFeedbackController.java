package com.cloud.baowang.site.controller.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserFeedbackApi;
import com.cloud.baowang.user.api.api.SiteUserFeedbackReplyTemplateApi;
import com.cloud.baowang.user.api.vo.user.SiteUserFbReTemplateReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFbReTemplateRespVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackDetailResVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackReplyReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "运营-信息配置管理-会员意见反馈")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/site-user-feedback/api")
public class SiteUserFeedbackController {

    private final SiteUserFeedbackApi siteUserFeedbackApi;
    private final SiteUserFeedbackReplyTemplateApi templateApi;


    //  模板
    @PostMapping("addTemplate")
    @Operation(summary = "新增模板")
    public ResponseVO<Boolean> addTemplate(@Valid @RequestBody SiteUserFbReTemplateReqVO editVO) {
        return templateApi.add(editVO);
    }

    @PostMapping("editTemplate")
    @Operation(summary = "编辑模板")
    public ResponseVO<Boolean> editTemplate(@Valid @RequestBody List<SiteUserFbReTemplateReqVO> editVO) {
        return templateApi.edit(editVO);
    }

    @PostMapping("listTemplate")
    @Operation(summary = "模板列表")
    public ResponseVO<List<SiteUserFbReTemplateRespVO>> listTemplate() {
        return templateApi.listTemplate();
    }


    @PostMapping("delTemplate")
    @Operation(summary = "模板删除")
    public ResponseVO<Boolean> delTemplate(@RequestBody IdVO idVO) {
        return templateApi.del(idVO);
    }

    // 反馈

    @PostMapping("feedbackList")
    @Operation(summary = "反馈列表")
    public ResponseVO<Page<SiteUserFeedbackSiteRespVO>> feedbackList(@RequestBody SiteUserFeedbackSiteReqVO reqVO) {
        reqVO.setCurOperator(CurrReqUtils.getAccount());
        return siteUserFeedbackApi.feedbackList(reqVO);
    }

    @PostMapping("lock")
    @Operation(summary = "锁单")
    public ResponseVO<SiteUserFeedbackSiteRespVO> lock(@RequestBody StatusListVO statusVO) {
        return siteUserFeedbackApi.lock(statusVO);
    }

    @PostMapping("reply")
    @Operation(summary = "回复")
    public ResponseVO<Boolean> reply(@RequestBody SiteUserFeedbackReplyReqVO reqVO) {
        return siteUserFeedbackApi.reply(reqVO);
    }

    @PostMapping("replyAgain")
    @Operation(summary = "再次回复")
    public ResponseVO<Boolean> replyAgain(@RequestBody SiteUserFeedbackReplyReqVO reqVO) {
        return siteUserFeedbackApi.replyAgain(reqVO);
    }

    @PostMapping("history")
    @Operation(summary = "历史信息")
    public ResponseVO<List<SiteUserFeedbackDetailResVO>> history(@RequestBody IdVO idVO) {
        return siteUserFeedbackApi.history(idVO);
    }




}



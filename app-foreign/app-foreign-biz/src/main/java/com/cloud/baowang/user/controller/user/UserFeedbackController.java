package com.cloud.baowang.user.controller.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdSVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.SiteUserFeedbackApi;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAddVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackDetailResVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAppPageReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAppResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "意见反馈")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/userFeedback/api")
public class UserFeedbackController {

    private final SiteUserFeedbackApi siteUserFeedbackApi;
    private final SystemParamApi systemParamApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return systemParamApi.getSystemParamByType(CommonConstant.FEEDBACK_QUESTION_TYPE);
    }
    @Operation(summary = "提交意见反馈")
    @PostMapping("submit")
    private ResponseVO<Boolean> submit(@RequestBody SiteUserFeedbackAddVO siteUserFeedbackAddVO) {
        return siteUserFeedbackApi.submit(siteUserFeedbackAddVO);
    }

    @Operation(summary = "用户意见反馈列表")
    @PostMapping("pageList")
    private ResponseVO<Page<SiteUserFeedbackAppResVO>> pageList(@RequestBody SiteUserFeedbackAppPageReqVO reqVO) {
        return siteUserFeedbackApi.userPageList(reqVO);
    }

    @Operation(summary = "用户意见反馈历史详情")
    @PostMapping("detail")
    private ResponseVO<List<SiteUserFeedbackDetailResVO>> detail(@RequestBody IdVO idVO) {
        siteUserFeedbackApi.read(idVO);
        return siteUserFeedbackApi.detail(idVO);
    }

    @Operation(summary = "删除")
    @PostMapping("del")
    private ResponseVO<Boolean> del(@RequestBody IdVO idVO) {
        return siteUserFeedbackApi.del(idVO);
    }

    @Operation(summary = "批量删除")
    @PostMapping("batchDel")
    private ResponseVO<Boolean> batchDel(@RequestBody IdSVO idsVO) {
        return siteUserFeedbackApi.batchDel(idsVO);
    }


}

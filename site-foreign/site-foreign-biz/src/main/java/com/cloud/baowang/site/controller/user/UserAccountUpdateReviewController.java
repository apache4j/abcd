package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.service.user.UserAccountUpdateReviewService;
import com.cloud.baowang.user.api.vo.ReviewVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewDetailsResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewReqVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rudger
 * 时间：2023-05-07
 * 会员账号修改审核接口
 */
@Tag(name = "会员-会员账号修改审核")
@RestController
@RequestMapping(value = "/user_account_update_review/api")
@AllArgsConstructor
public class UserAccountUpdateReviewController {

    private final UserAccountUpdateReviewService userAccountUpdateReviewService;

    @Operation(summary = "分页查询会员账号修改审核")
    @PostMapping("/getUserAccountUpdateReview")
    public ResponseVO<Page<UserAccountUpdateReviewResVO>> getUserAccountUpdateReview(@RequestBody UserAccountUpdateReviewReqVO userAccountUpdateReviewReqVO) {
        userAccountUpdateReviewReqVO.setAdminName(CurrReqUtils.getAccount());

        userAccountUpdateReviewReqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        userAccountUpdateReviewReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(userAccountUpdateReviewService.getUserAccountUpdateReview(userAccountUpdateReviewReqVO));
    }

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = "/userLocks")
    public ResponseVO getLock(@Valid @RequestBody IdVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userAccountUpdateReviewService.getLock(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = "/fristReviewFails")
    public ResponseVO fristReviewFail(@Valid @RequestBody ReviewVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userAccountUpdateReviewService.fristReviewFail(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = "/ReviewSuccess")
    public ResponseVO fristReviewSuccess(@Valid @RequestBody ReviewVO vo) {
        String registerIp = CurrReqUtils.getReqIp();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        vo.setHandicapMode(CurrReqUtils.getHandicapMode());
        return userAccountUpdateReviewService.fristReviewSuccess(vo, registerIp, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "审核详情")
    @PostMapping(value = "/UpdateReviewDetails")
    public ResponseVO<UserAccountUpdateReviewDetailsResVO> getUpdateReviewDetails(@RequestBody IdVO vo) {
        vo.setDataDesensitization(CurrReqUtils.getDataDesensity());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userAccountUpdateReviewService.getUpdateReviewDetails(vo);
    }
}

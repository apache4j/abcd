/*
package com.cloud.baowang.admin.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.admin.service.UserReviewService;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.adminLogin.LoginAdmin;
import com.cloud.baowang.common.core.vo.user.ReviewVO;
import com.cloud.baowang.common.core.vo.user.StatusVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateVO;
import com.cloud.baowang.user.api.vo.UserReviewDetailsVO;
import com.cloud.baowang.user.api.vo.UserReviewPageVO;
import com.cloud.baowang.user.api.vo.UserReviewResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

*/
/**
 * 新增会员审核
 * @author: dami
 *//*

@Tag(name = "会员审核-新增会员审核")
@RestController
@RequestMapping("/user-review/api")
@AllArgsConstructor
public class UserReviewController {

    private final HttpServletRequest request;

    private final UserReviewService userReviewService;





    @Operation(summary = "会员审核-新增会员审核-锁单或解锁")
    @PostMapping(value = "/lock")
    public ResponseVO<Boolean> lock(@Valid @RequestBody StatusVO vo) {
        return userReviewService.lock(vo,CurrentRequestUtils.getCurrentUserNo(), CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "会员审核-新增会员审核-一审通过")
    @PostMapping(value = "/reviewSuccess")
    public ResponseVO<Boolean> reviewSuccess(@Valid @RequestBody ReviewVO vo) {
        String registerIp = IPUtil.getIp(request);
        String registerHost = request.getRemoteHost();
        return userReviewService.reviewSuccess(vo, registerIp, registerHost, CurrentRequestUtils.getCurrentUserNo(), CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "会员审核-新增会员审核-一审拒绝")
    @PostMapping(value = "/reviewFail")
    public ResponseVO<Boolean> reviewFail(@Valid @RequestBody ReviewVO vo) {
        return userReviewService.reviewFail(vo, CurrentRequestUtils.getCurrentUserNo(), CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation( summary = "会员审核-新增会员审核-搜索")
    @PostMapping(value = "/getReviewPage")
    public ResponseVO<Page<UserReviewResponseVO>> getReviewPage(@RequestBody UserReviewPageVO vo) {
        return userReviewService.getReviewPage(vo, CurrentRequestUtils.getCurrentUserAccount());
    }

    @Operation(summary = "会员审核-新增会员审核-审核详情")
    @PostMapping(value = "/getReviewDetails")
    public ResponseVO<UserReviewDetailsVO> getReviewDetails(@Valid @RequestBody IdVO vo) {
        LoginAdmin loginAdmin = CommonAdminUtils.getLoginAdmin();
        return userReviewService.getReviewDetails(vo, loginAdmin.getDataDesensitization());
    }

    @Operation(summary = "会员审核-新增会员审核-查询会员页签下的未审核数量角标")
    @PostMapping(value = "/getNotReviewNum")
    public ResponseVO<List<UserAccountUpdateVO>> getNotReviewNum() {
        return userReviewService.getNotReviewNum();
    }
}
*/

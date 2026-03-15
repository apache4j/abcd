package com.cloud.baowang.site.controller.userManualUpDown;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserManualUpReviewApi;
import com.cloud.baowang.wallet.api.vo.WalletReviewListVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserManualUpReviewPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserManualUpReviewResponseVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.UserUpReviewDetailsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: kimi
 */
@Tag(name = "会员人工加额审核")
@AllArgsConstructor
@RestController
@RequestMapping("/user-manual-up-review/api")
public class UserManualUpReviewController {

    private final SystemParamApi systemParamApi;
    private final UserManualUpReviewApi userManualUpReviewApi;

    @Operation(summary = "锁单状态下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return systemParamApi.getSystemParamByType(CommonConstant.USER_REVIEW_LOCK_STATUS);
    }

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = "/lock")
    public ResponseVO<Boolean> lock(@Valid @RequestBody StatusListVO vo) {
        return userManualUpReviewApi.lock(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审通过-批量审核")
    @PostMapping(value = "/oneReviewSuccess")
    public ResponseVO<Boolean> oneReviewSuccess(@Valid @RequestBody WalletReviewListVO vo) {
        return userManualUpReviewApi.oneReviewSuccess(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value = "/oneReviewFail")
    public ResponseVO<Boolean> oneReviewFail(@Valid @RequestBody WalletReviewListVO vo) {
        return userManualUpReviewApi.oneReviewFail(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }

    @Operation(summary = "审核列表")
    @PostMapping(value = "/getUpReviewPage")
    public ResponseVO<Page<UserManualUpReviewResponseVO>> getUpReviewPage(@Valid @RequestBody UserManualUpReviewPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return ResponseVO.success(userManualUpReviewApi.getUpReviewPage(vo, CurrReqUtils.getAccount()));
    }

    @Operation(summary = "审核详情")
    @PostMapping(value = "/getUpReviewDetails")
    public ResponseVO<UserUpReviewDetailsVO> getUpReviewDetails(@Valid @RequestBody IdVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userManualUpReviewApi.getUpReviewDetails(vo);
    }

}

package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.withdraw.UserWithdrawRecordController;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordNotCollectInfoPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawRecordPagesVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Tag(name = "资金-会员资金记录-会员提款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/detail/user-withdraw-record/api")
public class DetailUserWithdrawRecordController {

    private final UserWithdrawRecordController userWithdrawRecordController;


    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        return userWithdrawRecordController.getDownBox();
    }

    @Operation(summary = "提款记录列表")
    @PostMapping(value = "/withdrawalRecordPageList")
    public ResponseVO<UserWithdrawRecordPagesVO> withdrawalRecordPageList(@RequestBody UserWithdrawalRecordRequestVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userWithdrawRecordController.withdrawalRecordPageList(vo);
    }

    @Operation(summary = "提款记录不包含提款信息列表")
    @PostMapping(value = "/withdrawalNotCollectInfoRecordPageList")
    public ResponseVO<UserWithdrawRecordNotCollectInfoPagesVO> withdrawalNotCollectInfoRecordPageList(@RequestBody UserWithdrawalRecordRequestVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userWithdrawRecordController.withdrawalNotCollectInfoRecordPageList(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserWithdrawalRecordRequestVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userWithdrawRecordController.export(vo);
    }

    @Operation(summary = "会员提款记录总数")
    @PostMapping(value = "/getWithDrawalRecord")
    public ResponseVO<UserDepositRecordRespVO> getWithDrawalRecord(@RequestBody UserWithdrawalRecordRequestVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userWithdrawRecordController.getWithDrawalRecord(vo);
    }
}

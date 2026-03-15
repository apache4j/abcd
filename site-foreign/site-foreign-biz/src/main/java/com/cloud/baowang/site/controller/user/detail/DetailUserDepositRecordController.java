package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.deposit.UserDepositRecordController;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Tag(name = "会员存款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/detail/user-deposit-record/api")
public class DetailUserDepositRecordController {

    private final UserDepositRecordController userDepositRecordController;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        return userDepositRecordController.getDownBox();
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getUserDepositRecordPage")
    public ResponseVO<UserDepositRecordPageRespVO> getUserDepositRecordPage(@RequestBody UserDepositRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userDepositRecordController.getUserDepositRecordPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserDepositRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userDepositRecordController.export(vo);

    }

    @Operation(summary = "会员存款记录总数")
    @PostMapping(value = "/getUserDepositRecord")
    public ResponseVO<UserDepositRecordRespVO> getUserDepositRecord(@RequestBody UserDepositRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userDepositRecordController.getUserDepositRecord(vo);
    }

}



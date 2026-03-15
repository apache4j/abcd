package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.userManualUpDown.UserManualUpRecordController;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: kimi
 */
@Slf4j
@AllArgsConstructor
@Tag(name = "会员人工加额记录")
@RestController
@RequestMapping("/detail/user-manual-up-record/api")
public class DetailUserManualUpRecordController {
    private final UserManualUpRecordController userManualUpRecordController;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        return userManualUpRecordController.getDownBox();
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getUpRecordPage")
    public ResponseVO<UserManualUpRecordResult> getUpRecordPage(@RequestBody UserManualUpRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userManualUpRecordController.getUpRecordPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserManualUpRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userManualUpRecordController.export(vo);
    }
}

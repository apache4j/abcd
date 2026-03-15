package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.platformCoinManualUpDown.UserPlatformCoinManualUpRecordController;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: kimi
 */
@Slf4j
@AllArgsConstructor
@Tag(name = "资金-会员资金记录-会员平台币上分记录")
@RestController
@RequestMapping("/detail/user-platform-coin-manual-up-record/api")
public class DetailUserPlatformCoinManualUpRecordController {
    private final UserPlatformCoinManualUpRecordController userPlatformCoinManualUpRecordController;

    @Operation(summary = "下拉框")
    @GetMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        return userPlatformCoinManualUpRecordController.getDownBox();
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getUpRecordPage")
    public ResponseVO<UserPlatformCoinManualUpRecordResult> getUpRecordPage(@RequestBody UserPlatformCoinManualUpRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userPlatformCoinManualUpRecordController.getUpRecordPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserPlatformCoinManualUpRecordPageVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userPlatformCoinManualUpRecordController.export(vo);
    }
}

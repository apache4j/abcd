package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.platformCoinManualUpDown.UserPlatformCoinManualDownRecordController;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @author:qiqi
 */
@Tag(name = "资金-会员资金记录-会员平台币下分记录")
@RestController
@RequestMapping("/detail/user-platform-coin-manual-down-record/api")
@AllArgsConstructor
@Slf4j
public class DetailUserPlatformCoinManualDownRecordController {
    private final UserPlatformCoinManualDownRecordController platformCoinManualDownRecordController;

    @GetMapping("getDownBox")
    @Operation(summary = "获取发起申请下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getActivityTemplateDownBox() {
        return platformCoinManualDownRecordController.getActivityTemplateDownBox();
    }

    @PostMapping("listUserManualDownRecordPage")
    @Operation(summary = "分页列表")
    public ResponseVO<UserPlatformCoinManualDownRecordResponseVO> listUserManualDownRecordPage(@RequestBody UserPlatformCoinManualDownRecordRequestVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return platformCoinManualDownRecordController.listUserManualDownRecordPage(vo);
    }

    @PostMapping("export")
    @Operation(summary = "记录导出")
    public ResponseVO<?> export(@RequestBody UserPlatformCoinManualDownRecordRequestVO vo, HttpServletResponse response) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return platformCoinManualDownRecordController.export(vo, response);
    }


}

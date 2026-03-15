package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.userManualUpDown.UserManualDownRecordController;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordResponseVO;
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
@Tag(name = "资金-会员资金记录-会员人工扣除记录")
@RestController
@RequestMapping("/detail/user-manual-down-record/api")
@AllArgsConstructor
@Slf4j
public class DetailUserManualDownRecordController {
    private final UserManualDownRecordController userManualDownRecordController;

    @GetMapping("getDownBox")
    @Operation(summary = "获取发起申请下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getActivityTemplateDownBox() {
        return userManualDownRecordController.getActivityTemplateDownBox();
    }

    @PostMapping("listUserManualDownRecordPage")
    @Operation(summary = "会员人工减额记录分页列表")
    public ResponseVO<UserManualDownRecordResponseVO> listUserManualDownRecordPage(@RequestBody UserManualDownRecordRequestVO userCoinRecordRequestVO) {
        if (ObjectUtil.isEmpty(userCoinRecordRequestVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userManualDownRecordController.listUserManualDownRecordPage(userCoinRecordRequestVO);
    }

    @PostMapping("export")
    @Operation(summary = "会员人工扣除记录导出")
    public ResponseVO<?> export(@RequestBody UserManualDownRecordRequestVO vo, HttpServletResponse response) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userManualDownRecordController.export(vo, response);

    }


}

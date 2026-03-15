package com.cloud.baowang.site.controller.user.detail;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.site.controller.userCoin.UserCoinRecordController;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiqi
 */
@RestController
@Tag(name = "资金-会员资金记录-会员主货币账变记录")
@RequestMapping("/detail/user-coin-record/api")
@Slf4j
@AllArgsConstructor
public class DetailUserCoinRecordController {


    private final UserCoinRecordController userCoinRecordController;


    @PostMapping("listUserCoinRecordPage")
    @Operation(summary = "会员账变记录分页列表")
    public ResponseVO<UserCoinRecordResponseVO> listUserCoinRecordPage(@RequestBody UserCoinRecordRequestVO userCoinRecordRequestVO) {
        if (ObjectUtil.isEmpty(userCoinRecordRequestVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userCoinRecordController.listUserCoinRecordPage(userCoinRecordRequestVO);
    }


    @PostMapping("export")
    @Operation(summary = "会员账变记录导出")
    public ResponseVO<?> export(@RequestBody UserCoinRecordRequestVO vo) {
        if (ObjectUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EMPTY);
        }
        return userCoinRecordController.export(vo);
    }

}

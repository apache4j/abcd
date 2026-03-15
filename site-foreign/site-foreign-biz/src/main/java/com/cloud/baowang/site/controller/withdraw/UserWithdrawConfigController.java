package com.cloud.baowang.site.controller.withdraw;


import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserWithdrawConfigApi;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigVO;
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
 * @author qiqi
 */

@RestController
@Tag(name = "会员提款设置管理")
@RequestMapping("/user-withdraw-config/api")
@AllArgsConstructor
public class UserWithdrawConfigController {

    private final UserWithdrawConfigApi userWithdrawConfigApi;


    @Operation(summary ="会员提款配置列表")
    @PostMapping("/listUserWithdrawConfig")
    public ResponseVO<List<UserWithdrawConfigVO>> listUserWithdrawConfig(@RequestBody UserWithdrawConfigRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userWithdrawConfigApi.listUserWithdrawConfig(vo);
    }

    @Operation(summary = "会员提款配置详情")
    @PostMapping("/detailUserWithdrawConfig")
    public ResponseVO<UserWithdrawConfigVO> detailUserWithdrawConfig(@Valid @RequestBody IdVO idVO) {
        return userWithdrawConfigApi.detailUserWithdrawConfig(idVO);
    }

    @Operation(summary = "会员提款配置添加")
    @PostMapping("/addUserWithdrawConfig")
    public ResponseVO<Integer> addUserWithdrawConfig(@Valid @RequestBody UserWithdrawConfigAddOrUpdateVO vo) {
        vo.setCreator(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userWithdrawConfigApi.addUserWithdrawConfig(vo);
    }

    @Operation(summary = "会员提款配置修改")
    @PostMapping("/updateUserWithdrawConfig")
    public ResponseVO<Integer> updateUserWithdrawConfig(@Valid @RequestBody UserWithdrawConfigAddOrUpdateVO userWithdrawConfigUpdateVO) {
        userWithdrawConfigUpdateVO.setUpdater(CurrReqUtils.getAccount());
        return userWithdrawConfigApi.updateUserWithdrawConfig(userWithdrawConfigUpdateVO);
    }



}

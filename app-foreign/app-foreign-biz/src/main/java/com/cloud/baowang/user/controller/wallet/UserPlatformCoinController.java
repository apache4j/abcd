package com.cloud.baowang.user.controller.wallet;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformBalanceRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinApi;
import com.cloud.baowang.wallet.api.api.UserPlatformTransferApi;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRecordReqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserPlatformTransferRespVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 用户平台币相关
 * @Author: Ford
 * @Date: 2024/10/16 09:57
 * @Version: V1.0
 **/
@Slf4j
@Tag(name = "客户端-我的-用户平台币")
@RestController
@AllArgsConstructor
@RequestMapping("/userPlatformCoin/api")
public class UserPlatformCoinController {

    private final UserPlatformCoinApi userPlatformCoinApi;

    private final UserPlatformTransferApi userPlatformTransferApi;

    private final UserInfoApi userInfoApi;

    @Operation(summary = "获取余额、转换汇率")
    @PostMapping("getUserPlatformBalance")
    public ResponseVO<UserPlatformBalanceRespVO> getUserPlatformBalance() {
        String userAccount = CurrReqUtils.getAccount();
        UserCoinQueryVO userCoinQueryVO=new UserCoinQueryVO();
        userCoinQueryVO.setUserId(CurrReqUtils.getOneId());
        userCoinQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        userCoinQueryVO.setUserAccount(userAccount);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
        userCoinQueryVO.setCurrencyCode(userInfoVO.getMainCurrency());
        //获取余额、转换汇率
        return  ResponseVO.success(userPlatformCoinApi.getUserPlatformBalance(userCoinQueryVO));
    }


    @Operation(summary = "平台币兑换")
    @PostMapping("transfer")
    public ResponseVO<String> transfer(@RequestBody @Validated UserPlatformTransferVO userPlatformTransferVO) {
        String userAccount = CurrReqUtils.getAccount();
        userPlatformTransferVO.setUserAccount(userAccount);
        userPlatformTransferVO.setUserId(CurrReqUtils.getOneId());
        userPlatformTransferVO.setSiteCode(CurrReqUtils.getSiteCode());
        log.info("平台币兑换开始:{}",userPlatformTransferVO);
        //创建存款记录
        return  userPlatformCoinApi.transfer(userPlatformTransferVO);
    }

    @PostMapping("platformTransferRecord")
    @Operation(summary = "会员平台币兑换记录")
    public ResponseVO<Page<ClientUserPlatformTransferRespVO>> platformTransferRecord(@RequestBody ClientUserPlatformTransferRecordReqVO vo) {
        int between = TimeZoneUtils.getDaysBetweenInclusive(vo.getBeginTime(), vo.getEndTime(), CurrReqUtils.getTimezone());
        if (between > 31) {
            return ResponseVO.fail(ResultCode.QUERY_THIRTY_RANGE);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUserId(CurrReqUtils.getOneId());
        return userPlatformTransferApi.platformTransferRecord(vo);
    }

}

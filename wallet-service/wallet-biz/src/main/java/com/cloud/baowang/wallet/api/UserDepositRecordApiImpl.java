package com.cloud.baowang.wallet.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.wallet.api.api.UserDepositRecordApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositRecordParam;
import com.cloud.baowang.wallet.service.UserDepositRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserDepositRecordApiImpl implements UserDepositRecordApi {

    private UserDepositRecordService userDepositRecordService;
    private UserInfoApi userInfoApi;

    @Override
    public UserDepositRecordPageRespVO getUserDepositRecordPage(UserDepositRecordPageVO vo) {
        return userDepositRecordService.getUserDepositRecordPage(vo);
    }

    @Override
    public ResponseVO<Long> getUserDepositRecordPageCount(UserDepositRecordPageVO vo) {
        return userDepositRecordService.getUserDepositRecordPageCount(vo);
    }

    /**
     * 代理客户端-存款记录
     */
    @Override
    public ResponseVO<Page<DepositRecordResponseVO>> depositRecord(UserDepositRecordParam vo) {
        // 仅支持最近两个月的存款记录查询
        /*long time = DateUtil.beginOfMonth(DateUtil.lastMonth()).getTime();
        if (vo.getStartTime() < time) {
            return ResponseVO.fail(ResultCode.DEPOSIT_RECORD_2MONTH);
        }*/

        // 无该用户信息
        if (StrUtil.isNotEmpty(vo.getUserAccount())) {
            GetByUserAccountVO userInfo = userInfoApi.getByUserAccountAndSiteCode(vo.getUserAccount(),vo.getSiteCode());
            if (null == userInfo) {
                return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST_);
            }
        }

        return ResponseVO.success(userDepositRecordService.agentUserDepositRecord(vo));
    }

    /**
     * 存款记录-下拉框
     */
    @Override
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        return userDepositRecordService.getDownBox();
    }

    @Override
    public UserDepositRecordRespVO getUserDepositRecord(UserDepositRecordPageVO vo) {
        return userDepositRecordService.getUserDepositRecord(vo);
    }


}

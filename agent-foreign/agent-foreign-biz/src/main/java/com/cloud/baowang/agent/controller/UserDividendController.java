package com.cloud.baowang.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminResVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserDividendApi;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendPageVO;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author : qiqi
 */
@Tag(name = "下级信息-用户红利")
@RestController
@RequestMapping("/client-agent-dividend/api")
@AllArgsConstructor
public class UserDividendController {

    private final UserInfoApi userInfoApi;

    private final AgentInfoApi agentInfoApi;

    private final UserDividendApi userDividendApi;

    @Operation(summary = "用户红利分页查询")
    @PostMapping("/userDividendPage")
    public ResponseVO<Page<UserDividendPageVO>> userDividendPage(@Valid @RequestBody UserDividendRequestVO requestVO) {
        if (ObjectUtil.isEmpty(requestVO.getBeginTime())) {
            throw new BaowangDefaultException(ResultCode.TIME_MUST_CHOOSE);
        }
        if (ObjectUtil.isNotEmpty(requestVO.getBeginTime()) &&
                OrderRecordAdminResVO.checkTime(requestVO.getBeginTime(), requestVO.getEndTime())) {
            throw new BaowangDefaultException(ResultCode.FORTY_DAY_OVER);
        }
        // 校验用户
        if (ObjectUtil.isNotEmpty(requestVO.getUserAccount()) && UserChecker
                .checkUserAccount(requestVO.getUserAccount())) {
            return ResponseVO.fail(ResultCode.USER_ACCOUNT_ERROR);
        }
        String agentAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        if (null == agentAccount) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        requestVO.setAgentIds(agentInfoApi.getSubAgentIdList(CurrReqUtils.getOneId()));
        if (ObjectUtil.isNotEmpty(requestVO.getUserAccount())) {
            Boolean userIsExists = userInfoApi.getUserInfoIsExists(requestVO.getAgentIds(),
                    requestVO.getUserAccount(), siteCode);
            if (userIsExists) {
                return ResponseVO.fail(ResultCode.USER_ACCOUNT_NOT_EXIST);
            }
        }
        requestVO.setSiteCode(siteCode);
        return userDividendApi.userDividendPage(requestVO);
    }
}

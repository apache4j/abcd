package com.cloud.baowang.report.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.enums.ApiConstants;
import com.cloud.baowang.report.api.vo.userwinlose.ClickUserAccountPageVO;
import com.cloud.baowang.report.api.vo.userwinlose.ClickUserAccountResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentReqVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListPageCondVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseListResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLosePageVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "remoteUserWinLoseApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员盈亏")
public interface UserWinLoseApi {

    String PREFIX = ApiConstants.PREFIX + "/userWinLoseApi/api";


    @Operation(summary = "会员会员盈亏列表")
    @PostMapping(value = PREFIX + "/getUserWinLosePage")
    ResponseVO<UserWinLoseResult> getUserWinLosePage(@RequestBody UserWinLosePageVO vo);

    @Operation(summary = "查询会员登录记录-总记录数")
    @PostMapping(value = PREFIX + "getTotalCount")
    Long getTotalCount(@RequestBody UserWinLosePageVO vo);


    @Operation(summary = "会员会员盈亏列表")
    @PostMapping(value = PREFIX + "/getUserWinLosePageCount")
    ResponseVO<Long> getUserWinLosePageCount(@RequestBody UserWinLosePageVO vo);

    @Operation(summary = "点击会员账号点击会员账号")
    @PostMapping(value = PREFIX + "/clickUserAccount")
    ResponseVO<Page<ClickUserAccountResponseVO>> clickUserAccount(@RequestBody ClickUserAccountPageVO vo);

    @Operation(summary = "查询代理下会员输赢金额 总计")
    @PostMapping(value = PREFIX + "/queryByTimeAndAgent")
    ResponseVO<List<UserWinLoseAgentVO>> queryByTimeAndAgent(@RequestBody UserWinLoseAgentReqVO vo);

    @Operation(summary = "会员盈亏明细查询")
    @PostMapping(value = PREFIX + "/queryListByParam")
    List<UserWinLoseResponseVO> queryListByParam(@RequestBody UserWinLoseAgentReqVO vo);

    @Operation(summary = "会员会员盈亏分页查询")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<Page<UserWinLoseListResponseVO>> listPage(@RequestBody UserWinLoseListPageCondVO vo);

    @Operation(summary = "投注人数统计")
    @PostMapping(value = PREFIX + "/getBetUserNum")
    Long getBetUserNum(UserWinLoseAgentReqVO userWinLoseAgentReqVO);
}

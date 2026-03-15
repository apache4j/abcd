package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserCoinRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userCoinRecord")
public interface
UserCoinRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userCoinRecord/api/";

    @PostMapping("listUserCoinRecordPage")
    @Operation(summary = "会员账变记录分页列表")
    public ResponseVO<UserCoinRecordResponseVO> listUserCoinRecordPage(@RequestBody UserCoinRecordRequestVO userCoinRecordRequestVO);

    @PostMapping("userCoinRecordPageCount")
    @Operation(summary = "会员账变总记录数")
    ResponseVO<Long> userCoinRecordPageCount(@RequestBody UserCoinRecordRequestVO userCoinRecordRequestVO);


    @PostMapping("getUserCoinRecords")
    @Operation(summary = "获取会员账变记录")
    ResponseVO<List<UserCoinRecordVO>> getUserCoinRecords(@RequestBody UserCoinRecordRequestVO userCoinRecordRequestVO);

    @PostMapping(PREFIX + "callFriendRechargeCount")
    @Operation(summary = "获取会员账变记录")
    Long callFriendRechargeCount(@RequestBody UserCoinRecordCallFriendsRequestVO requestVO);

    @PostMapping(PREFIX + "getOrderNoByOrders")
    @Operation(summary = "获取订单列表")
    List<String> getOrderNoByOrders(@RequestBody List<String> orders);

    @PostMapping(PREFIX + "winLoseRecalculateMainPage")
    @Operation(summary = "重算获取记录")
    Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(@RequestBody WinLoseRecalculateReqWalletVO reqWalletVO);


    @PostMapping(PREFIX + "getUserCoinRecord")
    @Operation(summary = "根据订单号，会员ID，业务类型")
    UserCoinRecordVO getUserCoinRecord(@RequestParam("orderNo") String orderNo, @RequestParam("userId") String userId, @RequestParam("balanceType") String balanceType);

    @PostMapping(PREFIX + "getUserCoinRecordPG")
    @Operation(summary = "根据订单号，会员ID，业务类型")
    List<UserCoinRecordVO> getUserCoinRecordPG(@RequestParam("orderNo") String orderNo, @RequestParam("userId") String userId, @RequestParam("balanceType") String balanceType);

    @PostMapping(PREFIX + "getUserCoinRecordsForEVO")
    @Operation(summary = "根据remark，会员ID")
    List<UserCoinRecordVO> getUserCoinRecordsForEVO(@RequestParam("remark") String remark, @RequestParam("userId") String userId);

    @PostMapping("getJDBUserCoinRecords")
    @Operation(summary = "获取会员账变记录")
    ResponseVO<UserCoinRecordVO> getJDBUserCoinRecords(@RequestBody JDBUserCoinRecordVO jdbUserCoinRecordVO);

    @PostMapping("getJDBBetRecords")
    @Operation(summary = "获取会员账变记录")
    ResponseVO<List<UserCoinRecordVO>> getJDBBetRecords(@RequestBody JDBUserCoinRecordVO jdbUserCoinRecordVO);

}

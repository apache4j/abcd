package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteUserPlatformCoinRecord",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - userPlatformCoinRecord")
public interface
UserPlatformCoinRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userPlatformCoinRecord/api/";

    @PostMapping("listUserPlatformCoinRecordPage")
    @Operation(summary = "会员账变记录分页列表")
    public ResponseVO<UserPlatformCoinRecordResponseVO> listUserPlatformCoinRecordPage(@RequestBody UserPlatformCoinRecordRequestVO userPlatformCoinRecordRequestVO);

    @PostMapping("userPlatformCoinRecordPageCount")
    @Operation(summary = "会员账变总记录数")
    ResponseVO<Long> userPlatformCoinRecordPageCount(@RequestBody UserPlatformCoinRecordRequestVO userPlatformCoinRecordRequestVO);

    @PostMapping( PREFIX + "winLoseRecalculateMainPage")
    @Operation(summary = "重算获取记录")
    Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(@RequestBody WinLoseRecalculateReqWalletVO reqWalletVO);

}

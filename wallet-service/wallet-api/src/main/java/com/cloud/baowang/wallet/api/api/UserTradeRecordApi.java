package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteUserTradeRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存款取款订单状态处理 服务")
public interface UserTradeRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userTradeRecordApi/api/";


    @Operation(summary = "客户端会员交易明细")
    @PostMapping(value = PREFIX+"tradeRecordList")
    Page<UserTradeRecordResponseVO> tradeRecordList(@RequestBody UserTradeRecordRequestVO vo);

    @Operation(summary = "客户端会员交易明细详情")
    @PostMapping(value = PREFIX+"tradeRecordDetail")
    UserTradeRecordDetailResponseVO tradeRecordDetail(@RequestBody UserTradeRecordDetailRequestVO vo);
}

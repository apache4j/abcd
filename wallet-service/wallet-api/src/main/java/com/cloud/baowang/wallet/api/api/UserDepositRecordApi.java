package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageRespVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserDepositRecordParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteUserDepositRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员存款记录 服务")
public interface UserDepositRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/userDepositRecord/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "getUserDepositRecordPage")
    UserDepositRecordPageRespVO getUserDepositRecordPage(@RequestBody UserDepositRecordPageVO vo);

    @Operation(summary = "总记录数")
    @PostMapping(value = PREFIX + "getUserDepositRecordPageCount")
    ResponseVO<Long> getUserDepositRecordPageCount(@RequestBody UserDepositRecordPageVO vo);

    @Operation(summary = "代理客户端-存款记录")
    @PostMapping(value = PREFIX + "depositRecord")
    ResponseVO<Page<DepositRecordResponseVO>> depositRecord(@RequestBody UserDepositRecordParam vo);

    @Operation(summary = "存款记录-下拉框")
    @PostMapping(value = PREFIX + "getDownBox")
    ResponseVO<Map<String, List<CodeValueVO>>> getDownBox();


    @Operation(summary = "会员存款记录总数")
    @PostMapping(value = PREFIX + "getUserDepositRecord")
    UserDepositRecordRespVO getUserDepositRecord(@RequestBody UserDepositRecordPageVO vo);
}

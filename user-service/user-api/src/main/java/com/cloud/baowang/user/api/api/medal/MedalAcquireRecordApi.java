package com.cloud.baowang.user.api.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteMedalAcquireRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC-勋章获取记录api")
public interface MedalAcquireRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/medalAcquireRecord/api";

    @Operation(summary = "勋章获取记录分页查询")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<Page<MedalAcquireRecordRespVO>> listPage(@RequestBody MedalAcquireRecordReqVO medalAcquireRecordReqVO);


    @Operation(summary = "勋章获取记录查询")
    @PostMapping(value = PREFIX + "/listByCond")
    ResponseVO<List<MedalAcquireRecordRespVO>> listByCond(@RequestBody MedalAcquireRecordCondReqVO medalAcquireRecordCondReqVO);


    @Operation(summary = "勋章获取记录数量查询")
    @PostMapping(value = PREFIX + "/countByCond")
    ResponseVO<Long> countByCond(@RequestBody MedalAcquireRecordCondReqVO medalAcquireRecordCondReqVO);

    @PostMapping(value = PREFIX + "getRecordByUserAccountAndMedalType")
    @Operation(summary = "根据会员账号,勋章类型,批量查询已领取记录")
    ResponseVO<List<MedalAcquireRecordRespVO>> getRecordByUserAccountAndMedalType(@RequestBody List<String> userAccount,
                                                                                  @RequestParam("siteCode") String siteCode,
                                                                                  @RequestParam("medalType") String medalCode);
}

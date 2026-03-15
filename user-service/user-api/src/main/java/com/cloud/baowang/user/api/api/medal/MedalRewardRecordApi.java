package com.cloud.baowang.user.api.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteMedalRewardRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC-宝箱获取记录api")
public interface MedalRewardRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/medalRewardRecord/api";

    @Operation(summary = "宝箱获取记录分页查询")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<Page<MedalRewardRecordRespVO>> listPage(@RequestBody MedalRewardRecordReqVO medalRewardRecordReqVO);


    @Operation(summary = "宝箱获取记录查询")
    @PostMapping(value = PREFIX + "/listByCond")
    ResponseVO<List<MedalRewardRecordRespVO>> listByCond(@RequestBody MedalRewardRecordCondReqVO medalRewardRecordCondReqVO);


    @Operation(summary = "宝箱获取记录数量查询")
    @PostMapping(value = PREFIX + "/countByCond")
    ResponseVO<Long> countByCond(@RequestBody MedalRewardRecordCondReqVO medalRewardRecordCondReqVO);

    @PostMapping(value = PREFIX + "getRecordByUserAccountAndMedalType")
    @Operation(summary = "根据会员账号,宝箱类型,批量查询已领取记录")
    ResponseVO<List<MedalRewardRecordRespVO>> getRecordByUserAccountAndMedalType(@RequestBody List<String> userAccount,
                                                                                  @RequestParam("siteCode") String siteCode,
                                                                                  @RequestParam("medalType") String medalCode);
}

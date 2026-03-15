package com.cloud.baowang.user.api.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigBatchUpdateReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteMedalRewardConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC-勋章奖励配置api")
public interface MedalRewardConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/medalRewardConfig/api";

    @Operation(summary = "勋章奖励配置分页查询")
    @PostMapping(value = PREFIX+"/listPage")
    ResponseVO<Page<MedalRewardConfigRespVO>> listPage(@RequestBody MedalRewardConfigReqVO medalRewardConfigReqVO);

    @Operation(summary = "勋章奖励配置初始化")
    @PostMapping(value = PREFIX+"/init/{siteCode}")
    ResponseVO<Boolean> init(@PathVariable("siteCode")String siteCode);

    @Operation(summary = "勋章奖励配置批量保存")
    @PostMapping(value = PREFIX+"/batchSave")
    ResponseVO<Void> batchSave(@RequestBody MedalRewardConfigBatchUpdateReqVO medalRewardConfigBatchUpdateReqVO);

}

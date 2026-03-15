package com.cloud.baowang.user.api.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordReqVo;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordVo;
import com.cloud.baowang.user.api.vo.vip.UserVipRewardReqVO;
import com.cloud.baowang.user.api.vo.vip.VIPSendRewardReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "vipAwardV2Api",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - vipAwardV2Api")
public interface VipAwardV2Api {
    String PREFIX = ApiConstants.PREFIX + "/vipAwardApi/v2/api/";



    @Operation(summary = "VIP等级生日奖励")
    @PostMapping(value = PREFIX + "birthDayAward")
    ResponseVO<Boolean> birthDayAward(@RequestBody VIPSendRewardReqVO vo) ;

    @Operation(summary = "VIP等级周奖励")
    @PostMapping(value = PREFIX + "weekAward")
    ResponseVO<Boolean> weekAward(@RequestBody VIPSendRewardReqVO vo) ;

}

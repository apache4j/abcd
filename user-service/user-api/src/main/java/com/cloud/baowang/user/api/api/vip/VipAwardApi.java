package com.cloud.baowang.user.api.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordReqVo;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "vipAwardApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - vipAwardApi")
public interface VipAwardApi {
    String PREFIX = ApiConstants.PREFIX + "/vipAwardApi/api/";

    @Operation(summary = "VIP奖励记录查询")
    @PostMapping(value = PREFIX + "queryVIPAwardList")
    ResponseVO<Page<SiteVipAwardRecordVo>> queryVIPAwardList(@RequestBody SiteVipAwardRecordReqVo pageVO);


}

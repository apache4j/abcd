package com.cloud.baowang.user.api.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "siteVipChangeRecordCn",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - siteVipChangeRecordCn")
public interface SiteVipChangeRecordCnApi {
    String PREFIX = ApiConstants.PREFIX + "/siteVipChangeRecordCn/api/";

    @Operation(summary = "VIP站点后台大陆盘vip等级变更记录")
    @PostMapping(value = PREFIX + "getList")
    ResponseVO<Page<SiteVipChangeRecordCnVO>> getList(@RequestBody SiteVipChangeRecordCnReqVO vo);

    @Operation(summary = "按照时间查询会员所在VIP等级")
    @PostMapping(value = PREFIX + "findVIPCodeByDay")
    Integer findVIPCodeByDay(@RequestParam("userId") String userId, @RequestParam("startDayTime") long startDayTime, @RequestParam("endDayTime") long endDayTime);
}

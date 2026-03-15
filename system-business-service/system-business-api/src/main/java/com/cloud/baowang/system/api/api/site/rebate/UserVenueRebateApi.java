package com.cloud.baowang.system.api.api.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.rebate.ReportUserRebateInitVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.rebate.user.RebateListVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditRspVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateDetailsRspVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "userVenueRebateApi", value = ApiConstants.NAME)
@Tag(name = "用户 返水 服务 - userVenueRebateApi")
public interface UserVenueRebateApi {

    String PREFIX = ApiConstants.PREFIX + "/user-venue-rebate/api/";


    @PostMapping(PREFIX+"userRebatePage")
    @Operation(summary = "返水审核列表")
    ResponseVO<Page<UserRebateAuditRspVO>> userRebatePage(@RequestBody UserRebateAuditQueryVO vo);

    @PostMapping(PREFIX+"userRebateDetails")
    @Operation(summary = "返水明细")
    ResponseVO<List<UserRebateDetailsRspVO>> userRebateDetails(@RequestBody UserRebateAuditQueryVO vo);


    @PostMapping(PREFIX+"userRebateRecordPage")
    @Operation(summary = "返水记录列表")
    ResponseVO<Page<UserRebateAuditRspVO>> userRebateRecordPage(@RequestBody UserRebateAuditQueryVO vo);

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = PREFIX+"lockRebate")
    ResponseVO<Boolean> lockRebate(@Valid @RequestBody RebateListVO vo);

    @Operation(summary = "拒绝《批量》 ")
    @PostMapping(value = PREFIX+"rejectRebate")
    ResponseVO<Boolean> rejectRebate(@Valid @RequestBody RebateListVO vo);

    @Operation(summary = "派发《批量》 ")
    @PostMapping(value = PREFIX+"issueRebate")
    ResponseVO<Boolean> issueRebate(@Valid @RequestBody RebateListVO vo);

    @Operation(summary = "生成审核列表")
    @PostMapping(value = PREFIX+"handleUserVenueBetInfo")
    ResponseVO<Void> handleUserVenueBetInfo(@RequestBody ReportUserRebateInitVO rebateInitVO);

    @Operation(summary = "查询不返水配置数量")
    @PostMapping(PREFIX + "rebateRecordCount")
    ResponseVO<Long> rebateRecordCount(@Valid @RequestBody UserRebateAuditQueryVO vo);

    @Operation(summary = "更新用户返水领取状态 siteCode,userId,orderNo")
    @PostMapping(PREFIX + "onUserRebateReceived")
    ResponseVO<Boolean> onUserRebateReceived(@Valid @RequestBody UserRebateAuditQueryVO vo);
}

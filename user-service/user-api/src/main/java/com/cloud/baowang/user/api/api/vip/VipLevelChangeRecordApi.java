package com.cloud.baowang.user.api.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordRequestVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(contextId = "vipChangeRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - vip等级变更记录api")
public interface VipLevelChangeRecordApi {

    /**
     * 分页获取变更记录列表
     *
     * @param requestVO 分页查询对象
     * @return 分页对象
     */
    @PostMapping("queryChangeRecordPage")
    @Operation(summary = "分页查询变更记录")
    ResponseVO<Page<SiteVipChangeRecordVO>> queryChangeRecordPage(@RequestBody SiteVipChangeRecordPageQueryVO requestVO);

    @PostMapping("getTotalCount")
    @Operation(summary = "获取总记录数")
    ResponseVO<Long> getTotalCount(@RequestBody SiteVipChangeRecordPageQueryVO vo);

    /**
     * 新增变更记录
     *
     * @param requestVO 变更记录请求对象
     * @return void
     */
    @PostMapping("insertChangeRecord")
    @Operation(summary = "新增一条变更记录")
    ResponseVO<Boolean> insertChangeInfo(@RequestBody SiteVipChangeRecordRequestVO requestVO);

    @PostMapping("insertChangeRecordList")
    @Operation(summary = "批量新增变更记录")
    ResponseVO<Boolean> insertChangeRecordList(@RequestBody List<SiteVipChangeRecordRequestVO> requestVOs);

}

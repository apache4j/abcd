package com.cloud.baowang.system.api.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteCustomerChannelApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - customerChannelApi")
public interface CustomerChannelApi {

    String PREFIX = ApiConstants.PREFIX + "/customerChannel/api";

    @Operation(summary = "查询客服通道列表")
    @PostMapping(PREFIX + "/queryCustomerChannelPage")
    ResponseVO<Page<CustomerChannelVO>> queryCustomerChannelPage(@RequestBody CustomerChannelRequestVO customerChannelRequestVO);

    @Operation(summary = "启用/禁用客服通道")
    @PostMapping(PREFIX + "/editCustomerChannelStatus")
    ResponseVO<?> editCustomerChannelStatus(@RequestBody CustomerChannelEditVO customerChannelEditVO);

    @Operation(summary = "新增站点查询客服通道")
    @PostMapping(PREFIX + "/queryCustomerChannel")
    ResponseVO<SiteCustomerChannelResVO> queryCustomerChannel(@RequestBody CustomerChannelRequestVO reqVO);

    @Operation(summary = "根据站点获取客服通道")
    @PostMapping(PREFIX + "/getSiteCustomerChannel")
    ClientCustomerChannelVO getSiteCustomerChannel(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取指定客服通道")
    @PostMapping(PREFIX + "/getSiteCustomerChannelByCode")
    CustomerChannelVO getSiteCustomerChannelByCode(@RequestParam("siteCode") String siteCode, @RequestParam("channelCode") String channelCode);

    @Operation(summary = "站点后台查询客服通道列表")
    @PostMapping(PREFIX + "/getSiteCustomerChannelPage")
    ResponseVO<Page<SiteCustomerChannelVO>> getSiteCustomerChannelPage(@RequestBody CustomerChannelRequestVO requestVO);

    @PostMapping(PREFIX +"editCustomerStatus")
    @Operation(summary ="站点客服通道状态修改")
    ResponseVO editCustomerStatus(@RequestBody ChannelStatusVO channelStatusVO);

    @Operation(summary = "根据站点获取美洽客服通道信息")
    @PostMapping(PREFIX + "/getMeiQiaChannelInfo")
    MeiQiaChannelVO getMeiQiaChannelInfo(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据站点获取最新的客服通道")
    @PostMapping(PREFIX + "/getCustomerChannel")
    CustomerChannelVO getCustomerChannel(@RequestParam("siteCode") String siteCode);
}

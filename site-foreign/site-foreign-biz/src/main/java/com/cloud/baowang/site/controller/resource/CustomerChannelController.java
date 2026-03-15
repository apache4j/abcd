package com.cloud.baowang.site.controller.resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelEditVO;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelRequestVO;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelVO;
import com.cloud.baowang.system.api.vo.operations.SiteCustomerChannelVO;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "运营-资源管理-客服通道管理")
@RestController
@RequestMapping("/customer-channel/api")
@AllArgsConstructor
public class CustomerChannelController {

    private final CustomerChannelApi customerChannelApi;

    @Operation(summary = "查询客服通道列表")
    @PostMapping("/pageList")
    public ResponseVO<Page<SiteCustomerChannelVO>> pageList(@RequestBody CustomerChannelRequestVO channelRequestVO) {
        channelRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return customerChannelApi.getSiteCustomerChannelPage(channelRequestVO);
    }

    @Operation(summary = "启用/禁用客服通道")
    @PostMapping("/updateStatus")
    public ResponseVO<?> editCustomerChannelStatus(@Valid @RequestBody ChannelStatusVO channelStatusVO){
        channelStatusVO.setUpdater(CurrReqUtils.getAccount());
        return customerChannelApi.editCustomerStatus(channelStatusVO);
    }
}

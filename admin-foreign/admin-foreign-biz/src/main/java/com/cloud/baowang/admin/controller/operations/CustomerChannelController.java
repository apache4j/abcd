package com.cloud.baowang.admin.controller.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelEditVO;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelRequestVO;
import com.cloud.baowang.system.api.vo.operations.CustomerChannelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "客服通道管理")
@RestController
@RequestMapping("/customer-channel/api")
@AllArgsConstructor
public class CustomerChannelController {

    private final CustomerChannelApi customerChannelApi;

    @Operation(summary = "查询客服通道列表")
    @PostMapping("/pageList")
    public ResponseVO<Page<CustomerChannelVO>> pageList(@RequestBody CustomerChannelRequestVO channelRequestVO) {
        return customerChannelApi.queryCustomerChannelPage(channelRequestVO);
    }

    @Operation(summary = "启用/禁用客服通道")
    @PostMapping("/addSite")
    public ResponseVO<?> editCustomerChannelStatus(@Valid @RequestBody CustomerChannelEditVO customerChannelEditVO){
        customerChannelEditVO.setUpdaterName(CurrReqUtils.getAccount());
        return customerChannelApi.editCustomerChannelStatus(customerChannelEditVO);
    }
}

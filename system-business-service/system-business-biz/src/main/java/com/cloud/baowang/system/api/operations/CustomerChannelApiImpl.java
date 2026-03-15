package com.cloud.baowang.system.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import com.cloud.baowang.system.service.operations.CustomerChannelService;
import com.cloud.baowang.system.service.operations.SiteCustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class CustomerChannelApiImpl implements CustomerChannelApi {

    private final CustomerChannelService customerChannelService;
    private final SiteCustomerService siteCustomerService;

    @Override
    public ResponseVO<Page<CustomerChannelVO>> queryCustomerChannelPage(CustomerChannelRequestVO customerChannelRequestVO) {
        return customerChannelService.queryCustomerChannelPage(customerChannelRequestVO);
    }

    @Override
    public ResponseVO<?> editCustomerChannelStatus(CustomerChannelEditVO customerChannelEditVO) {
        return customerChannelService.editCustomerChannelStatus(customerChannelEditVO);
    }

    @Override
    public ResponseVO<SiteCustomerChannelResVO> queryCustomerChannel(CustomerChannelRequestVO reqVO) {
        return customerChannelService.queryCustomerChannel(reqVO);
    }

    @Override
    public ClientCustomerChannelVO getSiteCustomerChannel(String siteCode){
        return customerChannelService.getSiteCustomerChannel(siteCode);
    }

    @Override
    public CustomerChannelVO getSiteCustomerChannelByCode(String siteCode, String channelCode) {
        return customerChannelService.getSiteCustomerChannelByCode(siteCode, channelCode);
    }

    @Override
    public ResponseVO<Page<SiteCustomerChannelVO>> getSiteCustomerChannelPage(CustomerChannelRequestVO requestVO) {
        return customerChannelService.getSiteCustomerChannelPage(requestVO);
    }

    @Override
    public ResponseVO editCustomerStatus(ChannelStatusVO channelStatusVO) {
        return ResponseVO.success(siteCustomerService.editStatus(channelStatusVO));
    }

    @Override
    public MeiQiaChannelVO getMeiQiaChannelInfo(String siteCode) {
        return customerChannelService.getMeiQiaChannelInfo(siteCode);
    }

    @Override
    public CustomerChannelVO getCustomerChannel(String siteCode) {
        return customerChannelService.getCustomerChannel(siteCode);
    }

}

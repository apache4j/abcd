package com.cloud.baowang.system.service.operations;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.CustomerType;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.po.operations.CustomerChannelPO;
import com.cloud.baowang.system.po.operations.SiteCustomerPO;
import com.cloud.baowang.system.repositories.operations.CustomerChannelRepository;
import com.cloud.baowang.system.service.customer.vendor.CustomerService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerChannelService extends ServiceImpl<CustomerChannelRepository, CustomerChannelPO> {

    private final CustomerChannelRepository customerChannelRepository;

    private final SiteCustomerService siteCustomerService;

    public ResponseVO<Page<CustomerChannelVO>> queryCustomerChannelPage(CustomerChannelRequestVO requestVO) {
        Page<CustomerChannelVO> result = null;
        try {
            Page<CustomerChannelPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            result = customerChannelRepository.queryCustomerChannelPage(page, requestVO);
        } catch (Exception e) {
            log.error("queryCustomerChannelPage error", e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        return ResponseVO.success(result);
    }

    public ResponseVO<?> editCustomerChannelStatus(CustomerChannelEditVO editVO) {
        CustomerChannelPO customerChannelPO = super.getById(editVO.getId());
        if (customerChannelPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(editVO.getStatus())) {
            LambdaQueryWrapper<SiteCustomerPO> query = Wrappers.lambdaQuery();
            //可能存在老数据多个站点用到了一个客服通道,这里防止sql报错
            query.eq(SiteCustomerPO::getChannelCode, customerChannelPO.getChannelCode()).last("limit 0,1");
            SiteCustomerPO siteCustomerPO = siteCustomerService.getOne(query);
            if (siteCustomerPO != null) {
                int count = siteCustomerService.getCountByStatus(siteCustomerPO.getSiteCode());
                if (count > 0) {
                    throw new BaowangDefaultException(ResultCode.SITE_CUSTOMER_CHANNEL_MUST_ONE_ENABLE);
                }
            }
        } else {
            //如果是禁用状态,判断下这个通道有没有被站点使用到,使用到不允许禁用
            LambdaQueryWrapper<SiteCustomerPO> query = Wrappers.lambdaQuery();
            query.eq(SiteCustomerPO::getChannelCode, customerChannelPO.getChannelCode());
            long count = siteCustomerService.count(query);
            if (count > 0) {
                throw new BaowangDefaultException(ResultCode.CUSTOMER_CHANNEL_AL_USED);
            }
        }

        customerChannelPO.setStatus(editVO.getStatus());
        customerChannelPO.setUpdatedTime(System.currentTimeMillis());
        customerChannelPO.setUpdaterName(editVO.getUpdaterName());
        return ResponseVO.success(super.updateById(customerChannelPO));
    }

    public ResponseVO<SiteCustomerChannelResVO> queryCustomerChannel(final CustomerChannelRequestVO reqVO) {
        SiteCustomerChannelResVO vo = new SiteCustomerChannelResVO();
        Page<CustomerChannelPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SiteCustomerChannelPageVO> resultPage = customerChannelRepository.queryCustomerChannel(page, reqVO);
        List<String> chooseId = Lists.newArrayList();
        List<String> allId = Lists.newArrayList();
        siteCustomerService.lambdaQuery().eq(SiteCustomerPO::getSiteCode, reqVO.getSiteCode()).list()
                .forEach(obj -> chooseId.add(obj.getChannelCode()));
        this.list().forEach(obj -> allId.add(obj.getChannelCode()));
        vo.setChooseId(chooseId);
        vo.setAllId(allId);
        vo.setPageVO(resultPage);
        return ResponseVO.success(vo);
    }

    public ClientCustomerChannelVO getSiteCustomerChannel(String siteCode) {
        CustomerChannelRequestVO requestVO = new CustomerChannelRequestVO();
        requestVO.setSiteCode(siteCode);
        requestVO.setStatus(EnableStatusEnum.ENABLE.getCode().toString());
        ClientCustomerChannelVO channelVO = customerChannelRepository.getSiteCustomerChannel(requestVO);
        if (channelVO == null) {
            throw new BaowangDefaultException(ResultCode.CUSTOMER_CHANNEL_CLOSE);
        }
        LambdaQueryWrapper<CustomerChannelPO> channelLqw = new LambdaQueryWrapper<>();
        channelLqw.eq(CustomerChannelPO::getChannelCode, channelVO.getChannelCode());
        CustomerChannelPO channelPO = customerChannelRepository.selectOne(channelLqw);
        if (null == channelPO) {
            throw new BaowangDefaultException(ResultCode.CUSTOMER_CHANNEL_CLOSE);
        }

        try {
            String userAccount = CurrReqUtils.getAccount();
            CustomerService customerService = SpringUtils.getBean(channelPO.getPlatformCode());
            String apiUrl = customerService.getApiUrl(channelPO, userAccount);
            channelVO.setChannelAddr(apiUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return channelVO;
    }


    public MeiQiaChannelVO getMeiQiaChannelInfo(String siteCode) {
        CustomerChannelRequestVO requestVO = new CustomerChannelRequestVO();
        requestVO.setSiteCode(siteCode);
        requestVO.setPlatformCode(CustomerType.MEIQIA.getCode());
        List<MeiQiaChannelVO> channelVOList = customerChannelRepository.getCustomerChannelByCode(requestVO);
        if (channelVOList == null || channelVOList.size() == 0) {
             return null;
        }

        for (MeiQiaChannelVO channelVO : channelVOList) {
            if (channelVO.getStatus() == 1) {
                return channelVO;
            }
        }

        return channelVOList.get(0);
    }

    public CustomerChannelVO getSiteCustomerChannelByCode(String siteCode, String channelCode) {
        LambdaQueryWrapper<SiteCustomerPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SiteCustomerPO::getSiteCode, siteCode);
        lqw.eq(SiteCustomerPO::getChannelCode, channelCode);
        List<SiteCustomerPO> siteCustomerPOS = siteCustomerService.list(lqw);
        if (CollectionUtil.isEmpty(siteCustomerPOS)) {
            return new CustomerChannelVO();
        }
        LambdaQueryWrapper<CustomerChannelPO> channelLqw = new LambdaQueryWrapper<>();
        channelLqw.eq(CustomerChannelPO::getChannelCode, siteCustomerPOS.get(0).getChannelCode());
        CustomerChannelPO channelPO = customerChannelRepository.selectOne(channelLqw);
        if (null == channelPO) {
            return new CustomerChannelVO();
        }

        CustomerChannelVO vo = ConvertUtil.entityToModel(channelPO, CustomerChannelVO.class);
        try {
            String userAccount = CurrReqUtils.getAccount();
            CustomerService customerService = SpringUtils.getBean(channelPO.getPlatformCode());
            String apiUrl = customerService.getApiUrl(channelPO, userAccount);
            vo.setChannelAddr(apiUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vo;
    }

    public ResponseVO<Page<SiteCustomerChannelVO>> getSiteCustomerChannelPage(CustomerChannelRequestVO requestVO) {
        Page<SiteCustomerChannelVO> result = null;
        try {
            Page<CustomerChannelPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            result = customerChannelRepository.getSiteCustomerChannelPage(page, requestVO);
        } catch (Exception e) {
            log.error("queryCustomerChannelPage error", e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
        return ResponseVO.success(result);
    }

    public CustomerChannelVO getCustomerChannel(String siteCode) {
        return customerChannelRepository.getCustomerChannel(siteCode);
    }
}

package com.cloud.baowang.system.repositories.operations;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.po.operations.CustomerChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerChannelRepository extends BaseMapper<CustomerChannelPO> {

    Page<CustomerChannelVO> queryCustomerChannelPage(Page<CustomerChannelPO> page, @Param("requestVO") CustomerChannelRequestVO requestVO);

    Page<SiteCustomerChannelPageVO> queryCustomerChannel(@Param("page") Page<CustomerChannelPO> page,
                                                         @Param("vo") CustomerChannelRequestVO reqVO);

    Page<SiteCustomerChannelVO> getSiteCustomerChannelPage(Page<CustomerChannelPO> page, @Param("vo") CustomerChannelRequestVO requestVO);

    ClientCustomerChannelVO getSiteCustomerChannel(@Param("vo") CustomerChannelRequestVO requestVO);

    List<MeiQiaChannelVO> getCustomerChannelByCode(@Param("vo") CustomerChannelRequestVO requestVO);

    CustomerChannelVO getCustomerChannel(@Param("siteCode") String siteCode);
}

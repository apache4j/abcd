package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.SiteRechargeChannelVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.po.SiteRechargeChannelPO;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteRechargeChannelRepository extends BaseMapper<SiteRechargeChannelPO> {
    Page<RechargeChannelResVO> queryPlatformAuthorize(@Param("page") Page<SystemRechargeChannelPO> page,
                                                      @Param("vo") RechargeChannelReqVO reqVO);

    List<SiteRechargeChannelVO> selectSiteSystemChannelList(@Param("rechargeWayIds") List<Long> rechargeWayIds,
                                                            @Param("siteCode") String siteCode);

    Page<SiteRechargeChannelRespVO> selectRechargePage(@Param("page")Page<SiteRechargeChannelRespVO> page, @Param("vo")SiteRechargeChannelReqVO vo);

    List<SiteRechargeChannelRespVO> selectBySort(@Param("vo")SiteRechargeChannelReqVO vo);

    List<SiteRechargeChannelChangeVO> selectSiteRechargeChannelChangeVO(@Param("siteCode")String siteCode);


    List<SiteSystemRechargeChannelRespVO> selectSiteRechargeChannelList(@Param("siteCode")String siteCode);

    SystemRechargeChannelBaseVO getChannelInfoByCurrencyAneWayId(@Param("currencyCode")String currencyCode, @Param("rechargeWayId")String rechargeWayId, @Param("siteCode")String siteCode, @Param("channelId")String channelId);

    SystemRechargeChannelBaseVO getChannelInfoByChannelId(@Param("currencyCode")String currencyCode, @Param("rechargeWayId")String rechargeWayId, @Param("siteCode")String siteCode, @Param("channelId")String channelId);
}

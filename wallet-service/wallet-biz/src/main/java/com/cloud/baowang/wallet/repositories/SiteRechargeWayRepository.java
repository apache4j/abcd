package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.api.vo.userCoin.SystemRechargeWayVO;
import com.cloud.baowang.wallet.po.SiteRechargeWayPO;
import com.cloud.baowang.wallet.po.SystemRechargeWayPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteRechargeWayRepository extends BaseMapper<SiteRechargeWayPO> {
    Page<RechargeAuthorizeResVO> queryDepositAuthorizePage(@Param("page") Page<SystemRechargeWayPO> page,
                                                           @Param("vo") RechargeAuthorizeReqVO reqVO);

    List<SiteRechargeWayResVO> queryBySite();

    Page<SiteRechargeWayResponseVO> selectRechargePage(@Param("page")Page<SiteRechargeWayResponseVO> page,@Param("vo") SiteRechargeWayRequestVO vo);

    List<SiteRechargeWayResponseVO> selectBySort(@Param("vo") SiteRechargeWayRequestVO vo);

    List<SystemRechargeWayVO> selectFrontWayList(@Param(("vo")) RechargeWayRequestVO rechargeWayRequestVO);


    List<SiteRechargeWayResChangeVO> selectRechargeWayList(@Param(("siteCode")) String siteCode);

    SystemRechargeWayDetailRespVO getRechargeWayByCurrencyAndNetworkType(@Param("currencyCode")String currencyCode, @Param("networkType")String networkType,
            @Param("siteCode")String siteCode,@Param("wayId") String wayId);
}

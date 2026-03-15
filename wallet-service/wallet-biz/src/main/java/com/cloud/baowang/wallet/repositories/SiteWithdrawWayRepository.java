package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWithdrawWayResChangeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawAuthorizeReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawAuthorizeResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayRequestVO;
import com.cloud.baowang.wallet.po.SiteWithdrawWayPO;
import com.cloud.baowang.wallet.po.SystemWithdrawWayPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteWithdrawWayRepository extends BaseMapper<SiteWithdrawWayPO> {
    Page<WithdrawAuthorizeResVO> queryWithdrawAuthorizePage(@Param("page") Page<SystemWithdrawWayPO> page,
                                                            @Param("vo") WithdrawAuthorizeReqVO reqVO);

    List<SiteWithdrawWayResVO> queryBySite();

    List<SiteWithdrawWayResVO> queryBySiteAndTypeCode(@Param("siteCode") String siteCode,
                                                      @Param("typeCode") String typeCode);

    List<SiteWithdrawWayResVO> queryWithdrawListBySite(@Param("siteCode") String siteCode);

    List<SiteWithdrawWayResVO> queryListBySiteAndCurrencyCode(@Param("siteCode") String siteCode,
                                                              @Param("currencyCode") String currencyCode);

    Page<SiteWithdrawWayResponseVO> selectWithdrawPage(@Param("page")Page<SiteWithdrawWayResponseVO> page, @Param("vo") SiteWithdrawWayRequestVO vo);

    List<SiteWithdrawWayResponseVO> selectBySort(@Param("vo") SiteWithdrawWayRequestVO vo);

    List<SystemWithdrawWayPO> selectFrontWayList(@Param("vo")WithdrawWayRequestVO withdrawWayRequestVO);


    List<SiteWithdrawWayResChangeVO> getWithdrawWayBySiteCodeList(@Param("siteCode") String siteCode);

    List<String> getWithdrawCollectInfoList(@Param("siteCode") String siteCode,
                                            @Param("withdrawTypeCode") String withdrawTypeCode,@Param("currencyCode")String currencyCode);
}

package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWithdrawChannelChangeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelResVO;
import com.cloud.baowang.wallet.po.SiteWithdrawChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteWithdrawChannelRepository extends BaseMapper<SiteWithdrawChannelPO> {
    Page<WithdrawChannelResVO> queryWithdrawPlatformAuthorize(@Param("page") Page<SystemWithdrawChannelPO> page,
                                                              @Param("vo") WithdrawChannelReqVO reqVO);


    List<SiteWithdrawChannelVO> selectSiteWithdrawChannelList(@Param("siteCode") String siteCode,
                                                              @Param("wayList") List<String> wayList,
                                                              @Param("status")Integer status);

    Page<SiteWithdrawChannelResponseVO> selectWithdrawPage(@Param("page")Page<SiteWithdrawChannelResponseVO> page,@Param("vo") SiteWithdrawChannelRequestVO vo);

    List<SiteWithdrawChannelResponseVO> selectBySort(@Param("vo") SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO);

    List<SiteWithdrawChannelChangeVO> getWithdrawChannelBySiteCode(@Param("siteCode") String siteCode);
}

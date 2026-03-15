package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressVO;
import com.cloud.baowang.wallet.po.HotWalletAddressPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface HotWalletAddressRepository extends BaseMapper<HotWalletAddressPO> {



    Page<UserHotWalletAddressVO> listUserHotAddress(@Param("page") Page<HotWalletAddressPO> page,@Param("vo") UserHotWalletAddressRequestVO vo);

    UserHotWalletAddressVO sumUserHotWalletAddress(@Param("vo") UserHotWalletAddressRequestVO vo);

    Long userHotWalletAddressPageCount(@Param("vo") UserHotWalletAddressRequestVO vo);


    Page<AgentHotWalletAddressVO> listAgentHotAddress(@Param("page") Page<HotWalletAddressPO> page, @Param("vo") AgentHotWalletAddressRequestVO vo);

    AgentHotWalletAddressVO sumAgentHotWalletAddress(@Param("vo") AgentHotWalletAddressRequestVO vo);

    Long agentHotWalletAddressPageCount(@Param("vo") AgentHotWalletAddressRequestVO vo);
}

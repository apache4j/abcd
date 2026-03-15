package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordResponseVO;
import com.cloud.baowang.wallet.po.UserPlatformTransferRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;


/**
 * @author qiqi
 */
@Mapper
public interface UserPlatformTransferRecordRepository extends BaseMapper<UserPlatformTransferRecordPO> {

    Page<UserTradeRecordResponseVO> tradeRecordPlatformList(@Param("page") Page<UserTradeRecordResponseVO> page, @Param("vo") UserTradeRecordRequestVO vo);
    BigDecimal getTransferAmountByAgentIds(@Param("vo") AgentWinLossParamVO paramVO);

    BigDecimal getTransferAmountByUserAccount(@Param("userAccount") String userAccount, @Param("siteCode") String siteCode);

    Integer existsTransferRecordByUserId(@Param("userId") String userId);
}

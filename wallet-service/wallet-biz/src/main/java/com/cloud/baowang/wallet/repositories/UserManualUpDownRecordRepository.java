package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.cloud.baowang.wallet.api.vo.agent.AgentUserTeamParam;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentWinLossParamVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserDepositSumVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.*;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import com.cloud.baowang.wallet.po.UserManualUpDownRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 会员人工加减额记录 Mapper 接口
 *
 * @author kimi
 * @since 2024-05-20 10:00:00
 */
@Mapper
public interface UserManualUpDownRecordRepository extends BaseMapper<UserManualUpDownRecordPO> {

    Page<UserManualUpRecordResponseVO> getPage(Page<UserManualUpRecordResponseVO> page, @Param("vo") UserManualUpRecordPageVO vo);

    // 会员人工加额记录 总计
    UserManualUpRecordResponseVO getTotalPage(@Param("vo") UserManualUpRecordPageVO vo);
    Long getPageCount(@Param("vo") UserManualUpRecordPageVO vo);

    /**
     * 会员人工减额计数
     * @param vo
     * @return
     */
    UserManualDownRecordVO sumUserManualDown(@Param("vo") UserManualDownRecordRequestVO vo);

    Page<UserManualUpReviewResponseVO> getUpReviewPage(
            Page<UserManualUpReviewResponseVO> page,
            @Param("vo") UserManualUpReviewPageVO vo,
            @Param("adminName") String adminName);

    Page<GetRecordResponseVO> getRecordPage(Page<GetRecordResponseVO> page, @Param("vo") GetRecordPageVO vo);
    Long getTotalCountUpRecord(@Param("vo") GetRecordPageVO vo);

    Page<UserRechargeReviewResponseVO> getReviewPage(
            Page<UserRechargeReviewResponseVO> page,
            @Param("vo") UserRechargeReviewPageVO vo,
            @Param("adminName") String adminName);


    Page<GetUserRechargeRecordResponseVO> getRechargeRecordPage(Page<GetUserRechargeRecordResponseVO> page,
                                                                @Param("vo") GetUserRechargeRecordPageVO vo);


    Long getTotalCount(@Param("vo") GetUserRechargeRecordPageVO vo);

    List<HashMap> selectDepositActiveInfo(@Param("vo") AgentUserTeamParam param);

    List<WalletAgentSubLineResVO> getManualAmountGroupAgent(@Param("vo") WalletAgentSubLineReqVO reqVO);

    Page<UserManualUpDownRecordPO> selectUpReviewPage(Page<UserManualUpDownRecordPO> page, @Param("vo") UserManualUpReviewPageVO vo);

    List<UserDepositSumVO> getUserManualUpAmount(@Param("vo") AgentWinLossParamVO vo);


}

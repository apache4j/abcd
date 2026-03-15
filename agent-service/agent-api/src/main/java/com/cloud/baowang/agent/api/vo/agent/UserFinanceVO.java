package com.cloud.baowang.agent.api.vo.agent;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Schema(title ="会员财务信息")
public class UserFinanceVO {


    @Schema(description ="盈利信息")
    private UserProfitVO userProfitVO;

    @Schema(description ="财务会员投注信息")
    private UserBetsVO userBets;

    @Schema(description ="财务会员充提信息")
    private AgentUserDepositWithdrawVO  userDepositWithdraw;

    @Schema(description ="活动优惠信息")
    private UserPromotionsVO userPromotionsVO;

    @Schema(description ="输赢前3")
    private List<UserVenueTopVO> winLoseTopThree;

    @Schema(description ="投注前3")
    private List<UserVenueTopVO> betsTopThree;
}

package com.cloud.baowang.report.api.vo.user;

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
public class ReportUserFinanceVO {


    @Schema(description ="财务会员投注信息")
    private ReportUserBetsVO userBets;

    @Schema(description ="财务会员充提信息")
    private ReportUserDepositWithdrawVO  reportUserDepositWithdrawVO;


    @Schema(description ="输赢前3")
    private List<ReportUserVenueTopVO> winLoseTopThree;

    @Schema(description ="投注前3")
    private List<ReportUserVenueTopVO> betsTopThree;
}

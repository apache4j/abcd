package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.vo.agent.GetDepositStatisticsByAgentIdVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountReqVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountVO;
import com.cloud.baowang.wallet.api.vo.report.DepositWithdrawAllRecordVO;
import com.cloud.baowang.wallet.api.vo.site.GetAllArriveAmountBySiteCodeResponseVO;
import com.cloud.baowang.wallet.api.vo.site.GetDepositStatisticsBySiteCodeVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.*;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import com.cloud.baowang.wallet.service.UserDepositWithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserDepositWithdrawApiImpl implements UserDepositWithdrawApi {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;
    private final UserDepositWithdrawService userDepositWithdrawService;

    @Override
    public ResponseVO<List<GetDepositStatisticsByAgentIdVO>> getDepositStatisticsByAgentId(String siteCode,
                                                                                           Long start,
                                                                                           Long end,
                                                                                           String agentId,
                                                                                           Integer type,
                                                                                           String dbZone,
                                                                                           String currencyCode
    ) {
        return ResponseVO.success(userDepositWithdrawalRepository.getDepositStatisticsByAgentId(siteCode, start, end, agentId, type, dbZone,currencyCode));
    }

    @Override
    public ResponseVO<List<GetDepositStatisticsBySiteCodeVO>> getDepositStatisticsBySiteCode(String siteCode, Long start, Long end, Integer type, String dbZone, String currencyCode) {
        return ResponseVO.success(userDepositWithdrawalRepository.getDepositStatisticsBySiteCode(siteCode, start, end, type, dbZone, currencyCode));
    }

    @Override
    public ResponseVO<List<GetDepositStatisticsBySiteCodeVO>> getDepositWithdrawnUserCountBySiteCode(String siteCode, Long start, Long end, Integer type, String dbZone, String currencyCode) {
        return ResponseVO.success(userDepositWithdrawalRepository.getDepositWithdrawnUserCountBySiteCode(siteCode, start, end, type, dbZone, currencyCode));
    }

    @Override
    public GetAllArriveAmountByAgentUserResponseVO getAllArriveAmountByAgentUser(String siteCode,
                                                                                 String agentAccount, String userAccount, Long start, Long end, Integer type) {
        return userDepositWithdrawalRepository.getAllArriveAmountByAgentUser(siteCode, agentAccount, userAccount, start, end, type);
    }

    @Override
    public List<GetAllArriveAmountBySiteCodeResponseVO> getAllArriveAmountBySiteCode(String siteCode, Long start, Long end) {
        return userDepositWithdrawalRepository.getAllArriveAmountBySiteCode(siteCode, start, end);
    }

    @Override
    public List<GetAllArriveAmountByAgentIdVO> getAllArriveAmountByAgentId(String siteCode, String agentAccount, Long start, Long end) {
        return userDepositWithdrawalRepository.getAllArriveAmountByAgentId(siteCode, agentAccount, start, end);

    }

    @Override
    public List<GetAllWithdrawAmountByAgentIdVO> getAllWithdrawAmountByAgentId(String siteCode, String agentAccount, Long start, Long end) {
        return userDepositWithdrawalRepository.getAllWithdrawAmountByAgentId(siteCode, agentAccount, start, end);
    }


    @Override
    public Map<String, Map<String, List<UserDepositWithdrawalRecordVO>>> selectGroupByTime(Long startTime, Long endTime, String siteCode) {
        return userDepositWithdrawService.selectGroupByTime(startTime, endTime, siteCode);
    }

    @Override
    public List<UserDepositWithdrawalResVO> getListByBankNoAndSiteCode(String withdrawTypeCode, String riskControlAccount, String wayId, String siteCode) {
        return userDepositWithdrawService.getListByBankNoAndSiteCode(withdrawTypeCode, riskControlAccount,wayId, siteCode);
    }

    @Override
    public List<DepositWithdrawAllRecordVO> getAllDepositWithdrawRecord(Long startTime, Long endTime, List<String> siteCodes) {
        return userDepositWithdrawService.getAllDepositWithdrawRecord(startTime,endTime,siteCodes);
    }

    /**
     * 查询 存取款 手续费
     * @param userDepositWithdrawPageReqVO 参数
     * @return
     */
    @Override
    public Page<UserDepositWithdrawalResVO> findDepositWithdrawPage(UserDepositWithdrawPageReqVO userDepositWithdrawPageReqVO) {
        return userDepositWithdrawService.findDepositWithdrawPage(userDepositWithdrawPageReqVO);
    }

    @Override
    public List<UserDepositAmountVO> queryDepositAmountByUserIds(UserDepositAmountReqVO vo) {
        return userDepositWithdrawService.queryDepositAmountByUserIds(vo);
    }
}

package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.play.api.vo.user.PlayUserDataDetailVO;
import com.cloud.baowang.play.api.vo.user.PlayUserDataVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserDataDetailVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserDataVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAdminResVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordAggregationDTO;
import com.cloud.baowang.play.repositories.OrderRecordRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class PlayServiceService {


    private final OrderRecordRepository orderRecordRepository;


    /**
     * 查询注单汇总
     */
    private UserDataDetailVO getUserDataDetailVO(OrderRecordAdminResVO vo) {
        List<OrderRecordAggregationDTO> recordAggregationDTOList = orderRecordRepository.orderCountAndSumGroup(vo);
        BigDecimal winLossAmountTotal = BigDecimal.ZERO;
        BigDecimal betAmountTotal = BigDecimal.ZERO;
        long betNumTotal = 0L;
        if (CollectionUtil.isNotEmpty(recordAggregationDTOList)) {
            winLossAmountTotal = recordAggregationDTOList.stream().map(OrderRecordAggregationDTO::getWinLossAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            betAmountTotal = recordAggregationDTOList.stream().map(OrderRecordAggregationDTO::getBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            betNumTotal = recordAggregationDTOList.stream()
                    .mapToLong(OrderRecordAggregationDTO::getNum)
                    .sum();
        }
        return UserDataDetailVO.builder().totalBet(betAmountTotal).totalBetCount(betNumTotal).totalWins(winLossAmountTotal).build();
    }

    public PlayUserDataVO getUserDataDetail(String userAccount, String gameId) {

        //查汇总注单
        OrderRecordAdminResVO gameTotalVo = new OrderRecordAdminResVO();
        gameTotalVo.setUserAccount(userAccount);
        UserDataDetailVO userDataDetailVO = getUserDataDetailVO(gameTotalVo);

        //查游戏汇总
        OrderRecordAdminResVO gameIdVo = new OrderRecordAdminResVO();
        gameIdVo.setUserAccount(userAccount);
        gameIdVo.setGameId(gameId);
        UserDataDetailVO gameUserDetail = getUserDataDetailVO(gameIdVo);

        PlayUserDataDetailVO userDataVO = PlayUserDataDetailVO.builder()
                .totalWins(gameUserDetail.getTotalWins())
                .totalBetCount(gameUserDetail.getTotalBetCount())
                .totalBet(gameUserDetail.getTotalBet())
                .build();
        return PlayUserDataVO.builder().totalWins(userDataDetailVO.getTotalWins()).totalBet(userDataDetailVO.getTotalBet())
                .totalBetCount(userDataDetailVO.getTotalBetCount()).betList(Lists.newArrayList(userDataVO)).build();
    }

}

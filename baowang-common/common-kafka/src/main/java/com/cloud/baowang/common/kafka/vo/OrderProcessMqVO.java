package com.cloud.baowang.common.kafka.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "注单处理mq集合")
public class OrderProcessMqVO {
    //注单新增会员盈亏mq
    private List<UserWinLoseMqVO> userWinLoseMqList ;
    //注单更新会员盈亏mq
    private List<UserWinLoseMqVO> userWinLoseUpdateMqList;
    //注单新增场馆报表mq
    private List<VenueWinLossMqVO> venueWinLossMqList;
    //注单更新场馆报表mq
    private List<VenueWinLossMqVO> venueWinLossUpdateMqList;
     //注单新增打码量mq
    private List<UserTypingAmountRequestVO> typingAmountList;
    //注单更新打码量mq
    private List<UserTypingAmountRequestVO> typingAmountUpdateList;
    //注单新增VIP晋级mq
    private List<UserVIPFlowRequestVO> userVIPFlowList;
    //注单更新VIP晋级mq
    private List<UserVIPFlowRequestVO> userVIPFlowUpdateList;
    //平台盈利mq
    private List<UserVenueWinLossMqVO> userVenueWinLossMqList;
    // 用户最新投注mq
    private List<UserLatestBetVO> userLatestBetVOList;



    public static OrderProcessMqVO init() {
        OrderProcessMqVO vo = new OrderProcessMqVO();
        vo.setTypingAmountList(new ArrayList<>());
        vo.setTypingAmountUpdateList(new ArrayList<>());
        vo.setUserVIPFlowList(new ArrayList<>());
        vo.setUserVIPFlowUpdateList(new ArrayList<>());
        vo.setUserWinLoseMqList(new ArrayList<>());
        vo.setUserWinLoseUpdateMqList(new ArrayList<>());
        vo.setVenueWinLossMqList(new ArrayList<>());
        vo.setVenueWinLossUpdateMqList(new ArrayList<>());
        vo.setUserVenueWinLossMqList(new ArrayList<>());
        vo.setUserLatestBetVOList(new ArrayList<>());
        return vo;
    }
}

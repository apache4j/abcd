package com.cloud.baowang.play.api.vo.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdUpdateBaseReq implements Serializable {

    // 操作 ID
    private Integer ActionId;
    // 赛事 ID
    private String MatchID;
    // 当前交易金额变更余额
    private List<CmdUpdateDataReq> TicketDetails;



}

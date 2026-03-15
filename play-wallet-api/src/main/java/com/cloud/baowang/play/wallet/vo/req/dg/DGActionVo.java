package com.cloud.baowang.play.wallet.vo.req.dg;

import lombok.Data;

@Data
public class DGActionVo {
    private String token;
    private String data;
    private Long ticketId;
    private Integer type;
    private String detail;
    private Member member;
}

package com.cloud.baowang.play.api.vo.dg2.req;

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

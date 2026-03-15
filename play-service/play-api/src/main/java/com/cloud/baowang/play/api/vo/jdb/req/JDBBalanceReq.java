package com.cloud.baowang.play.api.vo.jdb.req;

import lombok.Data;

@Data
public class JDBBalanceReq {



    private Integer action;
    /** 当前系统时间 */
    private Long ts;
    /** 玩家账号 */
    private String uid;

    private String currency;
    /** 游戏类型 */
    private Integer gType;

    /** 机台类型 */
    private Integer mType;


    private String systemSessionId;



}

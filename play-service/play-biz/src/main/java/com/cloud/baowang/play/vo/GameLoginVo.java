package com.cloud.baowang.play.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameLoginVo {

    /**
     * 三方数据源
     */
    private String source;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 商户ID
     */
    private String merchantNo;

    /**
     * GameLoginTypeEnums 枚举
     */
    private String type;

    /**
     * 场馆CODE
     */
    private String venueCode;
}

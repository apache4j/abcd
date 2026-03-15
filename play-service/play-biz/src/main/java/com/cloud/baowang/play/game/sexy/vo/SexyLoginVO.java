package com.cloud.baowang.play.game.sexy.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SexyLoginVO implements Serializable {

    /** 密钥 */
    private String cert;

    /** 代理id */
    private String agentId;

    /** 代理id */
    private String userId;


    private String language;

    /** 代理id */
    private String oddsMode;

    private String currency;



}

package com.cloud.baowang.play.api.vo.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三方要限制token长度，所以简写
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PGUserToken {

    /**
     * 三方用户名
     */
    private String n;

    /**
     * 三方币种
     */
    private String c;

    /**
     * 创建时间戳毫秒
     */
    private Long e;

    /**
     * 线路密钥
     */
    private String k;

}

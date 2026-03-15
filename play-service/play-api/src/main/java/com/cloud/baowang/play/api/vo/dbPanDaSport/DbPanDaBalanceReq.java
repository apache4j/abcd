package com.cloud.baowang.play.api.vo.dbPanDaSport;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbPanDaBalanceReq {

    /**
     * 商户编码
     * 必填
     */
    private String merchantCode;

    /**
     * 用户名
     * 必填
     */
    private String userName;

    /**
     * 时间戳（13位，Long型转字符串）
     * 必填
     */
    private String timestamp;

    /**
     * 签名
     * 必填
     */
    private String signature;

    public boolean valid() {
        return StrUtil.isAllNotEmpty(merchantCode, userName, timestamp, signature);
    }

}

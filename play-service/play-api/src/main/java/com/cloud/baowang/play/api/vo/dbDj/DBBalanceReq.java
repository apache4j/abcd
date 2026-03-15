package com.cloud.baowang.play.api.vo.dbDj;


import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

@Data
public class DBBalanceReq {


    /**
     * 用户名（2-32位）
     */
    private String username;

    /**
     * Unix 时间戳（精确到秒）
     */
    private Long time;

    /**
     * 密钥（MD5加密计算）
     */
    private String sign;


    public Boolean valid() {
        return ObjectUtil.isAllNotEmpty(username, time, sign);
    }


}

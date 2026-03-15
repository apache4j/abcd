package com.cloud.baowang.play.wallet.vo.req.db.sh.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class SHBalanceQueryBatchVO {
    private String merchantCode;

    private String params;

    @JSONField(serialize = false)
    private String signature;

    private Long timestamp;

}

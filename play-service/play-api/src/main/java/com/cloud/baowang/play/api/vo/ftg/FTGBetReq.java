package com.cloud.baowang.play.api.vo.ftg;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FTGBetReq extends FTGBaseReq {


    /**
     * 標準 16-byte UUID。
     * 運營商不要重複使用，並且不要異
     * 動該參數。
     */
    private String transaction_uuid;


    /**
     * 注單ID
     */
    private String bet_id;

    private Integer currency;

    /**
     * 下注金額
     */
    private BigDecimal amount;


    /**
     * 遊戲類型
     */
    private String category;

    /**
     * 遊戲大廳編號
     */
    private String lobby_id;

    /**
     * 當局狀態
     */
    private Boolean round_closed;

    public Boolean valid() {
        return ObjectUtil.isAllNotEmpty(this.getUid(), this.getToken(), transaction_uuid,
                bet_id, currency, amount);
    }


}

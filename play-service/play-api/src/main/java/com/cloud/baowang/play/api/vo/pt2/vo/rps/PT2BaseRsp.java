package com.cloud.baowang.play.api.vo.pt2.vo.rps;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;

import com.cloud.baowang.play.api.vo.pt2.enums.PT2ErrorEnums;
import com.cloud.baowang.play.api.vo.pt2.vo.Balance;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class PT2BaseRsp {

    private PT2RspVO rspVO;


    public PT2BaseRsp(PT2RspVO rspVO) {
        this.rspVO = rspVO;
    }


    public static PT2BaseRsp failed(PT2RspVO rspVO, PT2ErrorEnums errorMsg) {
        Map<String, String> error = new HashMap<>();
        error.put("code", errorMsg.getMessage());
        rspVO.setError(JSONObject.toJSONString(error));
        return new PT2BaseRsp(rspVO);
    }



    public static PT2BaseRsp success(PT2RspVO rspVO) {
        return new PT2BaseRsp(rspVO);
    }

    public static PT2BaseRsp success(PT2RspVO rspVO, BigDecimal amount) {
        String dateStr = TimeZoneUtils.formatTimestampToGMT(System.currentTimeMillis(), TimeZoneUtils.patten_yyyyMMddHHmmssSSS);
        Balance balance = Balance.builder().real(amount.setScale(2, RoundingMode.DOWN)).timestamp(dateStr).build();
        rspVO.setBalance(balance);
        return new PT2BaseRsp(rspVO);
    }

}

package com.cloud.baowang.play.api.vo.acelt;


import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ACELTAccountChangeCallBackReq extends ACELTBaseReq {

    /**
     * 交易凭证
     */
    private String transferReference;

    private String operatorId;


    public Boolean valid() {
        return ObjectUtil.isAllNotEmpty(this.getOperatorId(), this.getSign(), this.getTimestamp()
                , transferReference, operatorId);
    }


}

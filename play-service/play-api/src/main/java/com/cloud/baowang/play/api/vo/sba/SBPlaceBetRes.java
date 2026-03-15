package com.cloud.baowang.play.api.vo.sba;

import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SBPlaceBetRes extends SBResBaseVO {

    /**
     * 从传入参数取得
     */
    private String refId ;

    /**
     * 商户系统交易 id
     */
    private String licenseeTxId;



}

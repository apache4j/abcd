package com.cloud.baowang.play.api.vo.third.SBA;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "沙巴体育注册对象")
public class SBARegisterReqVO implements Serializable {

    /**
     * 厂商识别码, 最大长度 = 50
     */
    private String vendorId;


    /**
     * 会员账号（建议跟 Username 一样）, 支援 ASCII Table 33-126, 最大长度 = 30
     */
    private String vendorMemberId;


    /**
     * 厂商ID。若為子網站則需帶入子網站名稱
     */
    private String operatorId;


    /**
     * 会员登入名称, （支援 ASCII Table 33-126），最大长度 = 30
     */
    private String username;


    /**
     * 为此会员设置赔率类型。请参考附件"赔率类型表"
     */
    private Integer oddstype;

    /**
     * 为此会员设置币别。请参考附件中"币别表"
     */
    private Integer currency;


    /**
     * 会员单笔最大限制转账金额, 最高可设定 9999999999.
     */
    private BigDecimal maxtransfer;


    /**
     * 会员单笔最小限制转账金额 最小限制转账金额必须大于等于0.
     */
    private BigDecimal mintransfer;



    public static Map<String, String> getRegisterInfo(SBARegisterReqVO reqVO){
        if(ObjectUtil.isEmpty(reqVO)){
            return null;
        }
        if(ObjectUtil.isEmpty(reqVO.getMaxtransfer())){
            reqVO.setMaxtransfer(BigDecimal.valueOf(9999999999L));
        }

        if(ObjectUtil.isEmpty(reqVO.getMintransfer())){
            reqVO.setMintransfer(BigDecimal.valueOf(1L));
        }
        Map<String, String> map = Maps.newHashMap();
        map.put("vendor_id",reqVO.getVendorId());
        map.put("vendor_member_id",reqVO.getVendorMemberId());
        map.put("operatorId",reqVO.getOperatorId());
        map.put("username",reqVO.getUsername());
        map.put("oddstype",String.valueOf(reqVO.getOddstype()));
        map.put("currency",String.valueOf(reqVO.getCurrency()));
        map.put("maxtransfer",String.valueOf(reqVO.getMaxtransfer()));
        map.put("mintransfer",String.valueOf(reqVO.getMintransfer()));
        return map;
    }

}

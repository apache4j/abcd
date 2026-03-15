package com.cloud.baowang.common.kafka.vo;


import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(title = "保证金增减请求对象")
public class SitSecurityBalanceMqVO extends MessageBaseVO {


    //来源订单号
    private String  sourceOrderNo;

    //站点编码
    private String siteCode;

    //调整金额
    private BigDecimal adjustAmount;

    //业务类型
    /**
     * {@link com.cloud.baowang.wallet.api.enums.SiteSecuritySourceCoinTypeEnums}
     */
    private String sourceCoinType;
    //帐变类型
    /**
     * {@link SiteSecurityCoinTypeEnums}
     */
    private String coinType;
    /**
     * 会员/代理 id
     */
    private String userId;
    /**
     * 会员名称
     */
    private String userName;
    //修改人
    private String updateUser;

}

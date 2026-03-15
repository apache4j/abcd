package com.cloud.baowang.wallet.api.vo.userwallet;


import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员交易记录详情请求对象")
public class UserTradeRecordDetailRequestVO extends PageVO {

    @Schema(description = "交易类型 取列表里面返回的 tradeType")
    private String tradeType;

    @Schema(description = "交易方式类型 取列表里面返回的 tradeWayType")
    private String tradeWayType;

    @Schema(description = "订单编号")
    private String orderNo;

    private String userId;



}

package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "站点提款方式分页查询操作")
public class SiteWithdrawWayRequestVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 提款类型
     */
    @Schema(description = "提款类型Id")
    private String withdrawTypeId;


    @Schema(description = "提款方式中文名称")
    private String withdrawWay;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    /**
     * 手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额
     */
    @Schema(description = "手续费类型 手续费类型 0百分比 1固定金额 2百分比+固定金额 字典code:fee_type")
    private Integer feeType;
    @Schema(description = "站点编码 ",hidden = true)
    private String siteCode;

}

package com.cloud.baowang.system.api.vo.exchange;

import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/20 20:51
 * @Version: V1.0
 **/
@Data
@Schema(description = "虚拟货币汇率配置查询条件")
public class RateReqVO extends PageVO {

    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 汇率调整方式
     */
    @Schema(description = "汇率调整方式",hidden = true)
    private String adjustWay;

   /* @Schema(description = "展示方式 WITHDRAW:取款 RECHARGE:存款")
    private ShowWayEnum showWayEnum;*/
    @Schema(description = "汇率类型",hidden = true)
    private String rateType;


   /* public ShowWayEnum getShowWayEnum() {
        return showWayEnum==null?ShowWayEnum.RECHARGE:showWayEnum;
    }*/
}

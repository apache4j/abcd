package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 兑换码生成表
 * @author BEJSON.com
 * @date 2025-10-27
 */
@Data
@NoArgsConstructor
@TableName("site_activity_redemption_gen_code_info")
public class SiteActivityRedemptionGenCodePO extends BasePO {

    /**
     * 兑换码明细表主键
     */
    private Long activityDetailId;

    /**
     * 6位兑换码，生成规则:数值字母随机组合
     */
    private String code;

    /**
     * 批次号，10位数字
     */
    private String batchNo;

    /**
     * 币种，冗余字段
     */
    private String currency;

    @Schema(description = "状态,0:未兑换,1:已兑换")
    private Integer status;

}

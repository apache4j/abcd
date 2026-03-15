package com.cloud.baowang.wallet.api.vo.recharge;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/12 09:59
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "充值通道返回参数 SiteRechargeAuthorizeResVO")
@I18nClass
public class SiteRechargeAuthorizeResVO {



    @Schema(description = "全部充值方式集合")
    private List<FeeVO> allID;

    @Schema(description = "选中充值方式集合")
    private List<SiteRechargeWayQueryVO> siteDeposit;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currency;

    @Schema(description = "充值通道返回分页参数")
    private Page<RechargeAuthorizeResVO> pageVO;
}

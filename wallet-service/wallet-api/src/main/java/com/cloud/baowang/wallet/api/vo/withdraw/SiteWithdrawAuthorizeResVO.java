package com.cloud.baowang.wallet.api.vo.withdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.wallet.api.vo.recharge.FeeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/12 11:32
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点提款授权返回参数")
@I18nClass
public class SiteWithdrawAuthorizeResVO {

    @Schema(description = "选中充值方式集合")
    private List<FeeVO> chooseID;

    @Schema(description = "全部充值方式集合")
    private List<FeeVO> allID;

    @Schema(description = "选中提款方式+提款通道反查对象")
    private List<SiteWithdrawChannelQueryVO> siteWithdraw;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "站点提款授权返回分页")
    private Page<WithdrawAuthorizeResVO> pageVO;
}

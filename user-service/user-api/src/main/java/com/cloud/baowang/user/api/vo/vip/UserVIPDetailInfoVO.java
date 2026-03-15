package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @Author : dami
 * @Date : 11/6/24 11:20 AM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP详情返回对象")
@I18nClass
public class UserVIPDetailInfoVO implements Serializable {




    /**是否显示反水特权配置*/
    @Schema(description = "是否显示反水特权配置 0 不显示 1 显示")
    private Integer rebateConfig;

    @Schema(description = "反水列表集合数据")
    private List<SiteRebateConfigWebCopyVO> rebates;

//    public BigDecimal getUpgradeVipExp() {
//        return Optional.ofNullable(upgradeVipExp).orElse(BigDecimal.ZERO);
//    }
//
//    public BigDecimal getUpgradeVipNeedExp() {
//        return Optional.ofNullable(upgradeVipNeedExp).orElse(BigDecimal.ZERO);
//    }

    @Schema(description = "vip提款额度")
    private List<UserVipWithdrawConfigCopyAPPVO> userVipWithdrawConfig;

    @Schema(description = "保级/降级 天数")
    private Integer relegationDays;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "vip等级图标")
    private String vipGradeIcon;

}

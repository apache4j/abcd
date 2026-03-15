package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员信息扩展表
 *
 * @author kimi
 * @since 2024-03-20 10:00:00
 */
@Data
@Accessors(chain = true)
@TableName("user_info_extra")
@Schema(description = "会员信息扩展表")
public class UserInfoExtraPO extends SiteBasePO implements Serializable {

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "累计投注量")
    private BigDecimal totalValidAmount;

    @Schema(description = "累计盈利")
    private BigDecimal totalWinLoseAmount;
}

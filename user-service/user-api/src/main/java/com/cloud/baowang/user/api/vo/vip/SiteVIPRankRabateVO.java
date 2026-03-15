package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * @Author : 小智
 * @Date : 2024/8/2 15:22
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP段位返回对象")
@I18nClass
public class SiteVIPRankRabateVO implements Serializable {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "VIP段位code")
    private Integer vipRankCode;

    @Schema(description = "VIP段位名称")
    private String vipRankName;

    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "vip段位名称i18Code")
    private String vipRankNameI18nCode;

    @Schema(description = "是否显示反水特权配置")
    private Integer rebateConfig;

}

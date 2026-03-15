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
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP段位返回对象")
@I18nClass
public class SiteVIPSystemRankVO implements Serializable {

    @Schema(description = "vip段位名称i18Code")
    private Integer vipRankCode;

    @Schema(description = "vip段位名称i18Code")
    @I18nField
    private String vipRankNameI18nCode;

    @Schema(description = "VIP等级左边范围值code")
    private Integer minVipGrade;

    @Schema(description = "VIP等级左边范围值Name")
    private String minVipGradeName;

    @Schema(description = "VIP等级右边范围值code")
    private Integer maxVipGrade;

    @Schema(description = "VIP等级右边范围值name")
    private String maxVipGradeName;

    @Schema(description = "当前段位图片")
    private String vipIcon;

    @Schema(description = "当前vip段位图片完整地址")
    private String vipIconImage;

    @Schema(description = "段位颜色")
    private String rankColor;

    @Schema(description = "多语言-VIP段位名称")
    private List<SiteVIPGradeVO> siteVIPGradeVOList;

}

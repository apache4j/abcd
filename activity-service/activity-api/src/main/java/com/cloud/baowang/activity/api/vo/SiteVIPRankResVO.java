package com.cloud.baowang.activity.api.vo;

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
public class SiteVIPRankResVO implements Serializable {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "VIP段位code")
    private Integer vipRankCode;

    @Schema(description = "VIP等级列表")
    private List<Integer> vipGradeList;



    @Schema(description = "VIP等级对应最小等级")
    private Integer minVipGrade;
    @Schema(description = "VIP等级对应最小等级名称")
    private String minVipGradeName;

    @Schema(description = "VIP等级对应最大等级")
    private Integer maxVipGrade;

    @Schema(description = "VIP等级对应最大等级名称")
    private String maxVipGradeName;

    @Schema(description = "VIP段位名称")
    private String vipRankName;

    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    @Schema(description = "vip段位名称i18Code")
    private String vipRankNameI18nCode;



    @Schema(description = "多语言list")
    private List<I18nMsgFrontVO> vipRankNameI18nCodeList;

    @Schema(description = "VIP段位图标")
    private String vipIcon;








}

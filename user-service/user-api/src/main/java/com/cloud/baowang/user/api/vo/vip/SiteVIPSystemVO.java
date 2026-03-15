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
public class SiteVIPSystemVO implements Serializable {

    @Schema(description = "当前VIP段位")
    private Integer currentVIPRankCode;

    @Schema(description = "当前VIP等级")
    private Integer currentVIPGradeCode;

    private List<SiteVIPSystemRankVO> siteVIPSystemRankVOList;
}

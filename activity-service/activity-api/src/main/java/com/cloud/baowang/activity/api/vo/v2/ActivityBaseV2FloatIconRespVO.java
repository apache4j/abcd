package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@Schema(title = "浮标排序列表响应结果")
@I18nClass
public class ActivityBaseV2FloatIconRespVO implements Serializable {

    @Schema(title = "浮标排序列表")
    private List<ActivityBaseV2FloatIconVO> activityBaseV2FloatIconVOList;

    @Schema(title = "浮标展示数量")
    private Integer floatIconShowNumber;

}

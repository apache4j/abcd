package com.cloud.baowang.user.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@I18nClass
@Schema(title = "未登录推荐活动返回实体")
public class ActivityRecommendResVO {

    /**
     * 弹窗宣传图PC
     */
    @Schema(title = "弹窗宣传图PC")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String picShowupPcI18nCode;

    @Schema(title = "弹窗宣传图PC——URL")
    private String picShowupPcI18nCodeFileUrl;


    /**
     * 弹窗宣传图APP
     */
    @Schema(title = "弹窗宣传图APP")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String picShowupAppI18nCode;

    @Schema(title = "弹窗宣传图APP——URL")
    private String picShowupAppI18nCodeFileUrl;


    @Schema(title = "id")
    private String id;


    @Schema(title = "活动模板")
    private String activityTemplate;

    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;

}

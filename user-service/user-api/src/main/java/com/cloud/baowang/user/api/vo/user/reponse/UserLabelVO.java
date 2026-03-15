package com.cloud.baowang.user.api.vo.user.reponse;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "后台-code和value返回对象")
public class UserLabelVO {

    @Schema(title = "类型")
    private String type;


    @Schema(title = "编码")
    private String code;


    @Schema(title = "值")
    private String value;

    @Schema(title = "标签状态 0:非定制，1定制")
    private Integer customizeStatus;
}

package com.cloud.baowang.system.api.vo.site.tutorial;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title ="构建客户端返回对象")
public class TutorialClientShowRspVO extends TutorialClientShowVO {

    @Schema(title ="子集")
    @I18nField
    private List<TutorialClientShowVO> subset;
}

package com.cloud.baowang.system.api.vo.site.tutorial;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "站点底部栏等基础返回对象")
public class SiteBasicVo {

    @Schema(title = "类型")
    private String type;


    @Schema(title = "编码 1-关于我们,3-隐私政策,4-规则与条款,5-联系我们")
    private String code;

    @I18nField
    @Schema(title = "名称")
    private String value;

    @Schema(title = "详情-多语言-富文本")
    private String valueDetail;

    @Schema(title = "详情扩展")
    private String valueDetailExtend;
}

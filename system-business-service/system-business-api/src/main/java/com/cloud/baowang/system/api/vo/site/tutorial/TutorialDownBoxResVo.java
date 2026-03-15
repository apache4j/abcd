package com.cloud.baowang.system.api.vo.site.tutorial;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title ="下拉框通用请求对象 *Id对应code,*Name对应value")
public class TutorialDownBoxResVo {

    @Schema(title ="站点id")
    private String siteCode;

    @Schema(title ="大类id")
    private String categoryId;

    @Schema(title ="大类名称")
    private String categoryName;


    @Schema(title ="fen类id")
    private String classId;

    @Schema(title ="fen类名称")
    private String className;


    @Schema(title ="页签id")
    private String tabsId;

    @Schema(title ="页签名称")
    private String tabsName;


    @Schema(title ="内容id")
    private String contentId;

    @Schema(title ="内容id")
    private String contentName;

    @Schema(title ="位置标记 页面查询-0;新增页/编辑页-1")
    private int position;


}

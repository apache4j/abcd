package com.cloud.baowang.system.api.vo.site.agreement;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商务属性对象")
@I18nClass
public class BusinessBasicClientVO {

    private String id;
     @I18nField
    private String businessName;

    private String telegram;


    private String h5IconFullUrl;

    private String pcIconFullUrl;

    @Schema(title = "排序字段 1-第一位.依次递增")
    private Integer sort;
//    @Schema(hidden = true)
//    private String siteCode;
//
//    @Schema(hidden = true)
//    private Long updatedTime;
//    @Schema(hidden = true)
//    private String updater;

}

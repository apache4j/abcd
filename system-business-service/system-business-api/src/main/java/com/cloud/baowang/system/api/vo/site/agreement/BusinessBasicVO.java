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
public class BusinessBasicVO {

    private String id;
//    @JsonIgnore
    @Schema(title = "多语言集合 只需要language和message;language取language_type中的code;格式:zh-CN")
    private List<I18nMsgFrontVO> i18nMessages;

//    private String phone;

//    private String email;
    @I18nField
    private String businessName;

    private String telegram;


    private String h5Icon;

    private String pcIcon;

    private String h5IconFullUrl;

    private String pcIconFullUrl;

    @Schema(title = "排序字段 1-第一位.依次递增")
    private Integer sort;
//
//    private String wechat;
//
//    private String qq;
//
//    private String whatsApp;
//
//    private String messenger;

    @Schema(hidden = true)
    private String siteCode;

    @Schema(hidden = true)
    private Long updatedTime;
    @Schema(hidden = true)
    private String updater;

}

package com.cloud.baowang.user.api.vo.notice.user.reponse;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(description = "通知配置的响应对象")
public class UserNoticeRespVO implements Serializable {



    @Schema(title = "消息Id")
    private String targetId;

    @Schema(title = "通知类型(1=公告、3=通知,系统消息)")
    private Integer noticeType;

    @Schema(title = "通知标题")
//    @I18nField
    private String noticeTitleI18nCode;

//    @I18nField
    @Schema(title = "通知消息内容")
    private String messageContentI18nCode;

    @Schema(title = "图片地址")
    @I18nField(type =I18nFieldTypeConstants.FILE )
    private String picIconI18nCode;

    @Schema(title = "图片地址全路径")
    private String picIconI18nCodeFileUrl;

    /*@Schema(title = "发送对象(1=全部会员、2=特定会员、3=终端 4=全部代理，5特定代理)")
    private Integer targetType;*/

    @Schema(title = "阅读状态，0=未读、1=已读")
    private Integer readState;

    @Schema(title = "创建时间")
    private Long createdTime;

    @Schema(title = "创建时间")
    private String createdTimeStr;

    private String titleConvertValue;


    private String contentConvertValue;


    private String systemMessageCode;


    /**
     * 轮播开始时间
     */
//    @Schema(title = "轮播开始时间")
//    private Long carouselStartTime;
//
//    /**
//     * 轮播结束时间
//     */
//    @Schema(title = "轮播结束时间")
//    private Long carouselEndTime;
//
//    /**
//     * 弹窗类型(1:普通弹窗2:强制弹窗)
//     */
//    @Schema(title = "弹窗类型(1:普通弹窗2:强制弹窗)")
//    private Integer popUpType;


}

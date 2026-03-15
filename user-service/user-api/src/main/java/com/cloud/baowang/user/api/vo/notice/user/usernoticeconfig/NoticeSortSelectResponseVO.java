package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "公告排序-查询 返回")
@I18nClass
public class NoticeSortSelectResponseVO {

    @Schema(title = "公告ID")
    private String id;

   /* @Schema(title = "公告标题")
    private String noticeTitle;*/

    @Schema(title = "状态(0:发送1:撤回) ")
    private Integer status;

    @Schema(title = "公告标题")
    @I18nField
    private String noticeTitleI18nCode;

}

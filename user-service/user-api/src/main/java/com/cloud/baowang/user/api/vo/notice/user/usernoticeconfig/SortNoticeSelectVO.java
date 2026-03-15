package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * @author kimi
 */
@Data
@Schema(description = "优惠活动排序-查询")
public class SortNoticeSelectVO {

    @Schema(title = "通知类型(1:会员公告 4代理公告)")
    @NotNull(message = "通知类型(1:会员公告 4代理公告) system_param(notification_sort_type)")
    private Integer noticeType;
    @Schema(title = "siteCode",hidden = true)
    private String siteCode;
}

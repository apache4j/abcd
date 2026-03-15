package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 意见反馈回复模板
 *
 * @since 2023-07-26 10:00:00
 */

@Data
@Accessors(chain = true)
@TableName("site_user_feedback_reply_template")
@Schema(description = "意见反馈回复模板")
public class SiteUserFeedbackReplyTemplatePO extends SiteBasePO {

    @Schema(description = "模板内容")
    private String content;

}

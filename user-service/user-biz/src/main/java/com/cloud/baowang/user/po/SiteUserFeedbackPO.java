package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 意见反馈
 *
 * @since 2023-07-26 10:00:00
 */

@Data
@Accessors(chain = true)
@TableName(value = "site_user_feedback")
@Schema(description = "意见反馈")
public class SiteUserFeedbackPO extends SiteBasePO {

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员层级Id，多条根据','拼接")
    private String userLabel;

    /* VIP等级code */
    @Schema(description = "会员等级code")
    private Integer vipGradeCode;

    /* VIP等级名称 */
    @Schema(description = "会员等级名称")
    private String vipGradeName;

    @Schema(description = "问题类型")
    private Integer type;

    @Schema(description = "意见内容")
    private String content;

    @Schema(description = "相关订单号")
    private String orderId;

    @Schema(description = "截图")
    private String picUrls;

    @Schema(description = "回复内容")
    private String backContent;

    @Schema(description = "回复人")
    private String backAccount;

    @Schema(description = "回复时间")
    private Long backTime;

    /**
     *
     */
    @Schema(description = "锁单状态")
    private Integer lockStatus;

    @Schema(description = "锁单人")
    private String locker;


    @Schema(description = "回复层级")
    private Integer sort;

    @Schema(description = "是否已读 1 已读 | 0 未读")
    private Integer isRead;

    @Schema(description = "后续回复的主问题id | 当前问题最新反馈Id")
    private String feedId;

}

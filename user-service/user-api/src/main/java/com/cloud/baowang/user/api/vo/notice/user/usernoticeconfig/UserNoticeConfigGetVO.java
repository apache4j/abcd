package com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知配置查询单个入参对象")
public class UserNoticeConfigGetVO extends PageVO implements Serializable {

    @Schema(title = "siteCode",hidden = true)
    private String siteCode;
    private String id;
    /**
     * 通知类型(1:公告2:活动3:通知)
     */
    @Schema(title = "通知类型(1:公告2:活动3:通知4:系统消息)")
    private String noticeType;
    /**
     * 通知标题
     */
    @Schema(title = "通知标题")
    private String noticeTitle;

    @Schema(title = "通知标题-多语言-指定",hidden = true)
    private List<String> noticeTitleCodeList;
    @Schema(title = "创建人")
    private String operator;

    @Schema(title = "创建人")
    private String founder;
    /**
     * 发送对象
     */
    @Schema(title = "发送对象")
    private String sendObject;
    /**
     * 通知消息内容
     */
    @Schema(title = "通知消息内容")
    private String messageContent;
    /**
     * 前端发送对象(1:会员2:终端)
     */
    @Schema(title = "发送对象(1:会员2:终端 4.代理)")
    private Integer targetType;

    /**
     * 会员类型 1:全部会员 2:特定会员
     */
    @Schema(title = "会员类型 1:全部会员 2:特定会员 3:全部代理 4:特定代理")
    private Integer membershipType;
    /**
     * 发发送对象旋转特定会员的的时候，指定会员类型
     * 会员类型 1:vip等级 2:主货币 3:指定会员
     * 数据库存放的时候
     * specify_membership_type
     */
    @Schema(title ="特定会员类型 1:vip等级 2:主货币 3:指定会员")
    private Integer specifyMembershipType;

    /**
     * 当选择2主货币的时候
     * currency_code
     */
    @Schema(title = "货币")
    private String currencyCode;
    /**
     * 当选择1 vip等级
     * vip_grade_mix
     */
    @Schema(title = "vip等级下限")
    private Integer vipGradeMix;
    /**
     * 当选择1 vip等级
     * vip_grade_max
     */
    @Schema(title = "vip等级上限")
    private Integer vipGradeMax;


    @Schema(title = "是否极光发送")
    private Integer isPush;
}

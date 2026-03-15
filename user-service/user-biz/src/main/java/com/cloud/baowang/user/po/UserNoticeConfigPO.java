package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("user_notice_config")
@Schema(description = "通知配置记录")
public class UserNoticeConfigPO extends SiteBasePO implements Serializable {
    /**
     * 通知类型(1:公告,2:活动,3:通知,4:系统消息)
     */
    private Integer noticeType;
    /*
    通知标题
     */
    private String noticeTitleI18nCode;

    /*
    通知消息内容
     */
    private String messageContentI18nCode;

    /**
     * 活动图内容
     */
    private String picIconI18nCode;


    /**
     * 数据库 （1:会员2:终端 4：代理 5:商务)
     * {@link com.cloud.baowang.user.api.enums.notice.MemberTargetTypeEnum}
     */
    private Integer targetType;

    /**
     * 终端对象
     */
    @Schema(title = "终端对象")
    private String terminal;

    /**
     * 终端对象
     */
    private String sendAccounts;


    /**
     * 当发送对象是会员的时候
     * 会员类型 1:全部会员 2:特定会员
     */
    @Schema(title = "会员类型 1:全部会员 2:特定会员 3:全部代理 4:特定代理")
    private Integer memberShipType;

    /**
     * 当发送对象是代理的时候
     * 会员类型 1:全部代理 2:特定代理 notice_agent_type
     */
    @Schema(title = "代理类型  1:全部代理 2:特定代理")
    private Integer noticeAgentType;


    /**
     * 发发送对象旋转特定会员的的时候，指定会员类型
     * 会员类型 1:vip等级 2:主货币 3:指定会员
     * 数据库存放的时候
     * specify_membership_type
     */
    @Schema(title = "特定会员类型 1:vip等级 2:主货币 3:指定会员")
    private Integer specifyMemberShipType;


    /**
     * 根据主货币发送
     */
    private String currencyCode;

    /**
     * vip等级下限
     */
    private Integer vipGradeMix;
    /**
     * vip等级上限
     */
    private Integer vipGradeMax;


    /*
     * 状态(0:撤回 1:发送)
     */
    private Integer status;

    /**
     * 业务线(1:VIP权益类消息2:注册,3:存款,4:取款,5:取款,6:参与活动,7:VIP晋级,8:活动奖励到账通知)
     * {@link com.maya.baowang.enums.user.UserSysMessageEnum}
     */
    //private Integer businessLine;

    /**
     * 业务线
     * (1:VIP权益类消息(1:会员VIP返水,2:会员生日礼金,3:会员升级礼金,4:会员上半月红包,5:会员下半月红包)
     * 2:注册,(1:注册成功通知)
     * 3:存款,(1:存款订单成功通知)
     * 4:取款,(1:取款订单成功通知,2:取款订单失败通知)
     * 5:参与活动,(1:参与活动通知)
     * 6:VIP晋级,(1:VIP晋级通知)
     * 7:活动奖励到账通知(1:活动奖励)
     * )
     * {@link com.maya.baowang.enums.user.UserSysMessageEnum}
     */
    //private Integer messageType;

    /*@ApiModelProperty("通知平台 1 会员 2是代理 默认是1")
    private Integer platform ;*/
    /**
     * 弹窗类型(1:跑马灯 2:强制弹窗)
     */
    private Integer popUpType;

    /**
     * 是否极光推送(0:否 1:是)是否极光推送(0:否 1:是)
     */
    @Schema(title = "是否极光推送(0:否 1:是)")
    private Integer isPush;

    /**
     * 轮播开始时间
     */
    private Long carouselStartTime;

    /**
     * 轮播结束时间
     */
    private Long carouselEndTime;

    @Schema(title = "顺序")
    private Integer sort;

    @Schema(title = "图片链接")
    private String picIcon;

    private Integer noticeMerchantType;

}

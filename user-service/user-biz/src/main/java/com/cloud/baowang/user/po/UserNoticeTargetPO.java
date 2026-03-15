package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("user_notice_target")
@Schema(description = "发送通知对象表")
public class UserNoticeTargetPO extends SiteBasePO implements Serializable {
    /*
    通知Id
     */
    private String noticeId;
    /**
     * 通知类型(1=公告、2=活动、3=通知、4=系统消息)
     */
    private Integer noticeType;
    /**
     * 发送目标：1=会员、2=插入已读
     */
    //private Integer targetType;
    /**
     * 会员账号id
     */
    private String userId;
    /**
     * 终端
     */
    //private String deviceTerminalCode;
    /**
     * 阅读状态，0=未读、1=已读
     */
    private Integer readState;
    /**
     * 撤销状态，1=正常、2=撤销
     */
    private Integer revokeState;
    /*
    删除状态，1=正常、2=删除
     */
    private Integer deleteState;
    /**
     * 通知标题
     */
    private String noticeTitleI18nCode;
    /**
     * 消息内容
     */
    private String messageContentI18nCode;
    /**
     * 业务线(1:VIP权益类消息2:注册,3:存款,4:取款,5:取款,6:参与活动,7:VIP晋级,8:活动奖励到账通知)
     * {@link com.maya.baowang.enums.user.UserSysMessageEnum}
     */
    private Integer businessLine;

    /**
     * 1.会员消息 2 代理消息
     */
    private Integer platform;

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
    private Integer messageType;


    private String titleConvertValue;


    private String contentConvertValue;


    private String systemMessageCode;
}

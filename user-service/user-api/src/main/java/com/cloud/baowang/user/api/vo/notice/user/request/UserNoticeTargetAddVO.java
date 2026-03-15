package com.cloud.baowang.user.api.vo.notice.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Schema(description = "保存发送通知对象")
public class UserNoticeTargetAddVO implements Serializable {
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
     * 1.会员消息 2 代理消息
     */
    private Integer platform;


    /**
     * 内容动态值
     */
    private String contentConvertValue;

    /**
     * 标题内容动态值
     */
    private String titleConvertValue;

    private String systemMessageCode;

}

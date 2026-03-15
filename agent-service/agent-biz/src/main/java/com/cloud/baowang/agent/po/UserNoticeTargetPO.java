package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("user_notice_target")
@Schema(description = "发送通知对象表")
public class UserNoticeTargetPO extends BasePO implements Serializable {
    /*
    通知Id
     */
    private String noticeId;
    /*
    通知类型(1=公告、2=活动、3=通知)
     */
    private Integer noticeType;
    /**
     * 发送目标：1 全体会员 2 特定会员 3 终端 4 全部代理 5 特定代理
     */
    private Integer targetType;
    /*
    会员账号
     */
    private String userAccount;
    /*
    终端
     */
    private String deviceTerminalCode;
    /*
    阅读状态，1=未读、2=已读
     */
    private Integer readState;
    /*
    撤销状态，1=正常、2=撤销
     */
    private Integer revokeState;
    /*
    删除状态，1=正常、2=删除
     */
    private Integer deleteState;

    private String titleConvertValue;


    private String contentConvertValue;


    private String systemMessageCode;

}

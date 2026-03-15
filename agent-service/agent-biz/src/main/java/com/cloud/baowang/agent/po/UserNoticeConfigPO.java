package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("user_notice_config")
@Schema(name="通知配置记录")
public class UserNoticeConfigPO extends BasePO implements Serializable {
    /*
    通知类型(1:公告2:活动3:通知)
     */
    private Integer noticeType;
    /*
    通知标题
     */
    private String noticeTitle;
    /**
     * 发送对象
     *
     * @see
     */
    private String sendObject;
    /*
    通知消息内容
     */
    private String messageContent;

    private String operator;

    /**
     * 前端含义 发送对象(1:会员2:终端)
     * 数据库含义 发送对象(1=全部会员、2=特定会员、3=终端)
     */
    private Integer targetType;

    /*
    状态(0:发送1:撤回)
     */
    private Integer status;
}

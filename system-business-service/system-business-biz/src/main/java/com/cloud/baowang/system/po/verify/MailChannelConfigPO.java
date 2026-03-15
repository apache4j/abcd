package com.cloud.baowang.system.po.verify;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:25
 * @description: 邮箱通道配置表
 */
@Data
@TableName("mail_channel_config")
public class MailChannelConfigPO extends BasePO {
    /**
     * 通道ID
     */
    private String channelId;
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 通道代码
     */
    private String channelCode;
    /**
     * 授权数量
     */
    private Integer authCount;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 请求地址
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String password;
    /**
     * 发送者
     */
    private String sender;
    /**
     * 密钥
     */
    private String apiKey;
    /**
     * 发送模板
     */
    private String template;
}

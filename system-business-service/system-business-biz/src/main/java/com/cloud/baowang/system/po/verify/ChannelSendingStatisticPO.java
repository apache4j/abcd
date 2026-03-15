package com.cloud.baowang.system.po.verify;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 通道发送统计表
 */
@Builder
@Data
@TableName("channel_sending_statistic")
public class ChannelSendingStatisticPO  {

    /**
     * 站点code
     */
    private String siteCode;


    /**
     * 站点code
     */
    private String siteName;
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 通道代码
     */
    private String channelCode;


    /**
     * 通道ID
     */
    private String channelId;


    /**
     * 通道地址
     */
    private String host;
    /**
     * 通道类型 1-短信 2-邮箱
     */

    private String channelType;


    /**
     * 使用地区
     */
    private String address;

    /**
     * 地区code
     */
    private String  addressCode;
    /**
     * 接收者
     */
    private String  receiver;

    @TableId
    private String id;

    @TableField(fill = FieldFill.INSERT, value = "created_time")
    private Long createdTime;
}

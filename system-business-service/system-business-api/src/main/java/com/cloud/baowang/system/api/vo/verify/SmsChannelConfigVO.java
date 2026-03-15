package com.cloud.baowang.system.api.vo.verify;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:21
 * @description: 短信通道配置VO
 */
@Data
public class SmsChannelConfigVO extends BaseVO {
    /**
     * 使用地区
     */
    private String address;
    /**
     * 使用地区code
     */
    private String  addressCode;
    /**
     * 区号
     */
    private String areaCode;
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
     * 平台代码
     */
    private String platformCode;
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
     * 发送模板
     */
    private String template;
    
    @Schema(title = "接收者", hidden = true)
    private String receiver;
    @Schema(title = "验证码", hidden = true)
    private String verifyCode;

}

package com.cloud.baowang.agent.api.vo.agentLogin;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : kimi
 * @Date : 11/10/23 6:15 PM
 * @Version : 1.0
 */
@Data
@Schema(description = "代理登录记录 返回分页参数")
@I18nClass
public class AgentLoginRecordPageVO implements Serializable {

    @Schema(description = "登录状态code")
    private String loginStatus;

    @Schema(description = "登录状态中文名称")
    private String loginStatusText;

    @Schema(description = "代理ID")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型code")
    private String agentType;

    @Schema(description = "代理类型中文名称")
    private String agentTypeText;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "登录IP风控层级")
    private String ipControlId;

    @Schema(description = "登录IP风控层级名称")
    private String ipControlName;

    @Schema(description = "IP归属地")
    private String ipAttribution;

    @Schema(description = "登录终端code")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.DEVICE_TYPE)
    private Integer loginDevice;

    @Schema(description = "登录终端名称")
    private String loginDeviceText;

    @Schema(description = "终端设备号")
    private String deviceNumber;

    @Schema(description = "终端设备号风控层级id")
    private String deviceControlId;

    @Schema(description = "终端设备号风控层级名称")
    private String deviceControlName;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "设备版本")
    private String deviceVersion;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "登录时间")
    private Long loginTime;
    /* 标签 */
    @Schema(description = "标签")
    private String agentLabelId;

    @Schema(description = "代理标签列表")
    private List<AgentLabelResponseVO> labelList;

    @Schema(description = "代理标签")
    private String labelNameList;

    @Schema(description = "登录时间")
    private String loginTimeText;

    @Schema(description = "登录IP/风控层级")
    private String loginIp_$_ipControlName;

    public String getLoginIp_$_ipControlName(){
        if ((this.loginIp==null || this.loginIp.equals("null")) && (this.getIpControlName()==null || this.getIpControlName().equals("null")) ){
            return "";
        }
        if (this.loginIp==null|| this.loginIp.equals("null")){
            return this.getIpControlName();
        }
        if (this.getIpControlName()==null|| this.getIpControlName().equals("null")){
            return this.loginIp;
        }
        return this.loginIp + "$" + this.getIpControlName();
    }

    @Schema(description = "终端设备号/风控层级")
    private String deviceNumber_$_deviceControlName;

    public String getDeviceNumber_$_deviceControlName(){

        if ((this.deviceNumber==null || this.deviceNumber .equals("null"))&& (this.getDeviceControlName()==null|| this.getDeviceControlName().equals("null"))){
            return "";
        }
        if (this.deviceNumber==null|| this.deviceNumber .equals("null")){
            return this.getDeviceControlName();
        }
        if (this.getDeviceControlName()==null || this.getDeviceControlName().equals("null")){
            return this.deviceNumber;
        }
        return this.deviceNumber + "$" + this.getDeviceControlName();
    }
}

package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.user.api.vo.GetUserLabelByIdsResponseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 小智
 * @Date 15/5/23 10:34 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员详情用户登录日志信息")
@I18nClass
public class UserLoginInfoVO implements Serializable {

    /* 会员账号 */
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员UserId")
    private String userId;

    /* 账号类型 */
    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(title = "账号类型 1测试 2正式")
    private String accountTypeText;

    /* ip */
    @Schema(description = "ip")
    private String ip;

    /* ip风控层级 */
    @Schema(description = "ip风控层级")
    private String ipControl;

    @Schema(description = "注册IP+ip风控层级")
    private String ip_$_ipControl;

    public String getIp_$_ipControl() {
        String ipStr = (ip != null) ? ip : "";
        return ipStr;
    }


    /* ip归属地 */
    @Schema(description = "ip归属地")
    private String ipAddress;

    /* 登录网址 */
    @Schema(description = "登录网址")
    private String loginAddress;

    /* 登录终端 */
    @Schema(description = "登录终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TERMINAL)
    private String loginTerminal;

    /* 登录终端名称 */
    @Schema(description = "登录终端名称")
    private String loginTerminalText;

    /* 登录状态 */
    @Schema(description = "登录状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.LOGIN_TYPE)
    private Integer loginType;

    @Schema(description = "登录状态名称")
    private String loginTypeText;

    /* 设备号 */
    @Schema(description = "设备号")
    private String deviceNo;

    /* 设备风控层级 */
    @Schema(description = "设备风控层级")
    private String deviceControl;

    @Schema(description = "注册IP+设备风控层级")
    private String deviceNo_$_deviceControl;

    public String getDeviceNo_$_deviceControl() {
        return deviceNo;
    }

    /* 设备版本 */
    @Schema(description = "设备版本")
    private String deviceVersion;

    /* 登录时间 */
    @Schema(description = "登录时间")
    private Long loginTime;

    /* 备注 */
    @Schema(description = "备注")
    private String remark;

    @Schema(title = "站点code")
    private String siteCode;

    @Schema(description = "会员标签列表")
    private List<GetUserLabelByIdsResponseVO> labelList;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "上级代理", hidden = true)
    private String superAgentAccount;
}

package com.cloud.baowang.user.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "会员列表 返回")
@I18nClass
public class UserInfoResponseVO {

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "上级代理ID")
    private String superAgentId;
    @Schema(description = "上级代理账号")
    private String superAgentAccount;

    @Schema(description = "总代ID")
    private String generalAgentId;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "平台币")
    private String platformCurrency = CommonConstant.PLAT_CURRENCY_CODE;


    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;
    @Schema(description = "账号类型 1测试 2正式 - Text")
    private String accountTypeText;

    @Schema(description = "会员标签id")
    private String userLabelId;
    @Schema(description = "会员标签")
    private String userLabel;

    @Schema(description = "会员标签")
    private List<GetUserLabelByIdsResponseVO> userLabelVO;

    @Schema(description = "风控层级id")
    private String riskLevelId;
    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;
    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定 - Text")
    private String accountStatusText;

    @Schema(description = "vip等级")
    private Integer vipGrade;

    @Schema(description = "vip等级名称")
    private String vipGradeName;

    @Schema(description = "vip升级后的等级")
    private Integer vipGradeUp;

    @Schema(description = "vip升级后的等级")
    private String vipGradeUpName;

    @Schema(description = "vip段位")
    private Integer vipRank;

    @I18nField
    @Schema(description = "vip段位名称")
    private String vipRankName;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "首存时间")
    private Long firstDepositTime;
    @Schema(description = "首存时间 - 用于导出")
    private String firstDepositTimeStr;


    @Schema(description = "首存金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal firstDepositAmount;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;
    @Schema(description = "最后登录时间 - 用于导出")
    private String lastLoginTimeStr;

    @Schema(description = "钱包")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal centerWalletAmount;

    @Schema(description = "平台币余额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platAmount;

    @Schema(description = "余额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal centerWalletAmount_$_platAmount;
    @Schema(description = "离线天数")
    private Integer offlineDays;
    @Schema(description = "注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private Integer registry;
    @Schema(description = "注册终端 - Text")
    private String registryText;
    @Schema(description = "站点编号")
    private String siteCode;
    @Schema(description = "站点名称")
    private String siteName;
    @Schema(description = "站点编号+站点名称")
    private String siteCode_$_siteName;
    /**
     * 会员状态 1在线 2离线
     */
    @Schema(description = "online_status")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ONLINE_STATUS)
    private Integer onlineStatus = 0;
    @Schema(description = "账号类型 1测试 2正式 - Text")
    private String onlineStatusText;
    @Schema(description = "注册ip")
    private String registerIp;
    @Schema(description = "注册IP归属地")
    private String registerIpAttribution;
    @Schema(description = "最后登录ip")
    private String lastLoginIp;
    @Schema(description = "邀请人")
    private String Inviter;
    //friend_invite_code
    @Schema(description = "邀请码")
    private String friendInviteCode;
    @Schema(description = "ETH地址逗号分隔")
    private String ethAddress;
    @Schema(description = "TRON地址逗号分隔")
    private String tronAddress;
    @Schema(description = "手机区号")
    private String areaCode;
    @Schema(description = "手机号码")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "ip注册归属")
    private String ipaddress;
    @Schema(description = "盘口模式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_HANDICAP_MODE)
    private Integer handicapMode;

    @Schema(description = "0:国际盘 1:大陆盘")
    private String handicapModeText;


    public BigDecimal getCenterWalletAmount_$_platAmount() {
        return centerWalletAmount;
    }

    public String getSiteCode_$_siteName() {
        return siteCode;
    }

    public String getRegisterTimeStr() {
        if (CurrReqUtils.getTimezone() != null) {
            return TimeZoneUtils.formatTimestampToTimeZone(registerTime, CurrReqUtils.getTimezone());
        }
        return "";

    }

    public String getFirstDepositTimeStr() {
        if (CurrReqUtils.getTimezone() != null) {
            return TimeZoneUtils.formatTimestampToTimeZone(firstDepositTime, CurrReqUtils.getTimezone());
        }
        return "";
    }

    public String getLastLoginTimeStr() {
        if (CurrReqUtils.getTimezone() != null) {
            return TimeZoneUtils.formatTimestampToTimeZone(lastLoginTime, CurrReqUtils.getTimezone());
        }
        return "";
    }
}

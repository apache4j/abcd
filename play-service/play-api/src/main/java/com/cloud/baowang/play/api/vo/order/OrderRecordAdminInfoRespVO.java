package com.cloud.baowang.play.api.vo.order;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@I18nClass
public class OrderRecordAdminInfoRespVO {
    @Schema(description = "投注人信息")
    private BettorInfo bettorInfo;
    @Schema(description = "表头信息")
    private List<CodeValueNoI18VO> tableHead;
    @Schema(description = "注单信息head")
    private List<CodeValueNoI18VO> infoHead;
    @Schema(description = "表格内容信息")
    private List<Map<String, Object>> tableValue;
    @Schema(description = "注单信息返回对象")
    private OrderInfoVO orderInfo;


    @Data
    @I18nClass
    public static class BettorInfo {
        @Schema(description = "会员ID")
        private String userAccount;
        @Schema(description = "账号类型 1测试 2正式 3商务 4置换")
        @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
        private String accountType;
        @Schema(description = "账号类型名称")
        private String accountTypeText;
        @Schema(description = "账号状态")
        @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
        private String accountStatus;
        @Schema(description = "账号状态名称")
        private String accountStatusText;
        @Schema(description = "上级代理id")
        private String agentId;
        @Schema(description = "上级代理")
        private String agentAcct;
        @Schema(description = "该类游戏总输赢")
        private String classWinLossAmount;
        @Schema(description = "游戏账号")
        private String casinoUserName;
        @Schema(description = "VIP段位")
        private Integer vipRank;
        @Schema(description = "VIP段位-文本")
        private String vipRankText;
        @Schema(description = "VIP等级")
        private Integer vipGradeCode;
        @Schema(description = "VIP等级-文本")
        private String vipGradeText;
        @Schema(description = "币种")
        private String currency;
        @Schema(description = "站点code")
        private String siteCode;
        @Schema(description = "站点名称")
        private String siteName;
    }
}

package com.cloud.baowang.user.api.vo.user;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.user.api.vo.GetUserLabelByIdsResponseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 小智
 * @Date 11/5/23 5:30 PM
 * @Version 1.0
 */
@I18nClass
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户概要信息")
public class UserSummaryVO implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;


    @Schema(description = "账号类型 1测试 2正式 ")
    private Integer accountType;


    @Schema(description = "账号类型 1测试 2正式 ")
    private String accountTypeName;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private List<CodeValueVO> accountStatusName;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "离线天数")
    private Integer offlineDays;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "注册ip")
    private String registerIp;

    @Schema(description = "注册ip风控层级")
    private String registerIpRiskLevel;

    @Schema(description = "注册端")
    private Integer registry;

    @Schema(description = "注册端名称")
    private String registryName;

    @Schema(description = "上级代理id")
    private String superAgentId;

    @Schema(description = "上级代理名称")
    private String superAgentAccount;

    @Schema(description = "会员标签id")
    private List<String> userLabelId;

    @Schema(description = "会员标签名称")
    private String userLabelName;
    @Schema(description = "会员标签名称")
    private List<GetUserLabelByIdsResponseVO> userLabelNames;

    @Schema(description = "转代次数")
    private Integer transAgentTime;

    @Schema(description = "会员是否有修改审核的内容(0:无,1:有)")
    private Integer userReviewStatus;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "推荐人")
    private String inviter;

    @Schema(description = "tron热钱包地址")
    private String trcAddress;

    @Schema(description = "ETH热钱包地址")
    private String ethAddress;

    @Schema(description = "会员域名")
    private String memberDomain;
}

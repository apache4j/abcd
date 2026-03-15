package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 会员列表 Request
 *
 * @author kimi
 * @since 2024-03-20 10:00:00
 */
@Data
@Schema(description = "会员列表 Request")
public class UserInfoPageVO extends PageVO {

    @Schema(description = "注册时间-开始")
    private Long registerTimeStart;

    @Schema(description = "注册时间-结束")
    private Long registerTimeEnd;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private List<Integer> accountStatus;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "首存时间-开始")
    private Long firstDepositTimeStart;

    @Schema(description = "首存时间-结束")
    private Long firstDepositTimeEnd;

    @Schema(description = "账号类型 1-测试 2-正式")
    private List<Integer> accountType;


    @Schema(description = "注册终端")
    private Integer registry;


    @Schema(description = "注册ip")
    private String registerIp;

    @Schema(description = "最后登录ip")
    private String lastLoginIp;

    @Schema(description = "上级代理id")
    private String superAgentId;
    @Schema(description = "上级代理id")
    private String superAgentAccount;

    @Schema(description = "0-没有代理 1-有代理 为空-所有 ")
    private String agentAccountType;

    @Schema(description = "会员标签id")
    private List<String> userLabelIds;

    @Schema(description = "最后登录时间-开始")
    private Long lastLoginTimeStart;

    @Schema(description = "最后登录时间-结束")
    private Long lastLoginTimeEnd;

    @Schema(description = "离线天数-开始")
    private Integer offlineDaysStart;

    @Schema(description = "离线天数-结束")
    private Integer offlineDaysEnd;

    @Schema(description = "vip等级-开始")
    private Integer vipGradeStart;

    @Schema(description = "vip等级")
    private Integer vipGrade;

    @Schema(description = "vip等级-结束")
    private Integer vipGradeEnd;

    @Schema(description = "vip段位")
    private Integer vipRank;

    @Schema(description = "站点名称")
    private String siteCode;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "是否总台查询", hidden = true)
    private Boolean isAll = false;

    @Schema(description = "是否包含代理 1:包含 0:不包含 空:所有 ")
    private Integer agentFlag;

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

}

package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author mufan
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_change_record_cn")
public class SiteVipChangeRecordCnPO extends BasePO implements Serializable {

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "升降级标识(0:升级,1:降级")
    private Integer changeType;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;

    @Schema(description = "账号状态")
    private String accountStatus;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "会员标签")
    private String userLabel;

    @Schema(description = "会员风控层级id")
    private String userRiskLevelId;

    @Schema(description = "会员风控层级")
    private String userRiskLevel;

    @Schema(description = "变更前VIP段位")
    private Integer vipOld;

    @Schema(description = "变更后VIP段位")
    private Integer vipNow;

    @Schema(description = "是否人工调级0自动升级 1人工调级")
    private Integer isArtificialChange;

    @Schema(description = "升级到VIP等级的初始时间yyyy-mm-dd")
    private String upVipTime;


}

package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务基础信息
 */
@Data
@Schema(description = "商务基础数据分页vo")
@I18nClass
public class AgentMerchantPageRespVO implements Serializable {

    /**
     * 商务id-短
     */
    @Schema(description = "商务id-短")
    private String merchantId;

    /**
     * 商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    /**
     * 商务名称
     */
    @Schema(description = "商务名称")
    private String merchantName;


    /**
     * 账号状态 1正常 2登录锁定
     */
    @Schema(description = "账号状态 1正常 2登录锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String status;

    private String statusText;

    @Schema(description = "风控等级")
    private String riskLevel;

    @Schema(description = "风控id")
    private String riskId;

    @Schema(description = "下线总代总数")
    private Long agentCount;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Long updatedTime;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "当前商务是否存在包含待审核的流程,0.否,1.是,为1则禁用信息修改按钮")
    private Integer isHavePending;

}

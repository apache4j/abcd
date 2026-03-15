package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 小智
 * @Date 8/5/23 4:53 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP变更记录返水对象")
@I18nClass
@Builder
public class VIPChangeVO implements Serializable {

    @Schema(title = "变更类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VIP_CHANGE_TYPE)
    private String changeType;

    @Schema(title = "站点code")
    private String siteCode;

    @Schema(title = "变更类型名称")
    private String changeTypeText;

    @Schema(title = "变更前")
    private Integer changeBefore;

    @Schema(title = "变更后")
    private Integer changeAfter;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "会员id")
    private String userId;

    @Schema(title = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(title = "账号类型名称")
    private String accountTypeText;

    @Schema(title = "会员标签")
    private String userLabel;

    @Schema(title = "会员标签名称")
    private String userLabelText;

    @Schema(title = "风控层级")
    private String controlRank;

    @Schema(title = "风控层级名称")
    private String controlRankText;

    @Schema(title = "账号状态")
    private String accountStatus;

    @Schema(title = "账号状态名称")
    private List<CodeValueVO> accountStatusName;

    @Schema(title = "操作人")
    private String operator;

    @Schema(title = "变更时间")
    private Long changeTime;
}

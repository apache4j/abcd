package com.cloud.baowang.user.api.vo.user.reponse;



import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "会员列表-信息编辑-下拉框")
@I18nClass
public class UserListInformationDownVO {

    @Schema(title = "账号类型")
    private List<CodeValueVO> accountType;

    @Schema(title = "会员标签")
    private List<UserLabelVO> userLabel;

    @Schema(title = "会员风险层级")
    private List<UserRiskVO> riskControlLevel;

    @Schema(title = "账号状态")
    private List<CodeValueVO> accountState;

    /*@Schema(title = "性别")
    private List<CodeValueVO> gender;*/

    @Schema(title = "VIP等级")
    private List<CodeValueNoI18VO> vipGrade;

    @Schema(title = "货币列表")
    private List<CodeValueVO> currency;

    @Schema(title = "VIP段位")
    private List<CodeValueVO> vipRank;

    @Schema(title = "注册终端")
    private List<CodeValueVO> registry;

    @Schema(title = "会员状态")
    private List<CodeValueVO> onlineStatus;

}

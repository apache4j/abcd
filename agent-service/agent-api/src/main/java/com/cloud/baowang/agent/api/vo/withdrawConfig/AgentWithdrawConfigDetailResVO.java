package com.cloud.baowang.agent.api.vo.withdrawConfig;

import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoBasicVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(title = "代理提款设置 详情")
public class AgentWithdrawConfigDetailResVO {

    @Schema(title = "状态", description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(title = "状态")
    private String statusText;
    @Schema(title = "代理基本信息", description = "代理基本信息")
    private AgentInfoBasicVO agentInfoBasicVO;

//    @Schema(title = "配置详情", description = "配置详情")
//    private List<AgentWithdrawConfigDetailVO> detailList;

    @Schema(title = "配置详情", description = "配置详情 币种为key-提款方式 -> 1对多")
    private List<AgentWithdrawDetailRspVO> detailTotalList;
}

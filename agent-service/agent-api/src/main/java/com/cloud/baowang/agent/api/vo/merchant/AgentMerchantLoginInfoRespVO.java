package com.cloud.baowang.agent.api.vo.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "商务登录信息")
@I18nClass
public class AgentMerchantLoginInfoRespVO implements Serializable {
    @Schema(description = "分页数据")
    private Page<AgentMerchantLoginInfoVO> pages;
    @Schema(description = "总登录次数")
    private Long allLoginCount;
    @Schema(description = "登录成功次数")
    private Long successLoginCount;
    @Schema(description = "登录失败次数")
    private Long failLoginCount;
}

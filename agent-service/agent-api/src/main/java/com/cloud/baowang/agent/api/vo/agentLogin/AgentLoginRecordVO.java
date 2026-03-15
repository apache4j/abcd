package com.cloud.baowang.agent.api.vo.agentLogin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : kimi
 * @Date : 11/10/23 6:36 PM
 * @Version : 1.0
 */
@Data
@Schema(title = "代理登录记录 返回结果参数")
@I18nClass
public class AgentLoginRecordVO implements Serializable {

    @Schema(title = "总登录次数")
    private Long totalLoginCount;

    @Schema(title = "登录成功次数")
    private Long successLoginCount;

    @Schema(title = "登录失败次数")
    private Long failLoginCount;

    @Schema(title = "代理登录信息分页返回对象")
    private Page<AgentLoginRecordPageVO> agentLoginRecordPageVO;
}

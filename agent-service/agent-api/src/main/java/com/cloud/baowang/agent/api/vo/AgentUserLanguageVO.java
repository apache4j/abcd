package com.cloud.baowang.agent.api.vo;


import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description
 * @auther amos
 * @create 2024-10-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentUserLanguageVO implements Serializable {


    private String  userId;
    @Schema(description = "用户类别（0:用户；1:代理）")
    @NotNull(message = "用户类型不以为空")
    private String userType;

    @Schema(description = "消息类型")
    @NotNull(message = "消息类型不能为空")
    private SystemMessageEnum messageType;

}

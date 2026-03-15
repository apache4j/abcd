package com.cloud.baowang.play.api.vo.lobby;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: sheldon
 * @Date: 4/2/24 10:28 上午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "赌场体育彩票请求入参对象")
public class LobbyLTRequestVO {

    @Schema(description = "一级分类模板,体育:PE,彩票:LT")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String modelCode;



}

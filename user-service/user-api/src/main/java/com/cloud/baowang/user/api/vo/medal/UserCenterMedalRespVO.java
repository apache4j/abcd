package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/9 11:47
 * @Version: V1.0
 **/
@Data
@Schema(description = "用户个人中心TOP N勋章")
@I18nClass
public class UserCenterMedalRespVO {
    @Schema(description = "可点亮勋章数量")
    private long canLightNum;

    @Schema(description = "用户个人中心TOP N勋章列表")
    private List<UserCenterMedalRespDetailVO> userCenterMedalDetailRespVoList;

}

package com.cloud.baowang.user.api.vo.user;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 11/5/23 5:27 PM
 * @Version 1.0
 */
@I18nClass
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户基本信息")
public class UserBasicVO implements Serializable {

    @Schema(description ="id")
    private String id;

    @I18nField
    @Schema(description ="概要信息")
    private UserSummaryVO userSummaryVO;

    @Schema(description ="个人资料")
    private UserPersonalVO userPersonalVO;

    @Schema(description ="VIP信息")
    private UserVIPVO userVIPVO;

    @Schema(description ="是否脱敏")
    private Boolean dataDesensitization ;
}

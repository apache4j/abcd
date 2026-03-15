package com.cloud.baowang.system.api.vo.site;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : mufan
 * @Date : 2025/4/7 15:51
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点变更查询参数")
public class SiteInfoChangeRequestVO extends PageVO {

    @Schema(title = "操作对象名称")
    private String optionName;

    @Schema(title = "操作对象code")
    private String optionCode;

    @Schema(description = "操作模块名称，对应站点列表")
    private String optionModelName;

    @Schema(description = "0:新增、1:修改,2:删除等 SiteOptionTypeEnum")
    private Integer optionType;

    @Schema(description = "0:失败,1:成功 SiteOptionStatusEnum")
    private Integer optionStatus;

    @Schema(description = "登入ip")
    private String loginIp;

    @Schema(description ="操作开始时间")
    private Long startTime;

    @Schema(description ="操作结束时间")
    private Long endTime;

    @Schema(description = "操作人")
    private String operator;
}

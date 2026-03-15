package com.cloud.baowang.user.api.vo;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 11/5/23 7:45 PM
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户详情财务信息传入参数")
public class UserFinanceQueryVO extends PageVO implements Serializable{

    @NotNull(message = "userAccount can not be empty")
    @Schema(description ="会员账号")
    private String userAccount;

    @Schema(description ="站点编号")
    private String siteCode;


    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;



    private Boolean dataDesensitization = true;

}

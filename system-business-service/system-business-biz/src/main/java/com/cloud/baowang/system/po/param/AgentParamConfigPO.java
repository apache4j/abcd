package com.cloud.baowang.system.po.param;

import com.baomidou.mybatisplus.annotation.TableName;

import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@TableName("agent_param_config")
public class AgentParamConfigPO extends BasePO implements Serializable {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(title = "名称代码")
    private String paramCode;

    @Schema(title = "名称")
    private String paramName;

    @Schema(title = "类型: 1=百分比、2=固定值")
    private Integer paramType;

    @Schema(title = "值")
    private String paramValue;

    @Schema(title = "类型限制: 1=百分比和固定值、2=只有固定值")
    private Integer paramTypeLimit;

    @Schema(title = "创建者的账号")
    private String createName;

    @Schema(title = "修改者的账号")
    private String updateName;

    //job _handler
    @Schema(title = "关联定时任务")
    private String jobHandler;


}

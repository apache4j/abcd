/**
 * @(#)AgentLabel.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.label;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/10/12
 */
@Data
@Schema( title = "代理标签返回对象", description = "代理标签返回对象")
public class AgentLabelListVO  {
    @Schema( title = "id")
    private String id;

    @Schema( title = "标签名称")
    private String name;

    @Schema( title = "标签描述")
    private String description;

    @Schema( title = "标签人数")
    private Long userCount;

    @Schema( title = "创建人")
    private String creatorName;

    @Schema( title = "操作人")
    private String operator;

    @Schema( title = "创建时间")
    private Long createdTime;

    @Schema( title = "操作时间")
    private Long updatedTime;


}

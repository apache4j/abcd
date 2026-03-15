/**
 * @(#)AgentLabel.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.label;


import com.cloud.baowang.common.core.constants.CommonConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/10/12
 */
@Data
@Schema( title = "代理标签返回对象-包括该账号是否标记该标签", description = "代理标签返回对象-包括该账号是否标记该标签")
public class AgentLabelUserResponseVO {
    
    @Schema( title = "标签名称")
    private String label;

    @Schema( title = "会员数量")
    private Integer count;

    @Schema( title = "会员账号集合")
    private String userAccounts;

    @Schema( title = "会员账号")
    private String userAccount;

    @Schema( title = "该会员是否标记该标签 0=是没有标记 1=该会员标记了该标签")
    private Integer isLabel = CommonConstant.business_zero;


}

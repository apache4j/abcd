package com.cloud.baowang.user.api.vo.user.request;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员注册信息 请求参数")
public class UserRegistrationInfoReqVO extends PageVO implements Serializable {

    @Schema(description = "开始注册时间")
    private Long startRegistrationTime;

    @Schema(description = "结束注册时间")
    private Long endRegistrationTime;

    @Schema(description = "账号类型s")
    private List<String> memberType;

    @Schema(description = "账号类型")
    private Integer accountType;

    @Schema(description = "注册终端")
    private List<String> registerTerminal;

    @Schema(description = "会员ID")
    private String memberId;

    @Schema(description = "上级代理")
    private String superiorAgent;

    @Schema(description = "注册IP")
    private String registerIp;

    @Schema(description = "IP归属地")
    private String ipAttribution;

    @Schema(description = "会员账号")
    private String memberAccount;


    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机")
    private String phone;


    @Schema(description = "站点site",hidden = true)
    private String siteCode;

    //是否归属某个代理 1:归属 0:不归属 为空查询全部
    private Integer ownerAgent;

    private Boolean dataDesensitization;

    public Boolean getDataDesensitization() {
        return dataDesensitization ==null?Boolean.TRUE:dataDesensitization;
    }
}

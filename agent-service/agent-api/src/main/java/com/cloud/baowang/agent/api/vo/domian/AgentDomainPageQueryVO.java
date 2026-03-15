package com.cloud.baowang.agent.api.vo.domian;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "域名管理分页查询对象")
public class AgentDomainPageQueryVO extends PageVO implements Serializable {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "当前站点所属时区", hidden = true)
    private String timezone;

    @Schema(description = "登陆者", hidden = true)
    private String agentAccount;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "时间范围(0:今天,-7:近7天,-30:近30天) 9999:自定义")
    private Integer dateNum;

    @Schema(description = "自定义开始时间")
    private Long startTime;

    @Schema(description = "自定义结束时间")
    private Long endTime;


    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domainName;


    /**
     * 使用域名设置相同类型(system服务枚举)
     * AGENT_BACKEND(1, "代理后台"),
     * WEB_PORTAL(2, "网页端"),
     * SITE_BACKEND(3, "站点后台"),
     * BACKEND(4, "后端"),
     * DOWNLOAD_PAGE(5, "下载页"),
     * {@link com.cloud.baowang.common.core.enums.DomainInfoTypeEnum}
     */
    @Schema(description = "域名类型: 1=代理后台、2=网页端 system_param site_domain_type code")
    private Integer domainType;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

}

package com.cloud.baowang.system.api.vo.member;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "登录日志VO对象")
public class BusinessLoginInfoQueryVO extends PageVO {

    @Schema(description = "用户名称")
    private String userName;


    @Schema(description = "登录IP")
    private String ipaddr;

    @Schema(description = "业务ID 前端不传")
    private String businessId;

}

package com.cloud.baowang.common.core.vo.base;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ford
 * 站点分页
 */
@Data
@Schema(title = "站点-分页公共对象")
public class SitePageVO extends PageVO{

    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

}

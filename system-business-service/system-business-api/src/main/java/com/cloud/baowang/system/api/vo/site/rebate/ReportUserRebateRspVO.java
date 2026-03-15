package com.cloud.baowang.system.api.vo.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "审核列表vo")
@I18nClass
public class ReportUserRebateRspVO implements Serializable {
    private Page<ReportUserRebateInfoVO> pageInfo;
    private ReportUserRebateInfoVO totalRebateInfo;
}

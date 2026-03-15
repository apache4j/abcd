package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/6/27 19:23
 * @Version: V1.0
 **/
@Data
@Schema(title ="保证金帐变记录查询结果")
@I18nClass
public class SiteSecurityChangeLogAllRespVO {
    @Schema(title = "数据列表")
    private Page<SiteSecurityChangeLogRespVO> siteSecurityChangeLogRespVOPage;
    @Schema(title = "当前页")
    private SiteSecurityChangeLogRespVO currentPage;
    @Schema(title = "总计")
    private SiteSecurityChangeLogRespVO totalPage;

}

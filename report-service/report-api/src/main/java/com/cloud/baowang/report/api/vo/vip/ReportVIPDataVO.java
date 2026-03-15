package com.cloud.baowang.report.api.vo.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/11/6 19:39
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Data
@Schema(description = "vip报表查询返回数据")
public class ReportVIPDataVO implements Serializable {

    @Schema(title =   "全部合计")
    private ReportVIPDataPage totalPage;

    @Schema(title =   "分页列表")
    @I18nField
    private Page<ReportVIPDataPage> pageList;
}

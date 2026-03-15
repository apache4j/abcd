package com.cloud.baowang.report.api.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(title = "会员报表列表返回对象Response")
@I18nClass
public class UserInfoStatementResponseVO implements Serializable {

    //分页数据
    @Schema(title  = "分页数据")
    private Page<ReportUserInfoStatementVO> reportUserInfoStatementVOList;

    @Schema(title  = "小计数据")
    private ReportUserInfoStatementVO currentPage;
    @Schema(title  = "总计数据")
    private ReportUserInfoStatementVO totalPage;
}

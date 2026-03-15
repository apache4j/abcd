package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员存取报表返回对象")
public class ReportUserDepositWithdrawResponseVO {

    /**
     * 小计
     */
    private ReportUserDepositWithdrawVO currentPage;

    /**
     * 总计
     */
    private ReportUserDepositWithdrawVO totalPage;
    /**
     * 列表数据
     */
    private Page<ReportUserDepositWithdrawVO> page;
}

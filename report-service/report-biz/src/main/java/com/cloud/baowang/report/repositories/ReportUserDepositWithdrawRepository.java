package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawRequestVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserDepositWithdrawVO;
import com.cloud.baowang.report.po.ReportUserDepositWithdrawPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportUserDepositWithdrawRepository extends BaseMapper<ReportUserDepositWithdrawPO> {

    /**
     * 获取会员存取款总计
     * @param vo
     * @return
     */
    ReportUserDepositWithdrawVO sumReportDepositWithdraw(@Param("vo") ReportUserDepositWithdrawRequestVO vo);

    List<ReportUserDepositWithdrawVO> reportDepositWithdrawList(@Param("vo") ReportUserDepositWithdrawRequestVO vo);
}

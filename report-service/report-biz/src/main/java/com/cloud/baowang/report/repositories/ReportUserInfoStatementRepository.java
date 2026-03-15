package com.cloud.baowang.report.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cloud.baowang.report.api.vo.ReportUserInfoStatementPageVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserInfoStatementVO;
import com.cloud.baowang.report.api.vo.UserInfoStatementAgentPageVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLosePageVO;
import com.cloud.baowang.report.po.ReportUserInfoStatementPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportUserInfoStatementRepository extends BaseMapper<ReportUserInfoStatementPO> {


    Page<ReportUserInfoStatementVO> findPageList(Page<ReportUserInfoStatementPO> page, @Param("vo") ReportUserInfoStatementPageVO vo);

    Long findTotalCount(@Param("vo") ReportUserInfoStatementPageVO vo);

    /**
     * 按照 货币进行group by
     * @param vo
     * @return
     */
    List<ReportUserInfoStatementVO> getReportUserInfoStatementList(@Param("vo") ReportUserInfoStatementPageVO vo);

    Page<UserInfoStatementAgentPageVO> findPageListByAgent(Page<ReportUserInfoStatementPO> page, @Param("vo") ReportUserInfoStatementPageVO vo);


    Page<ReportUserInfoStatementResponseVO> pageListUserAccount(Page<ReportUserInfoStatementResponseVO> page, @Param("vo") ReportUserInfoStatementPageVO vo);
}




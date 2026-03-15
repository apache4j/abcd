package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.vo.ActivityOrderRecordReqVO;
import com.cloud.baowang.activity.api.vo.ActivityOrderRecordRespVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceReqVO;
import com.cloud.baowang.activity.api.vo.finance.ActivityFinanceRespVO;
import com.cloud.baowang.activity.po.SiteActivityOrderRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SiteActivityOrderRecordRepository extends BaseMapper<SiteActivityOrderRecordPO> {

    /**
     * 活动数据报表查询
     *
     * @param page 请求参数
     * @param vo   入参
     * @return
     */
    IPage<ActivityOrderRecordRespVO> getActivityOrderRecordPage(@Param("page") IPage<ActivityOrderRecordRespVO> page,
                                                                @Param("vo") ActivityOrderRecordReqVO vo);


    /**
     *  活动记录查询
     * @param page
     * @param activityFinanceReqVO
     * @return
     */

    Page<ActivityFinanceRespVO> financeListPage(@Param("page") IPage<ActivityFinanceRespVO> page,@Param("vo")ActivityFinanceReqVO activityFinanceReqVO);
}

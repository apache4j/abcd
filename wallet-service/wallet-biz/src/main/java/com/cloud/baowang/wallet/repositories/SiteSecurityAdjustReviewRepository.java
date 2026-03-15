package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.siteSecurity.*;
import com.cloud.baowang.wallet.po.SiteSecurityAdjustReviewPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SiteSecurityAdjustReviewRepository  extends BaseMapper<SiteSecurityAdjustReviewPO> {

    Page<SiteSecurityAdjustReviewVO> getReviewPage(Page<SiteSecurityAdjustReviewVO> page,
                                              @Param("vo") SiteSecurityReviewPageReqVO vo,
                                              @Param("adminName") String adminName);

    Page<SiteSecurityAdjustReviewLogVO> logsPageList(Page<SiteSecurityAdjustReviewLogVO> page,
                                                     @Param("vo") SiteSecurityReviewLogPageReqVO vo,
                                                     @Param("adminName") String adminName);

    Long logsPageListTotalCount(@Param("vo") SiteSecurityReviewLogPageReqVO vo);


    SiteSecurityAdjustReviewDetailVO detail(@Param("id") String id);
}

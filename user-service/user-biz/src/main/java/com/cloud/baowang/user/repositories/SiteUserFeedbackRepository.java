package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteRespVO;
import com.cloud.baowang.user.po.SiteUserFeedbackPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 会员意见反馈 Mapper 接口
 */
@Mapper
public interface SiteUserFeedbackRepository extends BaseMapper<SiteUserFeedbackPO> {
    Page<SiteUserFeedbackSiteRespVO> feedbackList(Page<SiteUserFeedbackSiteRespVO> page, @Param("vo") SiteUserFeedbackSiteReqVO reqVO, @Param("siteCode") String siteCode);

    List<SiteUserFeedbackPO> latestReply(@Param("vo") List<String> feedIds);
}
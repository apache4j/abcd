package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.SiteUserFeedbackPO;
import com.cloud.baowang.user.po.SiteUserFeedbackReplyTemplatePO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 会员意见反馈回复模板 Mapper 接口
 *
 */
@Mapper
public interface SiteUserFeedbackReplyTemplateRepository extends BaseMapper<SiteUserFeedbackReplyTemplatePO> {
}
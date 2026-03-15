package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageReqVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigRecordPageResVO;
import com.cloud.baowang.user.po.SiteUserLabelConfigRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员标签配置记录 Mapper 接口
 *
 * @author 阿虹
 * @since 2023-05-09 10:00:00
 */
@Mapper
public interface SiteUserLabelConfigRecordRepository extends BaseMapper<SiteUserLabelConfigRecordPO> {
    Page<UserLabelConfigRecordPageResVO> getLabelConfigRecordPage(Page<UserLabelConfigRecordPageResVO> page, @Param("vo") UserLabelConfigRecordPageReqVO vo, @Param("siteCode") String siteCode);

    Long getLabelConfigRecordTotal(@Param("vo") UserLabelConfigRecordPageReqVO vo, @Param("siteCode") String siteCode);
}
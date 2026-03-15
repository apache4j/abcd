package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageResponseVO;
import com.cloud.baowang.user.po.SiteUserLabelConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员标签配置 Mapper 接口
 *
 * @author 阿虹
 * @since 2023-05-04 10:00:00
 */
@Mapper
public interface SiteUserLabelConfigRepository extends BaseMapper<SiteUserLabelConfigPO> {
    Page<UserLabelConfigPageResponseVO> getLabelConfigPage(Page<UserLabelConfigPageResponseVO> page, @Param("vo") UserLabelConfigPageRequestVO vo, @Param("siteCodes") List<String> siteCodes);
}
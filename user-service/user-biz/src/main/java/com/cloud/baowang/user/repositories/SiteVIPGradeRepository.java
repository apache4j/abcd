package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author : 小智
 * @Date : 2024/8/2 14:52
 * @Version : 1.0
 */
@Mapper
public interface SiteVIPGradeRepository extends BaseMapper<SiteVIPGradePO> {
    Page<SiteVIPGradeVO> queryVIPGradePage(@Param("page") Page<SiteVIPGradeVO> page,
                                           @Param("siteCode") String siteCode);
}

package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.po.SiteVIPRankPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author : 小智
 * @Date : 2024/8/2 14:54
 * @Version : 1.0
 */
@Mapper
public interface SiteVIPRankRepository extends BaseMapper<SiteVIPRankPO> {
    Page<SiteVIPRankVO> queryVIPRankPage(@Param("page") Page<SiteVIPRankPO> page,
                                         @Param("siteCode") String siteCode);

    @Select("select " +
            "rank_color as rankColor , " +
            "vip_icon as vipIcon ," +
            "rebate_config as rebateConfig " +
            "from site_vip_rank where site_code = #{siteCode} and vip_rank_code = #{vipRank}")
    SiteVIPRankPO getVipRankByCodeAndSiteCode(@Param("siteCode") String siteCode,
                                              @Param("vipRank") Integer vipRank);

}

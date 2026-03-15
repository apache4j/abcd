package com.cloud.baowang.activity.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityRedBagConfigPO;
import org.apache.ibatis.annotations.Mapper;


/**
* @author awei
* @description 针对表【site_activity_red_bag_config(红包雨活动配置附表)】的数据库操作Mapper
* @createDate 2024-09-12 18:56:06
* @Entity generator.domain.SiteActivityRedBagConfigPO
*/
@Mapper
public interface SiteActivityRedBagConfigRepository extends BaseMapper<SiteActivityRedBagConfigPO> {

}
package com.cloud.baowang.activity.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.activity.po.SiteActivityRedBagSessionPO;
import org.apache.ibatis.annotations.Mapper;

/**
* @author awei
* @description 针对表【site_activity_red_bag_session(红包雨活动场次历史表)】的数据库操作Mapper
* @createDate 2024-09-14 16:58:10
*/
@Mapper
public interface SiteActivityRedBagSessionRepository extends BaseMapper<SiteActivityRedBagSessionPO> {

}





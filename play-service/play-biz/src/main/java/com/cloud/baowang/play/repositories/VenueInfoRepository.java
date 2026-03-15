package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloud.baowang.play.api.vo.venue.VenueInfoRequestVO;
import com.cloud.baowang.play.po.VenueInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏平台 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface VenueInfoRepository extends BaseMapper<VenueInfoPO> {

    IPage<VenueInfoPO> queryVenueInfoPage(IPage iPage, @Param("vo") VenueInfoRequestVO vo);


    IPage<VenueInfoPO> querySiteVenueInfoPage(IPage iPage, @Param("vo") VenueInfoRequestVO vo);

}

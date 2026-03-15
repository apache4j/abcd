package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.GameTwoCurrencySortPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏信息 Mapper 接口
 *
 * @author sheldon
 */
@Mapper
public interface GameTwoCurrencySortRepository extends BaseMapper<GameTwoCurrencySortPO> {


    Integer getMaxGameOneHomeSort(@Param("siteCode") String siteCode, @Param("gameOneId") String gameOneId,
                               @Param("currencyCode") String currencyCode);


    Integer getMaxGameOneHotSort(@Param("siteCode") String siteCode, @Param("gameOneId") String gameOneId,
                              @Param("currencyCode") String currencyCode);

}

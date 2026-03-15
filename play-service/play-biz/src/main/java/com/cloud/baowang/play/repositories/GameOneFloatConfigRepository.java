package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.vo.lobby.LobbyGameOneFloatVO;
import com.cloud.baowang.play.api.vo.venue.GameOneFloatConfigVO;
import com.cloud.baowang.play.po.GameOneFloatConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameOneFloatConfigRepository extends BaseMapper<GameOneFloatConfigPO> {


    Page<GameOneFloatConfigVO> getFloatConfigPage(@Param("page") IPage page,
                                                  @Param("gameOneId") String gameOneId,
                                                  @Param("siteCode") String siteCode,
                                                  @Param("status") Integer status);


    List<LobbyGameOneFloatVO> getLobbyGameOneFloat(@Param("siteCode") String siteCode, @Param("currencyCode") String currencyCode);

}

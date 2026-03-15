package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.GameCollectionPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 游戏收藏 接口
 *
 * @author sheldon
 */
@Mapper
public interface GameCollectionRepository extends BaseMapper<GameCollectionPO> {

}

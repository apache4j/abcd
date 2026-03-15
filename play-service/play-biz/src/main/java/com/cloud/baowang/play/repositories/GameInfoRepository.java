package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.vo.lobby.LobbyGameDetailRequestVO;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.po.GameInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 游戏信息 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface GameInfoRepository extends BaseMapper<GameInfoPO> {


    Page<SiteGameResponsePageVO> queryGameAuthorize(@Param("page") Page<GameInfoPO> page, @Param("vo") SiteGameRequestVO siteGameRequestVO);



    /**
     * 游戏大厅-首页游戏
     */
    IPage<GameInfoVO> getGameInfoLobbyPage(@Param("page") IPage<GameInfoPO> page, @Param("requestVO") LobbyGameDetailRequestVO vo , @Param("siteCode") String siteCode);


    /**
     * 根据二级分类+标签查出游戏数量
     */
    List<GameInfoVO> getGameInfoCountByTwoAndLabel(@Param("requestVO") LobbyGameDetailRequestVO vo , @Param("siteCode") String siteCode);


    /**
     * 游戏大厅-查询收藏的游戏列表
     */
    IPage<GameInfoVO> getCollectionGameInfoLobbyPage(@Param("page") IPage<GameInfoPO> page,
                                                     @Param("userId") String userId,
                                                     @Param("currencyCode") String currencyCode,
                                                     @Param("twoId") String twoId,
                                                     @Param("siteCode") String siteCode,
                                                     @Param("gameI18nCodeList") List<String> gameI18nCodeList);


    /**
     * 游戏大厅-首页热门游戏
     */
    IPage<GameInfoVO> getGameInfoHomeHotSort(@Param("page") IPage<GameInfoPO> page, @Param("requestVO") LobbyGameDetailRequestVO vo , @Param("siteCode") String siteCode);




    /**
     * 游戏大厅-点击一级分类查询游戏详情
     */
    List<GameInfoVO> getGameInfoByOneClassId(@Param("gameOneId") String gameOneId , @Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode);



    /**
     * 首页游戏
     */
    List<GameInfoVO> getLobbyTopGame(@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode);

    /**
     * 首页游戏-根据ID查游戏信息
     */
    GameInfoVO getLobbyTopGameById(@Param("gameId") String gameId ,@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode);


    /**
     * 首页-侧边栏
     */
    List<GameInfoVO> getLobbyLabelList(@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode);



    /**
     * 站点后台-游戏热门列表
     */
    List<GameInfoVO> getSiteGameHotList(@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode,
                                        @Param("gameOneId") String gameOneId);


    /**
     * 站点后台-首页热门列表
     */
    List<GameInfoVO> getSiteGameHomeHotList(@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode);


    /**
     * 站点后台-游戏首页一级分类列表
     */
    List<GameInfoVO> getSiteGameHomeList(@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode,
                                        @Param("gameOneId") String gameOneId);



    IPage<GameInfoVO> queryNewSiteGameInfoAndSiteGamePage(@Param("page") IPage<GameInfoPO> page, @Param("requestVO") GameInfoRequestVO vo, @Param("siteCode") String siteCode);


    /**
     * 站点后台-游戏首页一级分类列表
     */
    List<SiteGameInfoVO> getConfigSiteGameInfoList(@Param("currencyCode") String currencyCode , @Param("siteCode") String siteCode,
                                                   @Param("venueCode") String venueCode);



    List<GameInfoVO> queryGameByCurrencyList(@Param("venueCode") String venueCode,@Param("currencyList") List<String> currencyList);





}

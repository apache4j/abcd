package com.cloud.baowang.play.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.play.api.enums.venue.VenueCurrencyTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.GameHotRemTypeEnum;
import com.cloud.baowang.play.api.enums.GameInfoLabelEnum;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.game.sh.constant.SHConstantApi;
import com.cloud.baowang.play.game.sh.request.ShReqBase;
import com.cloud.baowang.play.game.sh.response.ShDeskStatusListResVO;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.*;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: sheldon
 * @Date: 3/29/24 6:31 下午
 */

@Slf4j
@Service
@AllArgsConstructor
public class AdminGameInfoService extends ServiceImpl<GameInfoRepository, GameInfoPO> {


//    private final GameJoinClassService gameJoinClassService;

    private final SiteGameService siteGameService;

    private final SiteGameHotSortService siteGameHotSortService;

    private final GameOneClassInfoService gameOneClassInfoService;

    private final GameTwoClassInfoRepository gameTwoClassInfoRepository;

    private final GameCollectionRepository gameCollectionRepository;

    private final VenueInfoRepository venueInfoRepository;

    private final I18nApi i18nApi;

    private final GameServiceFactory gameServiceFactory;

    private final GameTwoCurrencySortService gameTwoCurrencySortService;

    private final GameInfoService gameInfoService;

    private final SiteGameRepository siteGameRepository;

    private final GameInfoRepository gameInfoRepository;

    private Boolean handleCollectionUserId(GameInfoRequestVO requestVO) {
        String collectionUserId = requestVO.getCollectionUserId();
        if (ObjectUtil.isNotEmpty(collectionUserId)) {
            List<GameCollectionPO> gameCollectionList = gameCollectionRepository.selectList(Wrappers.lambdaQuery(GameCollectionPO.class)
                    .eq(GameCollectionPO::getUserId, collectionUserId));
            if (CollectionUtil.isEmpty(gameCollectionList)) {
                return Boolean.FALSE;
            }
            List<String> gameIds = new ArrayList<>(gameCollectionList.stream().map(GameCollectionPO::getGameId).toList());
            if (CollectionUtil.isNotEmpty(requestVO.getIds())) {
                gameIds.retainAll(requestVO.getIds());
            }

            if (CollectionUtil.isEmpty(gameIds)) {
                return Boolean.FALSE;
            }
            requestVO.setIds(gameIds);
        }
        return Boolean.TRUE;
    }

    /**
     * 总台游戏查询
     */
    public Page<GameInfoVO> adminGameInfoPage(GameInfoRequestVO requestVO) {
        Page<GameInfoPO> page = PageConvertUtil.getMybatisPage(requestVO);

        if (requestVO.getPageSize() == -1) {
            page.setSize(5000);
        }

        if (ObjectUtil.isNotEmpty(requestVO.getGameName())) {
            List<String> gameNameList = i18nApi.search(I18nSearchVO.builder()
                    .searchContent(requestVO.getGameName())
                    .bizKeyPrefix(I18MsgKeyEnum.GAME_NAME.getCode())
                    .lang(CurrReqUtils.getLanguage())
                    .build()).getData();
            if (CollectionUtil.isEmpty(gameNameList)) {
                return PageConvertUtil.getMybatisPage(requestVO);
            }

            if (CollectionUtil.isNotEmpty(gameNameList)) {
                requestVO.setGameI18nCodeList(gameNameList);
            }
        }


        if (ObjectUtil.isNotEmpty(requestVO.getExactSearchContent())) {
            List<String> gameNameList = i18nApi.search(I18nSearchVO.builder()
                    .exactSearchContent(requestVO.getExactSearchContent())
                    .bizKeyPrefix(I18MsgKeyEnum.GAME_NAME.getCode())
                    .lang(CurrReqUtils.getLanguage())
                    .build()).getData();
            if (CollectionUtil.isEmpty(gameNameList)) {
                return PageConvertUtil.getMybatisPage(requestVO);
            }

            if (CollectionUtil.isNotEmpty(gameNameList)) {
                requestVO.setGameI18nCodeList(gameNameList);
            }
        }

        IPage<GameInfoPO> gameInfoPage = baseMapper.selectPage(page, GameInfoPO.getQueryWrapper(requestVO));

        List<VenueInfoPO> venueInfoPOList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class));

        Map<String,List<VenueInfoPO>> venueInfoPOMap = venueInfoPOList.stream().collect(Collectors.groupingBy(VenueInfoPO::getVenueCode));


        IPage<GameInfoVO> resultPage = gameInfoPage.convert(x -> {
            GameInfoVO vo = new GameInfoVO();
            BeanUtils.copyProperties(x, vo);
//            String venueName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.VENUE_CODE, x.getVenueCode());
//            vo.setVenueName(venueName);

            List<VenueInfoPO> venueInfoList = venueInfoPOMap.get(x.getVenueCode());

            if(CollectionUtil.isNotEmpty(venueInfoList)) {
                VenueInfoPO venueInfoPO = venueInfoList.get(0);
                String venueCurrencyCode = null;
                if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(venueInfoPO.getVenueCurrencyType())) {
                    List<String> venueCurrencyList = venueInfoList.stream().map(VenueInfoPO::getCurrencyCode).toList();
                    venueCurrencyCode = String.join(",", venueCurrencyList);
                }else{
                    venueCurrencyCode = venueInfoPO.getCurrencyCode();
                }
                vo.setVenueCurrencyCode(venueCurrencyCode);
            }
            return vo;
        });
        resultPage.setRecords(resultPage.getRecords().stream().sorted(Comparator.comparing(GameInfoVO::getStatus)).toList());
        return ConvertUtil.toConverPage(resultPage);
    }

    /**
     * 中控后台查询游戏分页
     */
    public Page<SiteGameInfoVO> newSiteGameInfoPage(GameInfoRequestVO requestVO) {

        Page page = PageConvertUtil.getMybatisPage(requestVO);

        if (requestVO.getPageSize() == -1) {
            page.setSize(5000);
        }

        String siteCode = CurrReqUtils.getSiteCode();


        //传了一二级分类
        if (ObjectUtil.isNotEmpty(requestVO.getGameOneId()) || ObjectUtil.isNotEmpty(requestVO.getGameTwoId())) {
            List<GameTwoCurrencySortPO> gameTwoCurrencySortPOList = gameTwoCurrencySortService.getBaseMapper().selectList(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                    .eq(GameTwoCurrencySortPO::getSiteCode, siteCode)
                    .eq(ObjectUtil.isNotEmpty(requestVO.getGameTwoId()), GameTwoCurrencySortPO::getGameTwoId, requestVO.getGameTwoId())
                    .eq(ObjectUtil.isNotEmpty(requestVO.getGameOneId()), GameTwoCurrencySortPO::getGameOneId, requestVO.getGameOneId())
            );
            if (CollectionUtil.isEmpty(gameTwoCurrencySortPOList)) {
                return page;
            }

            List<String> gameList = gameTwoCurrencySortPOList.stream().map(GameTwoCurrencySortPO::getGameId).toList();
            gameList = gameList.stream().distinct().toList();
            requestVO.setIds(gameList);
        }


        List<GameOneClassInfoPO> gameOneList = gameOneClassInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(GameOneClassInfoPO.class)
                .eq(GameOneClassInfoPO::getSiteCode, siteCode));


        List<GameTwoClassInfoPO> gameTwoList = gameTwoClassInfoRepository.selectList(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getSiteCode, siteCode));


        IPage<GameInfoVO> gameInfoPage = baseMapper.queryNewSiteGameInfoAndSiteGamePage(page, requestVO, CurrReqUtils.getSiteCode());

        List<String> gameIds = gameInfoPage.getRecords().stream().map(GameInfoVO::getId).toList();


        // gameId -> "一级分类1,一级分类2"
        Map<String, String> gameTwoNameMap = Maps.newHashMap();

        // gameId -> "二级分类1,二级分类2"
        Map<String, String> gameOneNameMap = Maps.newHashMap();


        if (CollectionUtil.isNotEmpty(gameIds)) {
            List<GameTwoCurrencySortPO> gameTwoCurrencySortPOList = gameTwoCurrencySortService.getBaseMapper().selectList(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                    .eq(GameTwoCurrencySortPO::getSiteCode, siteCode)
                    .in(GameTwoCurrencySortPO::getGameId, gameIds)
                    .select(GameTwoCurrencySortPO::getGameId, GameTwoCurrencySortPO::getGameOneId, GameTwoCurrencySortPO::getGameTwoId));

            //k=gameId,v=一级分类id集合 , k=gameId,v=二级分类id集合
            if (CollectionUtil.isNotEmpty(gameTwoCurrencySortPOList)) {
                Map<String, List<String>> gameListOneMap = gameTwoCurrencySortPOList.stream()
                        .collect(Collectors.groupingBy(
                                GameTwoCurrencySortPO::getGameId,
                                Collectors.mapping(GameTwoCurrencySortPO::getGameOneId, Collectors.toList())
                        ));


                Map<String, List<String>> gameListTwoMap = gameTwoCurrencySortPOList.stream()
                        .collect(Collectors.groupingBy(
                                GameTwoCurrencySortPO::getGameId,
                                Collectors.mapping(GameTwoCurrencySortPO::getGameTwoId, Collectors.toList())
                        ));


                //k = 一级分类ID,v = 一级分类名称
                Map<String, String> gameOneMap = gameOneList.stream()
                        .collect(Collectors.toMap(GameOneClassInfoPO::getId, GameOneClassInfoPO::getDirectoryName));

                //k = 一级分类ID,v = 二级分类名称
                Map<String, GameTwoClassInfoPO> gameTwoMap = gameTwoList.stream()
                        .collect(Collectors.toMap(GameTwoClassInfoPO::getId, GameTwoClassInfoPO -> GameTwoClassInfoPO));


                gameOneNameMap = gameListOneMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .map(gameOneMap::get)
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .collect(Collectors.joining(","))
                        ));


                gameTwoNameMap = gameListTwoMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .map(gameTwoMap::get)
                                        .filter(Objects::nonNull)
                                        .map(GameTwoClassInfoPO::getTypeName)
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .collect(Collectors.joining(","))
                        ));


            }
        }


        Map<String, String> finalGameTwoNameMap = gameTwoNameMap;
        Map<String, String> finalGameOneNameMap = gameOneNameMap;
        IPage<SiteGameInfoVO> gameInfoIPage = gameInfoPage.convert(x -> {

            SiteGameInfoVO vo = new SiteGameInfoVO();
            BeanUtils.copyProperties(x, vo);
//            String venueName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.VENUE_CODE, x.getVenueCode());
//            vo.setVenueName(venueName);

            String gameTwoName = finalGameTwoNameMap.get(x.getId());
            String gameOneName = finalGameOneNameMap.get(x.getId());
            vo.setGameOneClassName(gameOneName);
            vo.setGameTwoClassName(gameTwoName);

            return vo;
        });

        return ConvertUtil.toConverPage(gameInfoIPage);
    }


    private List<String> getVenueCodeListByGameIds(List<String> gameList) {
        if (CollectionUtil.isEmpty(gameList)) {
            return Lists.newArrayList();
        }

        List<GameInfoPO> gameInfoPOList = gameInfoService.getBaseMapper().selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                .in(GameInfoPO::getId, gameList));

        if (CollectionUtil.isEmpty(gameInfoPOList)) {
            return Lists.newArrayList();
        }

        List<String> venueList = gameInfoPOList.stream().map(GameInfoPO::getVenueCode).toList();
        venueList = venueList.stream().distinct().toList();
        return venueList;
    }

    /**
     * 该方法是由总台改游戏状态 批量
     */
    public Boolean newUpAdminGameInfoStatusBatch(BatchGameClassStatusRequestUpVO batchRequestVO) {

        String lock = RedisConstants.UP_ADMIN_GAME_STATUS_BATCH;
        String lockCode = RedisUtil.acquireImmediate(lock, 100L);
        if (lockCode == null) {
            log.info("总控批量修改游戏状态,太频繁");
            throw new BaowangDefaultException(ResultCode.PLEASE_TRY_AGAIN_LATER);
        }

        try {

            Integer status = batchRequestVO.getStatus();
            List<String> idBatch = batchRequestVO.getIdBatch();
            if (idBatch.size() > 100) {
                //超过100条数据.只处理前100条数据
                idBatch = idBatch.stream().limit(100).toList();
            }


            //传入的时间校验
            if (ObjectUtil.isNotEmpty(batchRequestVO.getMaintenanceStartTime()) && ObjectUtil.isNotEmpty(batchRequestVO.getMaintenanceEndTime())) {
                if (batchRequestVO.getMaintenanceStartTime() >= batchRequestVO.getMaintenanceEndTime()) {
                    throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
                }
            }

            //获取出游戏的所有场馆
            List<String> venueCodeList = getVenueCodeListByGameIds(idBatch);
            if (CollectionUtil.isEmpty(venueCodeList)) {
                log.info("没有获取到游戏的场馆");
                return true;
            }

            UpdateWrapper<GameInfoPO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", idBatch)
                    .set("status", status)
                    .set("updater", CurrReqUtils.getAccount())
                    .set("updated_time", System.currentTimeMillis())
                    .set("last_status", null);


            if (status.equals(StatusEnum.OPEN.getCode())) {
                List<VenueInfoPO> venueInfoPOList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                        .in(VenueInfoPO::getVenueCode, venueCodeList)
                        .eq(VenueInfoPO::getStatus, StatusEnum.OPEN.getCode()));//游戏开启 场馆必须也是开启的
                if (CollectionUtil.isEmpty(venueInfoPOList)) {
                    log.info("没有开启的场馆,游戏不开启");
                    return true;
                }
                //取出已经处于开启状态的场馆列表
                venueCodeList = venueInfoPOList.stream().map(VenueInfoPO::getVenueCode).toList();
                venueCodeList = venueCodeList.stream().distinct().toList();
                updateWrapper.in("venue_code", venueCodeList);//要把游戏改成开启状态前提条件是该游戏的场馆必须也是开启的状态
            }


            //修改状态不是维护,则需要将维护时间跟备注去掉
            if (!StatusEnum.MAINTAIN.getCode().equals(status)) {
                updateWrapper.set("maintenance_start_time", null)
                        .set("maintenance_end_time", null)
                        .set("remark", null);
            }

            // 总控修改状态为维护
            if (StatusEnum.MAINTAIN.getCode().equals(status)) {
                updateWrapper.set(ObjectUtil.isNotEmpty(batchRequestVO.getMaintenanceStartTime()), "maintenance_start_time", batchRequestVO.getMaintenanceStartTime())
                        .set(ObjectUtil.isNotEmpty(batchRequestVO.getMaintenanceEndTime()), "maintenance_end_time", batchRequestVO.getMaintenanceEndTime())
                        .set(ObjectUtil.isNotEmpty(batchRequestVO.getRemark()), "remark", batchRequestVO.getRemark())
                        .eq("status", StatusEnum.OPEN.getCode());
            }

            // 总控修改状态为禁用 修改所有未关闭的游戏
            if (StatusEnum.CLOSE.getCode().equals(batchRequestVO.getStatus())) {
                updateWrapper.ne("status", batchRequestVO.getStatus());
            }

            baseMapper.update(null, updateWrapper);


            GameClassStatusRequestUpVO siteGameReq = GameClassStatusRequestUpVO.builder()
                    .ids(idBatch) //因为总控是向下改是用Ids的所以这个地方要透传进去
                    .status(status)
                    .maintenanceEndTime(batchRequestVO.getMaintenanceEndTime())
                    .maintenanceStartTime(batchRequestVO.getMaintenanceStartTime())
                    .remark(batchRequestVO.getRemark())
                    .build();
            //总控向下改游戏
            siteGameService.adminUpGameInfoStatus(siteGameReq);
            //清理总控缓存
            CompletableFuture.runAsync(LobbyCateUtil::deleteLobbyAllSiteGameInfo);
        } catch (Exception e) {
            log.error("总控批量修改游戏状态异常", e);
        } finally {
            if (ObjectUtil.isNotEmpty(lockCode)) {
                boolean release = RedisUtil.release(lock, lockCode);
                log.info("总控批量修改游戏状态:{},执行结束,删除锁:{}", lock, release);
            }
        }
        return Boolean.TRUE;
    }


    /**
     * 该方法是由总台改游戏状态
     */
    public Boolean upAdminGameInfoStatus(GameClassStatusRequestUpVO requestVO) {

        if (ObjectUtil.isNotEmpty(requestVO.getStatus())) {
            if (ObjectUtil.isEmpty(StatusEnum.nameByCode(requestVO.getStatus()))) {
                log.info("请求参数异常:{}", requestVO);
                return Boolean.FALSE;
            }
        }

        GameInfoPO gameInfoPO = baseMapper.selectById(requestVO.getId());
        if (ObjectUtil.isEmpty(gameInfoPO)) {
            log.info("请求参数异常:{},游戏不存在", requestVO);
            return Boolean.FALSE;
        }

        //重复操作
        if (gameInfoPO.getStatus().equals(requestVO.getStatus())) {
            return Boolean.TRUE;
        }

        //传入的时间校验
        if (ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()) && ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime())) {
            if (requestVO.getMaintenanceStartTime() >= requestVO.getMaintenanceEndTime()) {
                throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
            }
        }


        if (requestVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            List<VenueInfoPO> venueInfoPOList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                    .eq(VenueInfoPO::getVenueCode, gameInfoPO.getVenueCode()));
            if (CollectionUtil.isEmpty(venueInfoPOList)) {
                log.info("游戏的场馆不存在:{}", gameInfoPO);
                throw new BaowangDefaultException(ResultCode.VENUE_NOT_OPEN);
            }
            VenueInfoPO venueInfoPO = venueInfoPOList.get(0);

            //总台的场馆未开启,游戏不允许开启
            if (!StatusEnum.OPEN.getCode().equals(venueInfoPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.VENUE_NOT_OPEN);
            }
        }

        if (StatusEnum.MAINTAIN.getCode().equals(requestVO.getStatus())) {
            //游戏禁用状态,游戏不允许维护
            if (!StatusEnum.OPEN.getCode().equals(gameInfoPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.SITE_GAME_NOT_MAINTAIN);
            }
        }


        UpdateWrapper<GameInfoPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", requestVO.getId())
                .set("status", requestVO.getStatus())
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis())
                .set("last_status", null);


        //修改状态不是维护,则需要将维护时间跟备注去掉
        if (!StatusEnum.MAINTAIN.getCode().equals(requestVO.getStatus())) {
            updateWrapper.set("maintenance_start_time", null)
                    .set("maintenance_end_time", null)
                    .set("remark", null);
        }

        // 总控修改状态为维护
        if (StatusEnum.MAINTAIN.getCode().equals(requestVO.getStatus())) {
            updateWrapper.set(ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()), "maintenance_start_time", requestVO.getMaintenanceStartTime())
                    .set(ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime()), "maintenance_end_time", requestVO.getMaintenanceEndTime())
                    .set(ObjectUtil.isNotEmpty(requestVO.getRemark()), "remark", requestVO.getRemark());
        }

        // 总控修改状态为禁用 修改所有未关闭的游戏
        if (StatusEnum.CLOSE.getCode().equals(requestVO.getStatus())) {
            updateWrapper.ne("status", requestVO.getStatus());
        }

        baseMapper.update(null, updateWrapper);

        //因为总控是向下改是用Ids的所以这个地方要透传进去
        requestVO.setIds(Lists.newArrayList(requestVO.getId()));
        //总控向下改游戏
        siteGameService.adminUpGameInfoStatus(requestVO);

        CompletableFuture.runAsync(LobbyCateUtil::deleteLobbyAllSiteGameInfo);

        return true;
    }

    /**
     * 该方法是由总台改场馆 ,然后改场馆下的游戏
     */
    public Boolean newUpAdminVenueGameInfoStatus(GameClassStatusRequestUpVO requestVO,String venueCode) {
        UpdateWrapper<GameInfoPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("venue_code",venueCode)
                .set("status", requestVO.getStatus())
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis());

        //总控选择开启场馆,则将上一次的游戏状态同步回去
        if (StatusEnum.OPEN.getCode().equals(requestVO.getStatus())) {
            updateWrapper
//                    .isNotNull("last_status")
                    .in("last_status",Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode(),StatusEnum.CLOSE.getCode()))
//                    .ne("status", StatusEnum.CLOSE.getCode())
                    .in("status", Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
                    .setSql("status = last_status")
                    .set("last_status", null)
                    .set("maintenance_start_time", null)
                    .set("maintenance_end_time", null)
                    .set("remark", null);
        }else if (StatusEnum.MAINTAIN.getCode().equals(requestVO.getStatus())) {// 总控修改状态为维护 ,将开启中的游戏改为维护

            //原本就是维护的 只同步维护时间跟备注
            UpdateWrapper<GameInfoPO> maintainGame = new UpdateWrapper<>();
            maintainGame.eq("venue_code", venueCode)
                    .eq("status", StatusEnum.MAINTAIN.getCode())
                    .set("updater", CurrReqUtils.getAccount())
                    .set("updated_time", System.currentTimeMillis())
                    .set(ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()), "maintenance_start_time", requestVO.getMaintenanceStartTime())
                    .set(ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime()), "maintenance_end_time", requestVO.getMaintenanceEndTime())
                    .set(ObjectUtil.isNotEmpty(requestVO.getRemark()), "remark", requestVO.getRemark());
            baseMapper.update(null, maintainGame);
            updateWrapper.eq("status", StatusEnum.OPEN.getCode())
                    .setSql("last_status = status")
                    .set(ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()), "maintenance_start_time", requestVO.getMaintenanceStartTime())
                    .set(ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime()), "maintenance_end_time", requestVO.getMaintenanceEndTime())
                    .set(ObjectUtil.isNotEmpty(requestVO.getRemark()), "remark", requestVO.getRemark());
        }

        // 总控修改状态为关闭场馆 修改所有未关闭的游戏
        if (StatusEnum.CLOSE.getCode().equals(requestVO.getStatus())) {
            updateWrapper
//                    .ne("status", requestVO.getStatus())
                    .in("status", Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
                    .set("last_status", null)
                    .set("maintenance_start_time", null)
                    .set("maintenance_end_time", null)
                    .set("remark", null);
        }
        baseMapper.update(null, updateWrapper);

        //总控向下改游戏
//        siteGameService.adminUpGameInfoStatus(requestVO);

        siteGameService.adminUpVenueToGameStatus(requestVO, venueCode);


        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean upGameInfo(GameInfoAddOrUpdateVO requestVO) {
        if (ObjectUtils.isEmpty(requestVO.getId())) {
            log.info("缺少参数");
            return Boolean.FALSE;
        }

        GameInfoPO gameInfo = baseMapper.selectById(requestVO.getId());
        if (ObjectUtil.isEmpty(gameInfo)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        requestVO.setIconI18nCodeList(requestVO.getSeIconI18nCodeList());

        List<I18nMsgFrontVO> gameNameList = requestVO.getGameI18nCodeList().stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
        if (CollectionUtil.isNotEmpty(gameNameList)) {
            requestVO.setGameName(gameNameList.get(0).getMessage());
        }

        GameInfoPO gameInfoPO = new GameInfoPO();

        BeanUtils.copyProperties(requestVO, gameInfoPO);

        List<VenueInfoPO> venueInfoList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, requestVO.getVenueCode()));
        if (CollectionUtil.isEmpty(venueInfoList)) {
            log.info("请求参数异常:{},查不到场馆", requestVO);
            return Boolean.FALSE;
        }
        VenueInfoPO venueInfo = venueInfoList.get(0);

        Long count = baseMapper.selectCount(Wrappers.lambdaQuery(GameInfoPO.class)
                .eq(GameInfoPO::getVenueCode, requestVO.getVenueCode())
                .eq(GameInfoPO::getAccessParameters, requestVO.getAccessParameters())
                .ne(GameInfoPO::getId, requestVO.getId()));
        if (count > 0) {
            log.info("请求参数异常,平台接入参数在该平台重复: {}", requestVO);
            throw new BaowangDefaultException(ResultCode.PLATFORM_PARAM_REPEAT);
        }

        //场馆币种
        List<String> venueCurrencyList;
        if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(venueInfo.getVenueCurrencyType())) {
            venueCurrencyList = venueInfoList.stream().map(VenueInfoPO::getCurrencyCode).toList();
        } else {
            venueCurrencyList = Arrays.asList(venueInfo.getCurrencyCode().split(","));
        }

        //请求进来的游戏币种
        List<String> reqGameCurrencyList = Lists.newArrayList();
        if (StringUtils.isNotBlank(requestVO.getCurrencyCode())) {
            reqGameCurrencyList = Arrays.asList(requestVO.getCurrencyCode().split(","));
        }


        if(CollectionUtil.isNotEmpty(reqGameCurrencyList)){
            // 游戏的币种在场馆中不存在
            List<String> errorCurrency = reqGameCurrencyList.stream().filter(e -> !venueCurrencyList.contains(e)).toList();
            if (CollectionUtil.isNotEmpty(errorCurrency)) {
                log.info("参数异常,游戏的币种在场馆中不存在,游戏币种:{},场馆币种:{},差异币种:{}", reqGameCurrencyList, venueCurrencyList, errorCurrency);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }

        //旧的币种集合
        List<String> dbGameCurrencyList = Lists.newArrayList();
        if(StringUtils.isNotBlank(gameInfo.getCurrencyCode())){
            dbGameCurrencyList = Arrays.asList(gameInfo.getCurrencyCode().split(","));
        }


        //币种差 = 游戏原来的币种 - 新请求进来的币种
        Set<String> removeCurrency = new HashSet<>(dbGameCurrencyList);
        reqGameCurrencyList.forEach(removeCurrency::remove);

        if(CollectionUtil.isNotEmpty(removeCurrency)){

            //因为币种删除了所以关联关系要去除
            siteGameHotSortService.getBaseMapper().delete(Wrappers
                    .lambdaQuery(SiteGameHotSortPO.class)
                    .eq(SiteGameHotSortPO::getGameId, requestVO.getId())
                    .in(SiteGameHotSortPO::getCurrencyCode, removeCurrency));
            gameTwoCurrencySortService.getBaseMapper().delete(Wrappers
                    .lambdaQuery(GameTwoCurrencySortPO.class)
                    .eq(GameTwoCurrencySortPO::getGameId, requestVO.getId())
                    .in(GameTwoCurrencySortPO::getCurrencyCode, removeCurrency));
        }

        SiteGamePO siteGamePO = SiteGamePO.builder().currencyCode(String.join(",",reqGameCurrencyList)).build();
        siteGameRepository.update(siteGamePO, Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getGameInfoId, requestVO.getId()));

        String gameId = gameInfo.getGameId();
        String gameNameI18Code = I18MsgKeyEnum.GAME_NAME.getCode() + CommonConstant.UNDERLINE + gameId;
        String gameDescI18Code = I18MsgKeyEnum.GAME_DESC.getCode() + CommonConstant.UNDERLINE + gameId;
        String iconI18Code = I18MsgKeyEnum.GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;
        String seIconI18Code = I18MsgKeyEnum.SE_GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;
        String vtIconI18Code = I18MsgKeyEnum.VT_GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;
        String htIconI18Code = I18MsgKeyEnum.HT_GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;

        gameInfoPO.setGameI18nCode(gameNameI18Code);
        gameInfoPO.setGameDescI18nCode(gameDescI18Code);

        if (CollectionUtil.isNotEmpty(requestVO.getIconI18nCodeList())) {
            gameInfoPO.setIconI18nCode(iconI18Code);
        }
        if (CollectionUtil.isNotEmpty(requestVO.getSeIconI18nCodeList())) {
            gameInfoPO.setSeIconI18nCode(seIconI18Code);
        }

        if (CollectionUtil.isNotEmpty(requestVO.getVtIconI18nCodeList())) {
            gameInfoPO.setVtIconI18nCode(vtIconI18Code);
        }

        if (CollectionUtil.isNotEmpty(requestVO.getHtIconI18nCodeList())) {
            gameInfoPO.setHtIconI18nCode(htIconI18Code);
        }

        gameInfoPO.setVenueCode(venueInfo.getVenueCode());
        gameInfoPO.setVenueId(venueInfo.getId());
        gameInfoPO.setVenueName(venueInfo.getVenueName());
        gameInfoPO.setVenueType(venueInfo.getVenueType());

        toSetUrlHttpList(requestVO.getIconI18nCodeList());
        toSetUrlHttpList(requestVO.getSeIconI18nCodeList());
        toSetUrlHttpList(requestVO.getVtIconI18nCodeList());
        toSetUrlHttpList(requestVO.getHtIconI18nCodeList());

        List<I18nMsgFrontVO> iconI18nZh = requestVO.getIconI18nCodeList().stream()
                .filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
        if (CollectionUtil.isNotEmpty(iconI18nZh)) {
            gameInfoPO.setIcon(iconI18nZh.get(0).getMessage());
        }
        gameInfoPO.setUpdater(CurrReqUtils.getAccount());

        Boolean result = baseMapper.update(gameInfoPO, Wrappers
                .lambdaQuery(GameInfoPO.class)
                .eq(GameInfoPO::getId, requestVO.getId())) > 0;


        if (result) {
            Map<String, List<I18nMsgFrontVO>> reqMap = I18nMsgBindUtil.bind(I18nMsgBindUtil.bind(
                            I18nMsgBindUtil.bind(gameNameI18Code, requestVO.getGameI18nCodeList())
                            , gameDescI18Code, requestVO.getGameDescI18nCodeList()),
                    iconI18Code, requestVO.getIconI18nCodeList()
            );


            if (CollectionUtil.isNotEmpty(requestVO.getSeIconI18nCodeList())) {
                reqMap = I18nMsgBindUtil.bind(
                        reqMap,
                        seIconI18Code, requestVO.getSeIconI18nCodeList()
                );
            }

            if (CollectionUtil.isNotEmpty(requestVO.getVtIconI18nCodeList())) {
                reqMap = I18nMsgBindUtil.bind(
                        reqMap,
                        vtIconI18Code, requestVO.getVtIconI18nCodeList()
                );
            }

            if (CollectionUtil.isNotEmpty(requestVO.getHtIconI18nCodeList())) {
                reqMap = I18nMsgBindUtil.bind(
                        reqMap,
                        htIconI18Code, requestVO.getHtIconI18nCodeList()
                );
            }


            ResponseVO<Boolean> i18Result = i18nApi.update(reqMap);
            if (!i18Result.isOk() || !i18Result.getData()) {
                log.info("修改游戏调用i18失败,param:{},18Result:{}", i18Result, i18Result.getData());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        //清理总控缓存
        CompletableFuture.runAsync(LobbyCateUtil::deleteLobbyAllSiteGameInfo);
        return result;
    }

    /**
     * 站点清理游戏
     */
    private void siteCleanGameInfo() {
        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean addGameInfo(GameInfoAddOrUpdateVO requestVO) {

        List<Integer> devList = List.of(DeviceType.Home.getCode(),
                DeviceType.PC.getCode(),
                DeviceType.IOS_H5.getCode(),
                DeviceType.IOS_APP.getCode(),
                DeviceType.Android_H5.getCode(),
                DeviceType.Android_APP.getCode());
        String supportDevice = devList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        requestVO.setSupportDevice(supportDevice);

        requestVO.setIconI18nCodeList(requestVO.getSeIconI18nCodeList());

        // 验证请求数据
        validateRequest(requestVO);

        // 创建游戏信息对象
        GameInfoPO gameInfoPO = new GameInfoPO();

        List<I18nMsgFrontVO> gameNameList = requestVO.getGameI18nCodeList().stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
        if (CollectionUtil.isNotEmpty(gameNameList)) {
            requestVO.setGameName(gameNameList.get(0).getMessage());
        }

        BeanUtils.copyProperties(requestVO, gameInfoPO);

        // 校验平台信息
        VenueInfoPO venueInfoPO = getVenueInfo(requestVO.getVenueCode());

        // 检查游戏是否已存在
        checkGameExistence(requestVO.getGameName(), venueInfoPO.getVenueCode());

        // 检查平台接入参数是否重复
        checkAccessParameters(requestVO.getAccessParameters(), venueInfoPO.getVenueCode());

        // 生成游戏ID并设置其他属性
        populateGameInfoProperties(requestVO, gameInfoPO, venueInfoPO);


        List<I18nMsgFrontVO> iconI18nZh = requestVO.getIconI18nCodeList().stream()
                .filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
        if (CollectionUtil.isNotEmpty(iconI18nZh)) {
            gameInfoPO.setIcon(iconI18nZh.get(0).getMessage());
        }

        gameInfoPO.setCurrencyCode(venueInfoPO.getCurrencyCode());

        // 插入游戏信息到数据库
        if (baseMapper.insert(gameInfoPO) <= 0) {
            return Boolean.FALSE;
        }
        log.info("插入成功");
        toSetUrlHttpList(requestVO.getIconI18nCodeList());//游戏图片
        toSetUrlHttpList(requestVO.getSeIconI18nCodeList());//正方形游戏图片
        toSetUrlHttpList(requestVO.getVtIconI18nCodeList());//竖屏游戏图片
        toSetUrlHttpList(requestVO.getHtIconI18nCodeList());//横屏游戏图片


        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Maps.newHashMap();

        if (ObjectUtil.isNotEmpty(gameInfoPO.getGameI18nCode()) && CollectionUtil.isNotEmpty(requestVO.getGameI18nCodeList())) {
            i18nData.put(gameInfoPO.getGameI18nCode(), requestVO.getGameI18nCodeList());
        }
        if (ObjectUtil.isNotEmpty(gameInfoPO.getGameDescI18nCode()) && CollectionUtil.isNotEmpty(requestVO.getGameDescI18nCodeList())) {
            i18nData.put(gameInfoPO.getGameDescI18nCode(), requestVO.getGameDescI18nCodeList());
        }
        if (ObjectUtil.isNotEmpty(gameInfoPO.getIconI18nCode()) && CollectionUtil.isNotEmpty(requestVO.getIconI18nCodeList())) {
            i18nData.put(gameInfoPO.getIconI18nCode(), requestVO.getIconI18nCodeList());
        }
        if (ObjectUtil.isNotEmpty(gameInfoPO.getSeIconI18nCode()) && CollectionUtil.isNotEmpty(requestVO.getSeIconI18nCodeList())) {
            i18nData.put(gameInfoPO.getSeIconI18nCode(), requestVO.getSeIconI18nCodeList());
        }
        if (ObjectUtil.isNotEmpty(gameInfoPO.getVtIconI18nCode()) && CollectionUtil.isNotEmpty(requestVO.getVtIconI18nCodeList())) {
            i18nData.put(gameInfoPO.getVtIconI18nCode(), requestVO.getVtIconI18nCodeList());
        }
        if (ObjectUtil.isNotEmpty(gameInfoPO.getHtIconI18nCode()) && CollectionUtil.isNotEmpty(requestVO.getHtIconI18nCodeList())) {
            i18nData.put(gameInfoPO.getHtIconI18nCode(), requestVO.getHtIconI18nCodeList());
        }


        ResponseVO<Boolean> i18Result = i18nApi.insert(i18nData);
        if (!i18Result.isOk() || !i18Result.getData()) {
            log.info("新增游戏调用i18失败,param:{},18Result:{}", i18Result, i18Result.getData());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        return Boolean.TRUE;
    }

    private void toSetUrlHttpList(List<I18nMsgFrontVO> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        //防止传上来的图片地址是全路径的
        list.forEach(x -> {
            String message = x.getMessage();
            if (!StringUtils.isBlank(message)) {
                // 判断是否包含 "http"
                if (message.contains("http")) {
                    // 找到最后两个 "/" 的位置
                    int lastSlashIndex = message.lastIndexOf('/');
                    int secondLastSlashIndex = message.lastIndexOf('/', lastSlashIndex - 1);
                    // 截取从第二个 "/" 后面的部分
                    x.setMessage(message.substring(secondLastSlashIndex + 1));
                }
            }
        });
    }

    private void validateRequest(GameInfoAddOrUpdateVO requestVO) {
        requestVO.valid(requestVO.getGameI18nCodeList());
        requestVO.valid(requestVO.getGameDescI18nCodeList());
        requestVO.valid(requestVO.getIconI18nCodeList());

        if (ObjectUtil.isNotEmpty(requestVO.getLabel()) &&
                ObjectUtil.isEmpty(GameInfoLabelEnum.nameOfCode(requestVO.getLabel()))) {
            log.info("请求标签参数异常: {}", requestVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
    }

    private VenueInfoPO getVenueInfo(String venueCode) {
        List<VenueInfoPO> venueInfoList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, venueCode));
        if (CollectionUtil.isEmpty(venueInfoList)) {
            log.info("请求参数异常,平台不存在: {}", venueCode);
            throw new BaowangDefaultException(ResultCode.PLATFORM_NOT_CONFIGURED);
        }

        VenueInfoPO venueInfoPO = venueInfoList.get(0);

        if (venueInfoPO.getVenueCurrencyType().equals(VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode()) && ObjectUtil.isNotEmpty(venueInfoPO.getCurrencyCode())) {
            List<String> currencyList = venueInfoList.stream().map(VenueInfoPO::getCurrencyCode).toList();
            venueInfoPO.setCurrencyCode(String.join(",", currencyList));
        }

        return venueInfoPO;
    }

    private void checkGameExistence(String gameName, String venueCode) {
        Long count = baseMapper.selectCount(Wrappers.lambdaQuery(GameInfoPO.class)
                .eq(GameInfoPO::getVenueCode, venueCode)
                .eq(GameInfoPO::getGameName, gameName));
        if (count > 0) {
            log.info("请求参数异常,重复添加游戏: {}", gameName);
            throw new BaowangDefaultException(ResultCode.ADMIN_ALREADY_EXISTS);
        }
    }

    private void checkAccessParameters(String accessParameters, String venueCode) {
        Long count = baseMapper.selectCount(Wrappers.lambdaQuery(GameInfoPO.class)
                .eq(GameInfoPO::getVenueCode, venueCode)
                .eq(GameInfoPO::getAccessParameters, accessParameters));
        if (count > 0) {
            log.info("请求参数异常,平台接入参数在该平台重复: {}", accessParameters);
            throw new BaowangDefaultException(ResultCode.PLATFORM_PARAM_REPEAT);
        }
    }


    private void populateGameInfoProperties(GameInfoAddOrUpdateVO requestVO, GameInfoPO gameInfoPO, VenueInfoPO venueInfoPO) {
        QueryWrapper<GameInfoPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(" MAX(CONVERT(game_id, UNSIGNED)) AS gameId ");
        GameInfoPO gameInfoId = baseMapper.selectOne(queryWrapper);

        int maxGameId = 0;
        if (ObjectUtil.isNotEmpty(gameInfoId)) {
            maxGameId = Integer.parseInt(gameInfoId.getGameId());
        }

        String gameId = String.format("%05d", maxGameId + 1);
        gameInfoPO.setVenueCode(venueInfoPO.getVenueCode());
        gameInfoPO.setVenueId(venueInfoPO.getId());
        gameInfoPO.setVenueName(venueInfoPO.getVenueName());
        gameInfoPO.setVenueType(venueInfoPO.getVenueType());
        gameInfoPO.setGameId(gameId);
        //jdb,sexy单独处理下
        if (VenueEnum.JDB.getVenueCode().equals(venueInfoPO.getVenueCode()) || VenueEnum.SEXY.getVenueCode().equals(venueInfoPO.getVenueCode())) {
            gameInfoPO.setGameId(requestVO.getAccessParameters());
            gameId=requestVO.getAccessParameters();
        }
        gameInfoPO.setStatus(StatusEnum.CLOSE.getCode());
        gameInfoPO.setId(null);
        gameInfoPO.setUpdatedTime(System.currentTimeMillis());
        gameInfoPO.setUpdater(CurrReqUtils.getAccount());
        String gameNameI18Code = I18MsgKeyEnum.GAME_NAME.getCode() + CommonConstant.UNDERLINE + gameId;
        String gameDescI18Code = I18MsgKeyEnum.GAME_DESC.getCode() + CommonConstant.UNDERLINE + gameId;
        String iconI18Code = I18MsgKeyEnum.GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;
        String seIconI18Code = I18MsgKeyEnum.SE_GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;
        String vtIconI18Code = I18MsgKeyEnum.VT_GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;
        String htIconI18Code = I18MsgKeyEnum.HT_GAME_ICON.getCode() + CommonConstant.UNDERLINE + gameId;

        if (CollectionUtil.isNotEmpty(requestVO.getSeIconI18nCodeList())) {
            gameInfoPO.setSeIconI18nCode(seIconI18Code);
        }
        if (CollectionUtil.isNotEmpty(requestVO.getVtIconI18nCodeList())) {
            gameInfoPO.setVtIconI18nCode(vtIconI18Code);
        }
        if (CollectionUtil.isNotEmpty(requestVO.getHtIconI18nCodeList())) {
            gameInfoPO.setHtIconI18nCode(htIconI18Code);
        }
        if (CollectionUtil.isNotEmpty(requestVO.getIconI18nCodeList())) {
            gameInfoPO.setIconI18nCode(iconI18Code);
        }
        gameInfoPO.setGameI18nCode(gameNameI18Code);
        gameInfoPO.setGameDescI18nCode(gameDescI18Code);

    }

    public SiteGameResponseVO queryGameAuthorize(final SiteGameRequestVO siteGameRequestVO) {
        SiteGameResponseVO resultVO = new SiteGameResponseVO();
        Page<GameInfoPO> page = new Page<>(siteGameRequestVO.getPageNumber(), siteGameRequestVO.getPageSize());
        Page<SiteGameResponsePageVO> result = baseMapper.queryGameAuthorize(page, siteGameRequestVO);
//        result.getRecords().forEach(obj -> {
//            obj.setVenueTypeName(VenueTypeEnum.nameOfCode(obj.getVenueType()));
//        });
        List<String> allID = Lists.newArrayList();
        List<String> chooseId = Lists.newArrayList();

        List<String> finalChooseId = chooseId;
        siteGameService.selectList(new LambdaQueryWrapper<SiteGamePO>()
                        .eq(SiteGamePO::getSiteCode, siteGameRequestVO.getSiteCode()).eq(SiteGamePO::getVenueCode, siteGameRequestVO.getVenueCode()))
                .forEach(obj -> finalChooseId.add(obj.getGameInfoId()));
        List<GameInfoPO> gameInfoPOS = baseMapper.selectList(Wrappers.lambdaQuery(GameInfoPO.class).eq(GameInfoPO::getVenueCode, siteGameRequestVO.getVenueCode()));
        gameInfoPOS.forEach(obj -> allID.add(obj.getId()));
        if (StringUtils.isBlank(siteGameRequestVO.getSiteCode())) {
            gameInfoPOS.forEach(item -> finalChooseId.add(item.getGameId()));
        }
        if (CollectionUtil.isNotEmpty(chooseId)) {
            // 创建一个包含 allID 的集合
            Set<String> allIdSet = new HashSet<>(allID);

            // 过滤 chooseId，只保留在 allID 中存在的项
            chooseId = chooseId.stream()
                    .filter(allIdSet::contains)
                    .collect(Collectors.toList());
        }
        resultVO.setPageVO(result);
        resultVO.setAllID(allID);

        resultVO.setChooseID(chooseId);
        return resultVO;
    }


    public List<ShDeskInfoVO> getGameCodeList(String venueCode) {
        VenueEnum venueEnum = VenueEnum.nameOfCode(venueCode);

        if (venueCode.equals(VenueEnum.FTG.getVenueCode())) {
            return getFTGGameCodeList();
        } else if (venueCode.equals(VenueEnum.SH.getVenueCode())) {
            return getDeskStatusList();
        } else if (venueCode.equals(VenueEnum.CQ9.getVenueCode())) {
            return getCQ9GameCodeList();
        } else if (venueCode.equals(VenueEnum.JILI_03.getVenueCode())) {
            return getJILI3GameCodeList();
        } else if (venueCode.equals(VenueEnum.PG.getVenueCode())) {
            return getPGGameCodeList();
        } else if (venueCode.equals(VenueEnum.PP.getVenueCode())) {
            return getPPGameCodeList();
        } else if (venueCode.equals(VenueEnum.JDB.getVenueCode())) {
            return getJDBGameCodeList();
        } else if (venueCode.equals(VenueEnum.NEXTSPIN.getVenueCode())) {
            return getNextSpinGameCodeList();
        } else if (venueCode.equals(VenueEnum.LGD.getVenueCode())) {
            return getLGDGameCodeList();
        }else if(venueCode.equals(VenueEnum.EVO.getVenueCode())){
            return getEVOGameCodeList();
        }else {
            return getGameCodes(venueEnum);
        }
    }

    public List<ShDeskInfoVO> getNewGameCodeList(String venueCode) {
        GameService gameService = gameServiceFactory.getGameService(venueCode);
        return gameService.queryGameList();
    }

    /**
     * 查询JDB的游戏CODE
     */
    private List<ShDeskInfoVO> getJDBGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.JDB.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.JDB.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    /**
     * 查询CQ9的游戏CODE
     */
    private List<ShDeskInfoVO> getCQ9GameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.CQ9.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.CQ9.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    /**
     * 查询EVO的游戏CODE
     */
    private List<ShDeskInfoVO> getEVOGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.EVO.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.EVO.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }


    /**
     * 查询PG的游戏CODE
     */
    private List<ShDeskInfoVO> getPGGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.PG.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.PG.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    /**
     * 查询游戏CODE
     */
    private List<ShDeskInfoVO> getGameCodes(VenueEnum venueEnum) {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, venueEnum.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(venueEnum.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }


    /**
     * 查询PG的游戏CODE
     */
    private List<ShDeskInfoVO> getPPGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.PP.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.PP.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    /**
     * 查询FTG的游戏CODE
     */
    private List<ShDeskInfoVO> getFTGGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class).eq(VenueInfoPO::getVenueCode, VenueEnum.FTG.getVenueCode()));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.FTG.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    /**
     * 查询CQ9的游戏CODE
     */
    private List<ShDeskInfoVO> getJILI3GameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.JILI_03.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.JILI_03.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }


    /**
     * 查询JDB的游戏CODE
     */
    private List<ShDeskInfoVO> getNextSpinGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.NEXTSPIN.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.NEXTSPIN.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }

    /**
     * 查询JDB的游戏CODE
     */
    private List<ShDeskInfoVO> getLGDGameCodeList() {
        List<ShDeskInfoVO> resultList = Lists.newArrayList();
        VenueInfoPO venueInfoPO = venueInfoRepository.selectOne(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.LGD.getVenueCode())
                .last(" order by id  limit 1 "));
        VenueInfoVO venueInfoVO = new VenueInfoVO();
        BeanUtils.copyProperties(venueInfoPO, venueInfoVO);
        GameService gameService = gameServiceFactory.getGameService(VenueEnum.LGD.getVenueCode());
        ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(null, null, venueInfoVO);
        List<JSONObject> jsonList = jsonObjectResponseVO.getData();
        for (JSONObject item : jsonList) {
            resultList.add(JSONObject.parseObject(JSON.toJSONString(item), ShDeskInfoVO.class));
        }
        return resultList;
    }


    /**
     * 查询视讯的游戏CODE
     */
    private List<ShDeskInfoVO> getDeskStatusList() {
        List<VenueInfoPO> venueInfoList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, VenueEnum.SH.getVenueCode()));

        if (ObjectUtil.isEmpty(venueInfoList)) {
            return Lists.newArrayList();
        }
        VenueInfoPO venueInfo = venueInfoList.get(0);

        String url = venueInfo.getApiUrl() + SHConstantApi.DESK_STATUS_LIST;
        venueInfo.setApiUrl(url);
        Long timeStamp = System.currentTimeMillis();
        String hashStr = venueInfo.getMerchantNo() + timeStamp + venueInfo.getAesKey();
        ShReqBase shReqBase = new ShReqBase();
        shReqBase.setMerchantNo(venueInfo.getMerchantNo());
        shReqBase.setTimeStamp(timeStamp);
        shReqBase.setHashSign(hashStr);
        shReqBase.setMd5Sign(new HashMap<>(Map.of("merchantNo", venueInfo.getMerchantNo(), "timeStamp", timeStamp)));
        String response = HttpClientHandler.post(url, JSON.toJSONString(shReqBase));
        if (StringUtils.isBlank(response)) {
            return Lists.newArrayList();
        }

        ResponseVO<?> responseVO = JSONObject.parseObject(response, ResponseVO.class);
        if (0 != responseVO.getCode() || ObjectUtil.isEmpty(responseVO.getData())) {
            log.info("SH-调用桌台接口异常={}", responseVO);
            return Lists.newArrayList();
        }

        List<ShDeskStatusListResVO> list = JSON.parseArray(responseVO.getData().toString(), ShDeskStatusListResVO.class);
        if (CollectionUtil.isEmpty(list)) {
            log.info("SH-调用桌台接口转换异常={}", responseVO);
            return Lists.newArrayList();
        }


        return list.stream()
                // 提取每个 ShDeskStatusListResVO 对象的 deskResponseVOList
                .flatMap(shDeskStatusListResVO -> shDeskStatusListResVO.getDeskResponseVOList().stream())
                // 将 ShDeskStatusDetailResVO 转换为 DeskInfo
                .map(detail -> new ShDeskInfoVO(detail.getDeskName(), detail.getDeskNumber()))
                // 收集到一个列表中
                .collect(Collectors.toList());
//        return getList();
    }


    public ResponseVO<List<GameInfoVO>> getByIds(List<String> gameIds) {
        LambdaQueryWrapper<GameInfoPO> query = Wrappers.lambdaQuery();
        query.in(GameInfoPO::getId, gameIds);
        List<GameInfoPO> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (list.size() != gameIds.size()) {
            throw new BaowangDefaultException(ResultCode.VENUE_CHOOSE_ERROR);
        }
        return ResponseVO.success(BeanUtil.copyToList(list, GameInfoVO.class));
    }


    public List<HotRemTypeVO> getHotRemTypeList() {
        List<HotRemTypeVO> result = Lists.newArrayList();
        HotRemTypeVO top1 = HotRemTypeVO.builder()
                .gameOneClassId(String.valueOf(GameHotRemTypeEnum.FRONT_PAGE.getCode()))
                .gameOneClassName(String.valueOf(GameHotRemTypeEnum.FRONT_PAGE.getCode()))
                .build();
        result.add(top1);

        List<GameOneClassInfoVO> list = gameOneClassInfoService.getAllGameOneClassInfoList();


        for (GameOneClassInfoVO x : list) {
            if (x.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())
                    || x.getModel().equals(GameOneModelEnum.SBA.getCode())) {
                continue;
            }
            HotRemTypeVO gameOne = HotRemTypeVO.builder()
                    .gameOneClassId(x.getId())
                    .gameOneClassName(x.getDirectoryI18nCode())
                    .build();
            result.add(gameOne);
        }

        //清理站点缓存
        siteCleanGameInfo();

        return Lists.newArrayList(result);
    }


    public List<GameInfoHotVO> getNewHotGameInfoList(HotRemTypeReqVO hotRemTypeReqVO) {

        if (ObjectUtil.isEmpty(hotRemTypeReqVO.getGameOneClassId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String gameOneClassId = hotRemTypeReqVO.getGameOneClassId();

        String currencyCode = hotRemTypeReqVO.getCurrencyCode();

        String siteCode = CurrReqUtils.getSiteCode();

        //首页游戏列表
        if (gameOneClassId.equals(GameHotRemTypeEnum.FRONT_PAGE.getCode())) {
            List<GameInfoVO> list = baseMapper.getSiteGameHomeHotList(currencyCode, siteCode);
            list = list.stream().sorted(Comparator.comparing(GameInfoVO::getHomeHotSort)).toList();

            return list.stream().map(x -> {
                GameInfoHotVO vo = new GameInfoHotVO();
                BeanUtils.copyProperties(x, vo);
                return vo;
            }).toList();
        }

        //一级分类热门排序
        List<GameInfoVO> list = baseMapper.getSiteGameHotList(currencyCode, siteCode, gameOneClassId);
        list = list.stream().sorted(Comparator.comparing(GameInfoVO::getGameOneHotSort)).toList();
        return list.stream().map(x -> {
            GameInfoHotVO vo = new GameInfoHotVO();
            BeanUtils.copyProperties(x, vo);
            return vo;
        }).toList();

    }


    /**
     * 一级分类排序
     */
    private Boolean upNewGameOneSort(UpHotRemTypeReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String gameOneId = reqVO.getGameOneClassId();
        List<UpHotRemTypeCurrencyReqVO> list = reqVO.getList();
        for (UpHotRemTypeCurrencyReqVO currencyReqVOItem : list) {
            String currencyCode = currencyReqVOItem.getCurrencyCode();
            List<GameClassInfoSetSortDetailVO> voList = currencyReqVOItem.getVoList();
            for (GameClassInfoSetSortDetailVO sortItem : voList) {
                GameTwoCurrencySortPO currencySortPO = GameTwoCurrencySortPO.builder()
                        .gameOneHomeSort(sortItem.getSort())
                        .build();
                LambdaQueryWrapper<GameTwoCurrencySortPO> queryWrapper = Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                        .eq(GameTwoCurrencySortPO::getSiteCode, siteCode)
                        .eq(GameTwoCurrencySortPO::getCurrencyCode, currencyCode)
                        .eq(GameTwoCurrencySortPO::getGameId, sortItem.getId())
                        .eq(GameTwoCurrencySortPO::getGameOneId, gameOneId);
                gameTwoCurrencySortService.getBaseMapper().update(currencySortPO, queryWrapper);
            }
        }
        //清理站点缓存
        siteCleanGameInfo();
        return Boolean.TRUE;
    }

    /**
     * 首页热门分类排序
     */
    private Boolean upNewHomeHotGameInfo(UpHotRemTypeReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();

        List<SiteGamePO> siteGamePOList = siteGameService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getSiteCode, siteCode));

        Map<String, SiteGamePO> siteGameMap = siteGamePOList.stream().collect(Collectors.toMap(SiteGamePO::getGameInfoId, Function.identity()));


        List<SiteGameHotSortPO> addList = Lists.newArrayList();
        List<UpHotRemTypeCurrencyReqVO> list = reqVO.getList();
        for (UpHotRemTypeCurrencyReqVO currencyReqVOItem : list) {
            String currencyCode = currencyReqVOItem.getCurrencyCode();
            siteGameHotSortService.getBaseMapper().delete(Wrappers.lambdaQuery(SiteGameHotSortPO.class)
                    .eq(SiteGameHotSortPO::getCurrencyCode, currencyCode)
                    .eq(SiteGameHotSortPO::getSiteCode, siteCode)
            );
            List<GameClassInfoSetSortDetailVO> voList = currencyReqVOItem.getVoList();
            for (GameClassInfoSetSortDetailVO sortItem : voList) {
                SiteGamePO siteGamePO = siteGameMap.get(sortItem.getId());
                if (siteGamePO == null) {
                    continue;
                }
                SiteGameHotSortPO siteGameHotSortPO = SiteGameHotSortPO
                        .builder()
                        .siteCode(siteCode)
                        .currencyCode(currencyReqVOItem.getCurrencyCode())
                        .siteGameId(siteGamePO.getId())
                        .gameId(sortItem.getId())
                        .homeHotSort(sortItem.getSort().longValue())
                        .build();
                addList.add(siteGameHotSortPO);
            }
        }
        if (CollectionUtil.isNotEmpty(addList)) {
            siteGameHotSortService.saveBatch(addList, addList.size());
        }
        //清理站点缓存
        siteCleanGameInfo();
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean upNewHotGameInfoList(UpHotRemTypeReqVO reqVO) {

        //一级分类排序
        if (!reqVO.getHotType()) {
            return upNewGameOneSort(reqVO);
        }

        String siteCode = CurrReqUtils.getSiteCode();

        //首页热门分类排序
        if (reqVO.getGameOneClassId().equals(GameHotRemTypeEnum.FRONT_PAGE.getCode())) {
            return upNewHomeHotGameInfo(reqVO);
        }

        List<SiteGamePO> siteGamePOList = siteGameService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getSiteCode, siteCode));

        Map<String, SiteGamePO> siteGameMap = siteGamePOList.stream().collect(Collectors.toMap(SiteGamePO::getGameInfoId, Function.identity()));

        List<UpHotRemTypeCurrencyReqVO> list = reqVO.getList();

        for (UpHotRemTypeCurrencyReqVO currencyReqVOItem : list) {

            if (ObjectUtil.isEmpty(reqVO.getGameOneClassId())) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            List<GameClassInfoSetSortDetailVO> voList = currencyReqVOItem.getVoList();

            if (CollectionUtil.isEmpty(voList)) {
                continue;
            }

            if (CollectionUtil.isEmpty(siteGamePOList)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            for (GameClassInfoSetSortDetailVO item : currencyReqVOItem.getVoList()) {
                SiteGamePO siteGamePO = siteGameMap.get(item.getId());
                if (siteGamePO == null) {
                    continue;
                }
                GameTwoCurrencySortPO currencySortPO = GameTwoCurrencySortPO.builder()
                        .gameOneHotSort(item.getSort())
                        .build();
                LambdaQueryWrapper<GameTwoCurrencySortPO> queryWrapper = Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                        .eq(GameTwoCurrencySortPO::getSiteCode, siteCode)
                        .eq(GameTwoCurrencySortPO::getCurrencyCode, currencyReqVOItem.getCurrencyCode())
                        .eq(GameTwoCurrencySortPO::getGameId, item.getId())
                        .eq(GameTwoCurrencySortPO::getGameOneId, reqVO.getGameOneClassId());
                gameTwoCurrencySortService.getBaseMapper().update(currencySortPO, queryWrapper);
            }
        }
        //清理站点缓存
        siteCleanGameInfo();
        return true;
    }


    public List<GameInfoHotVO> getNewGameInfoSortByGameOneId(HotRemTypeReqVO reqVO) {
        List<GameInfoVO> list = baseMapper.getSiteGameHomeList(reqVO.getCurrencyCode(), CurrReqUtils.getSiteCode(), reqVO.getGameOneClassId());
        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }

        list = list.stream().sorted(Comparator.comparing(GameInfoVO::getGameOneHomeSort)).toList();


        return list.stream().map(x -> {
            GameInfoHotVO vo = new GameInfoHotVO();
            BeanUtils.copyProperties(x, vo);
            return vo;
        }).toList();
    }


    public List<SiteGameInfoVO> getConfigSiteGameInfoList(GameInfoRequestVO vo) {
        return baseMapper.getConfigSiteGameInfoList(vo.getCurrencyCode(), CurrReqUtils.getSiteCode(), vo.getVenueCode());
    }


    public UpGameInfoCurrencyInfoVO getAddGameCurrencyInfo(AddGameCurrencyInfoVO requestVO) {
        List<GameInfoPO> list = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                .in(GameInfoPO::getId, requestVO.getIdBatch()));

        if (CollectionUtil.isEmpty(list)) {
            log.info("游戏不存在");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }


        List<String> venueCodeList = list.stream().map(GameInfoPO::getVenueCode).toList();
        venueCodeList = venueCodeList.stream().distinct().toList();

        if (venueCodeList.size() > 1) {
            throw new BaowangDefaultException(ResultCode.CROSS_VENUE);
        }

        String venueCode = venueCodeList.get(0);

        List<VenueInfoPO> venueInfoList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getVenueCode, venueCode));

        if (CollectionUtil.isEmpty(venueInfoList)) {
            log.info("场馆未找到:{}", venueCode);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        VenueInfoPO venueInfoPO = venueInfoList.get(0);
        String venueName = venueInfoPO.getVenuePlatformName();
        List<String> gameIdList = list.stream().map(GameInfoPO::getGameId).toList();

        List<String> venueCurrencyList = null;

        //单场馆币种
        if (VenueCurrencyTypeEnum.SINGLE_CURRENCY.getCode().equals(venueInfoPO.getVenueCurrencyType())) {
            venueCurrencyList = venueInfoList.stream().map(VenueInfoPO::getCurrencyCode).toList();
        } else {
            String currencyCode = venueInfoPO.getCurrencyCode();
            venueCurrencyList = Arrays.asList(currencyCode.split(","));
        }

        return UpGameInfoCurrencyInfoVO.builder()
                .venueName(venueName)
                .venueCode(venueInfoPO.getVenueCode())
                .gameIdList(gameIdList)
                .currencyCodeList(venueCurrencyList)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean addGameCurrencyInfo(AddGameCurrencyInfoVO addGameCurrencyInfoVO) {
        List<GameInfoPO> list = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                .in(GameInfoPO::getId, addGameCurrencyInfoVO.getIdBatch()));

        if (CollectionUtil.isEmpty(list)) {
            return Boolean.FALSE;
        }

        List<String> currencyList = addGameCurrencyInfoVO.getCurrencyList();

        if (CollectionUtil.isEmpty(currencyList)) {
            return Boolean.FALSE;
        }

        for (GameInfoPO item : list) {

            Set<String> gameCurrencyCodeSet = new HashSet<>();
            if (StringUtils.isNotBlank(item.getCurrencyCode())) {
                gameCurrencyCodeSet.addAll(Arrays.asList(item.getCurrencyCode().split(",")));
            }

            // 记录原始长度用于判断是否真的新增了
            int originalSize = gameCurrencyCodeSet.size();

            // 尝试添加新币种
            gameCurrencyCodeSet.addAll(currencyList);

            // 如果实际有新增
            if (gameCurrencyCodeSet.size() > originalSize) {
                String updatedCurrencyCode = String.join(",", gameCurrencyCodeSet);
                GameInfoPO gameInfoPO = GameInfoPO.builder().currencyCode(updatedCurrencyCode).build();
                gameInfoPO.setId(item.getId());
                if (gameInfoRepository.updateById(gameInfoPO) > 0) {
                    SiteGamePO siteGamePO = SiteGamePO.builder().currencyCode(updatedCurrencyCode).build();
                    siteGameRepository.update(siteGamePO, Wrappers.lambdaQuery(SiteGamePO.class)
                            .eq(SiteGamePO::getGameInfoId, item.getId()));
                }
            }
        }
        CompletableFuture.runAsync(LobbyCateUtil::deleteLobbyAllSiteGameInfo);
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean delGameCurrencyInfo(AddGameCurrencyInfoVO addGameCurrencyInfoVO) {
        List<GameInfoPO> list = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                .in(GameInfoPO::getId, addGameCurrencyInfoVO.getIdBatch()));

        if (CollectionUtil.isEmpty(list)) {
            return Boolean.FALSE;
        }

        //需要删除的币种
        List<String> currencyList = addGameCurrencyInfoVO.getCurrencyList();

        if (CollectionUtil.isEmpty(currencyList)) {
            return Boolean.FALSE;
        }

        for (GameInfoPO item : list) {


            if (StringUtils.isBlank(item.getCurrencyCode())) {
                continue;
            }
            Set<String> gameCurrencyCodeSet = new HashSet<>(Arrays.asList(item.getCurrencyCode().split(",")));

            // 记录原始长度用于判断是否真的删除了
            int originalSize = gameCurrencyCodeSet.size();

            // 尝试删除币种
            currencyList.forEach(gameCurrencyCodeSet::remove);

            if (gameCurrencyCodeSet.size() < originalSize) {
                String updatedCurrencyCode = String.join(",", gameCurrencyCodeSet);
                GameInfoPO gameInfoPO = GameInfoPO.builder().currencyCode(updatedCurrencyCode).build();
                gameInfoPO.setId(item.getId());
                if (gameInfoRepository.updateById(gameInfoPO) > 0) {
                    SiteGamePO siteGamePO = SiteGamePO.builder().currencyCode(updatedCurrencyCode).build();
                    siteGameRepository.update(siteGamePO, Wrappers.lambdaQuery(SiteGamePO.class)
                            .eq(SiteGamePO::getGameInfoId, item.getId()));
                    //因为币种删除了所以关联关系要去除
                    siteGameHotSortService.getBaseMapper().delete(Wrappers
                            .lambdaQuery(SiteGameHotSortPO.class)
                            .eq(SiteGameHotSortPO::getGameId, item.getId())
                            .in(SiteGameHotSortPO::getCurrencyCode, currencyList));
                    gameTwoCurrencySortService.getBaseMapper().delete(Wrappers
                            .lambdaQuery(GameTwoCurrencySortPO.class)
                            .eq(GameTwoCurrencySortPO::getGameId, item.getId())
                            .in(GameTwoCurrencySortPO::getCurrencyCode, currencyList));
                }
            }
        }
        CompletableFuture.runAsync(LobbyCateUtil::deleteLobbyAllSiteGameInfo);
        return true;
    }


}

package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.GameOneClassInfoRepository;
import com.cloud.baowang.play.repositories.GameTwoClassInfoRepository;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Service
@Slf4j
public class GameTwoClassInfoService extends ServiceImpl<GameTwoClassInfoRepository, GameTwoClassInfoPO> {


    private final SiteGameService siteGameService;

    private final I18nApi i18nApi;

    private final GameInfoService gameInfoService;

    private final GameOneClassInfoRepository gameOneClassInfoRepository;

    private final GameTwoCurrencySortService gameTwoCurrencySortService;

    private final LanguageManagerApi languageManagerApi;

    private final SiteApi siteApi;

    private final GameOneFloatConfigService gameOneFloatConfigService;

    public Boolean update(GameTwoClassInfoPO gameTwoClassInfoPO, LambdaQueryWrapper<GameTwoClassInfoPO> wrapper) {
        Boolean bool = baseMapper.update(gameTwoClassInfoPO, wrapper) > 0;
        if (bool) {
            LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        }
        return bool;
    }


    public Boolean delete(LambdaQueryWrapper<GameTwoClassInfoPO> wrapper) {
        Boolean bool = baseMapper.delete(wrapper) > 0;
        if (bool) {
            LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        }
        return bool;
    }

    public Boolean insert(GameTwoClassInfoPO gameTwoClassInfoPO) {
        Boolean bool = baseMapper.insert(gameTwoClassInfoPO) > 0;
        if (bool) {
            LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        }
        return bool;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean deleteAdminGameTwoClassInfoById(String id) {
        if (ObjectUtils.isEmpty(id)) {
            log.info("删除失败缺少参数");
            return Boolean.FALSE;
        }

        GameTwoClassInfoPO gameTwoClassInfoPO = baseMapper.selectOne(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(GameTwoClassInfoPO::getId, id));

        if (ObjectUtil.isEmpty(gameTwoClassInfoPO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!gameTwoClassInfoPO.getStatus().equals(StatusEnum.CLOSE.getCode())) {
            log.info("二级分类:{},删除失败,状态未禁用不可删", id);
            return Boolean.FALSE;
        }

        Boolean bool = delete(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getId, id)
                .eq(GameTwoClassInfoPO::getStatus, StatusEnum.CLOSE.getCode())
                .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (!bool) {
            log.info("二级分类:{},删除失败,状态未禁用不可改", id);
            return Boolean.FALSE;
        }

        //二级分类下存在游戏,不可以删
        Long count = gameTwoCurrencySortService.getBaseMapper().selectCount(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                .eq(GameTwoCurrencySortPO::getGameTwoId,id)
                .eq(GameTwoCurrencySortPO::getSiteCode,CurrReqUtils.getSiteCode()));

        if (count > 0) {
            throw new BaowangDefaultException(ResultCode.GAME_JOIN_TWO_CANNOT_BE_DELETED);
        }

        log.info("删除二级分类:{},同时删除该分类下的游戏关联关系:{}", id, count);
        i18nApi.deleteByMsgKey(gameTwoClassInfoPO.getTypeI18nCode());
        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        return Boolean.TRUE;
    }


    public Page<GameTwoClassInfoVO> getGameTwoClassInfoPage(GameClassTwoRequestVO requestVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<GameTwoClassInfoPO> wrapper = Wrappers
                .lambdaQuery(GameTwoClassInfoPO.class)
                .eq(ObjectUtil.isNotEmpty(requestVO.getId()), GameTwoClassInfoPO::getId, requestVO.getId())
                .like(ObjectUtil.isNotEmpty(requestVO.getTypeName()), GameTwoClassInfoPO::getTypeName, requestVO.getTypeName())
                .eq(ObjectUtil.isNotEmpty(requestVO.getStatus()), GameTwoClassInfoPO::getStatus, requestVO.getStatus())
                .eq(ObjectUtil.isNotEmpty(requestVO.getGameOneId()), GameTwoClassInfoPO::getGameOneId, requestVO.getGameOneId())
                .in(CollectionUtil.isNotEmpty(requestVO.getGameOneIds()), GameTwoClassInfoPO::getGameOneId, requestVO.getGameOneIds())
                .eq(GameTwoClassInfoPO::getSiteCode, siteCode).orderByDesc(GameTwoClassInfoPO::getStatus)
                .orderByAsc(GameTwoClassInfoPO::getSort)
                .orderByDesc(GameTwoClassInfoPO::getCreatedTime);

        IPage<GameTwoClassInfoPO> page = baseMapper.selectPage(PageConvertUtil.getMybatisPage(requestVO), wrapper);

        List<GameTwoClassInfoPO> list = page.getRecords();

        if (CollectionUtil.isEmpty(list)) {
            return PageConvertUtil.getMybatisPage(requestVO);
        }

        List<String> twoIds = list.stream().map(GameTwoClassInfoPO::getId).toList();

        List<String> oneIds = list.stream().map(GameTwoClassInfoPO::getGameOneId).distinct().collect(Collectors.toList());

        Map<String, String> gameOneNameMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(oneIds)) {
            List<GameOneClassInfoPO> gameOneClassInfoList = gameOneClassInfoRepository.selectList(Wrappers.lambdaQuery(GameOneClassInfoPO.class).in(GameOneClassInfoPO::getId, oneIds).eq(GameOneClassInfoPO::getSiteCode, siteCode));
            if (CollectionUtil.isNotEmpty(gameOneClassInfoList)) {
                gameOneNameMap = gameOneClassInfoList.stream().collect(Collectors.toMap(GameOneClassInfoPO::getId, GameOneClassInfoPO::getDirectoryName));
            }
        }


        List<GameTwoCurrencySortPO> gameTwoCurrencySortList = gameTwoCurrencySortService.getBaseMapper()
                .selectList(Wrappers
                        .lambdaQuery(GameTwoCurrencySortPO.class)
                        .in(GameTwoCurrencySortPO::getGameTwoId, twoIds)
                        .eq(GameTwoCurrencySortPO::getSiteCode, siteCode));

        Map<String, List<GameTwoCurrencySortPO>> mapGameTwoCurrencyMap = gameTwoCurrencySortList.stream().collect(Collectors.groupingBy(GameTwoCurrencySortPO::getGameTwoId));

        // 获取总台全部语种
        List<I18nMsgFrontVO> frontVOS = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        if (responseVO.isOk() && null != responseVO.getData()) {
            List<LanguageManagerListVO> languageManagerListVOS = languageManagerApi.list().getData();
            languageManagerListVOS.forEach(obj -> frontVOS.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }


        Map<String, String> finalGameOneNameMap = gameOneNameMap;
        IPage<GameTwoClassInfoVO> iPage = page.convert(s -> {
            GameTwoClassInfoVO vo = new GameTwoClassInfoVO();
            BeanUtils.copyProperties(s, vo);
            vo.setHtIconI18nCodeList(frontVOS);
            List<GameTwoCurrencySortPO> gameInfoList = mapGameTwoCurrencyMap.get(s.getId());
            int gameCount = 0;
            if (CollectionUtil.isNotEmpty(gameInfoList)) {
                List<String> twoGameIds = gameInfoList.stream().map(GameTwoCurrencySortPO::getGameId).toList();
                twoGameIds = twoGameIds.stream().distinct().toList();
                gameCount = twoGameIds.size();
            }
            vo.setGameSize(gameCount);


            String gameOneId = s.getGameOneId();
            if (!StringUtils.isBlank(gameOneId)) {
                String gameOneName = finalGameOneNameMap.get(gameOneId);
                vo.setGameOneName(gameOneName);
            }
            return vo;
        });


        List<GameTwoClassInfoVO> resultList = iPage.getRecords();
        resultList = sortGameTwoClassInfoList(resultList);
        iPage.setRecords(resultList);
        return ConvertUtil.toConverPage(iPage);
    }

    private List<GameTwoClassInfoVO> sortGameTwoClassInfoList(List<GameTwoClassInfoVO> list) {
        list.sort(new Comparator<GameTwoClassInfoVO>() {
            @Override
            public int compare(GameTwoClassInfoVO o1, GameTwoClassInfoVO o2) {
                int statusPriority1 = getStatusPriority(o1.getStatus());
                int statusPriority2 = getStatusPriority(o2.getStatus());

                // 优先按 status 排序
                if (statusPriority1 != statusPriority2) {
                    return Integer.compare(statusPriority1, statusPriority2);
                }

                // 然后按时间排序（updatedTime 优先，否则用 createdTime），倒序
                Long time1 = o1.getUpdatedTime() != null ? o1.getUpdatedTime() : o1.getCreatedTime();
                Long time2 = o2.getUpdatedTime() != null ? o2.getUpdatedTime() : o2.getCreatedTime();

                if (time1 == null && time2 == null) return 0;
                if (time1 == null) return 1;
                if (time2 == null) return -1;

                return Long.compare(time2, time1); // 倒序
            }

            private int getStatusPriority(Integer status) {
                if (status == null) return Integer.MAX_VALUE;
                return switch (status) {
                    case 1 -> 0;
                    case 2 -> 1;
                    case 3 -> 2;
                    default -> 3;
                };
            }
        });
        return list;
    }


    public Boolean setAdminSortGameTwoClassInfo(GameTwoSortReqVO requestVO) {
        List<GameClassInfoSetSortDetailVO> voList = requestVO.getVoList();
        for (GameClassInfoSetSortDetailVO item : voList) {
            GameTwoClassInfoPO po = GameTwoClassInfoPO.builder().sort(item.getSort()).build();
            update(po, Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                    .eq(GameTwoClassInfoPO::getId, item.getId())
                    .eq(GameTwoClassInfoPO::getGameOneId, requestVO.getGameOneClassId())
                    .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode()));
        }
        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean addAdminGameTwoClassInfo(GameTwoClassAddVO requestVO) {
        requestVO.valid(requestVO.getTypeI18nCodeList());
        GameOneClassInfoPO gameOneClassInfoPO = gameOneClassInfoRepository.selectById(requestVO.getGameOneId());

        if (ObjectUtil.isEmpty(gameOneClassInfoPO)) {
            log.info("一级分类未找到:{}", requestVO.getGameOneId());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<GameTwoClassCurrencyUserInfoVO> gameTwoClassCurrencyUserInfoList = requestVO.getGameTwoClassCurrencyUserInfoList();

        //单场馆游戏只能有一个二级分类并且只能有一个游戏
        if (gameOneClassInfoPO.getModel().equals(GameOneModelEnum.SBA.getCode())) {
            log.info("原声游戏无法新增二级分类:{}", requestVO.getGameOneId());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        //单场馆游戏只能有一个二级分类并且只能有一个游戏
        if (gameOneClassInfoPO.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())) {
            Long count = baseMapper.selectCount(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                    .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameTwoClassInfoPO::getGameOneId, CurrReqUtils.getSiteCode()));

            if (count > 0) {
                log.info("单场馆游戏只能新建一个二级分类:{}", requestVO.getGameOneId());
                throw new BaowangDefaultException(ResultCode.TWO_SIGN_VENUE_ERROR);
            }

            for (GameTwoClassCurrencyUserInfoVO item : gameTwoClassCurrencyUserInfoList) {
                if (item.getGameIds().size() > 1) {
                    log.info("单场馆游戏只能新建一个游戏:{}", requestVO.getGameOneId());
                    throw new BaowangDefaultException(ResultCode.TWO_SIGN_VENUE_ONE_GAME_ERROR);
                }
            }
        }

        int sort = 1;

        GameTwoClassInfoPO sortPO = baseMapper.selectOne(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                .orderByDesc(GameTwoClassInfoPO::getSort).last(" limit 1 "));

        if (ObjectUtil.isNotEmpty(sortPO)) {
            sort = sortPO.getSort() + 1;
        }

        GameTwoClassInfoPO gameTwoClassInfoPO = GameTwoClassInfoPO.builder().build();
        String typeI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_TWO_TYPE.getCode());
        BeanUtils.copyProperties(requestVO, gameTwoClassInfoPO);
        gameTwoClassInfoPO.setSort(sort);
        gameTwoClassInfoPO.setStatus(StatusEnum.CLOSE.getCode());
        gameTwoClassInfoPO.setSiteCode(CurrReqUtils.getSiteCode());
        gameTwoClassInfoPO.setTypeI18nCode(typeI18nCode);

        List<I18nMsgFrontVO> zhCnTypeName = requestVO.getTypeI18nCodeList().stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();

        if (CollectionUtil.isNotEmpty(zhCnTypeName)) {
            String typeName = zhCnTypeName.get(0).getMessage();
            gameTwoClassInfoPO.setTypeName(typeName);
            if (baseMapper.selectCount(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                    .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameTwoClassInfoPO::getTypeName, typeName)) > 0) {
                log.info("二级分类名称重复");
                throw new BaowangDefaultException(ResultCode.GAME_TWO_NAME_REPEAT);
            }
        }

        String h5I18nCode = toSetI18Code(gameTwoClassInfoPO.getHtIconI18nCode(), I18MsgKeyEnum.GAME_TWO_HT_ICON, requestVO.getHtIconI18nCodeList());
        gameTwoClassInfoPO.setHtIconI18nCode(h5I18nCode);

        Boolean bool = insert(gameTwoClassInfoPO);
        if (!bool) {
            log.info("二级分类添加失败");
            return Boolean.FALSE;
        }

        List<GameTwoCurrencySortPO> currencyInsterList = Lists.newArrayList();

        for (GameTwoClassCurrencyUserInfoVO currencyGameInfoItem : gameTwoClassCurrencyUserInfoList) {
            Integer maxGameOneHomeSort = getMaxGameOneHomeSort(currencyGameInfoItem.getCurrencyCode(), requestVO.getGameOneId());
            Integer maxGameOneHotSort = getMaxGameOneHotSort(currencyGameInfoItem.getCurrencyCode(), requestVO.getGameOneId());

            if (CollectionUtil.isNotEmpty(currencyGameInfoItem.getGameIds())) {

                List<GameClassInfoSetSortDetailVO> sortDetailList = currencyGameInfoItem.getGameIds().stream().distinct().toList();

                List<String> gameIds = sortDetailList.stream().map(GameClassInfoSetSortDetailVO::getId).toList();

                List<String> siteCodeGameIds = siteGameService.getSiteGameIdsBySiteCode(CurrReqUtils.getSiteCode(), gameIds);

                if (CollectionUtil.isNotEmpty(siteCodeGameIds)) {


                    for (GameClassInfoSetSortDetailVO item : sortDetailList) {
                        if (ObjectUtil.isEmpty(item.getId()) || ObjectUtil.isEmpty(item.getSort())) {
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                        if (!siteCodeGameIds.contains(item.getId())) {
                            continue;
                        }

                        String joinId = String.valueOf(IdUtil.getSnowflakeNextId());
                        GameTwoCurrencySortPO gameTwoCurrencySortPO = GameTwoCurrencySortPO.builder()
                                .sort(item.getSort())
                                .siteCode(CurrReqUtils.getSiteCode())
                                .currencyCode(currencyGameInfoItem.getCurrencyCode())
                                .gameTwoId(gameTwoClassInfoPO.getId())
                                .gameJoinId(joinId)
                                .gameId(item.getId())
                                .gameOneHotSort(maxGameOneHotSort++)
                                .gameOneHomeSort(maxGameOneHomeSort++)
                                .gameOneId(gameTwoClassInfoPO.getGameOneId())
                                .build();
                        currencyInsterList.add(gameTwoCurrencySortPO);
                    }
                }
            }
        }

        if (CollectionUtil.isNotEmpty(currencyInsterList)) {
            int batchSize = 2000;
            int total = currencyInsterList.size();
            for (int i = 0; i < total; i += batchSize) {
                int end = Math.min(i + batchSize, total);
                List<GameTwoCurrencySortPO> subList = currencyInsterList.subList(i, end);
                gameTwoCurrencySortService.saveBatch(subList,batchSize);
            }
        }


        Map<String, List<I18nMsgFrontVO>> i18ParamMap = I18nMsgBindUtil.bind(typeI18nCode, requestVO.getTypeI18nCodeList());
        ResponseVO<Boolean> responseVO = i18nApi.insert(i18ParamMap);
        if (!responseVO.isOk() || !responseVO.getData()) {
            log.info("调用i18是失败:param:{},result:{}", i18ParamMap, responseVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        //初始化悬浮
        gameOneFloatConfigService.initTwoFloatConfig(gameTwoClassInfoPO.getId(), gameOneClassInfoPO.getId(), typeI18nCode);


        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());

        return Boolean.TRUE;
    }

    public List<TwoCurrencyGameInfoListVO> gameInfoListByOneId(){

        String siteCode = CurrReqUtils.getSiteCode();

        List<GameInfoPO> gameInfoList = gameInfoService.getSiteGameInfoList(siteCode);

        if (CollectionUtil.isEmpty(gameInfoList)) {
            return Lists.newArrayList();
        }

        List<TwoCurrencyGameInfoListVO> twoCurrencyGameInfoListVO = Lists.newArrayList();

        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();

        String currencyCodes = siteVO.getCurrencyCodes();
        List<String> currencyList = Arrays.stream(currencyCodes.split(","))
                .map(String::trim)
                .toList();
        if (CollectionUtil.isEmpty(currencyList)) {
            return Lists.newArrayList();
        }

        for (String currencyCode : currencyList) {
            List<TwoGameInfoVO> currencyGameList = Lists.newArrayList();
            for (GameInfoPO gameInfoVO : gameInfoList) {
                if (ObjectUtil.isNotEmpty(gameInfoVO.getCurrencyCode())) {
                    List<String> gameCurrencyList = Arrays.asList(gameInfoVO.getCurrencyCode().split(","));
                    if(gameCurrencyList.contains(currencyCode)){
                        currencyGameList.add(
                                TwoGameInfoVO
                                        .builder()
                                        .id(gameInfoVO.getId())
                                        .status(gameInfoVO.getStatus())
                                        .gameName(gameInfoVO.getGameI18nCode())
                                        .venueCode(gameInfoVO.getVenueCode())
                                        .build()
                        );
                    }
                }
            }
            twoCurrencyGameInfoListVO.add(TwoCurrencyGameInfoListVO.builder().currencyCode(currencyCode).allGameList(currencyGameList).build());
        }

        return twoCurrencyGameInfoListVO;
    }

    public TwoGameInfoListVO getNewAdminGameInfoByTwoId(GameInfoDelVO request) {
        if(ObjectUtil.isEmpty(request.getId())){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        TwoGameInfoListVO resultList = TwoGameInfoListVO.builder().currencyInGameList(Lists.newArrayList()).currencyAllGameList(Lists.newArrayList()).build();
        String siteCode = CurrReqUtils.getSiteCode();

        List<GameInfoPO> gameInfoList = gameInfoService.getDBSiteGameInfoList(siteCode);

        if (CollectionUtil.isEmpty(gameInfoList)) {
            return resultList;
        }

        SiteVO siteVO = siteApi.getSiteInfo(siteCode).getData();

        String currencyCodes = siteVO.getCurrencyCodes();
        List<String> currencyList = Arrays.stream(currencyCodes.split(","))
                .map(String::trim)
                .toList();
        if (CollectionUtil.isEmpty(currencyList)) {
            return resultList;
        }

        //初始化的币种列表
        List<TwoCurrencyGameInfoListVO> initList = Lists.newArrayList();
        for (String currencyCode : currencyList) {
            initList.add(TwoCurrencyGameInfoListVO.builder().currencyCode(currencyCode).allGameList(Lists.newArrayList()).build());
        }

        List<TwoCurrencyGameInfoListVO> twoCurrencyGameInfoListVO = Lists.newArrayList();
        List<TwoCurrencyGameInfoListVO> allCurrencInGameList = Lists.newArrayList();

        for (String currencyCode : currencyList) {
            List<TwoGameInfoVO> currencyGameList = Lists.newArrayList();
            for (GameInfoPO gameInfoVO : gameInfoList) {



                if (ObjectUtil.isNotEmpty(gameInfoVO.getCurrencyCode())) {
                    List<String> gameCurrencyList = Arrays.asList(gameInfoVO.getCurrencyCode().split(","));
                    if(gameCurrencyList.contains(currencyCode)){
                        currencyGameList.add(
                                TwoGameInfoVO
                                        .builder()
                                        .id(gameInfoVO.getId())
                                        .status(gameInfoVO.getStatus())
                                        .gameName(gameInfoVO.getGameI18nCode())
                                        .venueCode(gameInfoVO.getVenueCode())
                                        .build()
                        );
                    }
                }
            }
            twoCurrencyGameInfoListVO.add(TwoCurrencyGameInfoListVO.builder().currencyCode(currencyCode).allGameList(currencyGameList).build());
        }

        resultList.setCurrencyAllGameList(twoCurrencyGameInfoListVO);


        List<GameTwoCurrencySortPO> gameTwoCurrencySortList = gameTwoCurrencySortService.getBaseMapper()
                .selectList(Wrappers
                        .lambdaQuery(GameTwoCurrencySortPO.class)
                        .eq(GameTwoCurrencySortPO::getGameTwoId, request.getId())
                        .eq(GameTwoCurrencySortPO::getSiteCode, CurrReqUtils.getSiteCode()));

        if (CollectionUtil.isEmpty(gameTwoCurrencySortList)) {
            resultList.setCurrencyInGameList(initList);
            return resultList;
        }

        Map<String, List<GameTwoCurrencySortPO>> gameTwoCurrencyMap = gameTwoCurrencySortList.stream()
                .collect(Collectors.groupingBy(GameTwoCurrencySortPO::getCurrencyCode));


        Map<String,GameInfoPO> gameMap = gameInfoList.stream().collect(Collectors.toMap(GameInfoPO::getId,Function.identity()));
        for (String currencyCode : currencyList) {
            List<TwoGameInfoVO> currencyGameList = Lists.newArrayList();
            List<GameTwoCurrencySortPO> gameTwoCurrencySortPOS = gameTwoCurrencyMap.get(currencyCode);
            if(CollectionUtil.isNotEmpty(gameTwoCurrencySortPOS)){
                for (GameTwoCurrencySortPO inGame :  gameTwoCurrencySortPOS){
                    GameInfoPO gameInfoVO = gameMap.get(inGame.getGameId());
                    if(ObjectUtil.isNotEmpty(gameInfoVO)){
                        currencyGameList.add(
                                TwoGameInfoVO
                                        .builder()
                                        .id(gameInfoVO.getId())
                                        .status(gameInfoVO.getStatus())
                                        .gameName(gameInfoVO.getGameI18nCode())
                                        .venueCode(gameInfoVO.getVenueCode())
                                        .sort(inGame.getSort())
                                        .build()
                        );
                    }
                }
            }

            currencyGameList = currencyGameList.stream().sorted(Comparator.comparing(TwoGameInfoVO::getSort)).toList();
            allCurrencInGameList.add(TwoCurrencyGameInfoListVO.builder().currencyCode(currencyCode).allGameList(currencyGameList).build());
        }
        resultList.setCurrencyInGameList(allCurrencInGameList);
        return resultList;

    }



    /**
     * 获取出最大的这个币种下的一级分类首页排序
     */
    private Integer getMaxGameOneHomeSort(String currencyCode, String gameOneId) {
        return gameTwoCurrencySortService.getBaseMapper().getMaxGameOneHomeSort(CurrReqUtils.getSiteCode(), gameOneId, currencyCode);
    }


    /**
     * 获取出最大的这个币种下的一级分类热门排序
     */
    private Integer getMaxGameOneHotSort(String currencyCode, String gameOneId) {
        return gameTwoCurrencySortService.getBaseMapper().getMaxGameOneHotSort(CurrReqUtils.getSiteCode(), gameOneId, currencyCode);
    }




    @Transactional(rollbackFor = {Exception.class})
    public Boolean updateGameTwoClassInfo(GameTwoClassUpVO requestVO) {
        List<I18nMsgFrontVO> languageList = requestVO.getTypeI18nCodeList();
        requestVO.valid(languageList);

        String siteCode = CurrReqUtils.getSiteCode();

        List<GameTwoClassCurrencyUserInfoVO> gameTwoClassCurrencyUserInfoList = requestVO.getGameTwoClassCurrencyUserInfoList();

        GameTwoClassInfoPO po = new GameTwoClassInfoPO();
        BeanUtils.copyProperties(requestVO, po);

        List<I18nMsgFrontVO> zhCnTypeName = requestVO.getTypeI18nCodeList().stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();

        if (CollectionUtil.isNotEmpty(zhCnTypeName)) {
            po.setTypeName(zhCnTypeName.get(0).getMessage());
        }

        GameOneClassInfoPO gameOneClassInfoPO = gameOneClassInfoRepository.selectById(requestVO.getGameOneId());

        if (ObjectUtil.isEmpty(gameOneClassInfoPO)) {
            log.info("修改二级分类.一级分类未找到:{}", requestVO.getGameOneId());
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        //原声游戏不存在二级分类
        if (gameOneClassInfoPO.getModel().equals(GameOneModelEnum.SBA.getCode())) {
            log.info("修改二级分类.原声游戏无法新增二级分类:{}", requestVO.getGameOneId());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }


        //单场馆游戏只能有一个二级分类并且只能有一个游戏
        if (gameOneClassInfoPO.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())) {
            Long count = baseMapper.selectCount(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                    .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameTwoClassInfoPO::getGameOneId, requestVO.getGameOneId()));

            if (count > 0) {
                log.info("修改二级分类.单场馆游戏只能新建一个二级分类:{}", requestVO.getGameOneId());
                throw new BaowangDefaultException(ResultCode.TWO_SIGN_VENUE_ERROR);
            }

            if (CollectionUtil.isNotEmpty(gameTwoClassCurrencyUserInfoList)) {
                for (GameTwoClassCurrencyUserInfoVO item : gameTwoClassCurrencyUserInfoList) {
                    if (item.getGameIds().size() > 1) {
                        log.info("修改二级分类.单场馆游戏只能新建一个游戏:{}", requestVO.getGameOneId());
                        throw new BaowangDefaultException(ResultCode.TWO_SIGN_VENUE_ONE_GAME_ERROR);
                    }
                }
            }
        }

        GameTwoClassInfoPO gameTwoClassInfoPO = baseMapper.selectOne(Wrappers
                .lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(GameTwoClassInfoPO::getId, requestVO.getId()));

        if (ObjectUtil.isEmpty(gameTwoClassInfoPO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }


        if (!gameTwoClassInfoPO.getGameOneId().equals(requestVO.getGameOneId())) {
            throw new BaowangDefaultException(ResultCode.GAME_TWO_UP_ERROR);
        }


        if (CollectionUtil.isNotEmpty(zhCnTypeName)) {
            String typeName = zhCnTypeName.get(0).getMessage();
            gameTwoClassInfoPO.setTypeName(typeName);
            if (baseMapper.selectCount(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                    .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .ne(GameTwoClassInfoPO::getId, requestVO.getId())
                    .eq(GameTwoClassInfoPO::getTypeName, typeName)) > 0) {
                log.info("二级分类名称重复");
                throw new BaowangDefaultException(ResultCode.GAME_TWO_NAME_REPEAT);
            }
        }

        String h5I18nCode = toSetI18Code(gameTwoClassInfoPO.getHtIconI18nCode(), I18MsgKeyEnum.GAME_TWO_HT_ICON, requestVO.getHtIconI18nCodeList());
        po.setHtIconI18nCode(h5I18nCode);
        Boolean bool = update(po, Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getId, requestVO.getId())
                .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
        );
        if (!bool) {
            log.info("修改二级分类:{},修改失败", requestVO.getId());
            return Boolean.FALSE;
        }

        gameTwoCurrencySortService.getBaseMapper().delete(Wrappers.lambdaQuery(GameTwoCurrencySortPO.class)
                .eq(GameTwoCurrencySortPO::getSiteCode, siteCode)
                .eq(GameTwoCurrencySortPO::getGameTwoId, requestVO.getId()));

        List<GameTwoCurrencySortPO> insertList = new ArrayList<>();

        for (GameTwoClassCurrencyUserInfoVO item : gameTwoClassCurrencyUserInfoList) {
            String currencyCode = item.getCurrencyCode();
            List<GameClassInfoSetSortDetailVO> gameIds = item.getGameIds();

            for (GameClassInfoSetSortDetailVO gameItem : gameIds) {
                GameTwoCurrencySortPO sortPO = GameTwoCurrencySortPO.builder()
                        .siteCode(siteCode)
                        .gameJoinId(String.valueOf(IdUtil.getSnowflakeNextId()))
                        .currencyCode(currencyCode)
                        .gameOneId(gameOneClassInfoPO.getId())
                        .gameTwoId(requestVO.getId())
                        .gameId(gameItem.getId())
                        .sort(gameItem.getSort())
                        .gameOneHomeSort(gameItem.getSort())
                        .gameOneHotSort(gameItem.getSort())
                        .build();
                insertList.add(sortPO);
            }
        }

        int batchSize = 2000;
        for (int i = 0; i < insertList.size(); i += batchSize) {
            int end = Math.min(i + batchSize, insertList.size());
            List<GameTwoCurrencySortPO> subList = insertList.subList(i, end);
            gameTwoCurrencySortService.saveBatch(subList,batchSize);
        }



        Map<String, List<I18nMsgFrontVO>> reqMap = I18nMsgBindUtil.bind(gameTwoClassInfoPO.getTypeI18nCode(), languageList);
        ResponseVO<Boolean> responseVO = i18nApi.update(reqMap);
        if (!responseVO.isOk() || !responseVO.getData()) {
            log.info("调用i18是失败:param:{},result:{}", reqMap, responseVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        return Boolean.TRUE;
    }



    /**
     * 指定KYE 枚举 插入或者修改到多语言表中
     */
    private String toSetI18Code(String i18Code, I18MsgKeyEnum keyEnum, List<I18nMsgFrontVO> iconI18nCodeList) {
        ResponseVO<Boolean> iconI18responseVO = null;
        if (ObjectUtil.isNotEmpty(i18Code) && CollectionUtil.isNotEmpty(iconI18nCodeList)) {
            Map<String, List<I18nMsgFrontVO>> req = Maps.newHashMap();
            req.put(i18Code, iconI18nCodeList);
            iconI18responseVO = i18nApi.update(req);
        } else {
            if(CollectionUtil.isEmpty(iconI18nCodeList)){
                return null;
            }
            for (I18nMsgFrontVO item : iconI18nCodeList){
                if(ObjectUtil.isEmpty(item.getMessage())){
                    item.setMessage("");
                }
            }
            i18Code = RedisKeyTransUtil.getI18nDynamicKey(keyEnum.getCode());
            Map<String, List<I18nMsgFrontVO>> pcReq = Maps.newHashMap();
            pcReq.put(i18Code, iconI18nCodeList);
            iconI18responseVO = i18nApi.insert(pcReq);
        }

        if (!iconI18responseVO.isOk() || !iconI18responseVO.getData()) {
            log.info("调用i18是失败:result:{}", iconI18responseVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return i18Code;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean updateAdminGameTwoClassInfoStatus(GameClassStatusRequestUpVO request) {
        GameTwoClassInfoPO gameTwoClassInfoPO = baseMapper.selectById(request.getId());
        if (ObjectUtil.isEmpty(gameTwoClassInfoPO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (ObjectUtil.isNotEmpty(request.getStatus())) {
            if (ObjectUtil.isEmpty(StatusEnum.nameByCode(request.getStatus()))) {
                log.info("请求参数异常:{}", request);
                return Boolean.FALSE;
            }
        }


        GameTwoClassInfoPO po = new GameTwoClassInfoPO();
        BeanUtils.copyProperties(request, po);

        Boolean bool = update(po, Wrappers.lambdaQuery(GameTwoClassInfoPO.class).eq(GameTwoClassInfoPO::getId, request.getId()).eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (!bool) {
            log.info("修改二级分类:{},修改失败", request.getId());
            return Boolean.FALSE;
        }

        if(request.getStatus().equals(StatusEnum.CLOSE.getCode())){
            //禁用启用,实时同步到悬浮到二级分类
            GameOneFloatConfigPO configPO = GameOneFloatConfigPO.builder().status(request.getStatus()).build();
            gameOneFloatConfigService.getBaseMapper().update(configPO, Wrappers.lambdaQuery(GameOneFloatConfigPO.class)
                    .eq(GameOneFloatConfigPO::getModel, GameOneModelEnum.CA.getCode())
                    .eq(GameOneFloatConfigPO::getGameTwoId, request.getId()));

        }

        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        return Boolean.TRUE;
    }


    public List<GameTwoClassInfoVO> getGameTwoClassInfoList() {
        List<GameTwoClassInfoVO> list = RedisUtil.getValue(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_GAME_TWO));
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }
        GameClassTwoRequestVO requestVO = GameClassTwoRequestVO.builder().build();
        requestVO.setPageSize(5000);
        List<GameTwoClassInfoVO> twoClassInfoVOList = getGameTwoClassInfoPage(requestVO).getRecords();

        if (CollectionUtil.isNotEmpty(twoClassInfoVOList)) {
            RedisUtil.setValue(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_GAME_TWO), twoClassInfoVOList, 10L, TimeUnit.SECONDS);

        }
        return twoClassInfoVOList;
    }

    public List<GameTwoClassInfoVO> getGameTwoClassInfoList(GameClassTwoRequestVO requestVO) {
        List<GameTwoClassInfoVO> gameTwoClassInfoList = getGameTwoClassInfoList();

        if (CollectionUtil.isEmpty(gameTwoClassInfoList)) {
            return Lists.newArrayList();
        }

        Integer status = requestVO.getStatus();
        if (ObjectUtil.isNotEmpty(status)) {
            gameTwoClassInfoList = gameTwoClassInfoList.stream().filter(item -> status.equals(item.getStatus())).toList();
        }

        String gameOneId = requestVO.getGameOneId();
        if (ObjectUtil.isNotEmpty(gameOneId)) {
            List<String> gameOneIds = CollectionUtil.isEmpty(requestVO.getGameOneIds()) ? Lists.newArrayList() : requestVO.getGameOneIds();
            gameOneIds.add(gameOneId);
            requestVO.setGameOneIds(gameOneIds);
        }

        List<String> gameOneIds = requestVO.getGameOneIds();
        if (CollectionUtil.isNotEmpty(gameOneIds)) {
            gameTwoClassInfoList = gameTwoClassInfoList.stream().filter(item -> gameOneIds.contains(item.getGameOneId())).toList();
        }
        gameTwoClassInfoList = gameTwoClassInfoList.stream().sorted(Comparator.comparing(GameTwoClassInfoVO::getSort)).toList();
        return gameTwoClassInfoList;
    }

}

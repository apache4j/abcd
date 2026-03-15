package com.cloud.baowang.play.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabsVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.utils.NumberUtil;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.ClientGameInfoLabelEnum;
import com.cloud.baowang.play.api.enums.GameInfoLabelEnum;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.game.ACELTMaxAmount;
import com.cloud.baowang.play.api.vo.game.SportLobbyEvents;
import com.cloud.baowang.play.api.vo.lobby.*;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.game.acelt.enums.AceltGameInfoEnum;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.GameCollectionRepository;
import com.cloud.baowang.play.repositories.GameInfoRepository;
import com.cloud.baowang.system.api.api.banner.SiteBannerConfigApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.api.partner.SitePartnerApi;
import com.cloud.baowang.system.api.api.partner.SitePaymentVendorApi;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.enums.banner.BannerLinkTargetEnum;
import com.cloud.baowang.system.api.vo.banner.SiteBannerConfigAppQueryVO;
import com.cloud.baowang.system.api.vo.banner.SiteBannerConfigPageRespVO;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.operations.DomainQueryVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@Service
@Slf4j
public class PlayLobbyGameService {


//    private final GameJoinClassService gameJoinClassService;

    private final GameCollectionRepository gameCollectionRepository;

    private final GameOneClassInfoService gameClassInfoService;

//    private final GameTwoClassInfoService gameTwoClassInfoService;

    private final GameInfoRepository gameInfoRepository;

    private final I18nApi i18nApi;

    private final SitePartnerApi sitePartnerApi;

    private final SitePaymentVendorApi sitePaymentVendorApi;

    private final SiteBannerConfigApi configApi;

    private final GameInfoService gameInfoService;

    private final ActivityBaseApi activityBaseApi;
    private final ActivityBaseV2Api activityBaseV2Api;

    private final GameServiceFactory gameServiceFactory;

    private final VenueInfoService venueInfoService;

    private final HelpCenterManageApi helpCenterManageApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final SportEventsService sportEventsService;

    private final GameTwoCurrencySortService gameTwoCurrencySortService;

    private final SiteEventsService siteEventsService;
    private final GameOneFloatConfigService gameOneFloatConfigService;

    private final DomainInfoApi domainInfoApi;


    /**
     * @param result          需要返回的数据
     * @param vo              一级分类组装好的数据
     * @param oneClassInfoVO  一级分类循环对象
     * @param siteCode        站点
     * @param sysCurrencyCode 币种
     * @return 一级分类中组装好的单场馆对象
     */
    private void setSignVenueLabel(List<LobbyGameOneVO> result, LobbyGameOneVO vo,
                                   GameOneClassInfoVO oneClassInfoVO,
                                   String siteCode, String sysCurrencyCode) {
        List<GameOneClassVenueInfoVO> lobbySignVenueCode = oneClassInfoVO.getLobbySignVenueCode();
        lobbySignVenueCode = lobbySignVenueCode.stream().filter(f ->
                f.getCurrencyCode().equals(sysCurrencyCode)
        ).toList();
        List<LobbySignVenueInfoVO> signVenueInfoList = Lists.newArrayList();

        List<LobbyGameOneFloatVO> pcFloatList = vo.getPcFloatList();

        Map<String, LobbyGameOneFloatVO> pcFloatMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(pcFloatList)) {
            pcFloatMap = pcFloatList.stream().collect(Collectors.toMap(LobbyGameOneFloatVO::getVenueCode, Function.identity()));
        }

        if (CollectionUtil.isNotEmpty(lobbySignVenueCode)) {
            for (GameOneClassVenueInfoVO gameOneClassVenueInfoVO : lobbySignVenueCode) {
                VenueInfoVO siteSignVenueConfig = venueInfoService.getSiteVenueInfoByVenueCode(siteCode, gameOneClassVenueInfoVO.getVenueCode(), null);

                if (siteSignVenueConfig != null && !siteSignVenueConfig.getStatus().equals(StatusEnum.CLOSE.getCode())) {
                    LobbyGameOneFloatVO lobbyGameOneFloatVO = pcFloatMap.get(gameOneClassVenueInfoVO.getVenueCode());

                    //场馆维护中单情况下需要把场馆的状态同步到一级悬浮页面
                    if (ObjectUtil.isNotEmpty(lobbyGameOneFloatVO) && siteSignVenueConfig.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {
                        lobbyGameOneFloatVO.setStatus(siteSignVenueConfig.getStatus());
                        lobbyGameOneFloatVO.setMaintenanceStartTime(siteSignVenueConfig.getMaintenanceStartTime());
                        lobbyGameOneFloatVO.setMaintenanceEndTime(siteSignVenueConfig.getMaintenanceEndTime());
                    }

                    String icon = siteSignVenueConfig.getH5IconI18nCode();
                    if (DeviceType.PC.getCode().equals(CurrReqUtils.getReqDeviceType())) {
                        icon = siteSignVenueConfig.getPcIconI18nCode();
                    }
                    LobbySignVenueInfoVO lobbySignVenueInfoVO = LobbySignVenueInfoVO.builder().build();
                    BeanUtils.copyProperties(siteSignVenueConfig, lobbySignVenueInfoVO);
                    lobbySignVenueInfoVO.setName(siteSignVenueConfig.getVenueNameI18nCode());
                    lobbySignVenueInfoVO.setSort(Long.valueOf(gameOneClassVenueInfoVO.getSort()));
                    lobbySignVenueInfoVO.setIcon(icon);
                    lobbySignVenueInfoVO.setGameOneClassId(oneClassInfoVO.getId());
                    lobbySignVenueInfoVO.setMaxRebateAmount(vo.getMaxRebateAmount());
                    signVenueInfoList.add(lobbySignVenueInfoVO);

                }
            }
        }

        vo.setGameInfoList(signVenueInfoList);
        if (CollectionUtil.isNotEmpty(signVenueInfoList)) {
            result.add(vo);
        }
    }


    /**
     * 沙巴体育侧边栏组装数据
     *
     * @param result          需要返回的数据
     * @param vo              一级分类组装好的数据
     * @param sysCurrencyCode 币种
     */
    private void setSBALabel(List<LobbyGameOneVO> result, LobbyGameOneVO vo, String sysCurrencyCode) {
        VenueInfoVO siteVenueInfoByVenueCode = venueInfoService.getSiteVenueInfoByVenueCode(CurrReqUtils.getSiteCode(), VenueEnum.SBA.getVenueCode(), null);
        if (siteVenueInfoByVenueCode == null || !siteVenueInfoByVenueCode.getStatus().equals(StatusEnum.OPEN.getCode())) {
            return;
        }
        //沙巴体育游戏 没有币种或者币种不支持不显示侧边栏
        if (CollectionUtil.isEmpty(siteVenueInfoByVenueCode.getCurrencyCodeList()) || !siteVenueInfoByVenueCode.getCurrencyCodeList().contains(sysCurrencyCode)) {
            return;
        }
        vo.setVenueCode(VenueEnum.SBA.getVenueCode());
        result.add(vo);
    }

    /**
     * 多游戏侧边栏组装数据
     *
     * @param result 需要返回的数据
     * @param vo     一级分类组装好的数据
     */
    private void setGameInfoListLabel(List<LobbyGameOneVO> result, Map<String, List<GameInfoVO>> gameInfoMap,
                                      GameOneClassInfoVO oneClassInfoVO, LobbyGameOneVO vo) {
        log.info("多游戏侧边栏组装数据:result:"+result+"--------gameInfoMap:------"+gameInfoMap+"--------oneClassInfoVO:"+oneClassInfoVO+"-------vo:"+vo);
        List<GameInfoVO> gameInfoVOS = gameInfoMap.get(oneClassInfoVO.getId());
        if (CollectionUtil.isEmpty(gameInfoVOS)) {
            return;
        }

        List<LobbyGameTwoVO> resultList = Lists.newArrayList();

        //配置的PC鼠标悬浮列表
        List<LobbyGameOneFloatVO> pcFloatList = vo.getPcFloatList();

        Map<String, LobbyGameOneFloatVO> pcFloatMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(pcFloatList)) {
            pcFloatMap = pcFloatList.stream().collect(Collectors.toMap(LobbyGameOneFloatVO::getGameTwoId, Function.identity()));
        }

        //根据二级分类的组装悬浮列表
        List<LobbyGameOneFloatVO> newPcFloatList = Lists.newArrayList();

        Map<String, List<GameInfoVO>> gameTwoMap = gameInfoVOS.stream().collect(Collectors.groupingBy(GameInfoVO::getGameTwoId));
        for (Map.Entry<String, List<GameInfoVO>> itemMap : gameTwoMap.entrySet()) {
            String twoId = itemMap.getKey();
            List<GameInfoVO> twoGameList = itemMap.getValue();
            log.info("twoGameList:{}", twoGameList);
            if (CollectionUtil.isEmpty(twoGameList)) {
                continue;
            }

            GameInfoVO gameInfoVO = twoGameList.get(0);

            LobbyGameOneFloatVO lobbyGameOneFloatVO =  pcFloatMap.get(twoId);
            if(ObjectUtil.isNotEmpty(lobbyGameOneFloatVO)){
                //二级分类的排序要与二级分类悬浮一样的
                lobbyGameOneFloatVO.setSort(gameInfoVO.getClassTwoSort().intValue());
                newPcFloatList.add(lobbyGameOneFloatVO);
            }

            resultList.add(LobbyGameTwoVO.builder()
                    .eventsNumber((long) twoGameList.size())
                    .id(twoId)
                    .gameOneClassId(oneClassInfoVO.getId())
                    .sort(gameInfoVO.getClassTwoSort().intValue())
                    .name(gameInfoVO.getGameTwoClassName())
                    .icon(gameInfoVO.getClassTwoIcon())
                    .maxRebateAmount(vo.getMaxRebateAmount())
                    .classTwoHtIconI18nCode(gameInfoVO.getClassTwoHtIconI18nCode())
                    .siteLabelChangeType(gameInfoVO.getSiteLabelChangeType())
                    .build());
        }
        resultList = resultList.stream().sorted(Comparator.comparing(LobbyGameTwoVO::getSort)).toList();
        newPcFloatList = newPcFloatList.stream().sorted(Comparator.comparing(LobbyGameOneFloatVO::getSort)).toList();
        vo.setTwoList(resultList);
        vo.setPcFloatList(newPcFloatList);
        log.info("配置完的vo:{}",vo);
        result.add(vo);
    }

    public BigDecimal queryGameOnePrizePool(String id) {
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.HOME_PRIZE_POOL)+id);

        GameOneClassInfoVO gameOneClassInfoVO  = RedisUtil.getValue(key);
        if (Objects.isNull(gameOneClassInfoVO)) {
            gameOneClassInfoVO = gameClassInfoService.getGameOneClassInfoById(id);
            RedisUtil.setValue(key, gameOneClassInfoVO, 5L, TimeUnit.MINUTES);
        }

        if (ObjectUtil.isEmpty(gameOneClassInfoVO)) {
            return null;
        }

        BigDecimal prizePoolStar = gameOneClassInfoVO.getPrizePoolStart();
        BigDecimal prizePoolEnd = gameOneClassInfoVO.getPrizePoolEnd();
        BigDecimal max = BigDecimal.valueOf(99999999);//最大

        BigDecimal maxData = gameOneClassInfoVO.getPrizePoolTotal();
        if (ObjectUtil.isEmpty(maxData)) {
            return null;
        }

        long random =   NumberUtil.getRandom(1, 2);
        long randomData = NumberUtil.getRandom(prizePoolStar.intValue(), prizePoolEnd.intValue());
        BigDecimal result;
        if (CommonConstant.business_one == random) {
            // 做加法
            result = maxData.add(BigDecimal.valueOf(randomData));
        } else {
            // 做减法
            result = maxData.subtract(BigDecimal.valueOf(randomData));
        }

        if (result.compareTo(max) > 0) {
            result = maxData;
        }

        return result.setScale(0, RoundingMode.DOWN);
    }

    public List<LobbyGameOneVO> newQueryLobbyLabelList() {
        String sysCurrencyCode = getCurrency();
        String siteCode = CurrReqUtils.getSiteCode();
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.SITE_LOBBY_LABEL, sysCurrencyCode));

        List<LobbyGameOneVO> cacheResultList = RedisUtil.getList(key);
        if (CollectionUtil.isNotEmpty(cacheResultList)) {
            return cacheResultList;
        }
        //根据币种查询出对应的一级分类
        List<GameOneClassInfoVO> gameOneInfoList = gameClassInfoService.getLobbyGameOneSortClassInfoList(sysCurrencyCode);
        if (CollectionUtil.isEmpty(gameOneInfoList)) {
            return Lists.newArrayList();
        }

        //侧边栏根据一级分类的 目录排序
        gameOneInfoList = gameOneInfoList.stream()
                .sorted(Comparator.comparingInt(GameOneClassInfoVO::getDirectorySort))
                .toList();

        //查出游戏大厅的所有游戏并且把每个一级分类下的游戏,Key =一级分类,v=游戏列表
        List<GameInfoVO> gameInfoList = gameInfoRepository.getLobbyLabelList(sysCurrencyCode, siteCode);
        Map<String, List<GameInfoVO>> gameInfoMap = gameInfoList.stream().collect(Collectors.groupingBy(GameInfoVO::getGameOneId));

        List<LobbyGameOneVO> result = Lists.newArrayList();

        Map<String, List<LobbyGameOneFloatVO>> lobbyMap = gameOneFloatConfigService.getFloatConfigMap(siteCode, sysCurrencyCode);

        for (GameOneClassInfoVO x : gameOneInfoList) {
            LobbyGameOneVO vo = new LobbyGameOneVO();
            vo.setGameOneClassId(x.getId());
            vo.setDirectoryName(x.getDirectoryI18nCode());
            vo.setHomeName(x.getHomeI18nCode());
            vo.setIcon(x.getIcon());
            vo.setIcon2(x.getIcon2());
            vo.setTypeIconI18nCode(x.getTypeIconI18nCode());
            vo.setModelCode(x.getModel());
            List<LobbyGameOneFloatVO> gameOneFloatList = lobbyMap.get(x.getId());
            vo.setPcFloatList(gameOneFloatList);
            vo.setRebateVenueType(x.getRebateVenueType());
            //根据币种取出一级分类中的返水配置
            Map<String, BigDecimal> currencyRebateMap = x.getCurrencyRebateMap();
            if (ObjectUtil.isNotEmpty(currencyRebateMap)) {
                BigDecimal rebateAmount = currencyRebateMap.get(sysCurrencyCode);
                vo.setMaxRebateAmount(rebateAmount);
            }
            switch (GameOneModelEnum.nameOfCode(x.getModel())) {
                case SIGN_VENUE -> setSignVenueLabel(result, vo, x, siteCode, sysCurrencyCode);//单场馆游戏
                case SBA -> setSBALabel(result, vo, sysCurrencyCode);//沙巴
                case CA, ACELT -> setGameInfoListLabel(result, gameInfoMap, x, vo);//多游戏跟场馆
            }
        }

        if (CollectionUtil.isNotEmpty(result)) {
            RedisUtil.setList(key, result, 5L, TimeUnit.MINUTES);
        }
        log.info("一级分类返回结果:{}",result);
        return result;

    }

    public List<LobbyGameTwoDetailVO> newQueryGameInfoByOneClassId(LobbyQueryTwoDetailRequestVO requestVO) {
        List<LobbyGameTwoDetailVO> resultList = new ArrayList<>();
        String gameOneId = requestVO.getGameOneId();

        // 获取一级分类信息
        GameOneClassInfoVO gameOneClassInfoVO = gameClassInfoService.getGameOneClassInfoById(gameOneId);
        if (gameOneClassInfoVO == null) {
            log.info("queryGameInfoByOneClassId: GameOneClassInfoVO not found for ID: {}", gameOneId);
            return resultList;
        }

        String sysCurrency = getCurrency();

        String siteCode = CurrReqUtils.getSiteCode();

        //一个一级分类一个币种一个数据缓存
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_QUERY_LOBBY_BY_ONE_GAME, gameOneId, sysCurrency));
        List<GameInfoVO> list = RedisUtil.getValue(key);
        if (CollectionUtil.isEmpty(list)) {
            list = gameInfoRepository.getGameInfoByOneClassId(gameOneId, sysCurrency, siteCode);
            if (CollectionUtil.isNotEmpty(list)) {
                RedisUtil.setValue(key, list, 5L, TimeUnit.MINUTES);
            }
        }

        Map<String, GameCollectionPO> finalGameCollectionMap = getGameCollection();

        List<LobbyGameInfoVO> gameInfoList = list.stream().map(x -> {
                    LobbyGameInfoVO vo = LobbyGameInfoVO.builder()
                            .build();

                    vo.setSort(x.getSort().longValue());
                    BeanUtils.copyProperties(x, vo);
                    boolean isCollected = finalGameCollectionMap.containsKey(x.getId());
                    vo.setCollect(isCollected);

                    vo.setIcon(x.getIconI18nCode());
                    vo.setName(x.getGameI18nCode());
                    vo.setGameCode(x.getAccessParameters());

                    VenueEnum venueEnum = VenueEnum.of(vo.getVenueCode());
                    if (venueEnum.getType().equals(VenueTypeEnum.ACELT)) {
                        //彩票的游戏根据玩法查询出对应的彩种
                        vo.setGameCategoryCode(AceltGameInfoEnum.getGameTypeByGameCode(x.getAccessParameters()));
                    }
                    return vo;
                }
        ).toList();

        //组装彩票的信息
//        toSetAceLtList(gameInfoList, sysCurrency);

        // 处理标签游戏信息
        addGameDetails(resultList, gameInfoList, GameInfoLabelEnum.HOT_RECOMMENDED.getCode());
        addGameDetails(resultList, gameInfoList, GameInfoLabelEnum.NEWS.getCode());


        Map<String, List<LobbyGameInfoVO>> gameTwoMap = gameInfoList.stream().collect(Collectors.groupingBy(LobbyGameInfoVO::getGameTwoId));

        List<LobbyGameTwoDetailVO> twoResultList = new ArrayList<>();
        for (Map.Entry<String, List<LobbyGameInfoVO>> item : gameTwoMap.entrySet()) {
            List<LobbyGameInfoVO> gameInfoVOS = item.getValue();
            if (CollectionUtil.isEmpty(gameInfoVOS)) {
                continue;
            }
            gameInfoVOS = gameInfoVOS.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getSort)).toList();
            LobbyGameTwoDetailVO lobbyGameTwoDetailVO = LobbyGameTwoDetailVO.builder()
                    .id(item.getKey())
                    .name(gameInfoVOS.get(0).getGameTwoClassName())
                    .label(ClientGameInfoLabelEnum.NULL.getCode())
                    .gameInfoList(gameInfoVOS)
                    .icon(gameInfoVOS.get(0).getClassTwoIcon())
                    .classTwoSort(gameInfoVOS.get(0).getClassTwoSort())
                    .build();
            twoResultList.add(lobbyGameTwoDetailVO);
        }

        twoResultList = twoResultList.stream().sorted(Comparator.comparing(LobbyGameTwoDetailVO::getClassTwoSort)).toList();
        // 获取二级分类详细信息
        resultList.addAll(twoResultList);

        return resultList;
    }


    /**
     * 添加游戏详细信息到结果列表
     */
    private void addGameDetails(List<LobbyGameTwoDetailVO> resultList,
                                List<LobbyGameInfoVO> gameInfoList, Integer label) {

        List<LobbyGameInfoVO> filteredGameInfoList = new ArrayList<>(gameInfoList.stream()
                .filter(gameInfo -> label.equals(gameInfo.getLabel()))
                .collect(Collectors.toMap(
                        LobbyGameInfoVO::getId,           // 以 ID 为 key
                        Function.identity(),              // value 是对象本身
                        (existing, replacement) -> existing // 如果 ID 重复，保留第一个
                ))
                .values());


        filteredGameInfoList = filteredGameInfoList.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getGameOneHotSort)).toList();
        if (GameInfoLabelEnum.NEWS.getCode().equals(label)) {
            filteredGameInfoList = filteredGameInfoList.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getId)).toList();
        }


        LobbyGameTwoDetailVO lobbyGameTwoDetailVO = LobbyGameTwoDetailVO.builder()
                .label(ClientGameInfoLabelEnum.nameOfCode(label).getCode())
                .gameInfoList(filteredGameInfoList)
                .build();


        if (CollectionUtils.isNotEmpty(filteredGameInfoList)) {
            resultList.add(lobbyGameTwoDetailVO);
        }
    }


    public Page<LobbyGameInfoVO> newQueryGameInfoByName(LobbyGameDetailRequestVO gameInfoRequest) {
//        String gameOneId = gameInfoRequest.getGameOneId();
//        // 获取一级分类信息
//        GameOneClassInfoVO gameOneClassInfoVO = gameClassInfoService.getGameOneClassInfoById(gameOneId);
//        if (gameOneClassInfoVO == null) {
//            log.info("queryGameInfoListByOneClassId: GameOneClassInfoVO not found for ID: {}", gameOneId);
//            return PageConvertUtil.getMybatisPage(gameInfoRequest);
//        }

        String sysCurrencyCode = getCurrency();
        gameInfoRequest.setCurrencyCode(sysCurrencyCode);
        String siteCode = CurrReqUtils.getSiteCode();

        if (ObjectUtil.isNotEmpty(gameInfoRequest.getGameNameCode())) {
            List<String> gameNameList = i18nApi.search(I18nSearchVO.builder()
                    .searchContent(gameInfoRequest.getGameNameCode())
                    .bizKeyPrefix(I18MsgKeyEnum.GAME_NAME.getCode())
                    .lang(CurrReqUtils.getLanguage())
                    .build()).getData();
            if (CollectionUtil.isEmpty(gameNameList)) {
                return PageConvertUtil.getMybatisPage(gameInfoRequest);
            }
            gameInfoRequest.setGameI18nCodeList(gameNameList);
        }

        IPage<GameInfoPO> page = PageConvertUtil.getMybatisPage(gameInfoRequest);

        IPage<GameInfoVO> iPage = gameInfoRepository.getGameInfoLobbyPage(page, gameInfoRequest, siteCode);

        IPage<LobbyGameInfoVO> lobbyGamePage = getGameInfoToLobbyGameInfo(iPage, sysCurrencyCode);

        List<LobbyGameInfoVO> lobbyGameList = lobbyGamePage.getRecords().stream().sorted(Comparator.comparing(LobbyGameInfoVO::getSort)).toList();

        lobbyGamePage.setRecords(lobbyGameList);

        return ConvertUtil.toConverPage(lobbyGamePage);
    }


    public Page<Skin4LobbyGameInfoVO> getGameInfoSkin4(LobbyGameDetailRequestVO requestVO,
                                                       Map<String, GameCollectionPO> finalGameCollectionMap) {
        IPage<GameInfoVO> iPage = gameInfoRepository.getGameInfoLobbyPage(
                PageConvertUtil.getMybatisPage(requestVO),
                requestVO,
                CurrReqUtils.getSiteCode()
        );

        List<Skin4LobbyGameInfoVO> records = iPage.getRecords().stream()
                .map(x -> {
                    Skin4LobbyGameInfoVO vo = new Skin4LobbyGameInfoVO();
                    BeanUtils.copyProperties(x, vo);

                    vo.setSort(Optional.ofNullable(x.getSort())
                            .map(Number::longValue)
                            .orElse(100L));

                    vo.setCollect(finalGameCollectionMap.containsKey(x.getId()));
                    vo.setIcon(x.getIconI18nCode());
                    vo.setName(x.getGameI18nCode());
                    vo.setGameCode(x.getAccessParameters());
                    return vo;
                })
                .toList();

        // 组装分页对象
        Page<Skin4LobbyGameInfoVO> resultPage = new Page<>(iPage.getCurrent(), iPage.getSize(), iPage.getTotal());
        resultPage.setRecords(records);
        return resultPage;
    }


    public Skin4LobbyGameByTwoListVO queryGameSkin4ByTwoId(Sin4LobbyGameRequestVO req) {


        Skin4LobbyGameByTwoListVO resultVO = Skin4LobbyGameByTwoListVO.builder()
                .allGameTotal(0L)
                .hotGameTotal(0L)
                .newGameTotal(0L)
                .gamePage(PageConvertUtil.getMybatisPage(req))
                .build();

        String sysCurrency = getCurrency();


        LobbyGameDetailRequestVO requestVO = LobbyGameDetailRequestVO.builder().gameTwoId(req.getGameTwoId()).currencyCode(sysCurrency).label(req.getSkin4Label()).build();
        requestVO.setPageSize(req.getPageSize());
        requestVO.setPageNumber(req.getPageNumber());

        GameInfoLabelEnum gameInfoLabelEnum = GameInfoLabelEnum.nameOfCode(req.getSkin4Label());

        String siteCode = CurrReqUtils.getSiteCode();


        Page<Skin4LobbyGameInfoVO> resultPage = null;

        Map<String, GameCollectionPO> finalGameCollectionMap = getGameCollection();

        if (requestVO.getPageSize() == -1) {
            requestVO.setPageSize(5000);
        }
        List<String> gameNameList = Lists.newArrayList();

        if (ObjectUtil.isNotEmpty(req.getGameName())) {
             gameNameList = i18nApi.search(I18nSearchVO.builder()
                    .searchContent(req.getGameName())
                    .bizKeyPrefix(I18MsgKeyEnum.GAME_NAME.getCode())
                    .lang(CurrReqUtils.getLanguage())
                    .build()).getData();
            if (CollectionUtil.isEmpty(gameNameList)) {
                resultVO.setCollectGamesTotal(0L);
                resultVO.setNewGameTotal(0L);
                return resultVO;
            }
            requestVO.setGameI18nCodeList(gameNameList);
            req.setGameI18nCodeList(gameNameList);
        }


        //传0就是查所有的游戏,把标签参数去掉
        if (GameInfoLabelEnum.NOT.equals(gameInfoLabelEnum)) {
            requestVO.setLabel(null);
            resultPage = getGameInfoSkin4(requestVO, finalGameCollectionMap);
        } else if (GameInfoLabelEnum.FAVORITE.equals(gameInfoLabelEnum)) {//收藏
            Page<LobbyGameInfoVO> lobbyGamePage = newQueryCollection(req);
            List<Skin4LobbyGameInfoVO> gameInfoResulList = BeanUtil.copyToList(lobbyGamePage.getRecords(), Skin4LobbyGameInfoVO.class);
            resultPage = new Page<>(lobbyGamePage.getCurrent(), lobbyGamePage.getSize(), lobbyGamePage.getTotal());
            resultPage.setRecords(gameInfoResulList);
        } else {
            //根据二级分类+标签查询
            resultPage = getGameInfoSkin4(requestVO, finalGameCollectionMap);
        }


        List<GameInfoVO> gameInfoVOList = gameInfoRepository.getGameInfoCountByTwoAndLabel(LobbyGameDetailRequestVO
                .builder()
                .gameTwoId(req.getGameTwoId())
                .gameI18nCodeList(gameNameList)
                .currencyCode(sysCurrency)
                .build(), siteCode);

        //所有游戏数量
        long allCount = gameInfoVOList.size();

        //热门标签游戏数量
        long hotCount = gameInfoVOList.stream()
                .filter(x -> x.getLabel().equals(GameInfoLabelEnum.HOT_RECOMMENDED.getCode()))
                .count();

        //最新游戏数量
        long newCount = gameInfoVOList.stream()
                .filter(x -> x.getLabel().equals(GameInfoLabelEnum.NEWS.getCode()))
                .count();


        resultVO.setAllGameTotal(allCount);
        resultVO.setHotGameTotal(hotCount);
        resultVO.setNewGameTotal(newCount);

        //如果登陆了获取出二级分类下的收藏游戏数量
        String userId = CurrReqUtils.getOneId();
        if (ObjectUtil.isNotEmpty(userId)) {
            //此处只是查询total,所以分页乱写
            IPage<GameInfoVO> iPage = gameInfoRepository.getCollectionGameInfoLobbyPage(new Page<>(1,2), userId,
                    sysCurrency, req.getGameTwoId(), siteCode, null);
            resultVO.setCollectGamesTotal(iPage.getTotal());
        }

        resultVO.setGamePage(resultPage);
        return resultVO;
    }


    public Page<LobbyGameInfoVO> getGameInfoHotByOneId(LobbyGameDetailRequestVO requestVO) {
        //只有查询所有的时候需要用到缓存
        IPage<GameInfoPO> page = PageConvertUtil.getMybatisPage(requestVO);
        if (requestVO.getPageSize() == -1) {
            page.setSize(5000);
        }
        String sysCurrency = getCurrency();

        String siteCode = CurrReqUtils.getSiteCode();

        requestVO.setCurrencyCode(sysCurrency);

        IPage<GameInfoVO> iPage = gameInfoRepository.getGameInfoLobbyPage(page, requestVO, siteCode);

        IPage<LobbyGameInfoVO> lobbyGamePage = getGameInfoToLobbyGameInfo(iPage, sysCurrency);

        List<LobbyGameInfoVO> lobbyGameList = lobbyGamePage.getRecords().stream().sorted(Comparator.comparing(LobbyGameInfoVO::getGameOneHotSort)).toList();

        lobbyGamePage.setRecords(lobbyGameList);

        return ConvertUtil.toConverPage(lobbyGamePage);
    }


    public Page<LobbyGameInfoVO> getGameInfoByTwoId(LobbyGameDetailRequestVO requestVO) {

        //只有查询所有的时候需要用到缓存
        boolean cateType = false;
        IPage<GameInfoPO> page = PageConvertUtil.getMybatisPage(requestVO);
        if (requestVO.getPageSize() == -1) {
            page.setSize(5000);
            cateType = true;
        }
        String sysCurrency = getCurrency();

        String siteCode = CurrReqUtils.getSiteCode();

        requestVO.setCurrencyCode(sysCurrency);

        String gameTwoId = requestVO.getGameTwoId();

        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_QUERY_LOBBY_BY_TWO_GAME, gameTwoId, sysCurrency));

        IPage<GameInfoVO> iPage = null;
        if (cateType) {
            iPage = RedisUtil.getValue(key);
        }

        if (iPage == null) {
            iPage = gameInfoRepository.getGameInfoLobbyPage(page, requestVO, siteCode);
            if (CollectionUtil.isNotEmpty(iPage.getRecords())) {
                RedisUtil.setValue(key, iPage, 5L, TimeUnit.MINUTES);
            }
        }

        IPage<LobbyGameInfoVO> lobbyGamePage = getGameInfoToLobbyGameInfo(iPage, sysCurrency);

        List<LobbyGameInfoVO> lobbyGameList = lobbyGamePage.getRecords().stream().sorted(Comparator.comparing(LobbyGameInfoVO::getSort)).toList();

        lobbyGamePage.setRecords(lobbyGameList);

        return ConvertUtil.toConverPage(lobbyGamePage);
    }

    private IPage<LobbyGameInfoVO> getGameInfoToLobbyGameInfo(IPage<GameInfoVO> iPage, String sysCurrency) {
        Map<String, GameCollectionPO> finalGameCollectionMap = getGameCollection();

        IPage<LobbyGameInfoVO> result = iPage.convert(x -> {
                    LobbyGameInfoVO vo = LobbyGameInfoVO.builder()
                            .build();
                    long sort = 100L;
                    if (ObjectUtil.isNotEmpty(x.getSort())) {
                        sort = x.getSort().longValue();
                    }

                    vo.setSort(sort);
                    BeanUtils.copyProperties(x, vo);
                    boolean isCollected = finalGameCollectionMap.containsKey(x.getId());
                    vo.setCollect(isCollected);

                    vo.setIcon(x.getIconI18nCode());
                    vo.setName(x.getGameI18nCode());
                    vo.setGameCode(x.getAccessParameters());

                    VenueEnum venueEnum = VenueEnum.of(vo.getVenueCode());
                    if (venueEnum.getType().equals(VenueTypeEnum.ACELT)) {
                        //彩票的游戏根据玩法查询出对应的彩种
                        vo.setGameCategoryCode(AceltGameInfoEnum.getGameTypeByGameCode(x.getAccessParameters()));
                    }
                    return vo;
                }
        );

        List<LobbyGameInfoVO> lobbyGameList = result.getRecords();

//        toSetAceLtList(lobbyGameList, sysCurrency);
        return result;
    }


    public Page<LobbyGameInfoVO> getGameInfoHomeHotSort(PageVO pageVO) {

        //查询所有的 可以直接查缓存.分页查询的不能走缓存
        boolean allType = false;
        IPage<GameInfoPO> page = PageConvertUtil.getMybatisPage(pageVO);
        if (pageVO.getPageSize() == -1) {
            page.setSize(5000);
            allType = true;
        }
        String sysCurrency = getCurrency();
        String siteCode = CurrReqUtils.getSiteCode();
        String key = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_QUERY_LOBBY_HOME_HOT_SORT, sysCurrency));
        IPage<GameInfoVO> iPage = null;

        //只有查询所有游戏的时候才走缓存
        if (allType) {
            iPage = RedisUtil.getValue(key);
        }

        if (iPage == null) {
            LobbyGameDetailRequestVO requestVO = LobbyGameDetailRequestVO.builder()
                    .currencyCode(sysCurrency)
                    .label(GameInfoLabelEnum.HOT_RECOMMENDED.getCode())
                    .build();

            iPage = gameInfoRepository.getGameInfoHomeHotSort(page, requestVO, siteCode);

//            List<GameInfoVO> gameList = iPage.getRecords();

            //获取首页热门排序字段
//            setGameInfoHomeHotSort(gameList, sysCurrency);
            RedisUtil.setValue(key, iPage, 5L, TimeUnit.MINUTES);
        }


        Map<String, GameCollectionPO> finalGameCollectionMap = getGameCollection();

        IPage<LobbyGameInfoVO> result = iPage.convert(x -> {
                    LobbyGameInfoVO vo = LobbyGameInfoVO.builder()
                            .build();

                    BeanUtils.copyProperties(x, vo);
                    boolean isCollected = finalGameCollectionMap.containsKey(x.getId());
                    vo.setCollect(isCollected);

                    vo.setIcon(x.getIconI18nCode());
                    vo.setName(x.getGameI18nCode());
                    vo.setGameCode(x.getAccessParameters());

                    VenueEnum venueEnum = VenueEnum.of(vo.getVenueCode());
                    if (venueEnum.getType().equals(VenueTypeEnum.ACELT)) {
                        //彩票的游戏根据玩法查询出对应的彩种
                        vo.setGameCategoryCode(AceltGameInfoEnum.getGameTypeByGameCode(x.getAccessParameters()));
                    }
                    return vo;
                }
        );

        List<LobbyGameInfoVO> lobbyGameList = result.getRecords();

//        toSetAceLtList(lobbyGameList, sysCurrency);

        lobbyGameList = lobbyGameList.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getHomeHotSort)).toList();

        result.setRecords(lobbyGameList);

        return ConvertUtil.toConverPage(result);
    }


    /**
     * 查询收藏
     */
    private Map<String, GameCollectionPO> getGameCollection() {
        if (ObjectUtil.isEmpty(CurrReqUtils.getOneId())) {
            return Maps.newHashMap();
        }

        LambdaQueryWrapper<GameCollectionPO> queryWrapper = Wrappers.lambdaQuery(GameCollectionPO.class)
                .eq(GameCollectionPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(GameCollectionPO::getUserId, CurrReqUtils.getOneId());

        List<GameCollectionPO> gameCollectionList = gameCollectionRepository.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(gameCollectionList)) {
            return Maps.newHashMap();
        }
        return gameCollectionList.stream().collect(Collectors.toMap(GameCollectionPO::getGameId, Function.identity()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean userGameCollection(LobbyGameCollectionRequestVO userGameCollectionMqVO) {
        int delCount = gameCollectionRepository.delete(Wrappers.lambdaQuery(GameCollectionPO.class)
                .eq(GameCollectionPO::getGameId, userGameCollectionMqVO.getGameId())
                .eq(GameCollectionPO::getUserId, CurrReqUtils.getOneId())
                .eq(GameCollectionPO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (!userGameCollectionMqVO.getType()) {
            return delCount > 0;
        }
        int count = gameCollectionRepository.insert(GameCollectionPO.builder()
                .userId(CurrReqUtils.getOneId())
                .gameId(userGameCollectionMqVO.getGameId())
                .siteCode(CurrReqUtils.getSiteCode())
                .build());
        return count > 0;
    }


    public Page<LobbyGameInfoVO> newQueryCollection(Sin4LobbyGameRequestVO pageVO) {
        String userId = CurrReqUtils.getOneId();
        if (ObjectUtil.isEmpty(userId)) {
            return PageConvertUtil.getMybatisPage(pageVO);
        }
        String siteCode = CurrReqUtils.getSiteCode();

        String sysCurrencyCode = getCurrency();

        LambdaQueryWrapper<GameCollectionPO> queryWrapper = Wrappers.lambdaQuery(GameCollectionPO.class)
                .eq(GameCollectionPO::getSiteCode, siteCode)
                .eq(GameCollectionPO::getUserId, userId);
        List<GameCollectionPO> gameCollectionList = gameCollectionRepository.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(gameCollectionList)) {
            return PageConvertUtil.getMybatisPage(pageVO);
        }

//        List<String> gameList = gameCollectionList.stream().map(GameCollectionPO::getGameId).toList();

        IPage<GameInfoPO> page = PageConvertUtil.getMybatisPage(pageVO);
        if (pageVO.getPageSize() == -1) {
            page.setSize(5000);
        }

        Map<String, Long> gameMap = gameCollectionList.stream().collect(Collectors.toMap(GameCollectionPO::getGameId, GameCollectionPO::getCreatedTime));

        IPage<GameInfoVO> iPage = gameInfoRepository.getCollectionGameInfoLobbyPage(page, userId, sysCurrencyCode, pageVO.getGameTwoId(), siteCode, pageVO.getGameI18nCodeList());

        IPage<LobbyGameInfoVO> lobbyGamePage = getGameInfoToLobbyGameInfo(iPage, sysCurrencyCode);

        lobbyGamePage.getRecords().forEach(x -> {
            Long createTime = gameMap.get(x.getId());
            x.setSort(createTime);
        });

        List<LobbyGameInfoVO> lobbyGameList = lobbyGamePage.getRecords().stream().sorted(Comparator.comparing(LobbyGameInfoVO::getSort).reversed()).toList();

        lobbyGamePage.setRecords(lobbyGameList);

        return ConvertUtil.toConverPage(lobbyGamePage);
    }


    /**
     * 获取用户币种
     */
    public String getCurrency() {
        return sportEventsService.getCurrency();
    }


    /**
     * 游戏首页数据组装
     *
     * @param resultList 返回的数据源
     * @param gameOneMap 一级分类下的游戏列表
     * @param item       一级分类
     */
    private void setLobbyTopGameGameInfoList(List<LobbyTopGameVO> resultList, Map<String, List<LobbyGameInfoVO>> gameOneMap, GameOneClassInfoVO item) {
        //一级分类下的游戏
        List<LobbyGameInfoVO> list = gameOneMap.get(item.getId());
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        list = list.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getGameOneHomeSort)).toList();
        resultList.add(LobbyTopGameVO.builder()
                .maxRebateAmount(item.getMaxRebateAmount())
                .gameOneId(item.getId())
                .name(item.getHomeI18nCode())
                .directoryI18nCode(item.getDirectoryI18nCode())
                .icon(item.getIcon())
                .icon2(item.getIcon2())
                .typeIconI18nCode(item.getTypeIconI18nCode())
                .homeSort(item.getHomeSort())
                .modelCode(item.getModel())
                .gameInfoList(list)
                .build());
    }


    /**
     * 游戏首页数据组装 沙巴体育
     *
     * @param resultList      返回的数据源
     * @param sysCurrencyCode 币种
     * @param item            一级分类
     */
    private void setLobbyTopSBAList(List<LobbyTopGameVO> resultList, List<LobbySportEventsVO> sportList, String sysCurrencyCode, GameOneClassInfoVO item) {

        VenueInfoVO siteVenueConfig = venueInfoService.getSiteVenueInfoByVenueCode(CurrReqUtils.getSiteCode(), VenueEnum.SBA.getVenueCode(), null);


        if (siteVenueConfig == null || siteVenueConfig.getStatus().equals(StatusEnum.CLOSE.getCode())) {
            return;
        }

        List<String> eventsIds = sportList.stream().map(LobbySportEventsVO::getEventsId).toList();
        if (CollectionUtil.isEmpty(eventsIds)) {
            return;
        }

        //单场馆游戏 没有币种或者币种不支持不显示侧边栏
        if (CollectionUtil.isEmpty(siteVenueConfig.getCurrencyCodeList()) || !siteVenueConfig.getCurrencyCodeList().contains(sysCurrencyCode)) {
            return;
        }
        resultList.add(LobbyTopGameVO.builder()
                .gameOneId(item.getId())
                .maxRebateAmount(item.getMaxRebateAmount())
                .gameTwoId(null)
                .eventsId(eventsIds)
                .name(item.getHomeI18nCode())
                .directoryI18nCode(item.getDirectoryI18nCode())
                .icon(item.getIcon())
                .icon2(item.getIcon2())
                .homeSort(item.getHomeSort())
                .modelCode(item.getModel())
                .gameInfoList(null)
                .build());
    }


    /**
     * @param resultList      返回数据源
     * @param siteCode        站点
     * @param sysCurrencyCode 币种
     * @param item            一级分类
     */
    private void setLobbyTopSignList(List<LobbyTopGameVO> resultList, String siteCode, String sysCurrencyCode, GameOneClassInfoVO item) {

        List<LobbySignVenueInfoVO> signVenueGameInfoList = Lists.newArrayList();


        List<GameOneClassVenueInfoVO> lobbySignVenueCode = item.getLobbySignVenueCode();
        lobbySignVenueCode = lobbySignVenueCode.stream().filter(f ->
                f.getCurrencyCode().equals(sysCurrencyCode)
        ).toList();

        if (CollectionUtil.isNotEmpty(lobbySignVenueCode)) {
            for (GameOneClassVenueInfoVO gameOneClassVenueInfoVO : lobbySignVenueCode) {
                VenueInfoVO siteSignVenueConfig = venueInfoService.getSiteVenueInfoByVenueCode(siteCode, gameOneClassVenueInfoVO.getVenueCode(), null);
                if (siteSignVenueConfig != null && !siteSignVenueConfig.getStatus().equals(StatusEnum.CLOSE.getCode())) {

                    String icon = siteSignVenueConfig.getH5IconI18nCode();
                    if (DeviceType.PC.getCode().equals(CurrReqUtils.getReqDeviceType())) {
                        icon = siteSignVenueConfig.getPcIconI18nCode();
                    }
                    LobbySignVenueInfoVO lobbySignVenueInfoVO = LobbySignVenueInfoVO.builder().build();
                    BeanUtils.copyProperties(siteSignVenueConfig, lobbySignVenueInfoVO);
                    lobbySignVenueInfoVO.setName(siteSignVenueConfig.getVenueNameI18nCode());
                    lobbySignVenueInfoVO.setSort(Long.valueOf(gameOneClassVenueInfoVO.getSort()));
                    lobbySignVenueInfoVO.setIcon(icon);
                    signVenueGameInfoList.add(lobbySignVenueInfoVO);

                }
            }

            resultList.add(LobbyTopGameVO.builder()
                    .maxRebateAmount(item.getMaxRebateAmount())
                    .gameOneId(item.getId())
                    .gameTwoId(null)
                    .name(item.getHomeI18nCode())
                    .icon(item.getIcon())
                    .icon2(item.getIcon2())
                    .homeSort(item.getHomeSort())
                    .modelCode(item.getModel())
                    .signGameInfoList(signVenueGameInfoList)
                    .build());
        }
    }


    public List<LobbyTopGameVO> newQueryLobbyTopGame() {

        List<LobbyTopGameVO> resultList = Lists.newArrayList();

        String sysCurrencyCode = getCurrency();


        String siteCode = CurrReqUtils.getSiteCode();

        List<GameOneClassInfoVO> gameOneInfoList = gameClassInfoService.getLobbyGameOneSortClassInfoList(sysCurrencyCode);
        if (CollectionUtil.isEmpty(gameOneInfoList)) {
            return Collections.emptyList();
        }

        //首页一级分类排序
        gameOneInfoList = gameOneInfoList.stream()
                .sorted(Comparator.comparingInt(GameOneClassInfoVO::getHomeSort))
                .toList();


        String lobbyTopGameKey = RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.KEY_QUERY_LOBBY_TOP_GAME, sysCurrencyCode));

        List<GameInfoVO> gameInfoList = RedisUtil.getValue(lobbyTopGameKey);

        if (CollectionUtil.isEmpty(gameInfoList)) {
            gameInfoList = gameInfoRepository.getLobbyTopGame(sysCurrencyCode, siteCode);
            if (CollectionUtil.isNotEmpty(gameInfoList)) {
                RedisUtil.setValue(lobbyTopGameKey, gameInfoList, 10L, TimeUnit.MINUTES);
            }
        }


        Map<String, GameCollectionPO> finalGameCollectionMap = getGameCollection();

        List<LobbyGameInfoVO> lobbyGameList = gameInfoList.stream().map(x -> {
            LobbyGameInfoVO vo = LobbyGameInfoVO.builder()
                    .build();

            BeanUtils.copyProperties(x, vo);

            vo.setGameOneId(x.getGameOneId());
            vo.setName(x.getGameI18nCode());
            vo.setIcon(x.getIconI18nCode());
            vo.setGameCode(x.getAccessParameters());

            boolean isCollected = finalGameCollectionMap.containsKey(x.getId());
            vo.setCollect(isCollected);

            VenueEnum venueEnum = VenueEnum.of(x.getVenueCode());
            if (venueEnum.getType().equals(VenueTypeEnum.ACELT)) {
                //彩票的游戏根据玩法查询出对应的彩种
                vo.setGameCategoryCode(AceltGameInfoEnum.getGameTypeByGameCode(x.getAccessParameters()));
            }
            return vo;
        }).toList();
        Map<String, List<LobbyGameInfoVO>> gameOneMap = lobbyGameList.stream().collect(Collectors.groupingBy(
                LobbyGameInfoVO::getGameOneId
        ));

        //组装彩票信息
//        toSetAceLtList(lobbyGameList, sysCurrencyCode);

        List<LobbySportEventsVO> sportList = sportEventsService.querySportEventsRecommend();

        for (GameOneClassInfoVO item : gameOneInfoList) {

/**
 //多游戏的一级分类没有游戏直接返回
 if (item.getModel().equals(GameOneModelEnum.CA.getCode())) {
 //一级分类下的游戏
 List<LobbyGameInfoVO> list = gameOneMap.get(item.getId());
 if (CollectionUtil.isEmpty(list)) {
 continue;
 }
 list = list.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getGameOneHomeSort)).toList();
 resultList.add(LobbyTopGameVO.builder()
 .gameOneId(item.getId())
 .name(item.getHomeI18nCode())
 .directoryI18nCode(item.getDirectoryI18nCode())
 .icon(item.getIcon())
 .typeIconI18nCode(item.getTypeIconI18nCode())
 .homeSort(item.getHomeSort())
 .modelCode(item.getModel())
 .gameInfoList(list)
 .build());
 }

 //彩票没有游戏直接返回
 if (item.getModel().equals(GameOneModelEnum.ACELT.getCode())) {
 //一级分类下的游戏
 List<LobbyGameInfoVO> list = gameOneMap.get(item.getId());
 if (CollectionUtil.isEmpty(list)) {
 continue;
 }
 list = list.stream().sorted(Comparator.comparing(LobbyGameInfoVO::getGameOneHomeSort)).toList();
 resultList.add(LobbyTopGameVO.builder()
 .gameOneId(item.getId())
 .name(item.getHomeI18nCode())
 .directoryI18nCode(item.getDirectoryI18nCode())
 .icon(item.getIcon())
 .typeIconI18nCode(item.getTypeIconI18nCode())
 .homeSort(item.getHomeSort())
 .modelCode(item.getModel())
 .gameInfoList(list)
 .build());
 }

 //沙巴体育
 if (item.getModel().equals(GameOneModelEnum.SBA.getCode())) {

 VenueInfoVO siteVenueConfig = venueInfoService.getSiteVenueInfoByVenueCode(CurrReqUtils.getSiteCode(), VenueEnum.SBA.getVenueCode(), null);


 if (siteVenueConfig == null || siteVenueConfig.getStatus().equals(StatusEnum.CLOSE.getCode())) {
 continue;
 }

 List<String> eventsIds = sportList.stream().map(LobbySportEventsVO::getEventsId).toList();
 if (CollectionUtil.isEmpty(eventsIds)) {
 continue;
 }

 //单场馆游戏 没有币种或者币种不支持不显示侧边栏
 if (CollectionUtil.isEmpty(siteVenueConfig.getCurrencyCodeList()) || !siteVenueConfig.getCurrencyCodeList().contains(sysCurrencyCode)) {
 continue;
 }
 resultList.add(LobbyTopGameVO.builder()
 .gameOneId(item.getId())
 .gameTwoId(null)
 .eventsId(eventsIds)
 .name(item.getHomeI18nCode())
 .directoryI18nCode(item.getDirectoryI18nCode())
 .icon(item.getIcon())
 .homeSort(item.getHomeSort())
 .modelCode(item.getModel())
 .gameInfoList(null)
 .build());
 }


 //单场馆游戏
 if (item.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())) {

 List<LobbySignVenueInfoVO> signVenueGameInfoList = Lists.newArrayList();


 List<GameOneClassVenueInfoVO> lobbySignVenueCode = item.getLobbySignVenueCode();
 lobbySignVenueCode = lobbySignVenueCode.stream().filter(f ->
 f.getCurrencyCode().equals(sysCurrencyCode)
 ).toList();

 if (CollectionUtil.isNotEmpty(lobbySignVenueCode)) {
 for (GameOneClassVenueInfoVO gameOneClassVenueInfoVO : lobbySignVenueCode) {
 VenueInfoVO siteSignVenueConfig = venueInfoService.getSiteVenueInfoByVenueCode(siteCode, gameOneClassVenueInfoVO.getVenueCode(), null);
 if (siteSignVenueConfig != null && !siteSignVenueConfig.getStatus().equals(StatusEnum.CLOSE.getCode())) {

 String icon = siteSignVenueConfig.getH5IconI18nCode();
 if (DeviceType.PC.getCode().equals(CurrReqUtils.getReqDeviceType())) {
 icon = siteSignVenueConfig.getPcIconI18nCode();
 }
 LobbySignVenueInfoVO lobbySignVenueInfoVO = LobbySignVenueInfoVO.builder().build();
 BeanUtils.copyProperties(siteSignVenueConfig, lobbySignVenueInfoVO);
 lobbySignVenueInfoVO.setName(siteSignVenueConfig.getVenueNameI18nCode());
 lobbySignVenueInfoVO.setSort(Long.valueOf(gameOneClassVenueInfoVO.getSort()));
 lobbySignVenueInfoVO.setIcon(icon);
 signVenueGameInfoList.add(lobbySignVenueInfoVO);

 }
 }

 resultList.add(LobbyTopGameVO.builder()
 .gameOneId(item.getId())
 .gameTwoId(null)
 .name(item.getHomeI18nCode())
 .icon(item.getIcon())
 .homeSort(item.getHomeSort())
 .modelCode(item.getModel())
 .signGameInfoList(signVenueGameInfoList)
 .build());


 }
 }
 **/

            switch (GameOneModelEnum.nameOfCode(item.getModel())) {
                case CA, ACELT -> setLobbyTopGameGameInfoList(resultList, gameOneMap, item);
                case SBA -> setLobbyTopSBAList(resultList, sportList, sysCurrencyCode, item);
                case SIGN_VENUE -> setLobbyTopSignList(resultList, siteCode, sysCurrencyCode, item);
            }
        }
        return resultList;
    }


    /**
     * @param venueCode    场馆
     * @param gameInfoList 游戏详情
     * @param userCurrency 用户币种
     * @param rateMap      站点币种汇率
     */
    private void toSetAceLtData(String venueCode, List<LobbyGameInfoVO> gameInfoList, String userCurrency, Map<String, BigDecimal> rateMap) {

        List<String> gameCategoryCodeList = gameInfoList.stream().map(LobbyGameInfoVO::getGameCategoryCode).filter(Objects::nonNull).toList();
        List<String> gameCodeList = gameInfoList.stream().map(LobbyGameInfoVO::getGameCode).filter(Objects::nonNull).toList();
        if (CollectionUtil.isEmpty(gameCategoryCodeList) || CollectionUtil.isEmpty(gameCodeList)) {
            return;
        }

        VenueInfoVO venueInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(venueCode, null);
        if (ObjectUtil.isNotEmpty(venueInfoVO)) {
            GameService gameService = gameServiceFactory.getGameService(venueCode);
            ResponseVO<List<JSONObject>> jsonObjectResponseVO = gameService.queryGameList(gameCategoryCodeList, gameCodeList, venueInfoVO);
            List<JSONObject> jsonList = jsonObjectResponseVO.getData();
            if (CollectionUtil.isEmpty(jsonList)) {
                return;
            }

            String siteCode = CurrReqUtils.getSiteCode();


            Random random = new Random();
            String maxWinCurrencySymbol = CurrencyEnum.symbolByCode(userCurrency);

            //用户是否登陆了 true = 登陆  false = 未登陆
            boolean userLogin = ObjectUtil.isNotEmpty(CurrReqUtils.getAccount());


            for (LobbyGameInfoVO x : gameInfoList) {
                String gameCode = x.getGameCode();
                String gameCategoryCode = x.getGameCategoryCode();

                if (StringUtils.isBlank(gameCode)) {
                    continue;
                }

                if (StringUtils.isBlank(gameCategoryCode)) {
                    continue;
                }

                BigDecimal winAmount;
                for (JSONObject item : jsonList) {
                    String backGameCode = item.get("gameCode") == null ? null : item.get("gameCode").toString();
                    String backGameCategoryCode = item.get("gameCategoryCode") == null ? null : item.get("gameCategoryCode").toString();

                    if (StringUtils.isBlank(backGameCode) || StringUtils.isBlank(backGameCategoryCode)) {
                        continue;
                    }

                    // 获取  3000-4000之间的随机数
                    BigDecimal usdWinAmount = BigDecimal.valueOf(3000 + random.nextInt(1001));
                    if (gameCode.equals(backGameCode) && gameCategoryCode.equals(backGameCategoryCode)) {
                        String key = String.format(RedisConstants.ACE_MAX_AMOUNT, venueCode, siteCode, gameCode, gameCategoryCode);
                        ACELTMaxAmount maxAmountObj = RedisUtil.getValue(key);

                        BigDecimal usdToUserCurrencyRate = rateMap.get(CurrencyEnum.USDT.getCode());
                        BigDecimal userCurrencyRate = rateMap.get(userCurrency);

                        if (maxAmountObj == null) {
                            if (usdToUserCurrencyRate == null) {
                                log.info("没有查到彩种到最大金额,并且默认USD的汇率没配置");
                                continue;
                            }

                            // 使用USD汇率转平台币
                            winAmount = AmountUtils.divide(usdWinAmount, usdToUserCurrencyRate);
                            // 存储最大金额到Redis
                            RedisUtil.setValue(key, ACELTMaxAmount.builder().maxAmount(winAmount).build());
                            winAmount = usdWinAmount; // 没有获取到时使用随机数代替
                        } else {
                            if (!userLogin) {
                                if (usdToUserCurrencyRate == null) {
                                    log.info("彩种最大金额用户汇率没配置USDT汇率:{}", userCurrency);
                                    continue;
                                }
                                winAmount = AmountUtils.multiply(maxAmountObj.getMaxAmount(), usdToUserCurrencyRate);
                            } else if (userCurrencyRate == null) {
                                log.info("彩种最大金额用户汇率没配置:{}", userCurrency);
                                continue;
                            } else {
                                winAmount = AmountUtils.multiply(maxAmountObj.getMaxAmount(), userCurrencyRate);
                            }
                        }

                        item.put("maxWin", winAmount);
                        item.put("maxWinCurrencySymbol", maxWinCurrencySymbol);
                        x.setData(item);
                    }
                }
            }
        }
    }

    /**
     * 拼接彩票的参数 暂时取消 (无彩票环境)
     */
    private void toSetAceLtList(List<LobbyGameInfoVO> result, String currencyCode) {
        if (CollectionUtil.isEmpty(result)) {
            return;
        }
        String siteCode = CurrReqUtils.getSiteCode();


        String key = RedisConstants.getSiteCodeKeyConstant(RedisConstants.ACELT_RATE);
        Map<String, BigDecimal> rateMap = RedisUtil.getValue(key);

        if (rateMap == null) {
            rateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
            RedisUtil.setValue(key, rateMap, 10L, TimeUnit.MINUTES);
        }

        if (CollectionUtil.isEmpty(rateMap)) {
            log.info("拼接彩票信息,最高奖异常,没有获取到站点的汇率:{}", siteCode);
            return;
        }

        if (ObjectUtil.isEmpty(currencyCode)) {
            currencyCode = CurrencyEnum.USDT.getCode();
        }

        List<LobbyGameInfoVO> aceltList = result.stream().filter(x -> VenueEnum.of(x.getVenueCode()).getType().equals(VenueTypeEnum.ACELT)).toList();
        if (CollectionUtil.isNotEmpty(aceltList)) {
            Map<String, List<LobbyGameInfoVO>> lobbyMap = result.stream().collect(Collectors.groupingBy(LobbyGameInfoVO::getVenueCode));
            for (Map.Entry<String, List<LobbyGameInfoVO>> item : lobbyMap.entrySet()) {
                toSetAceLtData(item.getKey(), item.getValue(), currencyCode, rateMap);
            }
        }
    }


    public List<LobbyPaymentVendorVO> queryPartnerList() {
        String key = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER);
        String jsonList = RedisUtil.getValue(key);
        if (!StringUtils.isBlank(jsonList)) {
            return JSON.parseArray(jsonList, LobbyPaymentVendorVO.class);
        }
        List<SitePartnerVO> partnerList = sitePartnerApi.getListBySiteCode(CurrReqUtils.getSiteCode()).getData();
        List<LobbyPaymentVendorVO> list = partnerList.stream().map(x -> {
            return LobbyPaymentVendorVO.builder().name(x.getPartnerName()).icon(x.getPartnerIcon()).id(x.getId()).build();
        }).toList();
        RedisUtil.setValue(key, JSON.toJSONString(list));
        return list;
    }


    public List<LobbyPaymentVendorVO> queryPaymentVendorList() {

        String key = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR);
        String jsonList = RedisUtil.getValue(key);
        if (!StringUtils.isBlank(jsonList)) {
            return JSON.parseArray(jsonList, LobbyPaymentVendorVO.class);
        }

        List<SitePaymentVendorVO> paymentVendorList = sitePaymentVendorApi.getListBySiteCode(CurrReqUtils.getSiteCode()).getData();

        List<LobbyPaymentVendorVO> list = paymentVendorList.stream().map(x -> {
            return LobbyPaymentVendorVO.builder().name(x.getVendorName()).icon(x.getVendorIconImage()).id(x.getId()).build();
        }).toList();

//        list = list.stream()
//                .limit(12)
//                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(list)) {
            RedisUtil.setValue(key, JSON.toJSONString(list));
        }

        return list;
    }

    public LobbyUnBannerResVO queryUnBannerList() {
        LobbyUnBannerResVO lobbyUnBannerResVO = RedisUtil.getValue(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_UN_BANNER));
        if (ObjectUtil.isNotEmpty(lobbyUnBannerResVO)) {
            return lobbyUnBannerResVO;
        }

        List<I18nMsgFrontVO> list = helpCenterManageApi.showUnLoginPic().getData().getI18nMessages();

        LobbyUnBannerResVO resVO = LobbyUnBannerResVO.builder().build();
        if (CollectionUtil.isNotEmpty(list)) {
            for (I18nMsgFrontVO item : list) {
                if (CommonConstant.UN_LOGIN_PC_URL.equals(item.getMessageKey())) {
                    resVO.setPcIcon(item.getMessage());
                }

                if (CommonConstant.UN_LOGIN_H5_URL.equals(item.getMessageKey())) {
                    resVO.setH5Icon(item.getMessage());
                }
            }

        }
        return resVO;
    }


    public List<LobbyBannerResVO> queryBannerList(LobbyBannerReqVO reqVO) {
        SiteBannerConfigAppQueryVO queryVO = new SiteBannerConfigAppQueryVO();
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        queryVO.setStatus(EnableStatusEnum.ENABLE.getCode());
        queryVO.setSiteTime(System.currentTimeMillis());

        ResponseVO<List<SiteBannerConfigPageRespVO>> listResponseVO = configApi.getListBySiteCode(queryVO);

        if (!listResponseVO.isOk() || CollectionUtil.isEmpty(listResponseVO.getData())) {
            return List.of();
        }

        String gameOneId = reqVO.getGameOneClassId();


        GameOneClassInfoVO gameOneClassInfoVO = null;
        //只要不传0 都查一级分类,因为0是游戏大厅
        if (Long.parseLong(gameOneId) > 0) {
            gameOneClassInfoVO = gameClassInfoService.getGameOneClassInfoById(gameOneId);
            if (ObjectUtil.isEmpty(gameOneClassInfoVO)) {
                return List.of();
            }
        }


        List<SiteBannerConfigPageRespVO> respVOS = listResponseVO.getData();
        Map<String, List<SiteBannerConfigPageRespVO>> map = respVOS.stream().collect(Collectors.groupingBy(
                SiteBannerConfigPageRespVO::getGameOneClassId
        ));

        List<SiteBannerConfigPageRespVO> bannerList = map.get(gameOneId);
        if (CollectionUtil.isEmpty(bannerList)) {
            return List.of();
        }


        List<String> gameIds = bannerList.stream().filter(x -> BannerLinkTargetEnum.GAME_ID.getCode().equals(x.getRedirectTarget()))
                .map(SiteBannerConfigPageRespVO::getRedirectTargetConfig).toList();
        Map<String, GameInfoPO> gameInfoMap = Maps.newHashMap();
        if (ObjectUtil.isNotEmpty(gameIds)) {
            List<GameInfoPO> gameInfoList = gameInfoService.getSiteGameInfoList(CurrReqUtils.getSiteCode());
            gameInfoList = gameInfoList.stream().filter(x -> x.getStatus().equals(StatusEnum.OPEN.getCode())).toList();
            if (CollectionUtil.isNotEmpty(gameInfoList)) {
                gameInfoMap = gameInfoList.stream().collect(Collectors.toMap(GameInfoPO::getGameId, GameInfoPO -> GameInfoPO));
            }
        }

        bannerList = Optional.ofNullable(bannerList)
                .orElse(Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(
                        SiteBannerConfigPageRespVO::getSort,
                        Comparator.nullsLast(Integer::compareTo) // null 往后排
                ))
                .toList();

        Map<String, List<ActivityBaseRespVO>> activituMap = Maps.newHashMap();
        List<String> activityNos = bannerList.stream().filter(x -> BannerLinkTargetEnum.ACTIVITY_ID.getCode().equals(x.getRedirectTarget()))
                .map(SiteBannerConfigPageRespVO::getRedirectTargetConfig).toList();
        if (ObjectUtil.isNotEmpty(activityNos)) {
            ActivityBaseVO baseVO = new ActivityBaseVO();
            baseVO.setShowStartTime(System.currentTimeMillis());
            baseVO.setShowEndTime(System.currentTimeMillis());
            baseVO.setStatus(EnableStatusEnum.ENABLE.getCode());
            baseVO.setActivityNoList(activityNos);
            baseVO.setSiteCode(CurrReqUtils.getSiteCode());
            baseVO.setShowTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
            ResponseVO<List<ActivityBaseRespVO>> activityResponse;

            if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
                //todo 此处调用表哥的国内盘活动接口
                activityResponse = activityBaseV2Api.queryActivityList(baseVO);
            }else {
                activityResponse = activityBaseApi.queryActivityList(baseVO);
            }


            if (activityResponse.isOk() && CollectionUtil.isNotEmpty(activityResponse.getData())) {
                activituMap = activityResponse.getData().stream().collect(Collectors.groupingBy(ActivityBaseRespVO::getActivityNo));
            }
        }


        Map<String, GameInfoPO> finalGameInfoMap = gameInfoMap;
        Map<String, List<ActivityBaseRespVO>> finalActivituMap = activituMap;

        List<LobbyBannerResVO> resultBannerList = Lists.newArrayList();
        String h5Domain = getH5Domain();
        for (SiteBannerConfigPageRespVO x : bannerList) {

            LobbyBannerResVO resVO = LobbyBannerResVO.builder()
                    .template(x.getRedirectTarget())
                    .icon(x.getBannerUrl())
                    .darkIcon(x.getDarkBannerUrl())
                    .darkH5Icon(x.getDarkH5BannerUrl())
                    .h5Icon(x.getH5BannerName())
                    .id(x.getId())
                    .isRedirect(x.getIsRedirect())
                    .switchTime(x.getSwitchTime())
                    .build();
            if (Long.parseLong(gameOneId) > 0 && gameOneClassInfoVO.getModel().equals(GameOneModelEnum.SBA.getCode())) {
                resVO.setVenueCode(GameOneModelEnum.SBA.getCode());
            }
            int redirect = Integer.parseInt(YesOrNoEnum.NO.getCode());

            if (BannerLinkTargetEnum.INTERNAL_LINK.getCode().equals(x.getRedirectTarget())) {
                resVO.setUrl(x.getRedirectTargetConfig());
                redirect = Integer.parseInt(YesOrNoEnum.YES.getCode());
            } else if (BannerLinkTargetEnum.GAME_ID.getCode().equals(x.getRedirectTarget())) {
                resVO.setGameCode(x.getRedirectTargetConfig());
                GameInfoPO gameInfoPO = finalGameInfoMap.get(x.getRedirectTargetConfig());
                if (ObjectUtil.isNotEmpty(gameInfoPO)) {
                    redirect = Integer.parseInt(YesOrNoEnum.YES.getCode());
                    resVO.setVenueCode(gameInfoPO.getVenueCode());
                    resVO.setGameCode(gameInfoPO.getAccessParameters());
                    resVO.setGameName(gameInfoPO.getGameI18nCode());
                    resVO.setGameId(gameInfoPO.getId());
                    VenueEnum venueEnum = VenueEnum.of(gameInfoPO.getVenueCode());
                    if (venueEnum.getType().equals(VenueTypeEnum.ACELT)) {
                        //彩票的游戏根据玩法查询出对应的彩种
                        resVO.setGameCategoryCode(AceltGameInfoEnum.getGameTypeByGameCode(gameInfoPO.getAccessParameters()));
                    }
                }
            } else if (BannerLinkTargetEnum.ACTIVITY_ID.getCode().equals(x.getRedirectTarget())) {

                List<ActivityBaseRespVO> activityBaseRespVO = finalActivituMap.get(x.getRedirectTargetConfig());
                if (CollectionUtil.isNotEmpty(activityBaseRespVO)) {
                    redirect = Integer.parseInt(YesOrNoEnum.YES.getCode());
                    resVO.setActivityTemplate(activityBaseRespVO.get(0).getActivityTemplate());
                    resVO.setActivityNameI18nCode(activityBaseRespVO.get(0).getActivityNameI18nCode());
                    resVO.setActivityId(activityBaseRespVO.get(0).getId());
                    resVO.setH5ActivityUrl(getH5DomainUrl(h5Domain,activityBaseRespVO.get(0).getActivityTemplate(),activityBaseRespVO.get(0).getId()));
                }
            }
            resVO.setIsRedirect(redirect);
            resultBannerList.add(resVO);
        }

        return resultBannerList;
    }
    public String getH5Domain(){
        DomainQueryVO domainQueryVO = new DomainQueryVO();
        domainQueryVO.setDomainType(DomainInfoTypeEnum.H5_PAGE.getType());
        domainQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        DomainVO domainByType = domainInfoApi.getDomainByType(domainQueryVO);
        if (domainByType != null && StrUtil.isNotEmpty(domainByType.getDomainAddr())) {
            String url;
            if (domainByType.getDomainAddr().contains("http")) {
                url = domainByType.getDomainAddr();
            } else {
                url = "https://" + domainByType.getDomainAddr();
            }
            return url;
        }
        return "";
    }
    public String getH5DomainUrl(String domain,  String activityTemplate, String activityId){
        return  domain + "/activity/list/" + activityTemplate + "/" + activityId;
    }


    public LobbyGameInfoVO queryNewGameInfoByGameId(LobbyGameInfoByCodeRequestVO requestVO) {
        String currencyCode = getCurrency();
        //传了场馆CODE查询的是单场馆游戏
        if (ObjectUtil.isNotEmpty(requestVO.getVenueCode()) && ObjectUtil.isEmpty(requestVO.getGameId())) {
            VenueInfoVO vo = venueInfoService.getSiteVenueInfoByVenueCode(CurrReqUtils.getSiteCode(), requestVO.getVenueCode(), null);
            if (ObjectUtil.isEmpty(vo)) {
                return null;
            }

            return LobbyGameInfoVO.builder().name(vo.getVenueName()).build();
        }

        if (ObjectUtil.isEmpty(requestVO.getGameId())) {
            return LobbyGameInfoVO.builder().build();
        }

        GameInfoVO gameInfoVO = gameInfoRepository.getLobbyTopGameById(requestVO.getGameId(), currencyCode, CurrReqUtils.getSiteCode());
        LobbyGameInfoVO vo = LobbyGameInfoVO.builder().build();

        if (ObjectUtil.isNotEmpty(gameInfoVO)) {
            Map<String, GameCollectionPO> finalGameCollectionMap = getGameCollection();
            BeanUtils.copyProperties(gameInfoVO, vo);
            vo.setGameOneId(gameInfoVO.getGameOneId());
            vo.setName(gameInfoVO.getGameI18nCode());
            vo.setIcon(gameInfoVO.getIconI18nCode());
            vo.setGameCode(gameInfoVO.getAccessParameters());
            boolean isCollected = finalGameCollectionMap.containsKey(gameInfoVO.getId());
            vo.setCollect(isCollected);

            VenueEnum venueEnum = VenueEnum.of(gameInfoVO.getVenueCode());
            if (venueEnum.getType().equals(VenueTypeEnum.ACELT)) {
                //彩票的游戏根据玩法查询出对应的彩种
                vo.setGameCategoryCode(AceltGameInfoEnum.getGameTypeByGameCode(gameInfoVO.getAccessParameters()));
            }
        }
        List<LobbyGameInfoVO> list = Lists.newArrayList(vo);
//        toSetAceLtList(list, currencyCode);
        return list.get(0);
    }


    public List<String> getLobbySiteEvents(SportLobbyEvents sportLobbyEvents) {
        return siteEventsService.getLobbySiteEvents(sportLobbyEvents);
    }


}

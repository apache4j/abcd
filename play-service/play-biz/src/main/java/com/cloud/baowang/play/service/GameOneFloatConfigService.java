package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.lobby.LobbyGameOneFloatVO;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.GameOneClassInfoRepository;
import com.cloud.baowang.play.repositories.GameOneFloatConfigRepository;
import com.cloud.baowang.play.repositories.GameTwoClassInfoRepository;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class GameOneFloatConfigService extends ServiceImpl<GameOneFloatConfigRepository, GameOneFloatConfigPO> {

    private final GameOneClassInfoRepository gameOneClassInfoRepository;

    private final I18nApi i18nApi;

    private final GameTwoClassInfoRepository gameTwoClassInfoRepository;

    private final SiteVenueConfigService siteVenueConfigService;

    private final GameOneVenueService gameOneVenueService;

    private final LanguageManagerApi languageManagerApi;

    private final SiteVenueService siteVenueService;

    private final VenueInfoService  venueInfoService;

    public GameOneFloatConfigService(GameOneClassInfoRepository gameOneClassInfoRepository,
                                     I18nApi i18nApi,
                                     GameTwoClassInfoRepository gameTwoClassInfoRepository,
                                     SiteVenueConfigService siteVenueConfigService,
                                     GameOneVenueService gameOneVenueService,
                                     LanguageManagerApi languageManagerApi,
                                     SiteVenueService siteVenueService,
                                     @Lazy VenueInfoService venueInfoService) {
        this.gameOneClassInfoRepository = gameOneClassInfoRepository;
        this.i18nApi = i18nApi;
        this.gameTwoClassInfoRepository = gameTwoClassInfoRepository;
        this.siteVenueConfigService = siteVenueConfigService;
        this.gameOneVenueService = gameOneVenueService;
        this.languageManagerApi = languageManagerApi;
        this.siteVenueService = siteVenueService;
        this.venueInfoService = venueInfoService;
    }


    /**
     * 新增二级分类的时候同步悬浮
     * 判断新增的二级分类对应的一级分类 是否已经添加了悬浮,如果已经新增了悬浮则需要同步到一级分类悬浮里面
     */
    public void initTwoFloatConfig(String gameTwoId, String gameOneId, String typeI18nCode) {
        if (baseMapper.selectCount(Wrappers.lambdaQuery(GameOneFloatConfigPO.class)
                .eq(GameOneFloatConfigPO::getModel, GameOneModelEnum.CA.getCode())
                .eq(GameOneFloatConfigPO::getGameOneId, gameOneId)) <= 0) {
            return;
        }
        GameOneFloatConfigPO configPO = GameOneFloatConfigPO.builder()
                .siteCode(CurrReqUtils.getSiteCode())
                .gameOneId(gameOneId)
                .gameTwoId(gameTwoId)
                .floatNameI18nCode(typeI18nCode)
                .model(GameOneModelEnum.CA.getCode())
                .status(StatusEnum.CLOSE.getCode())
                .build();
        baseMapper.insert(configPO);
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean addFloatConfig(GameOneFloatConfigAddReqVO requestVO) {

        String gameOneId = requestVO.getGameOneId();

        GameOneClassInfoPO gameOneClassInfoPO = gameOneClassInfoRepository.selectById(gameOneId);

        if (ObjectUtil.isEmpty(gameOneClassInfoPO)) {
            log.info("一级分类不存在:{}", gameOneId);
            return Boolean.FALSE;
        }

        GameOneModelEnum gameOneModelEnum = GameOneModelEnum.nameOfCode(gameOneClassInfoPO.getModel());

        if (!gameOneModelEnum.equals(GameOneModelEnum.CA) && !gameOneModelEnum.equals(GameOneModelEnum.SIGN_VENUE)) {
            log.info("只能配置多游戏跟单场馆的一级分类:{}", gameOneId);
            return Boolean.FALSE;
        }

        if (baseMapper.selectCount(Wrappers.lambdaQuery(GameOneFloatConfigPO.class).eq(GameOneFloatConfigPO::getGameOneId, gameOneId)) > 0) {
            throw new BaowangDefaultException(ResultCode.CANNOT_BE_CREATED_REPEATEDLY);
        }




        List<GameOneFloatConfigDetailAddReqVO> detailList = requestVO.getDetailList();

        List<GameOneFloatConfigPO> addList = Lists.newArrayList();

        String siteCode = CurrReqUtils.getSiteCode();

        Map<String, List<I18nMsgFrontVO>> i18Map = Maps.newHashMap();

        List<SiteVenueConfigPO> siteVenueList = siteVenueConfigService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(SiteVenueConfigPO.class)
                        .eq(SiteVenueConfigPO::getSiteCode, siteCode));
        Map<String, String> venueMap = siteVenueList.stream()
                .collect(Collectors.toMap(
                        SiteVenueConfigPO::getVenueCode,
                        v -> {
                            String name = v.getVenueName();
                            return (name == null || name.trim().isEmpty()) ? "" : name;
                        },
                        (first, second) -> first // key重复保留第一个
                ));



        for (GameOneFloatConfigDetailAddReqVO item : detailList) {


            String floatNameI18nCode = item.getFloatNameI18nCode();

            //如果一级分类归宿单场馆,则i18名称由前端传进来,因为传入的是场馆的名称CODE,多游戏的需生成I18CODE

            if (gameOneModelEnum.equals(GameOneModelEnum.SIGN_VENUE)) {
                if (ObjectUtil.isEmpty(item.getVenueCode())) {
                    log.info("单场馆类型,场馆CODE要传");
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                floatNameI18nCode = venueMap.get(item.getVenueCode());
            }


            //游戏一级分类-悬浮名称
//            if (gameOneModelEnum.equals(GameOneModelEnum.CA)) {
//                if (ObjectUtil.isEmpty(item.getGameTwoId())) {
//                    log.info("多游戏类型,二级分类要传");
//                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//                }
//                floatNameI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_FLOAT_NAME_I18N_CODE.getCode());
//                i18Map.put(floatNameI18nCode, item.getFloatNameI18nCodeList());
//            }

            //游戏一级分类-品牌图标
            String logoIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_LOGO_ICON_I18N_CODE.getCode());
            i18Map.put(logoIconI18nCode, item.getLogoIconI18nCodeList());

            //游戏一级分类-中图标
            String mediumIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_MEDIUM_ICON_I18N_CODE.getCode());
            i18Map.put(mediumIconI18nCode, item.getMediumIconI18nCodeList());


            GameOneFloatConfigPO configPO = GameOneFloatConfigPO
                    .builder()
                    .siteCode(siteCode)
                    .gameOneId(gameOneId)
                    .floatNameI18nCode(floatNameI18nCode)
                    .logoIconI18nCode(logoIconI18nCode)
                    .mediumIconI18nCode(mediumIconI18nCode)
                    .model(gameOneClassInfoPO.getModel())
                    .gameTwoId(item.getGameTwoId())
                    .venueCode(item.getVenueCode())
                    .status(StatusEnum.CLOSE.getCode())
                    .build();
            addList.add(configPO);
        }


        ResponseVO<Boolean> i18Res = i18nApi.update(i18Map);
        if (!i18Res.isOk() || !i18Res.getData()) {
            log.info("添加失败多语言同步异常");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        boolean resultBool = super.saveBatch(addList);
        CompletableFuture.runAsync(() -> LobbyCateUtil.deleteLobbySiteGameInfo(siteCode));
        return resultBool;
    }

    public List<GameOneFloatConfigByGameOneIdVO> getFloatConfigByGameOneId(String gameOneId) {
        String siteCode = CurrReqUtils.getSiteCode();

        List<GameOneFloatConfigByGameOneIdVO> resultList = Lists.newArrayList();

        GameOneClassInfoPO gameOneClassInfoPO = gameOneClassInfoRepository.selectById(gameOneId);
        if (ObjectUtil.isEmpty(gameOneClassInfoPO)) {
            return null;
        }
        // 获取总台全部语种
        List<I18nMsgFrontVO> frontVOS = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        if (responseVO.isOk() && null != responseVO.getData()) {
            List<LanguageManagerListVO> languageManagerListVOS = responseVO.getData();
            languageManagerListVOS.forEach(obj -> frontVOS.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }

        GameOneModelEnum gameOneModelEnum = GameOneModelEnum.nameOfCode(gameOneClassInfoPO.getModel());

        //如果一级分类的模板是多游戏的,则查二级分类
        if (gameOneModelEnum.equals(GameOneModelEnum.CA)) {
            List<GameTwoClassInfoPO> gameTwoClassInfo = gameTwoClassInfoRepository
                    .selectList(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                            .eq(GameTwoClassInfoPO::getGameOneId, gameOneId));


            for (GameTwoClassInfoPO item : gameTwoClassInfo) {
                GameOneFloatConfigByGameOneIdVO gameOneFloatConfigByGameOneIdVO =
                        GameOneFloatConfigByGameOneIdVO
                                .builder()
                                .gameTwoId(item.getId())
                                .floatNameI18nCode(item.getTypeI18nCode())
                                .logoIconI18nCodeList(frontVOS)
                                .mediumIconI18nCodeList(frontVOS)
                                .build();
                // 如果是场馆类型，则获取场馆类型的配置
               /* if (ObjectUtil.isNotEmpty(item.getv())) {
                    VenueInfoRequestVO venueInfoRequestVO = VenueInfoRequestVO.builder()
                            .siteCode(siteCode)
                            .venueCode(gameOneClassInfoPO.getVenueCode())
                            .venueJoinType(2)
                            .build();
                    Page<VenueInfoVO> siteVenueInfoPage = venueInfoService.getSiteVenueInfoPage(venueInfoRequestVO);
                    if (CollectionUtil.isNotEmpty(siteVenueInfoPage.getRecords())) {
                        // 取第一个场馆
                        VenueInfoVO venueInfoVO = siteVenueInfoPage.getRecords().get(0);
                        // 品牌图标-多语言-悬浮图标
                        gameOneFloatConfigByGameOneIdVO.setLogoIconI18nCodeList(venueInfoVO.getPcLogoCodeList());
                        // 中图标-多语言-悬浮图片 middleIconI18nCodeList
                        gameOneFloatConfigByGameOneIdVO.setMediumIconI18nCodeList(venueInfoVO.getMiddleIconI18nCodeList());
                    }
                }*/
                resultList.add(gameOneFloatConfigByGameOneIdVO);
            }
        }


        //根据一级分类查询出一级分类关联的场馆
        if (gameOneModelEnum.equals(GameOneModelEnum.SIGN_VENUE)) {
            List<GameOneVenuePO> gameOneVenueList = gameOneVenueService.getBaseMapper().selectList(Wrappers.lambdaQuery(GameOneVenuePO.class)
                    .eq(GameOneVenuePO::getGameOneId, gameOneId));
            if (CollectionUtil.isNotEmpty(gameOneVenueList)) {
                List<String> venueList = gameOneVenueList.stream().map(GameOneVenuePO::getVenueCode).toList();
                venueList = venueList.stream().distinct().toList();
                List<SiteVenueConfigPO> siteVenueList = siteVenueConfigService.getBaseMapper()
                        .selectList(Wrappers.lambdaQuery(SiteVenueConfigPO.class)
                                .eq(SiteVenueConfigPO::getSiteCode, siteCode));

                Map<String, String> venueMap = siteVenueList.stream()
                        .collect(Collectors.toMap(
                                SiteVenueConfigPO::getVenueCode,
                                v -> {
                                    String name = v.getVenueName();
                                    return (name == null || name.trim().isEmpty()) ? "" : name;
                                },
                                (first, second) -> first // key重复保留第一个
                        ));

                for (String venueCode : venueList) {
                    GameOneFloatConfigByGameOneIdVO gameOneFloatConfigByGameOneIdVO =
                            GameOneFloatConfigByGameOneIdVO
                                    .builder()
                                    .logoIconI18nCodeList(frontVOS)
                                    .mediumIconI18nCodeList(frontVOS)
                                    .venueCode(venueCode)
                                    .floatNameI18nCode(venueMap.get(venueCode))
                                    .build();
                    // 如果是场馆类型，则获取场馆类型的配置
                    if (ObjectUtil.isNotEmpty(venueCode)) {
                        VenueInfoRequestVO venueInfoRequestVO = VenueInfoRequestVO.builder()
                                .siteCode(siteCode)
                                .venueCode(venueCode)
                                .venueJoinType(2)
                                .build();
                        Page<VenueInfoVO> siteVenueInfoPage = venueInfoService.getSiteVenueInfoPage(venueInfoRequestVO);
                        if (CollectionUtil.isNotEmpty(siteVenueInfoPage.getRecords())) {
                            // 取第一个场馆
                            VenueInfoVO venueInfoVO = siteVenueInfoPage.getRecords().get(0);
                            // 品牌图标-多语言-悬浮图标
                            gameOneFloatConfigByGameOneIdVO.setLogoIconI18nCode(venueInfoVO.getPcLogoCode());
                            // 中图标-多语言-悬浮图片 middleIconI18nCodeList
                            gameOneFloatConfigByGameOneIdVO.setMediumIconI18nCode(venueInfoVO.getMiddleIconI18nCode());
                        }
                    }
                    resultList.add(gameOneFloatConfigByGameOneIdVO);
                }
            }
        }
        return resultList;
    }


    public Page<GameOneFloatConfigVO> getFloatConfigPage(GameOneFloatConfigAddReqVO requestVO) {

        String siteCode = CurrReqUtils.getSiteCode();

        IPage<GameInfoPO> page = PageConvertUtil.getMybatisPage(requestVO);

        Page<GameOneFloatConfigVO> resPage = baseMapper.getFloatConfigPage(page, requestVO.getGameOneId(), siteCode, requestVO.getStatus());


        List<SiteVenueConfigVO> siteVenueConfigList = siteVenueService.getSiteVenueListBySiteCode(siteCode);

        List<I18nMsgFrontVO> init = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        if (responseVO.isOk() && null != responseVO.getData()) {
            List<LanguageManagerListVO> languageManagerListVOS = responseVO.getData();
            languageManagerListVOS.forEach(obj -> init.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }

        resPage.getRecords().forEach(x -> {
            x.setFloatNameI18nCodeName(x.getFloatNameI18nCode());
            x.setLogoIconI18nCodeUrl(x.getLogoIconI18nCode());
            x.setLogoIconI18nCodeList(init);
            x.setMediumIconI18nCodeList(init);
            x.setMediumIconI18nCodeUrl(x.getMediumIconI18nCode());
            //场馆单悬浮状态是根据站点的场馆来的
            if(GameOneModelEnum.SIGN_VENUE.getCode().equals(x.getModel())){
                for (SiteVenueConfigVO venueConfig : siteVenueConfigList){
                    if(venueConfig.getVenueCode().equals(x.getVenueCode())){
                        x.setStatus(venueConfig.getStatus());
//                        x.setLogoIconI18nCode(venueConfig.getPcLogoCode());
//                        x.setLogoIconI18nCodeUrl(venueConfig.getPcLogoCode());
//                        x.setMediumIconI18nCode(venueConfig.getMiddleIconI18nCode());
//                        x.setMediumIconI18nCodeUrl(venueConfig.getMiddleIconI18nCode());
                    }
                }
            }
        });
        return resPage;
    }


    public Boolean upFloatConfigStatus(GameClassStatusRequestUpVO requestVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        GameOneFloatConfigPO configPO = GameOneFloatConfigPO.builder().status(requestVO.getStatus()).build();
        boolean upBool = baseMapper.update(configPO, Wrappers.lambdaQuery(GameOneFloatConfigPO.class)
                .eq(GameOneFloatConfigPO::getId, requestVO.getId())
                .eq(GameOneFloatConfigPO::getSiteCode, CurrReqUtils.getSiteCode())) > 0;
        CompletableFuture.runAsync(() -> LobbyCateUtil.deleteLobbySiteGameInfo(siteCode));
        return upBool;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean upFloatConfig(GameOneFloatConfigUpReqVO requestVO) {

        String siteCode = CurrReqUtils.getSiteCode();

        if (ObjectUtil.isEmpty(requestVO.getId())) {
            return Boolean.FALSE;
        }

        GameOneFloatConfigPO gameOneFloatConfigPO = baseMapper.selectById(requestVO.getId());
        if (gameOneFloatConfigPO == null) {
            log.info("一级悬浮不存在");
            return Boolean.FALSE;
        }


        String gameOneId = gameOneFloatConfigPO.getGameOneId();

        GameOneClassInfoPO gameOneClassInfoPO = gameOneClassInfoRepository.selectOne(Wrappers.lambdaQuery(GameOneClassInfoPO.class)
                .eq(GameOneClassInfoPO::getId, gameOneId));
        if (gameOneClassInfoPO == null) {
            log.info("一级分类不存在");
            return Boolean.FALSE;
        }


        GameOneModelEnum gameOneModelEnum = GameOneModelEnum.nameOfCode(gameOneClassInfoPO.getModel());

        Map<String, List<I18nMsgFrontVO>> i18Map = Maps.newHashMap();

        String floatNameI18nCode = requestVO.getFloatNameI18nCode();


        //如果是多游戏的名称则根据传入的修改,单场馆的名称是场馆的名称不让改
        if (gameOneModelEnum.equals(GameOneModelEnum.CA)) {
            i18Map.put(floatNameI18nCode, requestVO.getFloatNameI18nCodeList());
        }


        //游戏一级分类-品牌图标
        String logoIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_LOGO_ICON_I18N_CODE.getCode());
        i18Map.put(logoIconI18nCode, requestVO.getLogoIconI18nCodeList());

        //游戏一级分类-中图标
        String mediumIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_MEDIUM_ICON_I18N_CODE.getCode());
        i18Map.put(mediumIconI18nCode, requestVO.getMediumIconI18nCodeList());

        i18nApi.update(i18Map);

        GameOneFloatConfigPO configPO = GameOneFloatConfigPO
                .builder()
                .floatNameI18nCode(floatNameI18nCode)
                .logoIconI18nCode(logoIconI18nCode)
                .mediumIconI18nCode(mediumIconI18nCode)
                .build();
        configPO.setId(requestVO.getId());

        boolean upBool = baseMapper.updateById(configPO) > 0;

        CompletableFuture.runAsync(() -> LobbyCateUtil.deleteLobbySiteGameInfo(siteCode));

        return upBool;
    }


    /**
     * 根据站点跟币种查询出所有的一级分类悬浮列表,根据 key = 一级分类ID,v=悬浮列表平铺
     *
     * @param siteCode     站点
     * @param currencyCode 币种
     */
    public Map<String, List<LobbyGameOneFloatVO>> getFloatConfigMap(String siteCode, String currencyCode) {
        List<LobbyGameOneFloatVO> list = baseMapper.getLobbyGameOneFloat(siteCode, currencyCode);

        //查出站点的场馆状态
        List<SiteVenuePO> siteVenueList = siteVenueService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode, siteCode));
        Map<String, SiteVenuePO> siteVenueMap = siteVenueList.stream().collect(Collectors.toMap(SiteVenuePO::getVenueCode, Function.identity()));
        for (LobbyGameOneFloatVO item : list) {
            Integer venueSort = item.getVenueSort();
            Integer twoSort = item.getTwoSort();
            if (ObjectUtil.isNotEmpty(venueSort)) {
                item.setSort(venueSort);
            }
            if (ObjectUtil.isNotEmpty(twoSort)) {
                item.setSort(twoSort);
            }
            //单场馆的状态取场馆的状态
            if (GameOneModelEnum.SIGN_VENUE.getCode().equals(item.getModel())) {
                SiteVenuePO siteVenuePO = siteVenueMap.get(item.getVenueCode());
                if(siteVenuePO != null){
                    item.setStatus(siteVenuePO.getStatus());
                    item.setSiteLabelChangeType(siteVenuePO.getSiteLabelChangeType());
                }
            }
        }

        //如果是单场馆的话非禁用的,剩余的必须状态为开启
        list = list.stream().filter(x -> {
            if (GameOneModelEnum.SIGN_VENUE.getCode().equals(x.getModel())) {
                return !x.getStatus().equals(StatusEnum.CLOSE.getCode());
            }
            return x.getStatus().equals(StatusEnum.OPEN.getCode());
        }).toList();



        Map<String, List<LobbyGameOneFloatVO>> floatMap = list.stream().collect(Collectors.groupingBy(LobbyGameOneFloatVO::getGameOneId));

        //根据组装好的排序重新
        for (Map.Entry<String, List<LobbyGameOneFloatVO>> item : floatMap.entrySet()) {
            List<LobbyGameOneFloatVO> floatList = item.getValue();
            floatList = floatList.stream()
                    .sorted(Comparator.comparing(
                            LobbyGameOneFloatVO::getSort,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ))
                    .toList();
            item.setValue(floatList);
        }
        return floatMap;

    }

    public Boolean deFloatConfig(String id) {
        String siteCode = CurrReqUtils.getSiteCode();
        Boolean boolType = baseMapper.delete(Wrappers
                .lambdaQuery(GameOneFloatConfigPO.class)
                .eq(GameOneFloatConfigPO::getId, id)
//                .eq(GameOneFloatConfigPO::getStatus, StatusEnum.CLOSE.getCode())
        ) > 0;
        CompletableFuture.runAsync(() -> LobbyCateUtil.deleteLobbySiteGameInfo(siteCode));
        return boolType;
    }

}

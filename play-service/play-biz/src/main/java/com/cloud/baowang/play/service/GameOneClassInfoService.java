package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.enums.GameOneTypeEnum;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.*;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateClientShowVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: sheldon
 * @Date: 3/23/24 1:23 下午
 */
@RestController
@AllArgsConstructor
@Service
@Slf4j
public class GameOneClassInfoService extends ServiceImpl<GameOneClassInfoRepository, GameOneClassInfoPO> {

    private final GameOneClassInfoRepository gameOneClassInfoRepository;

    private final GameTwoClassInfoService gameTwoClassInfoService;

    private final SiteRebateApi siteRebateApi;

    private final I18nApi i18nApi;

    private final LanguageManagerApi languageManagerApi;


    private final GameOneCurrencySortRepository gameOneCurrencySortRepository;

    private final GameOneCurrencySortService gameOneCurrencySortService;

    private final SiteApi siteApi;

    private final GameOneVenueService gameOneVenueService;

    private final GameOneFloatConfigService floatConfigService;

    /**
     * 初始化站点一级分类
     *
     * @param siteCode 站点
     * @return 结果
     */
    public Boolean initGameOneClassInfo(String siteCode) {
        addGameOne(siteCode, VenueEnum.SBA);
        addGameOne(siteCode, VenueEnum.ACELT);
        return true;
    }

    private void addGameOne(String siteCode, VenueEnum venueEnum) {
        Long sbaGameOne = gameOneClassInfoRepository.selectCount(Wrappers.lambdaQuery(GameOneClassInfoPO.class)
                .eq(GameOneClassInfoPO::getSiteCode, siteCode)
                .eq(GameOneClassInfoPO::getModel, venueEnum.getVenueCode()));
        if (sbaGameOne <= 0) {
            String directoryI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_DIRECTORY.getCode());
            String homeI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_HOME.getCode());
            String gameOneIconI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_ICON.getCode());

            GameOneClassInfoPO gameOneClassInfoPO = GameOneClassInfoPO.builder()
                    .model(GameOneModelEnum.nameOfCode(venueEnum.getVenueCode()).getCode())
                    .status(StatusEnum.CLOSE.getCode())
                    .siteCode(siteCode)
                    .homeName(venueEnum.getVenueName())
                    .directoryName(venueEnum.getVenueName())
                    .directoryI18nCode(directoryI18Code)
                    .homeI18nCode(homeI18Code)
                    .typeIconI18nCode(gameOneIconI18Code)
                    .build();

            int addSbaCount = gameOneClassInfoRepository.insert(gameOneClassInfoPO);
            if (addSbaCount <= 0) {
                log.info("新建站点初始化一级分类沙巴体育失败:{}", siteCode);
            }
            List<I18nMsgFrontVO> directoryI18nCodeList = Lists.newArrayList();
            for (LanguageEnum languageEnum : LanguageEnum.values()) {
                directoryI18nCodeList.add(I18nMsgFrontVO.builder()
                        .language(languageEnum.getLang())
                        .message(venueEnum.getVenueName())
                        .messageKey(directoryI18Code)
                        .build());
            }


            List<I18nMsgFrontVO> homeI18nCodeList = Lists.newArrayList();
            for (LanguageEnum languageEnum : LanguageEnum.values()) {
                homeI18nCodeList.add(I18nMsgFrontVO.builder()
                        .language(languageEnum.getLang())
                        .message(venueEnum.getVenueName())
                        .messageKey(homeI18Code)
                        .build());
            }


            List<I18nMsgFrontVO> typeIconI18nCodeList = Lists.newArrayList();
            for (LanguageEnum languageEnum : LanguageEnum.values()) {
                typeIconI18nCodeList.add(I18nMsgFrontVO.builder()
                        .language(languageEnum.getLang())
                        .message(venueEnum.getVenueName())
                        .messageKey(gameOneIconI18Code)
                        .build());
            }


            Map<String, List<I18nMsgFrontVO>> req = Maps.newHashMap();
            req.put(directoryI18Code, directoryI18nCodeList);
            req.put(homeI18Code, homeI18nCodeList);
            req.put(gameOneIconI18Code, typeIconI18nCodeList);
            ResponseVO<Boolean> responseVO = i18nApi.insert(req);
            if (!responseVO.isOk() || !responseVO.getData()) {
                log.info("调用i18是失败:param:{},result:{}", req, responseVO);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }
    }

    public GameOneClassInfoVO getGameOneClassInfoById(String id) {

        List<GameOneClassInfoVO> list = getGameOneClassInfoList(StatusEnum.OPEN.getCode());

        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        list = list.stream().filter(x -> x.getStatus().equals(StatusEnum.OPEN.getCode())).toList();

        list = list.stream().filter(x -> x.getId().equals(id)).toList();

        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


//    public List<GameOneClassVenueInfoVO> getGameOneClassVenueInfo(String siteCode){
//        List<GameOneVenuePO> gameOneVenueList = gameOneVenueRepository.selectList(Wrappers.lambdaQuery(GameOneVenuePO.class)
//                .eq(GameOneVenuePO::getSiteCode, siteCode));
//
//
//    }


    public Page<GameOneClassInfoVO> getGameOneClassInfoPage(GameClassInfoRequestVO requestVO) {
        LambdaQueryWrapper<GameOneClassInfoPO> wrapper = new LambdaQueryWrapper<>();
        Page<GameOneClassInfoVO> resultPage = new Page<>();

        String siteCode = CurrReqUtils.getSiteCode();


        wrapper.eq(GameOneClassInfoPO::getSiteCode, siteCode)
                .eq(ObjectUtil.isNotEmpty(requestVO.getId()), GameOneClassInfoPO::getId, requestVO.getId())
                .eq(ObjectUtil.isNotEmpty(requestVO.getDirectoryName()), GameOneClassInfoPO::getDirectoryName, requestVO.getDirectoryName())
                .eq(ObjectUtil.isNotEmpty(requestVO.getHomeName()), GameOneClassInfoPO::getHomeName, requestVO.getHomeName())
                .eq(ObjectUtil.isNotEmpty(requestVO.getStatus()), GameOneClassInfoPO::getStatus, requestVO.getStatus())
//                .orderByAsc(GameOneClassInfoPO::getDirectorySort)
                .orderByDesc(GameOneClassInfoPO::getCreatedTime);
        Page<GameOneClassInfoPO> page = gameOneClassInfoRepository.selectPage(PageConvertUtil.getMybatisPage(requestVO), wrapper);
        List<GameOneClassInfoPO> list = page.getRecords();

        if (CollectionUtil.isEmpty(list)) {
            return resultPage;
        }


        Map<String, List<GameOneVenuePO>> gameOneVenueMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> ids = list.stream().filter(x -> x.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())).map(GameOneClassInfoPO::getId).toList();
            if (CollectionUtil.isNotEmpty(ids)) {
                List<GameOneVenuePO> gameOneVenueList = gameOneVenueService.getBaseMapper()
                        .selectList(Wrappers.lambdaQuery(GameOneVenuePO.class)
                                .in(GameOneVenuePO::getGameOneId, ids));
                gameOneVenueMap =  gameOneVenueList.stream()
                        .collect(Collectors.groupingBy(
                                GameOneVenuePO::getGameOneId)
                        );
            }
        }


        List<String> oneIds = list.stream().map(GameOneClassInfoPO::getId).collect(Collectors.toList());

        List<GameTwoClassInfoVO> gameTwoClassInfoList = gameTwoClassInfoService.getGameTwoClassInfoList(GameClassTwoRequestVO.builder().gameOneIds(oneIds).build());

        Map<String, List<GameTwoClassInfoVO>> gameTwoMap = gameTwoClassInfoList.stream().collect(Collectors.groupingBy(GameTwoClassInfoVO::getGameOneId));

        // 获取总台全部语种
        List<I18nMsgFrontVO> frontVOS = Lists.newArrayList();
        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerApi.list();
        if (responseVO.isOk() && null != responseVO.getData()) {
            List<LanguageManagerListVO> languageManagerListVOS = responseVO.getData();
            languageManagerListVOS.forEach(obj -> frontVOS.add(I18nMsgFrontVO.builder()
                    .language(obj.getCode()).languageName(obj.getName()).build()));
        }

       // Map<VenueTypeEnum, Map<String, BigDecimal>> rebateMap = Maps.newHashMap();
        Map<Integer, Map<String, BigDecimal>> rebateMap = Maps.newHashMap();
        SiteRebateClientShowVO siteRebateClientShowVO = SiteRebateClientShowVO.builder().siteCode(siteCode).build();
        ResponseVO<Map<Integer, Map<String, BigDecimal>>> rebateResultRes = siteRebateApi.rebateLabel(siteRebateClientShowVO);
        if(rebateResultRes.isOk()){
            if(rebateResultRes.getData() != null){
                rebateMap = rebateResultRes.getData();
            }
        }

        Map<String, List<GameOneVenuePO>> finalGameOneVenueMap = gameOneVenueMap;
        Map<Integer, Map<String, BigDecimal>> finalRebateMap = rebateMap;
        IPage<GameOneClassInfoVO> iPage = page.convert(s -> {
            GameOneClassInfoVO vo = new GameOneClassInfoVO();
            BeanUtils.copyProperties(s, vo);

            Integer rebateVenueType = s.getRebateVenueType();
            if(rebateVenueType != null){
               // VenueTypeEnum venueTypeEnum = VenueTypeEnum.of(rebateVenueType);
                Map<String, BigDecimal> currencyRebateMap = finalRebateMap.get(rebateVenueType);
                vo.setCurrencyRebateMap(currencyRebateMap);
            }


            if(s.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())){
                List<GameOneVenuePO> signVenueList = finalGameOneVenueMap.get(s.getId());
                if(CollectionUtil.isNotEmpty(signVenueList)){
                    List<GameOneClassVenueInfoVO> lobbySignVenueCode = signVenueList.stream().map(x->{
                        return GameOneClassVenueInfoVO.builder()
                                .venueCode(x.getVenueCode())
                                .currencyCode(x.getCurrencyCode())
                                .sort(x.getSort())
                                .build();
                    }).toList();
                    vo.setLobbySignVenueCode(lobbySignVenueCode);
                }else{
                    vo.setLobbySignVenueCode(Lists.newArrayList());
                }
            }



            List<GameTwoClassInfoVO> gameTwoList = gameTwoMap.get(s.getId());
            int twoCount = 0;
            if (CollectionUtil.isNotEmpty(gameTwoList)) {
                twoCount = gameTwoList.size();
            }
            vo.setTwoClassSize(twoCount);

            GameOneModelEnum gameOneModelEnum = GameOneModelEnum.nameOfCode(s.getModel());
            if (gameOneModelEnum != null) {
                vo.setGameOneType(gameOneModelEnum.getType());
            }
            vo.setHomeI18nCodeList(frontVOS);
            vo.setDirectoryI18nCodeList(frontVOS);
            if (!GameOneModelEnum.SBA.getCode().equals(s.getModel()) &&
                    !GameOneModelEnum.SIGN_VENUE.getCode().equals(s.getModel())) {
                vo.setTypeIconI18nCodeList(frontVOS);
            }
            return vo;
        });

        List<GameOneClassInfoVO> iPageList = iPage.getRecords();
        iPage.setRecords(sortGameOneClassInfoList(iPageList));
        BeanUtils.copyProperties(iPage, resultPage);
        return resultPage;
    }

    private List<GameOneClassInfoVO> sortGameOneClassInfoList(List<GameOneClassInfoVO> list) {
        list.sort(new Comparator<GameOneClassInfoVO>() {
            @Override
            public int compare(GameOneClassInfoVO o1, GameOneClassInfoVO o2) {
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


    public List<GameOneClassInfoVO> getAllGameOneClassInfoList() {
        GameClassInfoRequestVO requestVO = GameClassInfoRequestVO
                .builder()
                .build();
        requestVO.setPageSize(100);

        return getGameOneClassInfoPage(requestVO).getRecords();
    }


    /**
     * 游戏大厅,调用一级分类排序
     */
    public List<GameOneClassInfoVO> getLobbyGameOneSortClassInfoList(String currencyCode) {
        List<GameOneClassInfoVO> list = getGameOneClassInfoList(StatusEnum.OPEN.getCode());
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }

        return getToSetGameOneSort(list, currencyCode);
    }


    /**
     * 站点查询一级分类,将一级分类排序
     */
    public List<GameOneClassInfoVO> getSiteToSetGameOneSort(List<GameOneClassInfoVO> list, GameSortRequestVO gameSortRequestVO) {
        //先把所有的给默认排序100 往后排
        for (GameOneClassInfoVO item : list) {
            item.setDirectorySort(100);
            item.setHomeSort(100);
        }

        String currencyCode = gameSortRequestVO.getCurrencyCode();

        List<GameOneCurrencySortPO> gameOneCurrencySortList = gameOneCurrencySortRepository.selectList(Wrappers.lambdaQuery(GameOneCurrencySortPO.class)
                .eq(GameOneCurrencySortPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(GameOneCurrencySortPO::getCurrencyCode, currencyCode));

        for (GameOneClassInfoVO item : list) {
            for (GameOneCurrencySortPO sort : gameOneCurrencySortList) {
                if (sort.getGameOneId().equals(item.getId())) {
                    item.setDirectorySort(sort.getDirectorySort());
                    item.setHomeSort(sort.getHomeSort());
                    item.setCreatedTime(sort.getCreatedTime());
                }
            }
        }

        List<GameOneClassInfoVO> result = new ArrayList<>();


        if (ObjectUtil.isNotEmpty(gameSortRequestVO)) {
            //目录排序
            if (ObjectUtil.isNotEmpty(gameSortRequestVO.getDirectorySort()) && gameSortRequestVO.getDirectorySort()) {
                List<GameOneClassInfoVO> not100List = list.stream()
                        .filter(obj -> obj.getDirectorySort() != null && obj.getDirectorySort() != 100)
                        .sorted(Comparator.comparingInt(GameOneClassInfoVO::getDirectorySort))
                        .toList();

                List<GameOneClassInfoVO> sort100List = list.stream()
                        .filter(obj -> obj.getDirectorySort() != null && obj.getDirectorySort() == 100)
                        .sorted(Comparator.comparing(
                                GameOneClassInfoVO::getCreatedTime,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .toList();

// 合并两个集合：前面是非100的，后面是100的
                result.addAll(not100List);
                result.addAll(sort100List);
                return result;
            }
            //首页排序
            if (ObjectUtil.isNotEmpty(gameSortRequestVO.getHomeSort()) && gameSortRequestVO.getHomeSort()) {

                List<GameOneClassInfoVO> not100List = list.stream()
                        .filter(obj -> obj.getHomeSort() != 100)
                        .sorted(Comparator.comparingInt(GameOneClassInfoVO::getHomeSort))
                        .toList();


                List<GameOneClassInfoVO> sort100List = list.stream()
                        .filter(obj -> obj.getHomeSort() != null && obj.getHomeSort() == 100)
                        .sorted(Comparator.comparing(
                                GameOneClassInfoVO::getCreatedTime,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .toList();


// 合并两个集合：前面是非100的，后面是100的
                result.addAll(not100List);
                result.addAll(sort100List);
                return result;
            }


        }
        return list;
    }


    /**
     * 将一级分类排序
     */
    public List<GameOneClassInfoVO> getToSetGameOneSort(List<GameOneClassInfoVO> list, String currencyCode) {
        //先把所有的给默认排序100 往后排
        for (GameOneClassInfoVO item : list) {
            item.setDirectorySort(100);
            item.setHomeSort(100);
        }

        List<GameOneCurrencySortPO> gameOneCurrencySortList = Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(currencyCode)) {
            gameOneCurrencySortList = gameOneCurrencySortRepository.selectList(Wrappers.lambdaQuery(GameOneCurrencySortPO.class)
                    .eq(GameOneCurrencySortPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameOneCurrencySortPO::getCurrencyCode, currencyCode)
                    .in(GameOneCurrencySortPO::getGameOneId, list.stream().map(GameOneClassInfoVO::getId).toList()));
            if (CollectionUtil.isEmpty(gameOneCurrencySortList)) {
                return list;
            }

            List<String> gameOneList = gameOneCurrencySortList.stream().map(GameOneCurrencySortPO::getGameOneId).toList();
            list = list.stream().filter(x -> gameOneList.contains(x.getId())).toList();
        }

        for (GameOneClassInfoVO item : list) {
            for (GameOneCurrencySortPO sort : gameOneCurrencySortList) {
                if (sort.getGameOneId().equals(item.getId())) {
                    item.setDirectorySort(sort.getDirectorySort());
                    item.setHomeSort(sort.getHomeSort());
                }
            }
        }


        return list;
    }

    public List<GameOneClassInfoVO> getGameOneClassInfoList(Integer status) {
        //没有站点后台代表总台,总台不能走缓存直接查库
        if (ObjectUtil.isNotEmpty(CurrReqUtils.getSiteCode()) && !CommonConstant.ADMIN_CENTER_SITE_CODE.equals(CurrReqUtils.getSiteCode())) {

            List<GameOneClassInfoVO> list = RedisUtil.getValue(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_GAME_ONE));
            if (CollectionUtil.isNotEmpty(list)) {
                return list;
            }
        }

        GameClassInfoRequestVO requestVO = GameClassInfoRequestVO
                .builder()
                .build();
        requestVO.setPageSize(100);

        if (ObjectUtil.isNotEmpty(status)) {
            requestVO.setStatus(status);
        }

        List<GameOneClassInfoVO> list = getGameOneClassInfoPage(requestVO).getRecords();



        if (ObjectUtil.isNotEmpty(CurrReqUtils.getSiteCode()) && !CommonConstant.ADMIN_CENTER_SITE_CODE.equals(CurrReqUtils.getSiteCode())
                && CollectionUtil.isNotEmpty(list)) {
            RedisUtil.setValue(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_GAME_ONE), list, 10L, TimeUnit.MINUTES);
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean addGameOneClassInfo(GameClassInfoAddRequest requestVO) {
        requestVO.valid(requestVO.getDirectoryI18nCodeList());
        requestVO.valid(requestVO.getHomeI18nCodeList());

        List<I18nMsgFrontVO> zhCNDirectoryName = requestVO.getDirectoryI18nCodeList().stream()
                .filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();

        if (CollectionUtil.isNotEmpty(zhCNDirectoryName)) {
            requestVO.setDirectoryName(zhCNDirectoryName.get(0).getMessage());
        }

        if (ObjectUtil.isEmpty(requestVO.getGameOneType())) {
            log.info("一级分类必传类型");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (GameOneTypeEnum.nameOfCode(requestVO.getGameOneType()) == null) {
            log.info("一级分类必传类型参数异常");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (Objects.equals(GameOneTypeEnum.LOTTERY_ORIGINAL_SOUND.getCode(), requestVO.getGameOneType())
                || Objects.equals(GameOneTypeEnum.SBA_ORIGINAL_SOUND.getCode(), requestVO.getGameOneType())) {
            log.info("一级分类必传类型-原声数据只能系统初始化");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }


        GameOneModelEnum gameOneModelEnum = GameOneModelEnum.getByType(requestVO.getGameOneType());
        if (gameOneModelEnum == null) {
            log.info("一级分类必传类型-没查到对应的Model");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        List<I18nMsgFrontVO> zhCNHomeName = requestVO.getHomeI18nCodeList().stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();
        if (CollectionUtil.isNotEmpty(zhCNHomeName)) {
            String home = zhCNHomeName.get(0).getMessage();
            requestVO.setHomeName(home);
            if (gameOneClassInfoRepository.selectCount(Wrappers.lambdaQuery(GameOneClassInfoPO.class)
                    .eq(GameOneClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameOneClassInfoPO::getHomeName, home)) > 0) {
                throw new BaowangDefaultException(ResultCode.GAME_ONE_NAME_REPEAT);
            }
        }
        String siteCode = CurrReqUtils.getSiteCode();

        GameOneClassInfoPO po = GameOneClassInfoPO.builder().build();
        BeanUtils.copyProperties(requestVO, po);

        String directoryI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_DIRECTORY.getCode());
        String homeI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_HOME.getCode());
        String gameOneIconI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_ICON.getCode());
        po.setDirectoryI18nCode(directoryI18Code);
        po.setModel(gameOneModelEnum.getCode());
        po.setStatus(StatusEnum.CLOSE.getCode());
        po.setSiteCode(siteCode);
        po.setHomeI18nCode(homeI18Code);
        po.setTypeIconI18nCode(gameOneIconI18Code);
        po.setCreatedTime(System.currentTimeMillis());
        int count = gameOneClassInfoRepository.insert(po);

        if (count <= 0) {
            log.info("新增game_one_class,id失败");
            return Boolean.FALSE;
        }

        Map<String, List<I18nMsgFrontVO>> req = Maps.newHashMap();
        req.put(directoryI18Code, requestVO.getDirectoryI18nCodeList());
        req.put(homeI18Code, requestVO.getHomeI18nCodeList());
        req.put(gameOneIconI18Code, requestVO.getTypeIconI18nCodeList());
        ResponseVO<Boolean> responseVO = i18nApi.insert(req);
        if (!responseVO.isOk() || !responseVO.getData()) {
            log.info("调用i18是失败:param:{},result:{}", req, responseVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        //场馆一级分类
        if (GameOneTypeEnum.VENUE.getCode().equals(requestVO.getGameOneType())) {
            addGameOneVenue(po.getId(), requestVO.getGameOneClassVenue());
        }

        initGameOneCurrencySort(po.getId());
        return Boolean.TRUE;
    }


    /**
     * 新增一级分类的场馆关联关系
     */
    private void addGameOneVenue(String gameOneId, List<AddGameOneClassVenueVO> gameOneClassVenue) {
        gameOneVenueService.getBaseMapper().delete(Wrappers.lambdaQuery(GameOneVenuePO.class).eq(GameOneVenuePO::getGameOneId, gameOneId));

        if (CollectionUtil.isEmpty(gameOneClassVenue)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        boolean hasDuplicate = gameOneClassVenue.stream()
                .map(AddGameOneClassVenueVO::getCurrencyCode)
                .distinct()
                .count() < gameOneClassVenue.size();

        if (hasDuplicate) {
            log.info("一级分类,重复的币种:{}", gameOneClassVenue);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        String siteCode = CurrReqUtils.getSiteCode();

        List<GameOneVenuePO> gameOneVenueList = Lists.newArrayList();

        for (AddGameOneClassVenueVO item : gameOneClassVenue) {
            if (ObjectUtil.isEmpty(item.getCurrencyCode())) {
                log.info("币种缺少");
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }

            if (CollectionUtil.isEmpty(item.getVenueCodeList())) {
                log.info("场馆数据缺少");
                continue;
            }

            List<AddGameOneClassVenueSortVO> venueCodeList = item.getVenueCodeList();
            for (AddGameOneClassVenueSortVO sortVO : venueCodeList) {
                GameOneVenuePO venuePO = GameOneVenuePO
                        .builder()
                        .siteCode(siteCode)
                        .venueCode(sortVO.getVenueCode())
                        .sort(sortVO.getSort())
                        .gameOneId(gameOneId)
                        .currencyCode(item.getCurrencyCode())
                        .build();
                gameOneVenueList.add(venuePO);
            }
        }

        gameOneVenueService.saveBatch(gameOneVenueList);
    }


    /**
     * 新增的一级分类给默认排序
     */
    private void initGameOneCurrencySort(String gameOneId) {
        ResponseVO<SiteVO> site = siteApi.getSiteInfo(CurrReqUtils.getSiteCode());

        if (!site.isOk()) {
            return;
        }

        String currencyCodes = site.getData().getCurrencyCodes();
        if (ObjectUtil.isEmpty(currencyCodes)) {
            return;
        }

        List<String> currencyList = Arrays.stream(currencyCodes.split(","))
                .map(String::trim)
                .toList();

        if (CollectionUtil.isEmpty(currencyList)) {
            return;
        }

        GameClassInfoSetSortListVO gameClassInfoSetSort = new GameClassInfoSetSortListVO();
        List<GameClassInfoSetSortVO> list = Lists.newArrayList();


        List<Boolean> typeList = Lists.newArrayList();
        typeList.add(true);//目录排序
        typeList.add(false);//首页排序
        for (Boolean type : typeList) {
            for (String currencyCode : currencyList) {
                GameClassInfoSetSortVO directorySort = new GameClassInfoSetSortVO();
                directorySort.setType(type);
                directorySort.setCurrencyCode(currencyCode);
                GameClassInfoSetSortDetailVO gameOneList = new GameClassInfoSetSortDetailVO();
                gameOneList.setId(gameOneId);
                gameOneList.setSort(100);//新增加的一级分类给默认排序100 往后排
                directorySort.setVoList(List.of(gameOneList));
                list.add(directorySort);
            }
            gameClassInfoSetSort.setList(list);
            setSortGameOneClassInfo(gameClassInfoSetSort);
        }


    }

    public Boolean setSortGameOneClassInfo(GameClassInfoSetSortListVO infoRequest) {
        List<GameClassInfoSetSortVO> list = infoRequest.getList();

        List<GameOneCurrencySortPO> addList = Lists.newArrayList();
        for (GameClassInfoSetSortVO requestVO : list) {
            List<GameClassInfoSetSortDetailVO> voList = requestVO.getVoList();

            List<GameOneCurrencySortPO> gameOneCurrencySortList = gameOneCurrencySortRepository.selectList(Wrappers.lambdaQuery(GameOneCurrencySortPO.class)
                    .eq(GameOneCurrencySortPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameOneCurrencySortPO::getCurrencyCode, requestVO.getCurrencyCode()));


            Map<String, GameOneCurrencySortPO> gameOneCurrencySortMap = gameOneCurrencySortList.stream().collect(Collectors
                    .toMap(GameOneCurrencySortPO::getGameOneId, Function.identity()));


            for (GameClassInfoSetSortDetailVO item : voList) {
                GameOneCurrencySortPO po = GameOneCurrencySortPO.builder().build();
                po.setGameOneId(item.getId());
                if (requestVO.getType()) {
                    po.setDirectorySort(item.getSort());
                } else {
                    po.setHomeSort(item.getSort());
                }
                po.setUpdater(requestVO.getUpdater());
                po.setUpdatedTime(System.currentTimeMillis());
                po.setCurrencyCode(requestVO.getCurrencyCode());
                po.setSiteCode(CurrReqUtils.getSiteCode());
                if (gameOneCurrencySortMap.containsKey(item.getId())) {
                    gameOneCurrencySortRepository.update(po, Wrappers.lambdaQuery(GameOneCurrencySortPO.class)
                            .eq(GameOneCurrencySortPO::getSiteCode, CurrReqUtils.getSiteCode())
                            .eq(GameOneCurrencySortPO::getCurrencyCode, requestVO.getCurrencyCode())
                            .eq(GameOneCurrencySortPO::getGameOneId, item.getId()));
                } else {
                    addList.add(po);
                }
            }
        }


        if (CollectionUtil.isNotEmpty(addList)) {
            gameOneCurrencySortService.saveBatch(addList);
        }

        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean upGameOneClassInfoStatus(GameClassStatusRequestUpVO request) {
        GameOneClassInfoPO gameOneClassInfoPO = gameOneClassInfoRepository.selectOne(Wrappers
                .lambdaQuery(GameOneClassInfoPO.class)
                .eq(GameOneClassInfoPO::getId, request.getId())
                .eq(GameOneClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (ObjectUtil.isEmpty(gameOneClassInfoPO)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (ObjectUtil.isNotEmpty(request.getStatus())) {
            if (ObjectUtil.isEmpty(StatusEnum.nameByCode(request.getStatus()))) {
                log.info("请求参数异常:{}", request);
                return Boolean.FALSE;
            }
        }

        if (Objects.equals(request.getStatus(), StatusEnum.CLOSE.getCode())) {
            //禁用1级下的所有二级
            GameTwoClassInfoPO gameTwoClassInfoPO = GameTwoClassInfoPO.builder().build();
            gameTwoClassInfoPO.setStatus(StatusEnum.CLOSE.getCode());
            boolean twoUpBool = gameTwoClassInfoService.update(gameTwoClassInfoPO, Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                    .eq(GameTwoClassInfoPO::getGameOneId, request.getId())
                    .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode()));
            log.info("禁用该一级分类下的二级分类:{},result:{}", request.getId(), twoUpBool);
            //删除该一级分类下的关联所有游戏
        }

        boolean bool = gameOneClassInfoRepository.update(GameOneClassInfoPO.builder()
                .status(request.getStatus())
                .build(), Wrappers
                .lambdaQuery(GameOneClassInfoPO.class)
                .eq(GameOneClassInfoPO::getId, request.getId())
                .eq(GameOneClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())) > 0;
        log.info("修改一级分类:{},result:{}", request.getId(), bool);


        //修改为禁用的时候,
        if(request.getStatus().equals(StatusEnum.CLOSE.getCode())){
            GameOneFloatConfigPO configPO = GameOneFloatConfigPO.builder().status(request.getStatus()).build();
            floatConfigService.getBaseMapper().update(configPO,Wrappers.lambdaQuery(GameOneFloatConfigPO.class)
                    .eq(GameOneFloatConfigPO::getGameOneId,request.getId()));
        }

        if (bool) {
            LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        }
        return bool;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean upGameOneClassInfo(GameOneClassInfoUpVO requestVO) {

        if (ObjectUtil.isEmpty(requestVO.getId())) {
            return Boolean.FALSE;
        }
        List<I18nMsgFrontVO> directoryLanguageList = requestVO.getDirectoryI18nCodeList();
        requestVO.valid(directoryLanguageList);

        List<I18nMsgFrontVO> zhCNDirectoryName = directoryLanguageList.stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();

        if (CollectionUtil.isNotEmpty(zhCNDirectoryName)) {
            requestVO.setDirectoryName(zhCNDirectoryName.get(0).getMessage());
        }

        List<I18nMsgFrontVO> homeLanguageList = requestVO.getHomeI18nCodeList();
        requestVO.valid(homeLanguageList);

        List<I18nMsgFrontVO> zhCNHomeName = homeLanguageList.stream().filter(x -> x.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).toList();

        if (CollectionUtil.isNotEmpty(zhCNHomeName)) {
            requestVO.setHomeName(zhCNHomeName.get(0).getMessage());
        }


        GameOneClassInfoPO po = new GameOneClassInfoPO();
        BeanUtils.copyProperties(requestVO, po);


        GameOneClassInfoPO oneClassInfoPO = gameOneClassInfoRepository.selectById(requestVO.getId());
        if (ObjectUtil.isEmpty(oneClassInfoPO)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }


        if (CollectionUtil.isNotEmpty(zhCNHomeName)) {
            String home = zhCNHomeName.get(0).getMessage();
            if (gameOneClassInfoRepository.selectCount(Wrappers.lambdaQuery(GameOneClassInfoPO.class)
                    .eq(GameOneClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(GameOneClassInfoPO::getHomeName, home)
                    .ne(GameOneClassInfoPO::getId, requestVO.getId())) > 0) {
                throw new BaowangDefaultException(ResultCode.GAME_ONE_NAME_REPEAT);
            }
        }


        //一级分类的多语言图片,如果是单场馆游戏 或者 体育游戏 是没有多语言图片的.
        List<I18nMsgFrontVO> typeIconI18nCodeList = requestVO.getTypeIconI18nCodeList();
        if (CollectionUtil.isNotEmpty(typeIconI18nCodeList)) {
            if (!GameOneModelEnum.SBA.getCode().equals(oneClassInfoPO.getModel()) && !GameOneModelEnum.SIGN_VENUE.getCode().equals(oneClassInfoPO.getModel())) {
                requestVO.valid(typeIconI18nCodeList);
            }
        }


        GameOneTypeEnum gameOneTypeEnum = GameOneTypeEnum.nameOfCode(requestVO.getGameOneType());
        if (gameOneTypeEnum == null) {
            log.info("一级分类必传类型参数异常");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        //不是单场馆一级分类不需要配场馆字段
        if (gameOneTypeEnum != GameOneTypeEnum.VENUE) {
            po.setVenueCode(null);
            po.setVenueId(null);
        }


//        if (!oneClassInfoPO.getStatus().equals(StatusEnum.CLOSE.getCode())) {
//            throw new BaowangDefaultException(ResultCode.DELETE_OPEN_ERROR);
//        }


//        if (Objects.equals(GameOneTypeEnum.VENUE.getCode(), requestVO.getGameOneType())) {
//            if (ObjectUtil.isEmpty(requestVO.getVenueId())) {
//                log.info("修改,一级分类必传类型-单场馆,必须配置场馆");
//                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//            }
//
//            VenueInfoPO venueInfoPO = venueInfoRepository.selectById(requestVO.getVenueId());
//            if (ObjectUtil.isEmpty(venueInfoPO)) {
//                log.info("修改,一级分类必传类型-单场馆,场馆不存在");
//                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//            }
//            po.setVenueCode(venueInfoPO.getVenueCode());
//        }

        GameOneModelEnum gameOneModelEnum = GameOneModelEnum.getByType(requestVO.getGameOneType());
        if (gameOneModelEnum == null) {
            log.info("一级分类必传类型-没查到对应的Model");
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        //彩票跟体育的模板是不允许修改的
        if (oneClassInfoPO.getModel().equals(GameOneModelEnum.SBA.getCode())
                || oneClassInfoPO.getModel().equals(GameOneModelEnum.ACELT.getCode())) {
            if (!oneClassInfoPO.getModel().equals(gameOneModelEnum.getCode())) {
                log.info("一级分类原声游戏不允许修改模板");
                throw new BaowangDefaultException(ResultCode.GAME_ONE_MODEL_ERROR);
            }
        }
        po.setModel(gameOneModelEnum.getCode());

        String directoryI18nCode = oneClassInfoPO.getDirectoryI18nCode();
        Map<String, List<I18nMsgFrontVO>> directorReqMap = Maps.newHashMap();
        ResponseVO<Boolean> responseDirectoryVO = null;
        if (StringUtils.isBlank(directoryI18nCode)) {
            directoryI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_DIRECTORY.getCode());
            directorReqMap.put(directoryI18nCode, directoryLanguageList);
            responseDirectoryVO = i18nApi.insert(directorReqMap);
        } else {
            directorReqMap.put(directoryI18nCode, directoryLanguageList);
            responseDirectoryVO = i18nApi.update(directorReqMap);
        }

        if (!responseDirectoryVO.isOk() || !responseDirectoryVO.getData()) {
            log.info("responseDirectoryVO 调用i18是失败:param:{},result:{}", directorReqMap, responseDirectoryVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        String homeI18nCode = oneClassInfoPO.getHomeI18nCode();
        Map<String, List<I18nMsgFrontVO>> homeReqMap = Maps.newHashMap();
        ResponseVO<Boolean> responseHomeI18nCode = null;
        if (StringUtils.isBlank(homeI18nCode)) {
            homeI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_HOME.getCode());
            homeReqMap.put(homeI18nCode, homeLanguageList);
            responseHomeI18nCode = i18nApi.insert(homeReqMap);
        } else {
            homeReqMap.put(homeI18nCode, homeLanguageList);
            responseHomeI18nCode = i18nApi.update(homeReqMap);
        }

        if (CollectionUtil.isNotEmpty(typeIconI18nCodeList)) {
            String typeIconI18nCode = oneClassInfoPO.getTypeIconI18nCode();
            Map<String, List<I18nMsgFrontVO>> typeIconI18nMap = Maps.newHashMap();
            ResponseVO<Boolean> responseTypeIconI18n = null;
            if (StringUtils.isBlank(typeIconI18nCode)) {
                typeIconI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.GAME_ONE_ICON.getCode());
                typeIconI18nMap.put(typeIconI18nCode, typeIconI18nCodeList);
                responseTypeIconI18n = i18nApi.insert(typeIconI18nMap);
            } else {
                typeIconI18nMap.put(typeIconI18nCode, typeIconI18nCodeList);
                responseTypeIconI18n = i18nApi.update(typeIconI18nMap);
            }

            if (!responseHomeI18nCode.isOk() || !responseHomeI18nCode.getData() || !responseTypeIconI18n.getData()) {
                log.info("responseHomeI18nCode 调用i18是失败:param:{},result:{}", homeReqMap, responseHomeI18nCode);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            po.setTypeIconI18nCode(typeIconI18nCode);
        }
        po.setHomeI18nCode(homeI18nCode);
        po.setDirectoryI18nCode(directoryI18nCode);

//        int count = gameOneClassInfoRepository.update(po, Wrappers
//                .lambdaQuery(GameOneClassInfoPO.class)
//                .eq(GameOneClassInfoPO::getId, requestVO.getId())
//                .eq(GameOneClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
//        );


        LambdaUpdateWrapper<GameOneClassInfoPO> wrapper = Wrappers.<GameOneClassInfoPO>lambdaUpdate()
                .eq(GameOneClassInfoPO::getId, requestVO.getId())
                .eq(GameOneClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())
                // 字符串字段：非空才更新
                .set(ObjectUtil.isNotEmpty(po.getDirectoryName()), GameOneClassInfoPO::getDirectoryName, po.getDirectoryName())
                .set(ObjectUtil.isNotEmpty(po.getDirectoryI18nCode()), GameOneClassInfoPO::getDirectoryI18nCode, po.getDirectoryI18nCode())
                .set(ObjectUtil.isNotEmpty(po.getHomeName()), GameOneClassInfoPO::getHomeName, po.getHomeName())
                .set(ObjectUtil.isNotEmpty(po.getHomeI18nCode()), GameOneClassInfoPO::getHomeI18nCode, po.getHomeI18nCode())
                .set(ObjectUtil.isNotEmpty(po.getIcon()), GameOneClassInfoPO::getIcon, po.getIcon())
                .set(ObjectUtil.isNotEmpty(po.getIcon2()), GameOneClassInfoPO::getIcon2, po.getIcon2())
                .set(ObjectUtil.isNotEmpty(po.getTypeIconI18nCode()), GameOneClassInfoPO::getTypeIconI18nCode, po.getTypeIconI18nCode())
                .set(ObjectUtil.isNotEmpty(po.getModel()), GameOneClassInfoPO::getModel, po.getModel())
                .set(ObjectUtil.isNotEmpty(po.getVenueId()), GameOneClassInfoPO::getVenueId, po.getVenueId())
                .set(ObjectUtil.isNotEmpty(po.getVenueCode()), GameOneClassInfoPO::getVenueCode, po.getVenueCode())
                // 数值字段：非 null 才更新
                .set(po.getStatus() != null, GameOneClassInfoPO::getStatus, po.getStatus())
                .set(po.getRebateVenueType() != null, GameOneClassInfoPO::getRebateVenueType, po.getRebateVenueType())
                // 奖金池相关：无论是否为 null 都更新（覆盖）
                .set(GameOneClassInfoPO::getPrizePoolTotal, po.getPrizePoolTotal())
                .set(GameOneClassInfoPO::getPrizePoolStart, po.getPrizePoolStart())
                .set(GameOneClassInfoPO::getPrizePoolEnd, po.getPrizePoolEnd());

       int count = gameOneClassInfoRepository.update(null,wrapper);
        log.info("upGameOneClassInfo:id:{},count:{}", requestVO.getId(), count);




        //场馆一级分类
        if (GameOneTypeEnum.VENUE.getCode().equals(requestVO.getGameOneType())) {
            addGameOneVenue(po.getId(), requestVO.getGameOneClassVenue());

            //单场馆的一级分类修改删除一级分类悬浮.需要让他们手动重新新增
            floatConfigService.getBaseMapper().delete(Wrappers.lambdaQuery(GameOneFloatConfigPO.class)
                    .eq(GameOneFloatConfigPO::getGameOneId,requestVO.getId()));
        }


        if (count > 0) {
            LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());

        }

        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Boolean delGameOneClassInfoById(String id) {

        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<GameOneClassInfoPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GameOneClassInfoPO::getId, id)
                .eq(GameOneClassInfoPO::getSiteCode, siteCode);

        GameOneClassInfoPO oneClassInfoPO = gameOneClassInfoRepository.selectOne(wrapper);
        if (ObjectUtil.isEmpty(oneClassInfoPO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        if (!oneClassInfoPO.getStatus().equals(StatusEnum.CLOSE.getCode())) {
            throw new BaowangDefaultException(ResultCode.DELETE_OPEN_ERROR);
        }

        if (oneClassInfoPO.getModel().equals(GameOneModelEnum.SBA.getCode())
                || oneClassInfoPO.getModel().equals(GameOneModelEnum.ACELT.getCode())) {
            log.info("一级分类原声游戏不允许删除模板");
            throw new BaowangDefaultException(ResultCode.GAME_ONE_MODEL_DELETE_ERROR);
        }

        if (gameTwoClassInfoService.getBaseMapper().selectCount(Wrappers.lambdaQuery(GameTwoClassInfoPO.class)
                .eq(GameTwoClassInfoPO::getGameOneId, id)
                .eq(GameTwoClassInfoPO::getSiteCode, CurrReqUtils.getSiteCode())) > 0) {
            throw new BaowangDefaultException(ResultCode.DELETE_GAME_ERROR_TWO);
        }

        int count = gameOneClassInfoRepository.delete(wrapper);
        if (count <= 0) {
            return Boolean.FALSE;
        }

        gameOneCurrencySortRepository.delete(Wrappers.lambdaQuery(GameOneCurrencySortPO.class)
                .eq(GameOneCurrencySortPO::getGameOneId, id)
                .eq(GameOneCurrencySortPO::getSiteCode, CurrReqUtils.getSiteCode()));

        String directoryI18nCode = oneClassInfoPO.getDirectoryI18nCode();
        String homeI18nCode = oneClassInfoPO.getHomeI18nCode();
        List<String> delLanguageList = Lists.newArrayList(directoryI18nCode, homeI18nCode);
        ResponseVO<Boolean> delI18Result = i18nApi.deleteBatchByMsgKey(delLanguageList);
        if (!delI18Result.isOk() || !delI18Result.getData()) {
            log.info("删除一级分类i18失败");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        LobbyCateUtil.deleteLobbySiteGameInfo(CurrReqUtils.getSiteCode());
        return Boolean.TRUE;
    }

}

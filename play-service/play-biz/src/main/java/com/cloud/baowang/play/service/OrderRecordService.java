package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.common.core.constants.BigDecimalConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.order.report.*;
import com.cloud.baowang.play.game.db.acelt.DBAceLtOrderParseUtil;
import com.cloud.baowang.play.game.db.sh.DBSHOrderInfoUtil;
import com.cloud.baowang.play.util.DbDJOrderUtil;
import com.cloud.baowang.play.util.DbPanDaSportOrderUtil;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.enums.UserLabelEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.VIPAwardEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.agent.api.vo.agent.UserVenueTopVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoVenueStatisticalReqVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelInfoVenueStatisticalVO;
import com.cloud.baowang.play.api.vo.agent.GetNewest5OrderRecordParam;
import com.cloud.baowang.play.api.vo.agent.GetNewest5OrderRecordVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.es.util.PageConvertUtil;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.vo.agent.*;
import com.cloud.baowang.play.api.vo.order.*;
import com.cloud.baowang.play.api.vo.order.client.*;
import com.cloud.baowang.play.api.vo.user.PlayUserBetAmountSumVO;
import com.cloud.baowang.play.api.vo.user.PlayUserWinLossParamVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.acelt.enums.AceLtPlayTypeEnum;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.dg2.DG2OrderInfoUtil;
import com.cloud.baowang.play.game.evo.enums.EvoGameTypeI18n;
import com.cloud.baowang.play.game.evo.impl.EvoLanguageConversionUtils;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.game.im.impl.utils.MarblesBetTypeEnum;
import com.cloud.baowang.play.game.sexy.vo.SexyOrderInfoUtil;
import com.cloud.baowang.play.game.sh.impl.SAOrderInfoUtil;
import com.cloud.baowang.play.game.sh.impl.SHLanguageConversionUtils;
import com.cloud.baowang.play.mapper.OrderRecordEsMapper;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.po.SiteVenueConfigPO;
import com.cloud.baowang.play.po.SiteVenuePO;
import com.cloud.baowang.play.repositories.OrderRecordRepository;
import com.cloud.baowang.play.vo.AgentUserVenueListParam;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.vip.*;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserRebateApi;
import com.cloud.baowang.wallet.api.vo.rebate.OrderRebateRequestVO;
import com.cloud.baowang.wallet.api.vo.rebate.UserRebateVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.biz.OrderByParam;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.CommonConstant.ORDER_BY_ASC;
import static com.cloud.baowang.common.core.constants.CommonConstant.ORDER_BY_DESC;

@Slf4j
@Service
@AllArgsConstructor
public class OrderRecordService extends ServiceImpl<OrderRecordRepository, OrderRecordPO> {

    private final OrderRecordRepository orderRecordRepository;
    private final UserRebateApi userRebateApi;
    private final SiteApi siteApi;
    private final UserInfoApi userInfoApi;
    private final OrderRecordEsMapper orderRecordEsMapper;
    private final VipGradeApi vipGradeApi;
    private final VipRankApi vipRankApi;
    private final GameInfoService gameInfoService;
    private final AdminGameInfoService adminGameInfoService;
    private final GameServiceFactory gameServiceFactory;
    private final VenueInfoService venueInfoService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final CasinoMemberService casinoMemberService;
    private final I18nApi i18nApi;
    private final SystemDictConfigApi systemDictConfigApi;
    private final SiteVenueService siteVenueService;

    private static void ftgGameNameBind(OrderRecordPO record) {
        String gameName = record.getGameName();
        if (StrUtil.isNotBlank(gameName)) {
            Optional.ofNullable(I18nMessageUtil.getI18NMessage(gameName)).ifPresent(record::setGameName);
        }

        Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.FTG_ORDER_STATUS, record.getPlayType())).ifPresent(s -> {
            record.setPlayType(s);
            record.setResultList(s);
            record.setOrderInfo(s);
        });

        Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.FTG_GAME_TYPE, record.getBetContent())).ifPresent(s -> {
            record.setBetContent(s);
            record.setPlayInfo(s);
            record.setRoomType(s);
        });

    }

    private static void ltGameNameBind(OrderRecordPO record) {
        String gameName = record.getGameName();
        AtomicBoolean gameNameSetFlag = new AtomicBoolean(false);
        if (StrUtil.isNotBlank(gameName)) {
            Optional.ofNullable(I18nMessageUtil.getI18NMessage(gameName)).ifPresent(s -> {
                record.setGameName(s);
                gameNameSetFlag.set(true);
            });
        }
        if (!gameNameSetFlag.get()) {
            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.LT_GAME_TYPE, record.getThirdGameCode())).ifPresent(record::setGameName);
        }
        if (record.getVenueCode().equals(VenueEnum.DBACELT.getVenueCode())){
            record.setOrderInfo(DBAceLtOrderParseUtil.getOrderRecordInfo(record));
            return;
        }
        Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.ACELT_BET_TYPE, record.getBetContent())).ifPresent(s -> {
            record.setBetContent(s);
            record.setOrderInfo(s);
        });
        String playType = AceLtPlayTypeEnum.getNameByCode(record.getPlayType());
        record.setPlayType(playType);
        record.setPlayInfo(playType);
        Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.ACELT_PLAY_TYPE, record.getPlayType())).ifPresent(record::setPlayInfo);
    }

    private static void shGameNameBind(OrderRecordPO record) {
        String gameName = record.getGameName();
        VenueEnum venueEnum = VenueEnum.nameOfCode(record.getVenueCode());
        switch (venueEnum) {
            case EVO:
                Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.EVO_GAME_TYPE, record.getPlayType())).ifPresent(s -> {
                    record.setGameName(s);
                    record.setPlayInfo(s);
                });
                break;
            case SH, SH_ZHCN:
                Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_GAME_TYPE, gameName)).ifPresent(s -> {
                    record.setGameName(s);
                    record.setPlayInfo(s);
                });
                break;
            case SA:
                Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SA_GAME_TYPE, record.getPlayType())).ifPresent(s -> {
                    record.setGameName(s);
                    record.setPlayInfo(s);
                });
                break;
            case DG2, SEXY:
                Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_GAME_TYPE, gameName)).ifPresent(s -> {
                    record.setGameName(s);
                    record.setPlayInfo(DG2OrderInfoUtil.buildBetTypeStr(record.getPlayType(), record.getRoomType(), record.getPlayInfo()));
                });
                break;
        }
//        }else if (record.getVenueCode().equals(VenueEnum.DBSH.getVenueCode())) {
//            record.setPlayInfo(DBSHOrderInfoUtil.getBetTypeStr(record.getPlayType()));
//        }

//        if (record.getVenueCode().equals(VenueEnum.EVO.getVenueCode())) {
//            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.EVO_GAME_TYPE, record.getPlayType())).ifPresent(s -> {
//                record.setGameName(s);
//                record.setPlayInfo(s);
//            });
//        } else if (record.getVenueCode().equals(VenueEnum.SH.getVenueCode())) {
//            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_GAME_TYPE, gameName)).ifPresent(s -> {
//                record.setGameName(s);
//                record.setPlayInfo(s);
//            });
//        } else if (record.getVenueCode().equals(VenueEnum.SA.getVenueCode())) {
//            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SA_GAME_TYPE, record.getPlayType())).ifPresent(s -> {
//                record.setGameName(s);
//                record.setPlayInfo(s);
//            });
//
//        } else if (record.getVenueCode().equals(VenueEnum.DG2.getVenueCode()) || record.getVenueCode().equals(VenueEnum.SEXY.getVenueCode())) {
//            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_GAME_TYPE, gameName)).ifPresent(s -> {
//                record.setGameName(s);
//                record.setPlayInfo(DG2OrderInfoUtil.buildBetTypeStr(record.getPlayType(), record.getRoomType(), record.getPlayInfo()));
//            });
//        }
    }

    /**
     * 校验时间方法
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    private static boolean checkTimeZone(Long startTime, Long endTime, String timeZone) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            return true;
        }
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        if (startDate.after(endDate)) {
            return true;
        }
        List<String> betweenDates = TimeZoneUtils.getBetweenDates(startTime, endTime, timeZone);
        int num = 0;
        if (CollectionUtil.isNotEmpty(betweenDates)) {
            num = betweenDates.size();
        }
        int maxDays = 60;
        return num > maxDays;
    }

    // 处理每一批返回的搜索结果
    private static void processHits(SearchHit[] hits, List<OrderRecordPO> orderRecordPOList) {

        for (SearchHit hit : hits) {
            orderRecordPOList.add(JSONUtil.toBean(hit.getSourceAsString(), OrderRecordPO.class));
        }
    }

    public OrderRecordVO queryByOrderId(String orderId) {
        OrderRecordPO orderRecordPO = getOne(Wrappers.<OrderRecordPO>lambdaQuery().eq(OrderRecordPO::getOrderId, orderId));
        if (orderRecordPO == null) {
            return null;
        }
        OrderRecordVO vo = new OrderRecordVO();
        BeanUtils.copyProperties(orderRecordPO, vo);
        return vo;
    }

    public List<OrderRecordPO> findByOrderIds(List<String> orderIdList) {
        return list(Wrappers.<OrderRecordPO>lambdaQuery().in(OrderRecordPO::getOrderId, orderIdList));
    }

    public OrderRecordVO getByThirdOrderId(String orderId) {
        OrderRecordPO orderRecordPO = getOne(Wrappers.<OrderRecordPO>lambdaQuery().eq(OrderRecordPO::getThirdOrderId, orderId));
        if (orderRecordPO == null) {
            return null;
        }
        OrderRecordVO vo = new OrderRecordVO();
        BeanUtils.copyProperties(orderRecordPO, vo);
        return vo;
    }

    public List<OrderRecordPO> findByThirdOrderId(List<String> thirdOrderId) {
        return list(Wrappers.<OrderRecordPO>lambdaQuery().in(OrderRecordPO::getThirdOrderId, thirdOrderId));
    }

    public List<OrderRecordPO> findByThirdOrderIds(List<String> orderIdList, String venueCode) {
        return list(Wrappers.<OrderRecordPO>lambdaQuery().in(OrderRecordPO::getThirdOrderId, orderIdList).eq(OrderRecordPO::getVenueCode, venueCode));
    }

    public List<UserVenueTopVO> getAgentUserVenueList(final List<String> agentAcctIds, String timeZone) {
        if (CollUtil.isEmpty(agentAcctIds)) {
            return Collections.emptyList();
        }
        AgentUserVenueListParam vo = new AgentUserVenueListParam();
        Long startTime = DateUtils.getTodayStartTime(timeZone);
        Long endTime = DateUtils.getTodayEndTime(timeZone);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        vo.setAgentAcctIds(agentAcctIds);
        return orderRecordRepository.getAgentUserVenueLis(vo);

    }

    public List<HashMap> getAgentUserRecordByUserWinLoss(PlayAgentUserTeamParam param) {
        List<HashMap> result = orderRecordRepository.getAgentUserRecordByUserWinLoss(param);
        if (CollUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * @param timezone
     * @param beginDate
     * @param endDate
     * @param award
     * @param siteCode
     */
    public void sendUserRebate(String timezone, long beginDate, long endDate, VIPAwardEnum award, String siteCode) {
        List<String> siteVOList = Lists.newArrayList();
        try {
            // 统计需要返水的用户 (过滤调本周或本月已领取过的数据)
            List<UserRebateVO> userRebate = Lists.newArrayList();
            // 查询这个时区下所属的所有站点code 如果siteCode有值则使用传参指定siteCode
            if (StringUtils.isNotBlank(siteCode)) {
                siteVOList.add(siteCode);
            } else {
                ResponseVO<List<SiteVO>> allSiteResponse = siteApi.siteInfoAllstauts();
                if (null != allSiteResponse && allSiteResponse.isOk()) {
                    siteVOList = allSiteResponse.getData().stream().filter(obj -> obj.getTimezone()
                                    .equals(timezone) && obj.getStatus().equals(EnableStatusEnum.ENABLE.getCode()))
                            .map(SiteVO::getSiteCode).toList();
                }
            }
            if (VIPAwardEnum.WEEK_BONUS.equals(award) || VIPAwardEnum.MONTH_BONUS.equals(award)) {
                userRebate = orderRecordRepository.selectUserRebate(beginDate,
                        endDate, award.getCode(), null, siteVOList);
            } else if (VIPAwardEnum.WEEK_SPORT_BONUS.equals(award)) {
                userRebate = orderRecordRepository.selectUserRebate(beginDate,
                        endDate, award.getCode(), VenueTypeEnum.SPORTS.getCode(), siteVOList);
            }
            if (null == userRebate || ObjectUtil.isEmpty(userRebate)) {
                log.info("timezone:{}, beginDate:{}, endDate:{}, siteCode:{}, 无:{}数据",
                        timezone, beginDate, endDate, siteCode, award.getName());
                return;
            }
            Map<String, List<UserRebateVO>> userSiteRebateMap = userRebate.stream()
                    .collect(Collectors.groupingBy(UserRebateVO::getSiteCode));

            // 获取过期策略配置
            ResponseVO<List<SystemDictConfigRespVO>> responseVO = systemDictConfigApi
                    .getListByCode(DictCodeConfigEnums.VIP_BENEFIT_EXPIRATION_TIME.getCode());
            if (!responseVO.isOk() || null == responseVO.getData()) {
                log.error("获取:{} 配置数据为空或者异常", DictCodeConfigEnums.VIP_BENEFIT_EXPIRATION_TIME.getMsg());
            }
            List<String> finalSiteVOList = siteVOList;
            List<SystemDictConfigRespVO> dictList = responseVO.getData().stream().filter(obj ->
                    finalSiteVOList.contains(obj.getSiteCode()) || CommonConstant.business_zero.toString()
                            .equals(obj.getSiteCode())).toList();
            Map<String, String> dictMap = dictList.stream().collect(Collectors
                    .toMap(SystemDictConfigRespVO::getSiteCode, SystemDictConfigRespVO::getConfigParam));

            // 周返水以及月返水配置信息
            Map<String, List<SiteVIPRankVO>> siteVIPRankMap = vipRankApi.getVipRankListBySiteCodes(siteVOList);
            // 获取所有满足条件的用户信息
            List<String> userIds = userRebate.stream().map(UserRebateVO::getUserId).toList();
            int page = userIds.size() / 100;
            List<List<String>> userList = ListUtil.split(userIds, userIds.size() % 100 == 0 ? page : page + 1);
            List<UserInfoVO> userInfoVOList = Lists.newArrayList();
            for (List<String> list : userList) {
                userInfoVOList.addAll(userInfoApi.getUserInfoByUserIds(list));
            }
            List<OrderRebateRequestVO> list = Lists.newArrayList();
            for (Map.Entry<String, List<SiteVIPRankVO>> map : siteVIPRankMap.entrySet()) {
                List<UserRebateVO> userRebateVOList = userSiteRebateMap.get(map.getKey());
                // 满足条件的用户是否有不升级标签
                List<String> unLabelUserIds = userInfoApi.getUnLabelByUserIds(UserLabelEnum.NO_CASHBACK.getLabelId(),
                        map.getKey());

                // 过期时间
                long expireMillSecond = dictMap.containsKey(map.getKey()) ? Long.parseLong(dictMap.get(map
                        .getKey())) * 3600 * 1000 : Long.parseLong(dictMap
                        .get(CommonConstant.business_zero_str)) * 3600 * 1000;
                // 代理是否有会员福利
                List<String> unBenefitUserIds = userInfoApi.getUnBenefitByUserIds(AgentUserBenefitEnum.VIP_REWARD
                        .getCode().toString(), map.getKey());
                if (ObjectUtil.isEmpty(userRebateVOList)) {
                    log.error("VIP:{} 配置存在异常或者没有该站点:{} 的用户数据直接跳过", award.getName(), map.getKey());
                    continue;
                }
                List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoApi.getBySiteCode(
                        map.getKey());
                if (currencyInfoRespVOS.stream().anyMatch(obj -> null == obj.getFinalRate())) {
                    log.error("该站点:{}, 存在没有配置的汇率币种, 直接跳过", map.getKey());
                    continue;
                }
                for (UserRebateVO userRebateVO : userRebateVOList) {
                    UserInfoVO userInfoVO = userInfoVOList.stream().filter(obj -> obj.getUserId()
                            .equals(userRebateVO.getUserId())).findFirst().orElse(null);
                    if (null == userInfoVO) {
                        log.error("该用户id :{} 不存在", userRebateVO.getUserId());
                        continue;
                    }
                    // 不升级标签判断
                    if (ObjectUtil.isNotEmpty(userInfoVO.getUserLabelId())) {
                        if (ObjectUtil.isNotEmpty(unLabelUserIds) && unLabelUserIds
                                .contains(userInfoVO.getUserId())) {
                            log.info("该用户:{}, 存在:{} 标签无法升级", userInfoVO.getUserId(),
                                    UserLabelEnum.NO_CASHBACK.getName());
                            continue;
                        }
                    }
                    // 代理会员福利判断
                    if (ObjectUtil.isNotEmpty(unBenefitUserIds) && unBenefitUserIds
                            .contains(userInfoVO.getUserId())) {
                        log.info("该用户:{}, 上级代理:{}, 未勾选VIP福利不予发放奖励", userInfoVO.getUserId(),
                                userInfoVO.getSuperAgentAccount());
                        continue;
                    }
                    log.info("满足:{} 礼金的用户:{},站点:{}, 金额:{}", award.getName(), userRebateVO.getUserId(),
                            userRebateVO.getSiteCode(), userRebateVO.getValidBetAmount());
                    switch (award) {
                        case WEEK_BONUS -> sendWeekBonus(userInfoVO, userRebateVO,
                                map.getValue(), beginDate, endDate, expireMillSecond, list);
                        case MONTH_BONUS -> sendMonthBonus(userInfoVO, userRebateVO,
                                map.getValue(), beginDate, endDate, expireMillSecond, list);
                        case WEEK_SPORT_BONUS -> sendWeekSportBonus(userInfoVO, userRebateVO,
                                map.getValue(), beginDate, endDate, expireMillSecond, list);
                    }

                }
            }
            userRebateApi.recordUserRebate(list);
        } catch (Exception e) {
            log.error("时区:{}, beginTime:{}, endTime:{}, 奖励类型:{}, 待跑站点{}, 发生异常 ",
                    timezone, beginDate, endDate, award.getName(), siteVOList, e);
        }
    }

    private void sendWeekSportBonus(UserInfoVO userInfoVO, UserRebateVO userRebateVO,
                                    List<SiteVIPRankVO> siteVIPRankVOList, long beginDate, long endDate,
                                    long expireMillSecond, List<OrderRebateRequestVO> list) {
        SiteVIPRankVO siteVIPRankVO = siteVIPRankVOList.stream().filter(obj -> obj.getVipRankCode()
                        .equals(userInfoVO.getVipRank()) && Integer.parseInt(YesOrNoEnum.YES.getCode())
                        == obj.getWeekSportFlag())
                .findFirst().orElse(null);
        List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoApi.getBySiteCode(
                userInfoVO.getSiteCode());
        Map<String, BigDecimal> currencyMap = currencyInfoRespVOS.stream()
                .collect(Collectors.toMap(SiteCurrencyInfoRespVO::getCurrencyCode,
                        SiteCurrencyInfoRespVO::getFinalRate));
        if (null != siteVIPRankVO) {
            List<SiteVipSportVo> siteVipSportVoList = siteVIPRankVO.getSportVos();
            BigDecimal bonus = siteVipSportVoList.stream().filter(obj -> obj.getWeekSportBetAmount().compareTo(
                            Optional.ofNullable(AmountUtils.divide(userRebateVO.getValidBetAmount(),
                                    currencyMap.get(userRebateVO.getCurrency()))).orElse(BigDecimal.ZERO)) <= 0)
                    .max(Comparator.comparing(SiteVipSportVo::getWeekSportBetAmount))
                    .orElse(new SiteVipSportVo()).getWeekSportBonus();
            if (null == bonus || bonus.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("该用户:{} 周体育流水实际有效流水金额:{}, 主货币:{}, 不满足配置条件:{} 发送条件", userInfoVO.getUserId(),
                        userRebateVO.getValidBetAmount(), userInfoVO.getMainCurrency(), siteVipSportVoList.stream()
                                .map(SiteVipSportVo::getWeekSportBetAmount).toList());
                return;
            }
            OrderRebateRequestVO rebateRequestVO = new OrderRebateRequestVO();
            rebateRequestVO.setRebateAmount(bonus);
            rebateRequestVO.setUserId(userRebateVO.getUserId());
            rebateRequestVO.setUserAccount(userInfoVO.getUserAccount());
            rebateRequestVO.setSiteCode(userInfoVO.getSiteCode());
            rebateRequestVO.setVipRankCode(userInfoVO.getVipRank());
            rebateRequestVO.setUserName(userInfoVO.getUserName());
            rebateRequestVO.setOrderId("F" + SnowFlakeUtils.getSnowId());
            rebateRequestVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
            rebateRequestVO.setRebateTime(DateUtil.beginOfDay(new Date()).getTime());
            rebateRequestVO.setValidBetAmount(userRebateVO.getValidBetAmount());
            rebateRequestVO.setMainCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            rebateRequestVO.setRecordStartTime(beginDate);
            rebateRequestVO.setExpireTime(expireMillSecond);
            rebateRequestVO.setRecordEndTime(endDate);
            rebateRequestVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            rebateRequestVO.setFlag(Integer.parseInt(VIPAwardEnum.WEEK_SPORT_BONUS.getCode()));
            list.add(rebateRequestVO);
        }
    }

    private void sendMonthBonus(UserInfoVO userInfoVO, UserRebateVO userRebateVO,
                                List<SiteVIPRankVO> siteVIPRankVOList, long beginDate, long endDate,
                                long expireMillSecond, List<OrderRebateRequestVO> list) {
        SiteVIPRankVO siteVIPRankVO = siteVIPRankVOList.stream().filter(obj -> obj.getVipRankCode()
                        .equals(userInfoVO.getVipRank()) && Integer.parseInt(YesOrNoEnum.YES.getCode())
                        == obj.getMonthAmountFlag())
                .findFirst().orElse(null);
        List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoApi.getBySiteCode(
                userInfoVO.getSiteCode());
        Map<String, BigDecimal> currencyMap = currencyInfoRespVOS.stream()
                .collect(Collectors.toMap(SiteCurrencyInfoRespVO::getCurrencyCode,
                        SiteCurrencyInfoRespVO::getFinalRate));
        BigDecimal bonus = BigDecimal.ZERO;
        if (null != siteVIPRankVO) {
            if (siteVIPRankVO.getMonthAmountLimit().compareTo(Optional.ofNullable(AmountUtils
                            .divide(userRebateVO.getValidBetAmount(), currencyMap.get(userRebateVO.getCurrency())))
                    .orElse(BigDecimal.ZERO)) <= 0) {
                bonus = userRebateVO.getValidBetAmount().multiply(siteVIPRankVO.getMonthAmountProp1()
                                .divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.DOWN))
                        .multiply(siteVIPRankVO.getMonthAmountProp2()
                                .divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.DOWN));
            }
            if (bonus.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("该用户:{} 月流水实际有效流水金额:{}, 主货币:{}, 不满足配置条件:{} 发送条件", userInfoVO.getUserId(),
                        userRebateVO.getValidBetAmount(), userInfoVO.getMainCurrency(), siteVIPRankVO.getMonthAmountLimit());
                return;
            }
            // 满足最小下注金额可以发放返水
            OrderRebateRequestVO rebateRequestVO = new OrderRebateRequestVO();
            rebateRequestVO.setUserId(userRebateVO.getUserId());
            rebateRequestVO.setUserAccount(userInfoVO.getUserAccount());
            rebateRequestVO.setVipRankCode(userInfoVO.getVipRank());
            rebateRequestVO.setSiteCode(userInfoVO.getSiteCode());
            rebateRequestVO.setUserName(userInfoVO.getUserName());
            rebateRequestVO.setOrderId("F" + SnowFlakeUtils.getSnowId());
            rebateRequestVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
            rebateRequestVO.setRebateTime(DateUtil.beginOfDay(new Date()).getTime());
            rebateRequestVO.setValidBetAmount(userRebateVO.getValidBetAmount());
            rebateRequestVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.VIP_RIGHTS_INTERESTS_MONTH.getCode());
            rebateRequestVO.setRebateAmount(bonus.setScale(2, RoundingMode.DOWN));
            rebateRequestVO.setRecordStartTime(beginDate);
            rebateRequestVO.setExpireTime(expireMillSecond);
            rebateRequestVO.setMainCurrency(userInfoVO.getMainCurrency());
            rebateRequestVO.setRecordEndTime(endDate);
            rebateRequestVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            rebateRequestVO.setFlag(Integer.parseInt(VIPAwardEnum.MONTH_BONUS.getCode()));
            list.add(rebateRequestVO);
        }
    }

    private void sendWeekBonus(UserInfoVO userInfoVO, UserRebateVO userRebateVO,
                               List<SiteVIPRankVO> siteVIPRankVOList,
                               long beginDate, long endDate, long expireMillSecond, List<OrderRebateRequestVO> list) {
        SiteVIPRankVO siteVIPRankVO = siteVIPRankVOList.stream().filter(obj -> obj.getVipRankCode()
                        .equals(userInfoVO.getVipRank()) && Integer.parseInt(YesOrNoEnum.YES.getCode())
                        == obj.getWeekAmountFlag())
                .findFirst().orElse(null);
        List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoApi.getBySiteCode(
                userInfoVO.getSiteCode());
        Map<String, BigDecimal> currencyMap = currencyInfoRespVOS.stream()
                .collect(Collectors.toMap(SiteCurrencyInfoRespVO::getCurrencyCode,
                        SiteCurrencyInfoRespVO::getFinalRate));
        BigDecimal bonus = BigDecimal.ZERO;
        if (null != siteVIPRankVO) {
            if (siteVIPRankVO.getWeekAmountLimit().compareTo(Optional.ofNullable(AmountUtils
                            .divide(userRebateVO.getValidBetAmount(), currencyMap.get(userRebateVO.getCurrency())))
                    .orElse(BigDecimal.ZERO)) <= 0) {
                bonus = userRebateVO.getValidBetAmount().multiply(siteVIPRankVO.getWeekAmountProp1()
                                .divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.DOWN))
                        .multiply(siteVIPRankVO.getWeekAmountProp2()
                                .divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.DOWN));
            }
            if (bonus.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("该用户:{} 周流水实际有效流水金额:{}, 主货币:{}, 不满足配置条件:{} 发送条件", userInfoVO.getUserId(),
                        userRebateVO.getValidBetAmount(), userInfoVO.getMainCurrency(), siteVIPRankVO.getMonthAmountLimit());
                return;
            }
            // 满足最小下注金额可以发放返水
            OrderRebateRequestVO rebateRequestVO = new OrderRebateRequestVO();
            rebateRequestVO.setUserId(userRebateVO.getUserId());
            rebateRequestVO.setUserAccount(userInfoVO.getUserAccount());
            rebateRequestVO.setVipRankCode(userInfoVO.getVipRank());
            rebateRequestVO.setSiteCode(userInfoVO.getSiteCode());
            rebateRequestVO.setUserName(userInfoVO.getUserName());
            rebateRequestVO.setOrderId("F" + SnowFlakeUtils.getSnowId());
            rebateRequestVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
            rebateRequestVO.setRebateTime(DateUtil.beginOfDay(new Date()).getTime());
            rebateRequestVO.setValidBetAmount(userRebateVO.getValidBetAmount());
            rebateRequestVO.setRebateAmount(bonus.setScale(2, RoundingMode.DOWN));
            rebateRequestVO.setRecordStartTime(beginDate);
            rebateRequestVO.setExpireTime(expireMillSecond);
            rebateRequestVO.setMainCurrency(userInfoVO.getMainCurrency());
            rebateRequestVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.VIP_RIGHTS_INTERESTS_WEEK.getCode());
            rebateRequestVO.setRecordEndTime(endDate);
            rebateRequestVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            rebateRequestVO.setFlag(Integer.parseInt(VIPAwardEnum.WEEK_BONUS.getCode()));
            list.add(rebateRequestVO);
        }
    }

    public BigDecimal getTotalAmountByUserId(String userId, Long startTime, Long endTime) {
        UserWithdrawRunningWaterVO waterVO = orderRecordRepository.getTotalAmountByUserId(userId, startTime, endTime);
        if(null == waterVO || null ==  waterVO.getCompletedRunningWater()){
            return BigDecimal.ZERO;
        }
        return waterVO.getCompletedRunningWater();
    }

    /**
     * 场馆统计
     *
     * @param vo
     * @return
     */
    public List<AgentLowerLevelInfoVenueStatisticalVO> agentLowerLevelInfoVenueStatistical(AgentLowerLevelInfoVenueStatisticalReqVO vo) {
        return orderRecordRepository.agentLowerLevelInfoVenueStatistical(vo);
    }

    /**
     * 设置订单查询条件
     *
     * @return
     */
    public void initParams(LambdaEsQueryWrapper<OrderRecordPO> wrapper, OrderRecordAdminResVO obj) {
        LambdaEsQueryWrapper<OrderRecordPO> le = wrapper
                .eq(Strings.isNotBlank(obj.getOrderId()), OrderRecordPO::getOrderId, obj.getOrderId())
                .eq(Strings.isNotBlank(obj.getSiteCode()), OrderRecordPO::getSiteCode, obj.getSiteCode())
                .eq(Strings.isNotBlank(obj.getSiteName()), OrderRecordPO::getSiteName, obj.getSiteName())
                .in(CollUtil.isNotEmpty(obj.getVenueTypeList()), OrderRecordPO::getVenueType, obj.getVenueTypeList())
                .eq(Strings.isNotBlank(obj.getThirdOrderId()), OrderRecordPO::getThirdOrderId, obj.getThirdOrderId())
                .eq(Strings.isNotBlank(obj.getUserAccount()), OrderRecordPO::getUserAccount, obj.getUserAccount())
                .in(CollUtil.isNotEmpty(obj.getAccountType()), OrderRecordPO::getAccountType, obj.getAccountType())
                .eq(Strings.isNotBlank(obj.getCasinoUserName()), OrderRecordPO::getCasinoUserName, obj.getCasinoUserName())
                .in(CollUtil.isNotEmpty(obj.getVenueCode()), OrderRecordPO::getVenueCode, obj.getVenueCode())
                .eq(Strings.isNotBlank(obj.getBetIp()), OrderRecordPO::getBetIp, obj.getBetIp())
                .eq(Strings.isNotBlank(obj.getCurrency()), OrderRecordPO::getCurrency, obj.getCurrency())
                .like(Strings.isNotBlank(obj.getGameNo()), OrderRecordPO::getGameNo, obj.getGameNo())
                .eq(Strings.isNotBlank(obj.getAgentAcct()), OrderRecordPO::getAgentAcct, obj.getAgentAcct())
                .in(CollUtil.isNotEmpty(obj.getOrderStatusList()), OrderRecordPO::getOrderClassify, obj.getOrderStatusList())
                .eq(Objects.nonNull(obj.getChangeStatus()), OrderRecordPO::getChangeStatus, obj.getChangeStatus())
                .in(CollUtil.isNotEmpty(obj.getDeviceType()), OrderRecordPO::getDeviceType, obj.getDeviceType())
                .ge(Objects.nonNull(obj.getBetAmountMin()), OrderRecordPO::getBetAmount, obj.getBetAmountMin())
                .le(Objects.nonNull(obj.getBetAmountMax()), OrderRecordPO::getBetAmount, obj.getBetAmountMax())
                .ge(Objects.nonNull(obj.getValidAmountMin()), OrderRecordPO::getValidAmount, obj.getValidAmountMin())
                .le(Objects.nonNull(obj.getValidAmountMax()), OrderRecordPO::getValidAmount, obj.getValidAmountMax())
                .ge(Objects.nonNull(obj.getWinLossAmountMin()), OrderRecordPO::getWinLossAmount, obj.getWinLossAmountMin())
                .le(Objects.nonNull(obj.getWinLossAmountMax()), OrderRecordPO::getWinLossAmount, obj.getWinLossAmountMax())
                .in(CollUtil.isNotEmpty(obj.getVipRankList()), OrderRecordPO::getVipRank, obj.getVipRankList())
                .in(CollUtil.isNotEmpty(obj.getVipGradeList()), OrderRecordPO::getVipGradeCode, obj.getVipGradeList())
                .ge(Objects.nonNull(obj.getBetBeginTime()), OrderRecordPO::getBetTime, obj.getBetBeginTime())
                .le(Objects.nonNull(obj.getBetEndTime()), OrderRecordPO::getBetTime, obj.getBetEndTime())
                .ge(Objects.nonNull(obj.getSettleBeginTime()), OrderRecordPO::getSettleTime, obj.getSettleBeginTime())
                .le(Objects.nonNull(obj.getSettleEndTime()), OrderRecordPO::getSettleTime, obj.getSettleEndTime())
                .ge(Objects.nonNull(obj.getFirstSettleBeginTime()), OrderRecordPO::getFirstSettleTime, obj.getFirstSettleBeginTime())
                .le(Objects.nonNull(obj.getFirstSettleEndTime()), OrderRecordPO::getFirstSettleTime, obj.getFirstSettleEndTime())
                .eq(ObjectUtil.isNotEmpty(obj.getTransactionId()),OrderRecordPO::getTransactionId,obj.getTransactionId()).
                eq(StrUtil.isNotEmpty(obj.getFreeRoundGameOrderNo()), OrderRecordPO::getEventInfo, obj.getFreeRoundGameOrderNo());

        if (CollUtil.isNotEmpty(obj.getVenueTypeList())) {
            Integer venueType = obj.getVenueTypeList().get(CommonConstant.business_zero);
            if (Strings.isNotBlank(obj.getGameName())) {
                VenueTypeEnum typeEnum = VenueTypeEnum.of(venueType);
                switch (typeEnum) {
                    case FISHING, ELECTRONICS, CHESS, ACELT, MARBLES -> {
                        String keyPrefix = I18MsgKeyEnum.GAME_NAME.getCode();
                        I18nSearchVO i18nSearchVO = I18nSearchVO.builder().bizKeyPrefix(keyPrefix).lang(CurrReqUtils.getLanguage()).exactSearchContent(obj.getGameName()).build();
                        ResponseVO<List<String>> search = i18nApi.search(i18nSearchVO);
                        List<String> data = search.getData();
                        if (CollUtil.isEmpty(data)) {
                            data = List.of(CommonConstant.SUPER_ADMIN);
                        }
                        le.in(OrderRecordPO::getGameName, data);
                    }
                    case SH -> {
                        List<String> codeList = null;

                        List<String> replacedList = null;

                        List<String> bizKeyPrefixList = List.of(I18MsgKeyEnum.SH_GAME_TYPE.getCode(), I18MsgKeyEnum.SA_GAME_TYPE.getCode(),I18MsgKeyEnum.EVO_GAME_TYPE.getCode(),I18MsgKeyEnum.SEXY_GAME_TYPE.getCode());
                        ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder()
                                .bizKeyPrefixList(bizKeyPrefixList)
                                .lang(CurrReqUtils.getLanguage())
                                .searchContent(obj.getGameName()).build());
                        if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                            //i18列表CODE
                            codeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).collect(Collectors.toList());


                            //将i8CODE 过滤掉前缀
                            replacedList = codeList.stream()
                                    .map(s -> s.replace(I18MsgKeyEnum.SH_GAME_TYPE.getCode() + "_", "")
                                            .replace(I18MsgKeyEnum.SA_GAME_TYPE.getCode() + "_", "")
                                            .replace(I18MsgKeyEnum.SEXY_GAME_TYPE.getCode() + "_", "")
                                            .replace(I18MsgKeyEnum.EVO_GAME_TYPE.getCode() + "_", ""))
                                    .collect(Collectors.toList());

                        }else{
                            //传入到用户名称,没有查到  默认随机给一个名称数据库中查不到就行.
                            codeList = new ArrayList<>(List.of("alksdjfaioe"));
                            replacedList = new ArrayList<>(List.of("alksdjfaioe"));
                        }
                        if (obj.getVenueCode()!=null && obj.getVenueCode().contains(VenueEnum.DBSH.getVenueCode())){
                            replacedList.add(obj.getGameName());
                            codeList.add(obj.getGameName());
                        }

                        if (CollUtil.isNotEmpty(codeList) && CollUtil.isNotEmpty(replacedList)) {

                            List<String> finalCodeList = codeList;
                            List<String> fianlReplacedList = replacedList;
                            wrapper.and(w -> w.in(OrderRecordPO::getPlayType, fianlReplacedList).or().in(OrderRecordPO::getGameName, finalCodeList));

                        } else if (CollUtil.isNotEmpty(codeList)) {
                            le.in(CollUtil.isNotEmpty(codeList), OrderRecordPO::getRoomType, codeList);
                        } else if (CollUtil.isNotEmpty(replacedList)) {
                            le.in(OrderRecordPO::getGameName, replacedList);
                        }

                    }
                    case COCKFIGHTING ->
                            le.eq(Strings.isNotBlank(obj.getGameName()), OrderRecordPO::getGameName, obj.getGameName());
                    case ELECTRONIC_SPORTS, SPORTS ->
                            le.like(Strings.isNotBlank(obj.getGameName()), OrderRecordPO::getEventInfo, obj.getGameName());
                }
            }
        }
    }

    public void checkQueryTime(OrderRecordAdminResVO vo) {
        if (ObjectUtil.isEmpty(vo.getBetBeginTime()) && ObjectUtil.isEmpty(vo.getSettleBeginTime()) && ObjectUtil.isEmpty(vo.getFirstSettleBeginTime())) {
            throw new BaowangDefaultException(ResultCode.TIME_MUST_CHOOSE);
        }
        if (ObjectUtil.isNotEmpty(vo.getBetBeginTime()) &&
                OrderRecordAdminResVO.checkTime(vo.getBetBeginTime(), vo.getBetEndTime())) {
            throw new BaowangDefaultException(ResultCode.FORTY_DAY_OVER);
        }
        if (ObjectUtil.isNotEmpty(vo.getSettleBeginTime()) &&
                OrderRecordAdminResVO.checkTime(vo.getSettleBeginTime(), vo.getSettleEndTime())) {
            throw new BaowangDefaultException(ResultCode.FORTY_DAY_OVER);
        }
        if (ObjectUtil.isNotEmpty(vo.getFirstSettleBeginTime()) &&
                OrderRecordAdminResVO.checkTime(vo.getFirstSettleBeginTime(), vo.getFirstSettleEndTime())) {
            throw new BaowangDefaultException(ResultCode.FORTY_DAY_OVER);
        }
    }

    /**
     * easy es 添加默认排序
     *
     * @param orderByParamList 排序参数
     */
    private void addUniqueKeyOrder(List<OrderByParam> orderByParamList) {
        OrderByParam uniqueKeyOrder = new OrderByParam();

        uniqueKeyOrder.setOrder(OrderRecordPO.Fields.id + ".keyword");
        uniqueKeyOrder.setSort(ORDER_BY_DESC);
        orderByParamList.add(uniqueKeyOrder);
    }

    /**
     * 注单查询状态 页面查询统一逻辑 结算包含重结算 取消
     *
     * @param dto query
     */
    private void dtoCommonLogic(OrderRecordAdminResVO dto) {
        if (CollUtil.isNotEmpty(dto.getOrderStatusList())) {
            // 结算包含重结算
            if (dto.getOrderStatusList().contains(ClassifyEnum.SETTLED.getCode())) {
                dto.getOrderStatusList().add(ClassifyEnum.RESETTLED.getCode());
            }
        }
    }

    /**
     * 原始注单信息json转map
     *
     * @param parlayInfo 原始注单信息
     * @return map
     */
    private Map<String, Object> getParlayInfoList(String parlayInfo) {
        Map<String, Object> parlayMap;
        if (StringUtils.isNotBlank(parlayInfo)) {
            try {
//                String json = JSON.parseObject(parlayInfo, String.class);
//                parlayMap = (Map<String, Object>) JSONObject.parse(json);
                parlayMap = JSONUtil.parseObj(parlayInfo);
                return parlayMap;
            } catch (Exception e) {
                log.error("注单明细类型转换map发生异常", e);
            }
        }
        return null;
    }

    private void calculateOrderAmountAndNum(OrderRecordAggregationDTO aggregationDTO, AtomicLong count, AtomicReference<BigDecimal> totalBetAmount, AtomicReference<BigDecimal> totalValidAmount, AtomicReference<BigDecimal> totalWinLossAmount) {
        count.set(count.addAndGet(aggregationDTO.getNum()));
        totalBetAmount.set(totalBetAmount.get().add(Optional.ofNullable(aggregationDTO.getBetAmount()).orElse(BigDecimal.ZERO)));
        totalValidAmount.set(totalValidAmount.get().add(Optional.ofNullable(aggregationDTO.getValidAmount()).orElse(BigDecimal.ZERO)));
        totalWinLossAmount.set(totalWinLossAmount.get().add(Optional.ofNullable(aggregationDTO.getWinLossAmount()).orElse(BigDecimal.ZERO)));
    }

    public ResponseVO<Page<OrderRecordPageRespVO>> adminPage(OrderRecordAdminResVO dto) {
        // 参数校验
        checkQueryTime(dto);
        return adminPageQuery(dto);
    }

    /**
     * 分页查询
     *
     * @param dto 参数
     * @return
     */
    public ResponseVO<Page<OrderRecordPageRespVO>> adminPageQuery(OrderRecordAdminResVO dto) {

        List<SiteVO> siteAll =  siteApi.siteInfoAllstauts().getData();

        Map<String,Integer> siteMap = siteAll.stream().collect(Collectors.toMap(SiteVO::getSiteCode,SiteVO::getHandicapMode));

        // dto公共处理
        dtoCommonLogic(dto);
        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        initParams(wrapper, dto);
        // 排序参数
        List<OrderByParam> orderByParamList = new ArrayList<>();
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setOrder(Strings.isNotBlank(dto.getOrderField()) ? dto.getOrderField() : OrderRecordPO.Fields.betTime);
        orderByParam.setSort(Strings.isNotBlank(dto.getOrderType()) && dto.getOrderType().equals(ORDER_BY_ASC) ? ORDER_BY_ASC : ORDER_BY_DESC);
        orderByParamList.add(orderByParam);
        wrapper.orderBy(orderByParamList);
        // 默认排序 添加唯一键排序
        addUniqueKeyOrder(orderByParamList);
//        List<GameInfoVO> gameInfoVOS = gameInfoService.queryGameAccessParamListByVenueCode(List.of(VenueEnum.SH.getVenueCode(), VenueEnum.ACELT.getVenueCode()));
//        // 游戏名称填充
//        Map<String, GameInfoVO> shGameInfoMap = Optional.ofNullable(gameInfoVOS)
//                .map(s -> s.stream().collect(Collectors.toMap(GameInfoVO::getAccessParameters, p -> p, (k1, k2) -> k2)))
//                .orElse(Maps.newHashMap());
        // 查询
        EsPageInfo<OrderRecordPO> pageInfo = orderRecordEsMapper.pageQuery(wrapper, dto.getPageNumber(), dto.getPageSize());
        //这里已经调用siteCode查询场馆了，所以不需要传
        Map<String, String> venueInfoMap = new HashMap<>();
        //总控场馆名称
        if (Objects.equals(CurrReqUtils.getSiteCode(), "0")) {
            venueInfoMap = venueInfoService.getAdminVenueNameMap();
        } else {
            venueInfoMap = venueInfoService.getSiteVenueNameMap();
        }
        List<OrderRecordPageRespVO> list = Lists.newArrayList();
        pageInfo.getList().forEach(s -> {
            OrderRecordPageRespVO recordPageRespVO = getAdapterTransI18nNew(s);
            list.add(recordPageRespVO);
        });

        Page<OrderRecordPageRespVO> orderRecordPageRespVOPage = PageConvertUtil.convertPage(
                pageInfo, OrderRecordPageRespVO.class, list);
        List<OrderRecordPageRespVO> records = orderRecordPageRespVOPage.getRecords();
        String curSiteCode = dto.getCurSiteCode();
        // vip段位&等级
        Map<Integer, String> vipGradeMap = adaptiveVIPGrade(curSiteCode);
        Map<Integer, String> vipRankMap = adaptiveVIPRank(curSiteCode);

        if (CollUtil.isNotEmpty(records)) {
            for (OrderRecordPageRespVO record : records) {
                record.setSiteCode_$_siteName(record.getSiteCode());

                String vipRankText = "-";
                String vipGradeText = "-";

                Integer handicapMode = siteMap.get(record.getSiteCode());
                if(handicapMode != null && handicapMode.equals(SiteHandicapModeEnum.Internacional.getCode())){
                    vipRankText = vipRankMap.get(record.getVipRank());
                    vipGradeText = vipGradeMap.get(record.getVipGradeCode());
                }
                record.setVipRankText(vipRankText);
                record.setVipGradeText(vipGradeText);
                if(ObjectUtil.isNotEmpty(record.getVipGradeCode())) {
                    record.setZhVipGradeText("VIP" + (record.getVipGradeCode() - 1));
                }

                String venueName = venueInfoMap.get(record.getVenueCode());
                if (Objects.nonNull(venueName)) {
//                    record.setVenuePlatformName(venueInfoVO.getVenuePlatformName());
                    record.setVenueCodeText(venueName);
                }
            }
        }
        return ResponseVO.success(orderRecordPageRespVOPage);
    }


    public OrderRecordPageRespVO getAdapterTransI18nNew(OrderRecordPO record) {
        OrderRecordPageRespVO recordPageRespVO = new OrderRecordPageRespVO();
        Integer venueType = record.getVenueType();
        VenueTypeEnum venueTypeEnum = VenueTypeEnum.of(venueType);
        if (Objects.isNull(venueTypeEnum)) {
            return null;
        }
        VenueEnum venueEnum = VenueEnum.nameOfCode(record.getVenueCode());


        try {
            switch (venueTypeEnum) {
                case SH -> {
                    String orderInfo = record.getOrderInfo();
                    shGameNameBind(record);
                    String resultList = null;
                    String orderRecordInfo = gameServiceFactory.getGameInfoService(record.getVenueCode()).getOrderRecordInfo(record);


                    switch (venueEnum.getVenuePlatform()) {
                        case VenuePlatformConstants.SH:
                            recordPageRespVO.setLightningAmount(SHLanguageConversionUtils.getShLightningAmount(record.getParlayInfo(), record.getOrderInfo()));
                            recordPageRespVO.setTotalAmount(SHLanguageConversionUtils.getShTotalAmount(record.getParlayInfo(), record.getOrderInfo()));
                            resultList = SHLanguageConversionUtils.conversionBetResult(record.getResultList(), record.getOrderInfo());
                            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, orderInfo)).ifPresent(record::setPlayType);
                            break;
                        case VenuePlatformConstants.SA:
                            resultList = SAOrderInfoUtil.getSaResultList(record);
                            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SA_GAME_TYPE, orderInfo)).ifPresent(record::setPlayType);
                            break;
                    }
                    record.setOrderInfo(orderRecordInfo);
                    record.setResultList(resultList);

                }
                case ELECTRONICS -> {
                    String gameName = record.getGameName();
                    Optional.ofNullable(I18nMessageUtil.getI18NMessageInAdvice(gameName)).ifPresent(record::setGameName);
                    if (!record.getVenueCode().equals(VenuePlatformConstants.SPADE)) {
                        record.setOrderInfo(record.getThirdOrderId());
                    }
                }
                case CHESS -> {
                    String gameName = record.getGameName();
                    Optional.ofNullable(I18nMessageUtil.getI18NMessageInAdvice(gameName)).ifPresent(record::setGameName);
                    record.setOrderInfo(record.getThirdOrderId());
                }
                case ELECTRONIC_SPORTS -> {
                    record.setGameName(record.getEventInfo());
                    String orderRecordInfo = gameServiceFactory.getGameInfoService(record.getVenueCode()).getOrderRecordInfo(record);
                    record.setOrderInfo(orderRecordInfo);
                    switch (venueEnum.getVenuePlatform()){
                        case VenuePlatformConstants.TF:{
                            String gameNo = record.getGameNo();
                            if (StrUtil.isNotBlank(gameNo)) {
                                gameNo = gameNo.replace(CommonConstant.COMMA, "\n");
                                record.setGameNo(gameNo);
                            }
                            String eventInfo = record.getEventInfo();
                            if (StrUtil.isNotBlank(eventInfo)) {
                                eventInfo = eventInfo.replace(CommonConstant.COMMA, "\n");
                            }
                            record.setGameName(eventInfo);
                        }
                        case VenuePlatformConstants.DB_DJ:{
                            record.setGameNo(DbDJOrderUtil.getGameNo(record));
                        }
                    }

                }
                case SPORTS -> {
                    record.setGameName(record.getEventInfo());
                    String orderRecordInfo = gameServiceFactory.getGameInfoService(record.getVenueCode()).getOrderRecordInfo(record);
                    record.setOrderInfo(orderRecordInfo);
                    String gameNo = record.getGameNo();
                    if (StrUtil.isNotBlank(gameNo)) {
                        gameNo = gameNo.replace(CommonConstant.COMMA, "\n");
                        record.setGameNo(gameNo);
                    }
                    String eventInfo = record.getEventInfo();
                    if (StrUtil.isNotBlank(eventInfo)) {
                        eventInfo = eventInfo.replace(CommonConstant.COMMA, "\n");
                    }
                    record.setGameName(eventInfo);
                    if (Objects.requireNonNull(venueEnum) == VenueEnum.DB_PANDA_SPORT) {
                        record.setGameNo(DbPanDaSportOrderUtil.getGameNo(record));
                    }

                }
                case COCKFIGHTING -> {
                    String orderRecordInfo = gameServiceFactory.getGameInfoService(VenuePlatformConstants.S128).getOrderRecordInfo(record);
                    record.setOrderInfo(orderRecordInfo);
                }
                case ACELT -> {
                    ltGameNameBind(record);
                    switch (venueEnum.getVenuePlatform()) {
                        case VenuePlatformConstants.ACELT,VenuePlatformConstants.WP_ACELT:
                            String orderRecordInfo = gameServiceFactory.getGameInfoService(VenuePlatformConstants.ACELT).getOrderRecordInfo(record);
                            record.setOrderInfo(orderRecordInfo);
                            break;
                        case VenuePlatformConstants.DBACELT:
                            String dbRrderRecordInfo = gameServiceFactory.getGameInfoService(VenuePlatformConstants.DBACELT).getOrderRecordInfo(record);
                            record.setOrderInfo(dbRrderRecordInfo);
                            break;
                    }

                }
                case FISHING -> {
                    ftgGameNameBind(record);
                    record.setOrderInfo(record.getThirdOrderId());
                }
                case MARBLES -> {
                    if (venueEnum.getVenuePlatform().equals(VenuePlatformConstants.DBFISHING)) {
                        ftgGameNameBind(record);
                        record.setOrderInfo(record.getThirdOrderId());
                        break;
                    }
                    String gameName = record.getGameName();
                    Optional.ofNullable(I18nMessageUtil.getI18NMessageInAdvice(gameName)).ifPresent(record::setGameName);
                    String orderRecordInfo = gameServiceFactory.getGameInfoService(record.getVenueCode()).getOrderRecordInfo(record);
                    record.setOrderInfo(orderRecordInfo);
                }
            }
        } catch (Exception e) {
            log.error("注单详情异常:{}", record, e);
        }
        BeanUtils.copyProperties(record, recordPageRespVO);
        return recordPageRespVO;
    }

    /**
     * @param record po
     */
    public AgentUserOrderRecordPageVO getAdapterTransI18n(OrderRecordPO record, Map<String, GameInfoVO> gameInfoMap) {
        AgentUserOrderRecordPageVO recordPageRespVO = new AgentUserOrderRecordPageVO();

        Integer venueType = record.getVenueType();
        VenueTypeEnum venueTypeEnum = VenueTypeEnum.of(venueType);
        if (Objects.isNull(venueTypeEnum)) {
            return null;
        }
        try {
            VenueEnum venueEnum = VenueEnum.nameOfCode(record.getVenueCode());

            switch (venueTypeEnum) {
                case SH -> {
                    String orderInfo = record.getOrderInfo();
                    shGameNameBind(record);
                    String orderRecordInfo = gameServiceFactory.getGameInfoService(record.getVenueCode()).getOrderRecordInfo(record);
                    switch (venueEnum.getVenuePlatform()) {
                        case VenuePlatformConstants.SH:
                            recordPageRespVO.setLightningAmount(SHLanguageConversionUtils.getShLightningAmount(record.getParlayInfo(), record.getOrderInfo()));
                            recordPageRespVO.setTotalAmount(SHLanguageConversionUtils.getShTotalAmount(record.getParlayInfo(), record.getOrderInfo()));
                            Optional.ofNullable(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.SH_PLAY_TYPE, orderInfo)).ifPresent(record::setPlayType);
                            record.setResultList(orderRecordInfo);
                            record.setOrderInfo(orderRecordInfo);
                            break;
                        case VenuePlatformConstants.SA:
                            record.setOrderInfo(orderRecordInfo);
                            record.setResultList(orderRecordInfo);
                            break;
                        case VenuePlatformConstants.DG2:
                            record.setResultList(DG2OrderInfoUtil.getDGResultList(record.getOrderInfo(),record.getRoomType(),CurrReqUtils.getLanguage()));
                            record.setOrderInfo(orderRecordInfo);
                            break;
                        case VenuePlatformConstants.SEXY:
                            record.setResultList(SexyOrderInfoUtil.getSexyResultList(record.getOrderInfo(),record.getRoomType()));
                            record.setOrderInfo(orderRecordInfo);
                            break;
                        case VenuePlatformConstants.EVO:
                            record.setResultList(EvoLanguageConversionUtils.getSaResultList(record));
                            record.setOrderInfo(orderRecordInfo);
                            break;

                        case VenuePlatformConstants.DBSH:
                            record.setOrderInfo(orderRecordInfo);
                            record.setPlayInfo(DBSHOrderInfoUtil.getBetTypeStr(record.getPlayType()));
                            break;
                    }
                }
                case ELECTRONICS, CHESS -> {
                    String gameName = record.getGameName();
                    Optional.ofNullable(I18nMessageUtil.getI18NMessageInAdvice(gameName)).ifPresent(record::setGameName);
                    record.setOrderInfo(record.getThirdOrderId());
                }
                case ELECTRONIC_SPORTS, SPORTS -> {
                    record.setGameName(record.getEventInfo());
                }
                case COCKFIGHTING -> {
                }
                case ACELT -> {
                    ltGameNameBind(record);
                }
                case FISHING -> {
                    ftgGameNameBind(record);
                }
                case MARBLES -> {
                    String gameName = record.getGameName();
                    Optional.ofNullable(I18nMessageUtil.getI18NMessageInAdvice(gameName)).ifPresent(record::setGameName);
                    if (VenuePlatformConstants.DBFISHING.equals(venueEnum.getVenuePlatform())) {
                        record.setOrderInfo(record.getThirdOrderId());
                        break;
                    }
                    String orderRecordInfo = getMarblesOrderRecordInfo(record);
                    record.setOrderInfo(orderRecordInfo);
                }
            }
            BeanUtils.copyProperties(record, recordPageRespVO);
        } catch (Exception e) {
            log.error("代理游戏注单:{}", record, e);
        }
        return recordPageRespVO;
    }

    public Map<Integer, String> adaptiveVIPGrade(String siteCode) {
        Map<Integer, String> vipGradeMap;
        if (Objects.equals(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)) {
            vipGradeMap = Optional.ofNullable(vipGradeApi.getSystemVipGradeList())
                    .map(s -> s.stream().collect(Collectors.toMap(VIPGradeVO::getVipGradeCode, VIPGradeVO::getVipGradeName)))
                    .orElse(Maps.newHashMap());
        } else {
            List<SiteVIPGradeVO> vipGradeVOS = vipGradeApi.queryAllVIPGrade(siteCode);
            vipGradeMap = Optional.ofNullable(vipGradeVOS)
                    .map(s -> s.stream().collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipGradeName)))
                    .orElse(Maps.newHashMap());
        }
        return vipGradeMap;
    }

    public Map<Integer, String> adaptiveVIPRank(String siteCode) {
        Map<Integer, String> vipRankMap;
        if (Objects.equals(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)) {
            vipRankMap = Optional.ofNullable(vipRankApi.getVipRankList())
                    .map(s -> s.stream().collect(Collectors.toMap(VIPRankVO::getVipRankCode, VIPRankVO::getVipRankNameI18nCode)))
                    .orElse(Maps.newHashMap());
        } else {
            ResponseVO<List<SiteVIPRankVO>> listResponseVO = vipRankApi.getVipRankListBySiteCode(siteCode);
//            vipRankMap = Optional.ofNullable(listResponseVO)
//                    .map(ResponseVO::getData)
//                    .map(s -> s.stream().collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (v1, v2) -> v2)))
//                    .orElse(Maps.newHashMap());
            vipRankMap = Optional.ofNullable(listResponseVO)
                    .map(ResponseVO::getData)
                    .orElseGet(Collections::emptyList) // 如果 data 为 null，则使用空列表
                    .stream()
                    .filter(Objects::nonNull) // 过滤空元素
                    .filter(v -> v.getVipRankCode() != null && v.getVipRankNameI18nCode() != null) // 过滤 key/value 为 null
                    .collect(Collectors.toMap(
                            SiteVIPRankVO::getVipRankCode,
                            SiteVIPRankVO::getVipRankNameI18nCode,
                            (v1, v2) -> v2
                    ));
        }
        return vipRankMap;
    }

    public ResponseVO<OrderRecordAdminTotalRespVO> adminTotal(OrderRecordAdminResVO dto) {
        // 参数校验
        checkQueryTime(dto);
        // dto公共处理
        dtoCommonLogic(dto);
        // 游戏名称搜索参数绑定
        gameNameSearchParamBind(dto);
        // 查询参数
        List<OrderRecordAggregationDTO> recordAggregationDTOS = baseMapper.orderCountAndSumGroup(dto);
        AtomicLong settledCount = new AtomicLong(0);
        AtomicLong unsettledCount = new AtomicLong(0);
        AtomicLong totalNum = new AtomicLong(0);
        AtomicReference<BigDecimal> totalBetAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalValidAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> totalWinLossAmount = new AtomicReference<>(BigDecimal.ZERO);
        for (OrderRecordAggregationDTO entry : recordAggregationDTOS) {
            Integer classifyI = entry.getOrderClassify();
            if (Objects.equals(classifyI, ClassifyEnum.NOT_SETTLE.getCode())) {
                calculateOrderAmountAndNum(entry, unsettledCount, totalBetAmount, totalValidAmount, totalWinLossAmount);
            } else if (Objects.equals(classifyI, ClassifyEnum.SETTLED.getCode())
                    || Objects.equals(classifyI, ClassifyEnum.CANCEL.getCode())
                    || Objects.equals(classifyI, ClassifyEnum.RESETTLED.getCode())) {
                calculateOrderAmountAndNum(entry, settledCount, totalBetAmount, totalValidAmount, totalWinLossAmount);
            }
        }
        totalNum.set(settledCount.get() + unsettledCount.get());
        return ResponseVO.success(new OrderRecordAdminTotalRespVO(settledCount.get(), unsettledCount.get(), totalNum.get(), totalBetAmount.get(), totalValidAmount.get(), totalWinLossAmount.get()));
    }

    /**
     * 注单总计 游戏名称搜索 sql参数绑定
     *
     * @param obj
     */
    private void gameNameSearchParamBind(OrderRecordAdminResVO obj) {
        if (CollUtil.isNotEmpty(obj.getVenueTypeList())) {
            Integer venueType = obj.getVenueTypeList().get(CommonConstant.business_zero);
            if (Strings.isNotBlank(obj.getGameName())) {
                String gameName = obj.getGameName();
                obj.setGameName(null);
                VenueTypeEnum typeEnum = VenueTypeEnum.of(venueType);
                switch (typeEnum) {
                    case ELECTRONICS, CHESS, ACELT, MARBLES -> {
                        String keyPrefix = I18MsgKeyEnum.GAME_NAME.getCode();
                        I18nSearchVO i18nSearchVO = I18nSearchVO.builder().bizKeyPrefix(keyPrefix).lang(CurrReqUtils.getLanguage()).exactSearchContent(gameName).build();
                        ResponseVO<List<String>> search = i18nApi.search(i18nSearchVO);
                        List<String> data = search.getData();
                        if (CollUtil.isEmpty(data)) {
                            data = List.of(CommonConstant.SUPER_ADMIN);
                        }
                        obj.setGameNameList(data);
                    }
                    case SH -> {

                        GameInfoRequestVO build = GameInfoRequestVO.builder().venueCode(VenuePlatformConstants.SH).exactSearchContent(gameName).build();
                        Page<GameInfoVO> gameInfoVOPage = adminGameInfoService.adminGameInfoPage(build);
                        List<GameInfoVO> records = gameInfoVOPage.getRecords();
                        List<String> codeList = records.stream().map(GameInfoVO::getAccessParameters).toList();
                        obj.setRoomTypeList(codeList);


                        String msgKey = I18MsgKeyEnum.SH_GAME_TYPE.getCode();
                        ResponseVO<List<CodeValueVO>> listResponseVO = i18nApi.searchGetLookup(I18nSearchVO.builder()
                                .bizKeyPrefix(msgKey)
                                .lang(CurrReqUtils.getLanguage())
                                .searchContent(gameName)
                                .build());
                        if (CollUtil.isNotEmpty(listResponseVO.getData())) {
                            List<String> thirdGameCodeList = listResponseVO.getData().stream().map(CodeValueVO::getCode).toList();
                            thirdGameCodeList = thirdGameCodeList.stream().map(s -> s.replace(I18MsgKeyEnum.SH_GAME_TYPE.getCode() + "_", "")).toList();
                            obj.setThirdGameCodeList(thirdGameCodeList);
                        }
                    }
                    case COCKFIGHTING -> obj.setGameName(gameName);
                    case ELECTRONIC_SPORTS, SPORTS -> obj.setEventInfo(gameName);
                }
            }
        }
    }

    public ResponseVO<Long> orderExportCount(OrderRecordAdminResVO dto) {
        // 参数校验
        checkQueryTime(dto);
        dtoCommonLogic(dto);
        // 游戏名称搜索参数绑定
        gameNameSearchParamBind(dto);
        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        initParams(wrapper, dto);
        Long esCount = orderRecordEsMapper.selectCount(wrapper);
        return ResponseVO.success(esCount);
    }

    public ResponseVO<OrderRecordAdminInfoRespVO> orderInfo(OrderRecordAdminInfoResVO vo) {
        OrderRecordPO po = new LambdaQueryChainWrapper<>(orderRecordRepository).eq(OrderRecordPO::getOrderId, vo.getOrderId()).one();
        if (Objects.isNull(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        List<GameInfoVO> gameInfoVOS = gameInfoService.queryGameAccessParamListByVenueCode(List.of(VenueEnum.SH.getVenueCode(), VenueEnum.ACELT.getVenueCode()));
        // 游戏名称填充
        Map<String, GameInfoVO> shGameInfoMap = Optional.ofNullable(gameInfoVOS)
                .map(s -> s.stream().collect(Collectors.toMap(GameInfoVO::getAccessParameters, p -> p, (k1, k2) -> k2)))
                .orElse(Maps.newHashMap());
        // i18n
        getAdapterTransI18n(po, shGameInfoMap);
        GetByUserAccountVO userAccountVO = userInfoApi.getByUserInfoId(po.getUserId());
        Map<String, String> venueName = Maps.newHashMap();
        if (CurrReqUtils.getSiteCode().equals(CommonConstant.ADMIN_CENTER_SITE_CODE)) {
            venueName = venueInfoService.getAdminVenueNameMap();
        } else {
            venueName = venueInfoService.getSiteVenueNameMap();
        }


        OrderRecordAdminInfoRespVO respVO = new OrderRecordAdminInfoRespVO();
        OrderRecordAdminInfoRespVO.BettorInfo bettorInfo = new OrderRecordAdminInfoRespVO.BettorInfo();
        BeanUtils.copyProperties(userAccountVO, bettorInfo);
        respVO.setBettorInfo(bettorInfo);
        bettorInfo.setAgentAcct(userAccountVO.getSuperAgentAccount());
        String curSiteCode = CurrReqUtils.getSiteCode();
        // vip段位&等级
        Map<Integer, String> vipGradeMap = adaptiveVIPGrade(curSiteCode);
        Map<Integer, String> vipRankMap = adaptiveVIPRank(curSiteCode);
        //注单详情
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        BeanUtils.copyProperties(po, orderInfoVO);
        orderInfoVO.setVenueCodeText(venueName.get(po.getVenueCode()));
        orderInfoVO.setOrderStatusText(ClassifyEnum.nameOfCode(orderInfoVO.getOrderClassify()).getName());
        orderInfoVO.setVipGradeText(vipGradeMap.get(orderInfoVO.getVipGradeCode()));
        orderInfoVO.setVipRankText(vipRankMap.get(orderInfoVO.getVipRank()));
        orderInfoVO.setWinLossAmountStr(BigDecimalUtils.formatFourKeep4DecToStr(orderInfoVO.getWinLossAmount()));

        respVO.setOrderInfo(orderInfoVO);
        bettorInfo.setCasinoUserName(po.getCasinoUserName());
        bettorInfo.setVipGradeText(vipGradeMap.get(bettorInfo.getVipGradeCode()));
        bettorInfo.setVipRankText(vipRankMap.get(bettorInfo.getVipRank()));
        bettorInfo.setCurrency(userAccountVO.getMainCurrency());
        bettorInfo.setSiteCode(po.getSiteCode());
        bettorInfo.setSiteName(po.getSiteName());
        String thirdGameCode = po.getThirdGameCode();
        BigDecimal totalAmount = orderRecordRepository.classGameWinLossSum(po.getUserId(), po.getVenueCode(), thirdGameCode).setScale(4, RoundingMode.DOWN);

        bettorInfo.setClassWinLossAmount(BigDecimalUtils.formatFourKeep4DecToStr(totalAmount));
        VenueEnum venueEnum = VenueEnum.nameOfCode(po.getVenueCode());
        VenueTypeEnum typeEnum = venueEnum.getType();
        if (Objects.nonNull(typeEnum)) {
            if (VenueTypeEnum.SPORTS.equals(typeEnum)) {
                orderInfoVO.setRoomTypeName(gameServiceFactory.getGameInfoService(po.getVenueCode()).getBetType(po.getParlayInfo()));
            }
            OrderInfoLabel.OrderInfoLabelEnum orderLabelEnum = OrderInfoLabel.OrderInfoLabelEnum.getByType(typeEnum);
            if (Objects.nonNull(orderLabelEnum)) {
                respVO.setTableHead(orderLabelEnum.getTableLabel());
                respVO.setInfoHead(orderLabelEnum.getLabel());
                if (venueEnum.equals(VenueEnum.DBFISHING)) {
                    return ResponseVO.success(respVO);
                }
                if (CollUtil.isNotEmpty(orderLabelEnum.getTableLabel())) {
                    Map<String, Object> parlayInfoMap = getParlayInfoList(po.getParlayInfo());
                    try {
                        List<Map<String, Object>> tableValueMap = gameServiceFactory.getGameInfoService(po.getVenueCode()).getOrderInfo(parlayInfoMap);
                        respVO.setTableValue(tableValueMap);
                    } catch (Exception e) {
                        log.error("注单详情异常:{}", vo, e);
                    }

                }
            }
        }
        return ResponseVO.success(respVO);
    }

    public ResponseVO<OrderInfoVO> getLastOrderRecord(String userId) {
        OrderInfoVO result = new OrderInfoVO();
        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(OrderRecordPO::getUserId, userId)
                .orderByDesc(OrderRecordPO::getBetTime).limit(1);

        OrderRecordPO orderRecordPOS = orderRecordEsMapper.selectOne(wrapper);
        if (ObjectUtil.isEmpty(orderRecordPOS)) {
            return ResponseVO.success();
        }
        BeanUtils.copyProperties(orderRecordPOS, result);
        return ResponseVO.success(result);
//        List<OrderRecordVO> orderRecordVOS = ConvertUtil.entityListToModelList(orderRecordPOS, OrderRecordVO.class);
//
//        LambdaQueryChainWrapper<OrderRecordPO> eq = new LambdaQueryChainWrapper<>(baseMapper).eq(OrderRecordPO::getUserAccount, userAccount);
//        OrderRecordPO po = eq.orderByDesc(OrderRecordPO::getBetTime).last(" limit 1").one();
//        //注单详情
//        OrderInfoVO orderInfoVO = new OrderInfoVO();
//        if (!Objects.isNull(po)) {
//            BeanUtils.copyProperties(po, orderInfoVO);
//        }
//        return ResponseVO.success(orderInfoVO);
    }

    public ResponseVO<OrderRecordClientRespVO> clientOrderRecord(OrderRecordClientReqVO vo) {
        if (DateUtils.checkTime(vo.getBetStartTime(), vo.getBetEndTime(), 30)) {
            return ResponseVO.fail(ResultCode.QUERY_THIRTY_RANGE);
        }
        if (ObjUtil.isNull(vo.getVenueType())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        VenueTypeEnum typeEnum = VenueTypeEnum.of(vo.getVenueType());
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        String lang = CurrReqUtils.getLanguage();
        OrderRecordClientRespVO respVO = new OrderRecordClientRespVO();
        Map<String, String> siteVenueConfigPOMap = venueInfoService.getSiteVenueNameMap();
        List<VenueInfoVO> venueInfoVOList = venueInfoService.getSiteVenueInfoList();

        String language = CurrReqUtils.getLanguage();
        switch (typeEnum) {
            // 沙巴
            case SPORTS -> {
                String venueCode = VenueEnum.SBA.getVenueCode();
                //判断这个站点有没有沙巴体育
                Long count = siteVenueService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode,siteCode)
                        .eq(SiteVenuePO::getVenueCode,venueCode));
                if (count > 0) {
                    GameService gameService = gameServiceFactory.getGameService(venueCode);
                    VenueInfoVO venueInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(venueCode, null);
                    OrderRecordVenueClientReqVO reqVO = new OrderRecordVenueClientReqVO();
                    BeanUtils.copyProperties(vo, reqVO);
                    reqVO.setLang(language);
                    ResponseVO<OrderRecordClientRespVO> responseVO = gameService.orderClientQuery(reqVO, venueInfoVO, null);
                    OrderRecordClientRespVO data = responseVO.getData();
                    Optional.ofNullable(data).map(OrderRecordClientRespVO::getSabOrderList).ifPresent(s -> {
                        BigDecimal betAmount = s.stream().map(EventOrderClientResVO::getBetAmount).filter(ObjUtil::isNotNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal winLossAmount = s.stream().map(EventOrderClientResVO::getWinLossAmount).filter(ObjUtil::isNotNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal validAmount = s.stream().map(EventOrderClientResVO::getValidAmount).filter(ObjUtil::isNotNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                        ClientOrderTotalVO totalVO = new ClientOrderTotalVO();
                        totalVO.setBetNum((long) s.size());
                        totalVO.setBetAmount(betAmount);
                        totalVO.setWinLoseAmount(winLossAmount);
                        totalVO.setValidAmount(validAmount);
                        data.setTotalVO(totalVO);
                    });
                    return responseVO;
                } else {
                    Map<String, VenueInfoVO> venueMap = venueInfoVOList.stream().filter(e -> VenueTypeEnum.SPORTS.getCode()
                                    .equals(e.getVenueType()))
                            .collect(Collectors.toMap(VenueInfoVO::getVenueCode,
                                    Function.identity()));

                    //获取单场馆的体育场馆列表
                    List<String> venueCodes = VenueEnum.getSignSportVenueCodeList();
                    vo.setVenueCodes(venueCodes);

                    EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                    Page<SportOrderClientResVO> sportOrderPage = PageConvertUtil.convertPage(esPageInfo, SportOrderClientResVO.class);
                    List<OrderRecordPO> records = esPageInfo.getList();
                    if (CollUtil.isNotEmpty(records)) {
                        List<SportOrderClientResVO> data = new ArrayList<>();
                        records.forEach(record -> {
                            SportOrderClientResVO sportOrderClientResVO = gameServiceFactory
                                    .getGameInfoService(record.getVenueCode())
                                    .getSportData(record, language, venueMap.get(record.getVenueCode()));
                            if(ObjectUtil.isNotEmpty(sportOrderClientResVO)){
                                sportOrderClientResVO.setVenueName(siteVenueConfigPOMap.get(record.getVenueCode()));
                            }
                            data.add(sportOrderClientResVO);
                        });
                        sportOrderPage.setRecords(data);
                    }
                    long total = esPageInfo.getTotal();
                    ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                    totalVO.setBetNum(total);
                    respVO.setTotalVO(totalVO);
                    respVO.setSportOrderPage(sportOrderPage);
                    respVO.setIsSBA(false);
                }
            }
            case SH -> {
//                List<GameInfoVO> gameInfoVOS = gameInfoService.queryGameAccessParamListByVenueCode(List.of(VenueEnum.SH.getVenueCode(), VenueEnum.SA.getVenueCode(), VenueEnum.EVO.getVenueCode()));
                EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                List<OrderRecordPO> records = esPageInfo.getList();
                if (CollUtil.isNotEmpty(records)) {
                    records.forEach(record -> {
                        VenueEnum venueEnum = VenueEnum.nameOfCode(record.getVenueCode());
                        switch (venueEnum.getVenuePlatform()) {
                            case VenuePlatformConstants.SH:
                                Optional.ofNullable(record.getResultList())
                                        .ifPresent(s -> record.setResultList(SHLanguageConversionUtils.getClientConversionBetResult(s, record.getOrderInfo())));
                                break;
                            case VenuePlatformConstants.SA:
                                record.setResultList(SAOrderInfoUtil.getSaResultList(record));
                                break;
                            case VenuePlatformConstants.EVO:
                                record.setResultList(EvoLanguageConversionUtils.getSaResultList(record));
                                //shGameNameBind(record, gameInfoMap);
                                break;
                            case VenuePlatformConstants.DG2:
                                record.setResultList(DG2OrderInfoUtil.getDGResultList(record.getOrderInfo(),record.getRoomType(),CurrReqUtils.getLanguage()));
                                record.setBetContent(DG2OrderInfoUtil.buildBetTypeStr(record.getPlayType(),record.getRoomType(),record.getPlayInfo()));
                                break;
                            case VenuePlatformConstants.SEXY:
                                record.setResultList(SexyOrderInfoUtil.getSexyResultList(record.getOrderInfo(),record.getRoomType()));
                                record.setBetContent(SexyOrderInfoUtil.buildBetTypeStr(record.getPlayType(),record.getRoomType()));
                                break;

                            case VenuePlatformConstants.DBSH:
                                record.setResultList(DBSHOrderInfoUtil.getResultInfo(record.getResultList(),record.getThirdGameCode(),CurrReqUtils.getLanguage()));
                                record.setBetContent(DBSHOrderInfoUtil.getBetTypeStr(record.getPlayType()));
                                break;

                        }
                        shGameNameBind(record);
                    });
                }
                Page<TableOrderClientResVO> orderESPage = PageConvertUtil.convertPage(esPageInfo, TableOrderClientResVO.class);

                orderESPage.getRecords().forEach(record -> {
                    VenueEnum venueEnum = VenueEnum.nameOfCode(record.getVenueCode());
                    String type = null;

                    record.setVenueName(siteVenueConfigPOMap.get(record.getVenueCode()));

                    switch (venueEnum.getVenuePlatform()) {
                        case VenuePlatformConstants.SH: {
                            type = CommonConstant.SH_PLAY_TYPE;
                            record.setLightningAmount(SHLanguageConversionUtils.getShLightningAmount(record.getParlayInfo(), record.getOrderInfo()));
                            record.setTotalAmount(SHLanguageConversionUtils.getShTotalAmount(record.getParlayInfo(), record.getOrderInfo()));
                            break;
                        }
                        case VenuePlatformConstants.SA: {
                            type = String.format(CommonConstant.SA_BET_TYPE, record.getPlayType());
                            break;
                        }
                        case VenuePlatformConstants.DG2,VenuePlatformConstants.SEXY,VenuePlatformConstants.DBSH: {
                            type = CommonConstant.SEXY_GAME_TYPE;
                            break;
                        }
                        case VenuePlatformConstants.EVO: {
                            type = CommonConstant.EVO_PLAY_TYPE;
                            break;
                        }

                        default: {

                        }
                    }
                    String betContent;
                    if (ObjectUtil.equals(CommonConstant.EVO_PLAY_TYPE, type)) {
                        betContent = EvoGameTypeI18n.getName(record.getBetContent(), lang);
                    } else if (ObjectUtil.equals(CommonConstant.SEXY_GAME_TYPE, type)) {
                        betContent = record.getBetContent();
                    } else {
                        betContent = I18nMessageUtil.getSystemParamAndTrans(type, record.getBetContent());
                    }
                    record.setBetContentText(betContent);
                });


                respVO.setTableOrderPage(orderESPage);
                long total = esPageInfo.getTotal();
                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                totalVO.setBetNum(total);
                respVO.setTotalVO(totalVO);
            }
            case ELECTRONICS, CHESS -> {
                EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                Page<BasicOrderClientResVO> orderESPage = PageConvertUtil.convertPage(esPageInfo, BasicOrderClientResVO.class);

                if(CollectionUtil.isNotEmpty(orderESPage.getRecords())){
                    orderESPage.getRecords().stream().forEach(x->{
                        x.setVenueName(siteVenueConfigPOMap.get(x.getVenueCode()));
                    });
                }

                respVO.setBasicOrderPage(orderESPage);
                long total = esPageInfo.getTotal();
                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                totalVO.setBetNum(total);
                respVO.setTotalVO(totalVO);
            }
            case ACELT -> {
                EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                List<OrderRecordPO> records = esPageInfo.getList();
                if (CollUtil.isNotEmpty(records)) {
                    records.forEach(OrderRecordService::ltGameNameBind);
                }
                Page<LtOrderClientResVO> orderESPage = PageConvertUtil.convertPage(esPageInfo, LtOrderClientResVO.class);

                if(CollectionUtil.isNotEmpty(orderESPage.getRecords())){
                    orderESPage.getRecords().stream().forEach(x->{
                        VenueEnum venueEnum = VenueEnum.nameOfCode(x.getVenueCode());
                        x.setVenueName(siteVenueConfigPOMap.get(x.getVenueCode()));
                        if (VenueEnum.DBACELT.equals(venueEnum)) {
                            x.setOdds(DBAceLtOrderParseUtil.buildOdds(x.getOdds()));
                        }
                    });
                }


                respVO.setLtOrderPage(orderESPage);
                long total = esPageInfo.getTotal();
                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                totalVO.setBetNum(total);
                respVO.setTotalVO(totalVO);
            }
            case COCKFIGHTING -> {
                EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                Page<EventOrderClientResVO> orderESPage = PageConvertUtil.convertPage(esPageInfo, EventOrderClientResVO.class);
                if (orderESPage.getTotal() > 0) {
                    // 斗鸡使用playInfo存储teamInfo信息
//                    orderESPage.getRecords().forEach(e -> e.setTeamInfo(e.getPlayInfo()));
                    orderESPage.getRecords().forEach(x->{
                        x.setTeamInfo(x.getPlayInfo());
                        x.setVenueName(siteVenueConfigPOMap.get(x.getVenueCode()));
                    });

                }


                respVO.setEventOrderPage(orderESPage);
                long total = esPageInfo.getTotal();
                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                totalVO.setBetNum(total);
                respVO.setTotalVO(totalVO);
            }
            case ELECTRONIC_SPORTS -> {
                String venueCode = VenueEnum.TF.getVenueCode();
                //判断这个站点有没有TF
                Long count = siteVenueService.getBaseMapper().selectCount(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode,siteCode)
                        .eq(SiteVenuePO::getVenueCode,venueCode));
                long total =0L;

                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                if(count > 0){
                    GameService gameService = gameServiceFactory.getGameService(venueCode);
                    UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
                    VenueInfoVO venueInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(venueCode, userInfoVO.getMainCurrency());
                    OrderRecordVenueClientReqVO reqVO = new OrderRecordVenueClientReqVO();
                    BeanUtils.copyProperties(vo, reqVO);
                    reqVO.setLang(CurrReqUtils.getLanguage());
                    CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(CasinoMemberReq.builder()
                            .siteCode(siteCode)
                            .venueCode(venueCode)
                            .userId(userId)
                            .build());
                    if (ObjUtil.isNull(casinoMemberVO)) {
                        return ResponseVO.success(respVO);
                    }

                    ResponseVO<OrderRecordClientRespVO> responseVO = gameService.orderClientQuery(reqVO, venueInfoVO, casinoMemberVO);
                    OrderRecordClientRespVO sportEs = responseVO.getData();
                    Page<EventOrderClientResVO> iPage = sportEs.getEventOrderPage();
                    if (ObjectUtil.isNotEmpty(iPage)){
                        iPage.getRecords().forEach(x->{
                            x.setVenueName(siteVenueConfigPOMap.get(x.getVenueCode()));
                        });
                        total = iPage.getTotal();
                    }
                    totalVO.setBetNum(total);
                    responseVO.getData().setTotalVO(totalVO);
                    return responseVO;
                }else{
                    OrderRecordClientRespVO clientRespVO = new OrderRecordClientRespVO();
                    clientRespVO.setTotalVO(totalVO);
                    //获取单场馆的电竞场馆列表
                    List<String> venueCodes = VenueEnum.getESportVenueCodeList();
                    vo.setVenueCodes(venueCodes);
                    EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                    Page<EventOrderClientResVO> sportOrderPage = PageConvertUtil.convertPage(esPageInfo, EventOrderClientResVO.class);
                    List<OrderRecordPO> records = esPageInfo.getList();
                    if (CollUtil.isNotEmpty(records)) {
                        List<EventOrderClientResVO> data = new ArrayList<>();
                        records.forEach(record -> {
                            EventOrderClientResVO sportOrderClientResVO = gameServiceFactory
                                    .getGameInfoService(record.getVenueCode())
                                    .getESportData(record);
                            if(ObjectUtil.isNotEmpty(sportOrderClientResVO)){
                                sportOrderClientResVO.setVenueName(siteVenueConfigPOMap.get(record.getVenueCode()));
                            }
                            data.add(sportOrderClientResVO);
                        });
                        sportOrderPage.setRecords(data);
                    }
                    clientRespVO.setEventOrderPage(sportOrderPage);
                    return ResponseVO.success(clientRespVO);
                }

            }
            case FISHING -> {
                EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                Page<BasicOrderClientResVO> orderESPage = PageConvertUtil.convertPage(esPageInfo, BasicOrderClientResVO.class);

                orderESPage.getRecords().forEach(x->{
                    x.setVenueName(siteVenueConfigPOMap.get(x.getVenueCode()));
                });


                respVO.setBasicOrderPage(orderESPage);
                long total = esPageInfo.getTotal();
                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                totalVO.setBetNum(total);
                respVO.setTotalVO(totalVO);
            }
            case MARBLES -> {
                EsPageInfo<OrderRecordPO> esPageInfo = getOrderRecordPOEsPageInfo(vo);
                List<OrderRecordPO> records = esPageInfo.getList();
                if (CollUtil.isNotEmpty(records)) {
                    records.forEach(OrderRecordService::ltGameNameBind);
                }
                Page<LtOrderClientResVO> orderESPage = PageConvertUtil.convertPage(esPageInfo, LtOrderClientResVO.class, s -> adapterTransNew(s));

                orderESPage.getRecords().forEach(x->{
                    x.setVenueName(siteVenueConfigPOMap.get(x.getVenueCode()));
                });


                respVO.setLtOrderPage(orderESPage);
                long total = esPageInfo.getTotal();
                ClientOrderTotalVO totalVO = getClientOrderTotal(vo);
                totalVO.setBetNum(total);
                respVO.setTotalVO(totalVO);
            }
        }
        return ResponseVO.success(respVO);
    }

    private ClientOrderTotalVO getClientOrderTotal(OrderRecordClientReqVO vo) {
        // 已结算状态
        if (CollUtil.isNotEmpty(vo.getOrderClassifyList())) {
            if (vo.getOrderClassifyList().contains(ClassifyEnum.SETTLED.getCode())) {
                vo.getOrderClassifyList().add(ClassifyEnum.RESETTLED.getCode());
                vo.getOrderClassifyList().add(ClassifyEnum.CANCEL.getCode());
            }
        }
        // 聚合查询 走tidb

        return orderRecordRepository.clientOrderTotal(vo);
    }

    private EsPageInfo<OrderRecordPO> getOrderRecordPOEsPageInfo(OrderRecordClientReqVO vo) {
        // 已结算状态
        if (CollUtil.isNotEmpty(vo.getOrderClassifyList())) {
            if (vo.getOrderClassifyList().contains(ClassifyEnum.SETTLED.getCode())) {
                vo.getOrderClassifyList().add(ClassifyEnum.RESETTLED.getCode());
                vo.getOrderClassifyList().add(ClassifyEnum.CANCEL.getCode());
            }
        }

        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        wrapper
                .eq(OrderRecordPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(OrderRecordPO::getUserId, CurrReqUtils.getOneId())
                .eq(OrderRecordPO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(ObjUtil.isNotEmpty(vo.getVenueType()), OrderRecordPO::getVenueType, vo.getVenueType())
                .in(CollUtil.isNotEmpty(vo.getOrderClassifyList()), OrderRecordPO::getOrderClassify, vo.getOrderClassifyList())
                .in(CollUtil.isNotEmpty(vo.getVenueCodes()), OrderRecordPO::getVenueCode, vo.getVenueCodes())
                .ge(ObjUtil.isNotEmpty(vo.getBetStartTime()), OrderRecordPO::getBetTime, vo.getBetStartTime())
                .le(ObjUtil.isNotEmpty(vo.getBetEndTime()), OrderRecordPO::getBetTime, vo.getBetEndTime());
        // 排序参数
        List<OrderByParam> orderByParamList = new ArrayList<>();
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setOrder(OrderRecordPO.Fields.betTime);
        orderByParam.setSort(ORDER_BY_DESC);
        orderByParamList.add(orderByParam);
        // 默认排序 添加唯一键排序
        addUniqueKeyOrder(orderByParamList);
        wrapper.orderBy(orderByParamList);
        // 查询
        EsPageInfo<OrderRecordPO> pageInfo = orderRecordEsMapper.pageQuery(wrapper, vo.getPageNumber(), vo.getPageSize());
        return pageInfo;
    }

    public List<OrderRecordVO> getOrderListByOrderIds(GeOrderListVO vo) {
        List<OrderRecordPO> poList = findByOrderIds(vo.getOrderIdList());
        try {
            return ConvertUtil.convertListToList(poList, new OrderRecordVO());
        } catch (Exception e) {
            log.info("OrderRecordVO 转换错误");
        }

        return null;
    }

    public ResponseVO<Page<AgentUserOrderRecordPageVO>> getAgentUserPageList(AgentUserOrderRecordReqVO vo) {
        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        wrapper
                .in(OrderRecordPO::getAgentAcct, vo.getAgentAccounts())
                .eq(OrderRecordPO::getSiteCode, vo.getSiteCode())
                .eq(Strings.isNotBlank(vo.getCurrencyCode()), OrderRecordPO::getCurrency, vo.getCurrencyCode())
                .eq(Strings.isNotBlank(vo.getOrderId()), OrderRecordPO::getOrderId, vo.getOrderId())
                .eq(Strings.isNotBlank(vo.getUserAccount()), OrderRecordPO::getUserAccount, vo.getUserAccount())
                .ge(ObjUtil.isNotEmpty(vo.getStartTime()), OrderRecordPO::getBetTime, vo.getStartTime())
                .le(ObjUtil.isNotEmpty(vo.getEndTime()), OrderRecordPO::getBetTime, vo.getEndTime())
                .eq(Strings.isNotBlank(vo.getVenueCode()), OrderRecordPO::getVenueCode, vo.getVenueCode());
        // 排序参数
        List<OrderByParam> orderByParamList = new ArrayList<>();
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setOrder(OrderRecordPO.Fields.betTime);
        orderByParam.setSort(ORDER_BY_DESC);
        orderByParamList.add(orderByParam);
        // 默认排序 添加唯一键排序
        addUniqueKeyOrder(orderByParamList);
        wrapper.orderBy(orderByParamList);
        List<GameInfoVO> gameInfoVOS = gameInfoService.queryGameAccessParamListByVenueCode(List.of(VenueEnum.SH.getVenueCode(), VenueEnum.ACELT.getVenueCode()));
        // 游戏名称填充
        Map<String, GameInfoVO> shGameInfoMap = Optional.ofNullable(gameInfoVOS)
                .map(s -> s.stream().collect(Collectors.toMap(GameInfoVO::getAccessParameters, p -> p, (k1, k2) -> k2)))
                .orElse(Maps.newHashMap());
        // 查询
        EsPageInfo<OrderRecordPO> pageInfo = orderRecordEsMapper.pageQuery(wrapper, vo.getPageNumber(), vo.getPageSize());
        //这里已经调用siteCode查询场馆了，所以不需要传
        Map<String, String> venueInfoMap = venueInfoService.getSiteVenueNameMap();

        List<AgentUserOrderRecordPageVO> list = Lists.newArrayList();
        pageInfo.getList().forEach(s -> {
            list.add(getAdapterTransI18n(s, shGameInfoMap));
        });


        Page<AgentUserOrderRecordPageVO> orderRecordPageRespVOPage = PageConvertUtil.convertPage(
                pageInfo, AgentUserOrderRecordPageVO.class, list);
        if (orderRecordPageRespVOPage.getTotal() > 0) {
            orderRecordPageRespVOPage.getRecords().forEach(e -> {
                String venueName = venueInfoMap.get(e.getVenueCode());
                if (Objects.nonNull(venueName)) {
                    e.setVenuePlatformName(venueName);
                    e.setVenueCodeText(venueName);
                }
            });
        }
        return ResponseVO.success(orderRecordPageRespVOPage);
    }

    public ResponseVO<AgentBetOrderResVO> getAgentClientOrder(AgentUserOrderRecordReqVO vo) {
        try {
            if (checkTimeZone(vo.getStartTime(), vo.getEndTime(), vo.getTimeZone())) {
                return ResponseVO.fail(ResultCode.QUERY_NOT_SUPPORT_60);
            }
            AgentBetOrderResVO betOrderVO = new AgentBetOrderResVO();
            LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
            wrapper
                    .in(OrderRecordPO::getAgentAcct, vo.getAgentAccounts())
                    .eq(OrderRecordPO::getSiteCode, vo.getSiteCode())
                    .eq(Strings.isNotBlank(vo.getUserAccount()), OrderRecordPO::getUserAccount, vo.getUserAccount())
                    .in(ObjectUtil.isNotEmpty(vo.getAgentIds()), OrderRecordPO::getAgentId, vo.getAgentIds())
                    .ge(ObjUtil.isNotEmpty(vo.getStartTime()), OrderRecordPO::getBetTime, vo.getStartTime())
                    .le(ObjUtil.isNotEmpty(vo.getEndTime()), OrderRecordPO::getBetTime, vo.getEndTime())
                    .eq(Strings.isNotBlank(vo.getVenueCode()), OrderRecordPO::getVenueCode, vo.getVenueCode());
            // 排序参数
            List<OrderByParam> orderByParamList = new ArrayList<>();
            OrderByParam orderByParam = new OrderByParam();
            orderByParam.setOrder(OrderRecordPO.Fields.betTime);
            orderByParam.setSort(ORDER_BY_DESC);
            orderByParamList.add(orderByParam);
            // 默认排序 添加唯一键排序
            addUniqueKeyOrder(orderByParamList);
            wrapper.orderBy(orderByParamList);

            // 查询
            EsPageInfo<OrderRecordPO> pageInfo = orderRecordEsMapper.pageQuery(wrapper, vo.getPageNumber(), vo.getPageSize());
            Page<AgentBetGameOrderVO> orderRecordPageRespVOPage = PageConvertUtil.convertPage(pageInfo, AgentBetGameOrderVO.class);
            List<AgentBetGameOrderVO> records = orderRecordPageRespVOPage.getRecords();
            if (records == null || records.isEmpty() || orderRecordPageRespVOPage.getTotal() == 0) {
                return ResponseVO.success(betOrderVO);
            }
            // 结果处理
            //这里已经调用siteCode查询场馆了，所以不需要传
            List<VenueInfoVO> responseVO = venueInfoService.getAdminVenueInfoByVenueCodeList(records.stream().map(AgentBetGameOrderVO::getVenueCode).filter(Strings::isNotBlank).distinct().toList());
            Map<String, VenueInfoVO> venueInfoMap = responseVO.stream()
                    .collect(Collectors.toMap(venueInfo -> venueInfo.getVenueCode(), venueInfo -> venueInfo, (existing, replacement) -> existing));

            Map<String, String> venueNameMap = venueInfoService.getSiteVenueNameMap();

            for (AgentBetGameOrderVO record : records) {

                String venueName = venueNameMap.get(record.getVenueCode());
                if (Objects.nonNull(venueName)) {
                    record.setVenuePlatformName(venueName);
                    record.setVenueName(venueName);
                }
                VenueInfoVO venueInfoVO = venueInfoMap.get(record.getVenueCode());
                String icon = null;
                if (ObjectUtil.isNotEmpty(venueInfoVO)) {
                    icon = venueInfoVO.getH5IconI18nCode();
                    if (DeviceType.PC.getCode().equals(CurrReqUtils.getReqDeviceType())) {
                        icon = venueInfoVO.getPcIconI18nCode();
                    }
                }
                record.setGameIcon(icon);
                record.setWinLossAmount(record.getWinLossAmount() == null ? BigDecimal.ZERO : record.getWinLossAmount());
            }

            betOrderVO.setAgentBetGameOrderVOPage(orderRecordPageRespVOPage);
            betOrderVO.setTotalBetNum(records.stream().filter(e -> e.getBetNum() != null).mapToLong(AgentBetGameOrderVO::getBetNum).sum());
            //计算总和
            OrderRecordPO totalAmount = calculateAmountTotal(vo, List.of(OrderRecordPO.Fields.betAmount, OrderRecordPO.Fields.validAmount, OrderRecordPO.Fields.winLossAmount));
            if (totalAmount != null) {
                betOrderVO.setTotalValidAmount(Optional.ofNullable(totalAmount.getValidAmount()).orElse(BigDecimal.ZERO));
                betOrderVO.setTotalBetAmount(Optional.ofNullable(totalAmount.getBetAmount()).orElse(BigDecimal.ZERO));
                betOrderVO.setTotalWinLossAmount(totalAmount.getWinLossAmount() == null ? BigDecimal.ZERO : totalAmount.getWinLossAmount());
            } else {
                betOrderVO.setTotalValidAmount(BigDecimal.ZERO);
                betOrderVO.setTotalBetAmount(BigDecimal.ZERO);
                betOrderVO.setTotalWinLossAmount(BigDecimal.ZERO);
            }
            return ResponseVO.success(betOrderVO);
        } catch (Exception e) {
            log.error("客户端查询该会员:{} 投注记录异常", vo.getUserAccount(), e);
            return ResponseVO.fail(ResultCode.QUERY_CLIENT_ORDER_ERROR);
        }
    }

    private OrderRecordPO calculateAmountTotal(AgentUserOrderRecordReqVO vo, List<String> fields) {
        QueryWrapper<OrderRecordPO> wrapper = new QueryWrapper<>();

        if (CollUtil.isEmpty(fields)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        wrapper.in(StrUtil.toUnderlineCase(OrderRecordPO.Fields.agentId), vo.getAgentIds())
                .eq(Strings.isNotBlank(vo.getUserAccount()), StrUtil.toUnderlineCase(OrderRecordPO.Fields.userAccount), vo.getUserAccount())
                .ge(ObjUtil.isNotEmpty(vo.getStartTime()), StrUtil.toUnderlineCase(OrderRecordPO.Fields.betTime), vo.getStartTime())
                .le(ObjUtil.isNotEmpty(vo.getEndTime()), StrUtil.toUnderlineCase(OrderRecordPO.Fields.betTime), vo.getEndTime())
                .eq(Strings.isNotBlank(vo.getVenueCode()), StrUtil.toUnderlineCase(OrderRecordPO.Fields.venueCode), vo.getVenueCode());

        String queryStr = "";
        for (String field : fields) {
            queryStr += String.format("sum(%s) %s,", StrUtil.toUnderlineCase(field), field);
        }
        queryStr = queryStr.replaceAll(",$", "");
        wrapper.select(queryStr);

        return orderRecordRepository.selectOne(wrapper);

    }

    /**
     * 查询当前代理 最新五条 游戏输赢 数据
     *
     * @param param
     * @return
     */
    public ResponseVO<List<GetNewest5OrderRecordVO>> getNewest5OrderRecord(GetNewest5OrderRecordParam param) {
        String agentAccount = param.getAgentAccount();
        OrderRecordAdminResVO reqVO = new OrderRecordAdminResVO();
        reqVO.setAgentAcct(agentAccount);
        reqVO.setSiteCode(param.getSiteCode());
        reqVO.setAccountType(com.google.common.collect.Lists.newArrayList(2));
        reqVO.setOrderStatusList(com.google.common.collect.Lists.newArrayList(ClassifyEnum.SETTLED.getCode()));
        //  reqVO.setSettleBeginTime(DateUtil.beginOfMonth(DateUtil.lastMonth()).getTime());
        //  reqVO.setSettleEndTime(DateUtil.endOfMonth(new Date()).getTime());
        reqVO.setPageNumber(1);
        reqVO.setPageSize(5);
        if (!CommonConstant.PLAT_CURRENCY_CODE.equals(param.getCurrencyCode())) {
            reqVO.setCurrency(param.getCurrencyCode());
        }
        reqVO.setCurSiteCode(param.getSiteCode());
        ResponseVO<Page<OrderRecordPageRespVO>> responseVO = this.adminPageQuery(reqVO);
        if (responseVO.isOk()) {
            Page<OrderRecordPageRespVO> orderPageVO = responseVO.getData();
            List<OrderRecordPageRespVO> records = orderPageVO.getRecords();
            Map<String, BigDecimal> siteCurrencyRate = siteCurrencyInfoApi.getAllFinalRate(param.getSiteCode());
            if (CollUtil.isNotEmpty(records)) {
                List<GetNewest5OrderRecordVO> result = com.google.common.collect.Lists.newArrayList();
                for (OrderRecordPageRespVO record : records) {
                    GetNewest5OrderRecordVO recordVO = new GetNewest5OrderRecordVO();
                    recordVO.setVenueCode(record.getVenueCode());
                    recordVO.setUserAccount(record.getUserAccount());
                    recordVO.setWinLossAmount(record.getWinLossAmount());
                    recordVO.setOrderClassify(record.getOrderClassify());
                    recordVO.setSettleTime(record.getFirstSettleTime());
                    recordVO.setCurrencyCode(record.getCurrency());
                    recordVO.setVenuePlatformName(record.getVenuePlatformName());
                    // 状态 0未结算 1已结算 2已取消 3重结算
                    if (null != record.getOrderClassify()) {
                        ClassifyEnum classifyEnum = ClassifyEnum.nameOfCode(record.getOrderClassify());
                        recordVO.setOrderClassifyName(null == classifyEnum ? null : classifyEnum.getName());
                    }
                    //币种汇率转换为平台币
                    if (CommonConstant.PLAT_CURRENCY_CODE.equals(param.getCurrencyCode())) {
                        BigDecimal rate = siteCurrencyRate.get(record.getCurrency());
                        BigDecimal targetAmountWtc = AmountUtils.divide(record.getWinLossAmount(), rate);
                        recordVO.setWinLossAmount(targetAmountWtc);
                        recordVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    }
                    result.add(recordVO);
                }
                return ResponseVO.success(result);
            } else {
                return ResponseVO.success(Lists.newArrayList());
            }
        } else {
            return ResponseVO.fail(ResultCode.SELECT_LATEST_5ORDER_FAIL);
        }
    }

    public List<PlayUserBetAmountSumVO> getUserOrderAmountByAgent(PlayAgentWinLossParamVO vo) {
        return orderRecordRepository.getUserOrderAmountByAgent(vo);
    }

    public Long getDistinctByTimeSiteCodeCurrencyCode(String dateStr, String siteCode, String currencyCode) {
        // 1. 转换为当天的开始时间（00:00:00）
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        LocalDateTime startOfDay = date.atStartOfDay();  // 获取当天的00:00:00
        long startTimestamp = startOfDay.toEpochSecond(ZoneOffset.UTC);  // 转换为时间戳

        // 2. 转换为当天的结束时间（23:59:59）
        LocalDateTime endOfDay = date.atTime(23, 59, 59);  // 获取当天的23:59:59
        long endTimestamp = endOfDay.toEpochSecond(ZoneOffset.UTC);  // 转换为时间戳

        return orderRecordRepository.getDistinctByTimeSiteCodeCurrencyCode(startTimestamp, endTimestamp, siteCode, currencyCode);
    }

    public Page<VenueWinLoseRecalculateVO> venueWinLoseRecalculatePage(VenueWinLoseRecalculateReqVO vo) {
        return orderRecordRepository.venueWinLoseRecalculatePage(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<PlayUserBetAmountSumVO> getUserOrderAmountByUserId(PlayUserWinLossParamVO vo) {
        return orderRecordRepository.getUserOrderAmountByUserId(vo);
    }

    public Integer getBetUserCount(PlayAgentWinLossParamVO vo) {
        return orderRecordRepository.getBetUserCount(vo);
    }

    public Page<WinLoseRecalculateVO> winLoseRecalculatePage(WinLoseRecalculateReqVO vo) {
        return orderRecordRepository.winLoseRecalculatePage(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<WinLoseRecalculateFeelSpinVO> winLoseRecalculateFreeSpinPage(WinLoseRecalculateReqVO vo) {
        return orderRecordRepository.winLoseRecalculateFreeSpinPage(vo);
    }

    public Long getUserNewBetOrder(String userId) {
        LambdaQueryWrapper<OrderRecordPO> query = Wrappers.lambdaQuery();
        query.eq(OrderRecordPO::getUserId, userId);
        query.orderByDesc(OrderRecordPO::getCreatedTime).last("limit 0,1");
        OrderRecordPO one = this.getOne(query);
        if (one != null) {
            return one.getBetTime();
        }
        return null;
    }

    public List<BigDecimal> getUserAmountRecordByTime(String userId, Long lastWithdrawTime, Long endTime) {
        LambdaQueryWrapper<OrderRecordPO> query = Wrappers.lambdaQuery();
        ArrayList<Integer> params = new ArrayList<>();
        params.add(ClassifyEnum.SETTLED.getCode());
        params.add(ClassifyEnum.RESETTLED.getCode());
        query.ge(OrderRecordPO::getFirstSettleTime, lastWithdrawTime).le(OrderRecordPO::getFirstSettleTime, endTime).eq(OrderRecordPO::getUserId, userId).in(OrderRecordPO::getOrderClassify, params);
        query.select(OrderRecordPO::getValidAmount);

        List<Object> validAmounts = orderRecordRepository.selectObjs(query);
        return validAmounts.stream()
                .map(amount -> (BigDecimal) amount)
                .toList();
    }

    public List<OrderRecordVO> getNotSettleOrderListByUserIds(UserIdsVO vo) {
        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(OrderRecordPO::getOrderStatus, ClassifyEnum.NOT_SETTLE.getCode());
        wrapper.in(OrderRecordPO::getUserId, vo.getUserIdList());

        List<OrderRecordPO> orderRecordPOS = orderRecordEsMapper.selectList(wrapper);
        List<OrderRecordVO> orderRecordVOS = ConvertUtil.entityListToModelList(orderRecordPOS, OrderRecordVO.class);
        return orderRecordVOS;
    }


    public List<OrderRecordVO> getNotSettleOrderListByUserId(String userId) {
        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(OrderRecordPO::getUserId, userId);
        wrapper.eq(OrderRecordPO::getOrderStatus, ClassifyEnum.NOT_SETTLE.getCode());
        wrapper.eq(OrderRecordPO::getCreatedTime, DateUtils.removeHHmmssToLong(DateUtils.addDay(new Date(), -40)));
        List<OrderRecordPO> orderRecordPOS = orderRecordEsMapper.selectList(wrapper);
        List<OrderRecordVO> orderRecordVOS = ConvertUtil.entityListToModelList(orderRecordPOS, OrderRecordVO.class);
        return orderRecordVOS;
    }


    public void adapterTransNew(OrderRecordPO record) {
        Integer venueType = record.getVenueType();
        VenueTypeEnum venueTypeEnum = VenueTypeEnum.of(venueType);
        if (Objects.isNull(venueTypeEnum)) {
            return;
        }
        VenueEnum venueEnum = VenueEnum.nameOfCode(record.getVenueCode());
        if (venueEnum.equals(VenueEnum.DBFISHING)){
            return;
        }
        switch (venueTypeEnum) {
            case MARBLES -> {
                Map<String, Object> map = JSONObject.parseObject(record.getParlayInfo(), Map.class);
                String betDetails = (String) map.get("BetDetails");
                Map<String, String> data = changeMap(betDetails);
                if (map.containsKey("BetType")) {
                    record.setBetContent(MarblesBetTypeEnum.of((String) map.get("BetType"), CurrReqUtils.getLanguage()));
                    if (data.containsKey("Odds")) {
                        record.setOdds(String.valueOf(data.get("Odds")));
                    }
                }
            }
        }
    }

    private Map<String, String> changeMap(String str) {
        Map<String, String> betDataMap = new HashMap<>();

        // 按分号分割各部分
        String[] parts = str.split(";\\s*");
        for (String part : parts) {
            // 分割键值对
            String[] keyValue = part.split(":\\s*", 2);
            if (keyValue.length != 2) continue;
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();
            betDataMap.put(key, value);
        }
        return betDataMap;
    }

    public List<OrderRecordVO> getNotSettleOrderListBySiteCode(String siteCode) {

        LambdaEsQueryWrapper<OrderRecordPO> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(OrderRecordPO::getOrderStatus, ClassifyEnum.NOT_SETTLE.getCode());
        wrapper.gt(OrderRecordPO::getCreatedTime, DateUtil.offsetMonth(DateUtil.date(), -2).getTime());
        wrapper.in(OrderRecordPO::getSiteCode, siteCode);
        List<OrderRecordPO> orderRecordPOS = orderRecordEsMapper.selectList(wrapper);
        List<OrderRecordVO> orderRecordVOS = ConvertUtil.entityListToModelList(orderRecordPOS, OrderRecordVO.class);
        return orderRecordVOS;
    }

    public String getMarblesOrderRecordInfo(OrderRecordPO recordPO) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Map<String, Object> map = JSONObject.parseObject(recordPO.getParlayInfo(), Map.class);
            if (map.containsKey("ChineseGameName")) {
                String gameName = (String) map.get("ChineseGameName");
                if (ObjectUtil.isNotEmpty(gameName)) {
                    stringBuilder.append(gameName).append("\n");
                }
            }
            if (map.containsKey("GameNo")) {
                String issueNo = (String) map.get("GameNo");
                if (ObjectUtil.isNotEmpty(issueNo)) {
                    stringBuilder.append("期号:");
                    stringBuilder.append(issueNo).append("\n");
                }
            }
            if (map.containsKey("BetOn")) {
                stringBuilder.append("玩法:");
                stringBuilder.append((String) map.get("BetOn")).append("\n");
            }
            String betDetails = (String) map.get("BetDetails");
            Map<String, String> data = changeMap(betDetails);
            if (map.containsKey("BetType")) {
                String nums = (String) map.get("BetType");
                if (ObjectUtil.isNotEmpty(nums)) {
                    stringBuilder.append("下注:");
                    stringBuilder.append(nums);
                }
                if (data.containsKey("Odds")) {
                    String curOdd = String.valueOf(data.get("Odds"));
                    if (ObjectUtil.isNotEmpty(curOdd)) {
                        stringBuilder.append("@").append(curOdd).append("\n");
                    }
                }
            }
            if (data.containsKey("Result")) {
                String lotteryNum = (String) data.get("Result");
                if (ObjectUtil.isNotEmpty(lotteryNum)) {
                    stringBuilder.append("开奖结果:");
                    stringBuilder.append(lotteryNum).append("\n");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return stringBuilder.toString();
    }

    ;

}

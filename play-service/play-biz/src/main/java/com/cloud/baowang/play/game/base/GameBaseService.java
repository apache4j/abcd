package com.cloud.baowang.play.game.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.account.api.api.AccountPlayApi;
import com.cloud.baowang.account.api.enums.AccountBalanceStatusEnums;
import com.cloud.baowang.account.api.enums.AccountCoinTypeEnums;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountCoinResultVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.SiteStatusEnums;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.SiteMaintenanceVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.AccountUserCoinRequestMqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.po.SiteVenuePO;
import com.cloud.baowang.play.service.SiteVenueService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoValidRequestVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserCoinRecordApi;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY;

/**
 * 三方游戏业务默认逻辑
 */
@Component
@Slf4j
public class GameBaseService {

    @Autowired
    public VenueUserAccountConfig venueUserAccountConfig;

    // 彩票拉单线程池
    @Autowired
    public ThreadPoolTaskExecutor ltPullBetThreadPool;

    @Autowired
    protected CasinoMemberService casinoMemberService;
    @Autowired
    private UserInfoApi userInfoApi;
    @Autowired
    private GameInfoService gameInfoService;
    @Autowired
    private SiteApi siteApi;

    @Autowired
    private UserCoinRecordApi userCoinRecordApi;
    @Resource
    protected UserCoinApi userCoinApi;

    @Resource
    private SiteVenueService siteVenueService;

    @Resource
    private VenueInfoService venueInfoService;

    @Resource
    private SiteCurrencyInfoApi siteCurrencyInfoApi;

    @Resource
    private AccountPlayApi accountPlayApi;

    /**
     * 获取VIP返水配置
     *
     * @return
     */
    protected Map<String, String> getVIPRebateSingleVOMap(String venueCode) {
        return Maps.newHashMap();
    }

    /**
     * 体育和电竞有效投注额计算
     * <p>
     * 看输赢金额大于投注金额，有效投注等于投注金额
     * <p>
     * 输赢金额小于投注金额，有效投注即为输赢金额
     * <p>
     * 注单是输的，那就是投注金额为有效投注金额
     *
     * @param betAmount     投注额
     * @param winLossAmount 输赢金额
     * @return validBetAmount 有效投注额
     */
    protected BigDecimal computerValidBetAmount(BigDecimal betAmount, BigDecimal winLossAmount) {
        if (Objects.isNull(winLossAmount) || Objects.isNull(betAmount)) {
            return null;
        }
        //输赢金额小于0  validBetAmount = 投注额
        if (winLossAmount.compareTo(BigDecimal.ZERO) < 0) {
            return betAmount;
        }
        //输赢金额等于0  validBetAmount = 0
        if (winLossAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // 输赢金额大于=投注金额 validBetAmount = 投注额
        if (winLossAmount.compareTo(betAmount) >= 0) {
            return betAmount;
        }
        // 输赢金额小于投注金额 validBetAmount = 输赢金额
        if (winLossAmount.compareTo(betAmount) < 0) {
            return winLossAmount;
        }
        return betAmount;
    }

    /**
     * 电子、捕鱼、刮刮乐=电子：
     * 有效流水 = 投注额
     * 真人、体育、斗鸡、电竞、彩票 棋牌 娱乐：
     * 当输赢金额(绝对值) >= 投注金额，有效投注 = 投注金额
     * 当输赢金额（绝对值） < 投注金额，有效投注 = 输赢金额
     *
     * @param betAmount     投注金额
     * @param winLossAmount 输赢金额
     * @param venueTypeEnum 平台类型
     * @return 有效投注
     */
    protected BigDecimal computerValidBetAmount(BigDecimal betAmount, BigDecimal winLossAmount, VenueTypeEnum venueTypeEnum) {
        if (Objects.isNull(winLossAmount) || Objects.isNull(betAmount)) {
            return null;
        }
        return switch (venueTypeEnum) {
            case SPORTS, ACELT, COCKFIGHTING, ELECTRONIC_SPORTS, SH, CHESS, MARBLES -> {
                BigDecimal winAb = winLossAmount.abs();
                if (winAb.compareTo(betAmount) >= 0) {
                    yield betAmount;
                } else {
                    yield winAb;
                }
            }
            default -> {
                yield betAmount;
            }
        };
    }

    protected UserInfoVO getByUserId(String userId) {
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if (userInfoVO == null) {
            log.info("用户账号查询失败,不存在:{}", userId);
        }
        return userInfoVO;
    }

    /**
     * 生成创建会员密码
     */
    public String genVenueUserPassword() {
        return StringUtil.createCharacter(8);
    }


    // 场馆用户关联信息
    protected Map<String, CasinoMemberPO> getCasinoMemberByUsers(List<String> usernames, String venuePlatform) {
        if (CollectionUtil.isEmpty(usernames)) {
            return Maps.newHashMap();
        }
        CasinoMemberReq casinoMemberReq = CasinoMemberReq.builder()
                .venuePlatform(venuePlatform)
                .venueUserAccountList(usernames).build();
        return casinoMemberService.getCasinoMemberMap(casinoMemberReq);
    }

    protected List<UserCoinRecordVO> getUserCoinRecords(UserCoinRecordRequestVO userCoinRecordRequestVO) {
        ResponseVO<List<UserCoinRecordVO>> data = userCoinRecordApi.getUserCoinRecords(userCoinRecordRequestVO);
        if (!data.isOk()) {
            log.info("获取会员账变记录失败,{}", userCoinRecordRequestVO);
            return null;
        }
        return data.getData();
    }


    protected Long userCoinRecordPageCount(UserCoinRecordRequestVO userCoinRecordRequestVO) {
        ResponseVO<Long> data = userCoinRecordApi.userCoinRecordPageCount(userCoinRecordRequestVO);
        if (!data.isOk()) {
            log.info("获取会员账变记录失败,{}", userCoinRecordRequestVO);
        }
        return data.getData();
    }


    /**
     * 增加场馆用户前缀
     * 根据场馆来确定前缀.因为部分场馆不支持特殊字符
     *
     * @return
     */
    public String addVenueUserAccountPrefix(String userAccount, String venueCode) {
        return venueUserAccountConfig.addVenueUserAccountPrefix(userAccount, venueCode);
    }


    /**
     * 根据 商户 + 平台 查询场馆
     *
     * @param merchantNo    商户
     * @param venuePlatform 平台
     * @return 场馆
     */
    protected VenueEnum getVenueCodeByMerchantNoAndVenuePlatform(String merchantNo, String venuePlatform) {
        VenueInfoVO venueInfoVO = venueInfoService.venueInfoByPlatMerchant(venuePlatform, merchantNo);
        if (ObjectUtil.isEmpty(venueInfoVO)) {
            log.info("查询场馆信息失败,商户不存在:{},平台{}", merchantNo, venuePlatform);
            return null;
        }
        return VenueEnum.nameOfCode(venueInfoVO.getVenueCode());
    }


    protected VenueInfoVO getVenueInfoByMerchant(String merchantNo, String venuePlatform) {
        VenueInfoVO venueInfoVO = venueInfoService.venueInfoByPlatMerchant(venuePlatform, merchantNo);
        if (ObjectUtil.isEmpty(venueInfoVO)) {
            log.info("查询场馆信息失败,商户不存在:{},{}", merchantNo, venueInfoVO.getVenueCode());
        }
        return venueInfoVO;
    }

    protected VenueInfoVO getVenueInfoByMerchantKey(String merchantKey, String venuePlatform) {
        VenueInfoVO venueInfoVO = venueInfoService.venueInfoByPlatMerchant(venuePlatform, merchantKey);
        if (ObjectUtil.isEmpty(venueInfoVO)) {
            log.info("查询场馆信息失败,商户密钥不存在:{},{}", merchantKey, venueInfoVO.getVenueCode());
        }
        return venueInfoVO;
    }


    protected VenueInfoVO getVenueInfo(String venueCode, String currencyCode) {
        return venueInfoService.getAdminVenueInfoByVenueCode(venueCode, currencyCode);
    }

    protected BigDecimal checkJoinActivity(String userId, String siteCode, String venueCode) {
        CheckActivityVO checkActivityVO = new CheckActivityVO();
        checkActivityVO.setUserId(userId);
        checkActivityVO.setSiteCode(siteCode);
        checkActivityVO.setVenueCode(venueCode);
        BigDecimal venueAmount = gameInfoService.checkJoinActivity(checkActivityVO);

        //不允许下注
        if (venueAmount == null || venueAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.info("不允许下注.调用游戏校验失败:{},result:{}", checkActivityVO, venueAmount);
        }
        return venueAmount;
    }


    protected String getVenueUserAccount(String userAccount) {
        String venueUserAccount = venueUserAccountConfig.getVenueUserAccount(userAccount);
        if (venueUserAccount == null) {
            log.info("用户账号解析失败,不存在:{}", userAccount);
        }
        return venueUserAccount;
    }


    /**
     * 查询用户钱包信息
     */
    protected UserCoinWalletVO getUserCenterCoin(String userId) {
        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        if (userCoinWalletVO == null) {
            log.info("用户钱包,不存在:{}", userId);
        }
        return userCoinWalletVO;
    }

    protected BigDecimal getUserBalance(String userId) {
        UserCoinWalletVO userCoinWalletVO = userCoinApi.getUserCenterCoin(UserCoinQueryVO.builder().userId(userId).build());
        if (userCoinWalletVO == null) {
            return BigDecimal.ZERO;
        }
        return userCoinWalletVO.getTotalAmount();
    }


    /**
     * 判断场馆 跟站点是否开启 可以下注
     *
     * @return false = 可以下注,true=不可以下注
     */
    protected boolean venueMaintainClosed(String venueCode, String siteCode) {
        //增加站点维护判断
        String serverStatusKey = KEY_SERVER_MAINTAIN_SITE_KEY.concat(siteCode);
        String serverStatusInfo = RedisUtil.getValue(serverStatusKey);
        if (org.springframework.util.StringUtils.hasText(serverStatusInfo)) {
            SiteMaintenanceVO siteMaintenanceVO = JSON.parseObject(serverStatusInfo, SiteMaintenanceVO.class);
            if (Objects.equals(SiteStatusEnums.MAINTENANCE.getStatus(), siteMaintenanceVO.getSiteStatus()) ||
                    Objects.equals(SiteStatusEnums.DISABLE.getStatus(), siteMaintenanceVO.getSiteStatus())) {
                log.info("钱包账变公共逻辑 站点状态未开启 不允许下注:venueCode:{},siteCode:{}", venueCode, siteCode);
                return true;
            }
        }

        SiteVenuePO venueInfoPO = siteVenueService.getOne(Wrappers.<SiteVenuePO>lambdaQuery()
                .eq(SiteVenuePO::getVenueCode, venueCode)
                .eq(SiteVenuePO::getSiteCode, siteCode)
                .last("limit 1"));

        if (ObjectUtil.isEmpty(venueInfoPO)) {
            return true;
        }

        return !StatusEnum.OPEN.getCode().equals(venueInfoPO.getStatus());
    }


    // 用户信息
    protected Map<String, UserInfoVO> getUserInfoByUserIds(List<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        List<UserInfoVO> userInfoVOS = userInfoApi.getUserInfoByUserIds(userIds);
        return userInfoVOS.stream().collect(Collectors.toMap(UserInfoVO::getUserId, p -> p, (k1, k2) -> k2));
    }
    // 用户登录信息

    protected Map<String, UserLoginInfoVO> getLoginInfoByUserIds(List<String> userIds) {
        Map<String, UserLoginInfoVO> loginVOMap = Maps.newHashMap();
        if (CollectionUtil.isEmpty(userIds)) {
            return loginVOMap;
        }
        List<UserLoginInfoVO> loginInfoList = userInfoApi.getLatestLoginInfoByUserIds(userIds);
        if (CollUtil.isNotEmpty(loginInfoList)) {
            loginVOMap = loginInfoList.stream().collect(Collectors.toMap(UserLoginInfoVO::getUserId, p -> p, (k1, k2) -> k2));
        }
        return loginVOMap;
    }

    protected Map<String, GameInfoPO> getGameInfoByVenueCode(String venueCode) {
        List<GameInfoPO> gameInfoPOList = gameInfoService.queryGameByVenueCode(venueCode);
        return gameInfoPOList.stream().collect(Collectors.toMap(GameInfoPO::getAccessParameters, GameInfoPO -> GameInfoPO, (k1, k2) -> k2));
    }

    protected Map<String, String> getSiteNameMap() {
        List<SiteVO> siteAll = siteApi.siteInfoAllstauts().getData();
        return siteAll.stream().collect(Collectors.toMap(SiteVO::getSiteCode, SiteVO::getSiteName));
    }


    /**
     * 获取的都是派奖记录的注单
     */
    protected void processChangeRecords(List<OrderRecordVO> list, Map<String, UserInfoVO> userMap, String venueCode) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> collect = list.stream().map(OrderRecordVO::getThirdOrderId).collect(Collectors.toList());
        List<String> orderNoList = userCoinRecordApi.getOrderNoByOrders(collect);
        if (CollectionUtil.isNotEmpty(orderNoList) && list.size() == orderNoList.size()) {
            log.info("账变与注单一致，不需要补单");
            return;
        }
        // 需要补账变的注单记录
        List<OrderRecordVO> orderRecordVOList = list.stream().filter(s -> !orderNoList.contains(s.getThirdOrderId())).collect(Collectors.toList());
        log.info("注单与账变补匹配， 存在的数据包含{}", JSONObject.toJSONString(orderRecordVOList));
        for (OrderRecordVO orderRecordVO : orderRecordVOList) {
            UserInfoVO userInfoVO = userMap.get(orderRecordVO.getUserId());
            if (Objects.isNull(userInfoVO)) {
                log.error("用户信息不存在，userId:{}", orderRecordVO.getUserId());
                continue;
            }
            UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
            userCoinAddVOPayout.setOrderNo(orderRecordVO.getThirdOrderId());
            userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency());
            userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
            userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode());
            userCoinAddVOPayout.setUserId(userInfoVO.getUserId());
            // 这个地方是omg的输赢字段， 对应的场馆可以根据venueCode, 特殊处理
            if (venueCode.startsWith(VenueEnum.JILIPLUS.getVenueCode()) || venueCode.startsWith(VenueEnum.PGPLUS.getVenueCode()) ||
                    venueCode.startsWith(VenueEnum.PPPLUS.getVenueCode())) {
                userCoinAddVOPayout.setCoinValue(orderRecordVO.getPayoutAmount().abs());
            }
            userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            CoinRecordResultVO result = toUserCoinHandle(userCoinAddVOPayout);
            log.info("账变的变更记录{}", JSONObject.toJSONString(result));
        }

    }

    protected boolean venueGameMaintainClosed(String venueCode, String siteCode, String gameCode) {
        GameInfoPO gameInfoPO = gameInfoService.getGameInfoByCode(siteCode, gameCode, venueCode);
        return ObjectUtil.isEmpty(gameInfoPO) || !gameInfoPO.getStatus().equals(StatusEnum.OPEN.getCode());
    }

    protected boolean userGameLock(UserInfoVO userInfoVO) {
        String accountStatus = userInfoVO.getAccountStatus();
        if (StringUtils.isEmpty(accountStatus)) {
            return false;
        }
        return Arrays.asList(accountStatus.split(CommonConstant.COMMA)).contains(UserStatusEnum.GAME_LOCK.getCode()) ||
                Arrays.asList(accountStatus.split(CommonConstant.COMMA)).contains(UserStatusEnum.LOGIN_LOCK.getCode());
    }


    /**
     * 发送账变流量消息
     *
     * @param userCoinAddVO 账变对象
     */
    private void sendMsgFlow(UserCoinAddVO userCoinAddVO) {
        UserInfoVO userInfo = getByUserId(userCoinAddVO.getUserId());
        String siteCode = userInfo.getSiteCode();
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (ObjectUtil.isEmpty(currencyRateMap) || currencyRateMap.get(siteCode) == null) {
            log.info("账变流量消息异常:{}", siteCode);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        String venueCode = userCoinAddVO.getVenueCode();
        BigDecimal fateBig = currencyRateMap.get(siteCode);
        AccountUserCoinRequestMqVO accountUserReqVO = new AccountUserCoinRequestMqVO();
        accountUserReqVO.setUserAccount(userInfo.getUserAccount());
        accountUserReqVO.setUserId(userInfo.getUserId());
        accountUserReqVO.setSiteCode(userInfo.getSiteCode());
        accountUserReqVO.setAccountCoinType(userCoinAddVO.getAccountCoinType());
        accountUserReqVO.setBalanceType(userCoinAddVO.getBalanceType());
        accountUserReqVO.setCurrencyCode(userCoinAddVO.getCurrency());
        accountUserReqVO.setAccountStatus(userInfo.getAccountStatus());
        accountUserReqVO.setInnerOrderNo(userCoinAddVO.getOrderNo());
        accountUserReqVO.setThirdOrderNo(userCoinAddVO.getThirdOrderNo());
        accountUserReqVO.setToThirdCode(venueCode);
        accountUserReqVO.setCoinTime(userCoinAddVO.getCoinTime());
        accountUserReqVO.setCoinValue(userCoinAddVO.getCoinValue());
        accountUserReqVO.setFinalRate(fateBig);
        accountUserReqVO.setFreezeFlag(userCoinAddVO.getFreezeFlag());
        KafkaUtil.send(TopicsConstants.ACCOUNT_GAME_TOPIC, accountUserReqVO);
    }

    private BigDecimal getFinalRate(UserInfoVO userInfo) {
        if (userInfo == null) {
            log.info("账变异常,没有获取到账户信息");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(userInfo.getSiteCode());
        if (allFinalRate == null) {
            log.info("账变异常,没有获取到站点:{},汇率信息", userInfo.getSiteCode());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        BigDecimal rate = allFinalRate.get(userInfo.getMainCurrency());
        if (rate == null) {
            log.info("账变异常,没有获取到站点:{},币种:{}的汇率信息", userInfo.getSiteCode(), userInfo.getMainCurrency());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return rate;
    }

    /**
     * 根据类型调用账变方法
     *
     * @param userCoinAddVO 账变对象
     * @return 账变结果
     */
    public CoinRecordResultVO toUserCoinHandle(UserCoinAddVO userCoinAddVO) {
        Integer accountOpenFlag = RedisUtil.getValue(RedisConstants.ACCOUNT_OPEN_FLAG);
        log.info("toUserCoinHandle-accountOpenFlag: "+accountOpenFlag);
        if (ObjUtil.isEmpty(accountOpenFlag) || CommonConstant.business_zero.equals(accountOpenFlag)) {
            //全部老账变
            return userCoinApi.addCoin(userCoinAddVO);
        } else if (CommonConstant.business_one.equals(accountOpenFlag)) {
            //财务流量推送
            sendMsgFlow(userCoinAddVO);
            return userCoinApi.addCoin(userCoinAddVO);
        } else if (CommonConstant.business_two.equals(accountOpenFlag)) {
            //全部走新的账务接口
            String coinType = userCoinAddVO.getCoinType();
            WalletEnum.CoinTypeEnum coinTypeEnum = WalletEnum.CoinTypeEnum.nameOfCode(coinType);
            return switch (coinTypeEnum) {
                case GAME_BET ->//投注
                        userToBet(userCoinAddVO);
                case CANCEL_BET ->//投注取消
                        userToCancelBet(userCoinAddVO);
                case GAME_PAYOUT ->//派彩
                        userToPayOut(userCoinAddVO);
                case RECALCULATE_GAME_PAYOUT ->//重算派彩
                        userToRecalculatePayout(userCoinAddVO);
                case CANCEL_GAME_PAYOUT ->//派彩取消
                        userToCancelPayOut(userCoinAddVO);
                default -> {
                    log.info("走新的账务接口类型异常:{}",coinType);
                    throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                }
            };
        }else{
            log.info("走新的账务接口类型异常1:{}",accountOpenFlag);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }


    /**
     * 派彩取消
     */
    protected CoinRecordResultVO userToCancelPayOut(UserCoinAddVO userCoinAddVO) {
        AccountUserCoinAddReqVO req = toUserCoinAddVOCovAccountUserCoinAddReqVO(userCoinAddVO);
        AccountCoinResultVO coinResultVO = accountPlayApi.userRecalculateGamePayout(req);
        AccountBalanceStatusEnums accountResultStatus = coinResultVO.getResultStatus();
        UpdateBalanceStatusEnums resultStatus = UpdateBalanceStatusEnums.of(accountResultStatus.getCode());
        CoinRecordResultVO result = new CoinRecordResultVO();
        BeanUtils.copyProperties(coinResultVO, result);
        result.setResultStatus(resultStatus);
        log.info("userToBet:{}",coinResultVO);
        return result;
    }

    /**
     * 重算派彩
     */
    protected CoinRecordResultVO userToRecalculatePayout(UserCoinAddVO userCoinAddVO) {
        AccountUserCoinAddReqVO req = toUserCoinAddVOCovAccountUserCoinAddReqVO(userCoinAddVO);
        AccountCoinResultVO coinResultVO = accountPlayApi.userRecalculateGamePayout(req);
        AccountBalanceStatusEnums accountResultStatus = coinResultVO.getResultStatus();
        UpdateBalanceStatusEnums resultStatus = UpdateBalanceStatusEnums.of(accountResultStatus.getCode());
        CoinRecordResultVO result = new CoinRecordResultVO();
        BeanUtils.copyProperties(coinResultVO, result);
        result.setResultStatus(resultStatus);
        log.info("userToBet:{}",coinResultVO);
        return result;
    }

    /**
     * 派彩
     */
    protected CoinRecordResultVO userToPayOut(UserCoinAddVO userCoinAddVO) {
        AccountUserCoinAddReqVO req = toUserCoinAddVOCovAccountUserCoinAddReqVO(userCoinAddVO);
        AccountCoinResultVO coinResultVO = accountPlayApi.userGamePayout(req);
        AccountBalanceStatusEnums accountResultStatus = coinResultVO.getResultStatus();
        UpdateBalanceStatusEnums resultStatus = UpdateBalanceStatusEnums.of(accountResultStatus.getCode());
        CoinRecordResultVO result = new CoinRecordResultVO();
        BeanUtils.copyProperties(coinResultVO, result);
        result.setResultStatus(resultStatus);
        log.info("userToBet:{}",coinResultVO);
        return result;
    }

    /**
     * 取消下注
     */
    protected CoinRecordResultVO userToCancelBet(UserCoinAddVO userCoinAddVO) {
        AccountUserCoinAddReqVO req = toUserCoinAddVOCovAccountUserCoinAddReqVO(userCoinAddVO);
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCancelCoin(req);
        AccountBalanceStatusEnums accountResultStatus = coinResultVO.getResultStatus();
        UpdateBalanceStatusEnums resultStatus = UpdateBalanceStatusEnums.of(accountResultStatus.getCode());
        CoinRecordResultVO result = new CoinRecordResultVO();
        BeanUtils.copyProperties(coinResultVO, result);
        result.setResultStatus(resultStatus);
        log.info("userToBet:{}",coinResultVO);
        return result;
    }

    private AccountUserCoinAddReqVO toUserCoinAddVOCovAccountUserCoinAddReqVO(UserCoinAddVO userCoinAddVO) {
        UserInfoVO userInfo = getByUserId(userCoinAddVO.getUserId());
        BigDecimal finalRate = getFinalRate(userInfo);
        AccountUserCoinAddReqVO req = new AccountUserCoinAddReqVO();
        BeanUtils.copyProperties(userCoinAddVO, req);
        req.setAgentId(userInfo.getSuperAgentId());
        req.setAgentAccount(userInfo.getSuperAgentAccount());
        req.setSiteCode(userInfo.getSiteCode());
        req.setUserId(userInfo.getUserId());
        req.setUserAccount(userInfo.getUserAccount());
        req.setUserName(userInfo.getUserName());
        req.setUserLabelId(userInfo.getUserLabelId());
        req.setVipGradeCode(userInfo.getVipGradeCode());
        req.setVipRank(userInfo.getVipRank());
        req.setAccountType(userInfo.getAccountType());
        req.setRiskLevel(userInfo.getRiskLevel());
        req.setRiskLevelId(userInfo.getRiskLevelId());
        req.setCurrencyCode(userInfo.getMainCurrency());
        req.setAccountStatus(userInfo.getAccountStatus());
        req.setInnerOrderNo(userCoinAddVO.getOrderNo());
        req.setThirdOrderNo(userCoinAddVO.getThirdOrderNo());
        req.setFinalRate(finalRate);
        req.setToThirdCode(userCoinAddVO.getVenueCode());
        return req;
    }

    /**
     * 用户下注接口
     *
     * @return 下注结果
     */
    protected CoinRecordResultVO userToBet(UserCoinAddVO userCoinAddVO) {
        AccountUserCoinAddReqVO req = toUserCoinAddVOCovAccountUserCoinAddReqVO(userCoinAddVO);
        AccountCoinResultVO coinResultVO = accountPlayApi.userBetCoin(req);
        AccountBalanceStatusEnums accountResultStatus = coinResultVO.getResultStatus();
        UpdateBalanceStatusEnums resultStatus = UpdateBalanceStatusEnums.of(accountResultStatus.getCode());
        CoinRecordResultVO result = new CoinRecordResultVO();
        BeanUtils.copyProperties(coinResultVO, result);
        result.setResultStatus(resultStatus);
        log.info("userToBet:{}",coinResultVO);
        return result;
    }


    /**
     * 处理用户的投注和派彩余额变更操作。
     * <p>
     * 此方法执行两步操作：
     * 1. 记录投注（支出）金额；
     * 2. 记录派彩（收入）金额；
     * 若投注记录失败，则不会进行派彩记录。
     * </p>
     *
     * @param userInfoVO    用户信息对象，包含用户ID、主币种等
     * @param transactionId 交易订单号，投注和派彩共用同一订单号
     * @param betAmount     投注金额（支出），会取绝对值处理
     * @param payoutAmount  派彩金额（收入），会取绝对值处理
     * @return CoinRecordResultVO 返回余额变更结果对象，包含状态和结果信息
     */
    protected CoinRecordResultVO updateBalanceBetPayOut(UserInfoVO userInfoVO, String transactionId, BigDecimal betAmount, BigDecimal payoutAmount) {
        // 构建投注记录对象（支出）
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId); // 设置订单号
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency()); // 设置币种
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode()); // 设置为支出类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode()); // 设置币种类型为游戏投注
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode()); // 设置业务币种类型为游戏投注
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode()); // 设置客户币种类型为游戏投注
        userCoinAddVO.setUserId(userInfoVO.getUserId()); // 设置用户ID
        userCoinAddVO.setCoinValue(betAmount.abs()); // 投注金额（绝对值）
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)); // 设置用户信息

        // 如果投注金额大于0，才进行投注记录
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) > 0) {
            // 发起扣款（投注）操作
            CoinRecordResultVO coinRecordResultVO = toUserCoinHandle(userCoinAddVO);
            // 如果投注失败，直接返回失败结果，不进行派彩操作
            if (!coinRecordResultVO.getResultStatus().equals(UpdateBalanceStatusEnums.SUCCESS)) {
                return coinRecordResultVO;
            }
        }

        // 构建派彩记录对象（收入）
        UserCoinAddVO userCoinAddVOPayout = new UserCoinAddVO();
        userCoinAddVOPayout.setOrderNo(transactionId); // 同一订单号
        userCoinAddVOPayout.setCurrency(userInfoVO.getMainCurrency()); // 设置币种
        userCoinAddVOPayout.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode()); // 设置为收入类型
        userCoinAddVOPayout.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()); // 设置币种类型为游戏派彩
        userCoinAddVOPayout.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode()); // 设置业务币种类型为游戏派彩
        userCoinAddVOPayout.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET_PAYOUT.getCode()); // 设置客户币种类型为游戏派彩
        userCoinAddVOPayout.setUserId(userInfoVO.getUserId()); // 设置用户ID
        userCoinAddVOPayout.setCoinValue(payoutAmount.abs()); // 派彩金额（绝对值）
        userCoinAddVOPayout.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class)); // 设置用户信息

        // 发起派彩（收入）操作
        return toUserCoinHandle(userCoinAddVOPayout);
    }

    /**
     * 余额小于投注金额
     *
     * @param userAmount 用户金额
     * @param betAmount  投注金额
     */
    protected boolean compareAmount(BigDecimal userAmount, BigDecimal betAmount) {
        log.info("用户可用余额{}, 扣减的余额{}", userAmount, betAmount);
        log.info("可用和扣减比对{}", userAmount.compareTo(BigDecimal.ZERO) > 0 && userAmount.compareTo(betAmount.abs()) >= 0);
        return userAmount.compareTo(BigDecimal.ZERO) > 0 && userAmount.compareTo(betAmount.abs()) >= 0;
    }


    protected CoinRecordResultVO updateBalanceBet(UserInfoVO userInfoVO, String transactionId, BigDecimal transferAmount, String remark, String venueCode) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(transactionId);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_BET.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(transferAmount.abs());
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setVenueCode(venueCode);
        userCoinAddVO.setThirdOrderNo(transactionId);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }


    /**
     * 投注取消
     */
    protected CoinRecordResultVO updateBalanceBetCancel(UserInfoVO userInfoVO, String orderNo, BigDecimal amount, String remark, String venueCode) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        // 账变类型
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.CANCEL_BET.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
        userCoinAddVO.setAccountCoinType(AccountCoinTypeEnums.GAME_CANCEL_PAYOUT.getCode());
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setCoinValue(amount.abs());
        userCoinAddVO.setRemark(remark);
        userCoinAddVO.setThirdOrderNo(remark);
        userCoinAddVO.setVenueCode(venueCode);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        //修改余额 记录账变
        return toUserCoinHandle(userCoinAddVO);
    }


    protected UserCoinRecordVO getUserCoinRecordByRemarkNo(String remark, String userId, String balanceType) {
        return userCoinRecordApi.getUserCoinRecord(remark, userId, balanceType);
    }

    protected GameInfoPO getGameInfoByCode(String siteCode, String gameCode, String venueCode) {
        return gameInfoService.getGameInfoByCode(siteCode, gameCode, venueCode);

    }

}

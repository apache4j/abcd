package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenueJoinTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.enums.GameOneModelEnum;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.GameOneClassInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.constants.ThirdRedisLockKey;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.base.ThirdGameBusinessAdapter;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.vo.VenueMaintainVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberLoginVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import com.cloud.baowang.user.api.api.UserInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class GamePlayService {

    private final PlatformTransactionManager transactionManager;
    private final ThirdGameBusinessAdapter thirdGameBusinessAdapter;
    private final CasinoMemberService casinoMemberService;
    private final CasinoMemberLoginService casinoMemberLoginService;
    private final UserInfoApi userInfoApi;
    private final VenueUserAccountConfig venueUserAccountConfig;
    private final GameInfoService gameInfoService;
    private final VenueInfoService venueInfoService;
    private final GameServiceFactory gameServiceFactory;
    private final SiteVenueService siteVenueService;
    private final GameOneClassInfoService gameOneClassInfoService;


//    public ResponseVO loginGame(LoginVO loginVo, VenueDetailVO venueInfoVO) {
//        String siteCode = CurrentRequestUtils.getSiteCode();
//        String userId = CurrentRequestUtils.getCurrentOneId();
//        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(new UserBasicRequestVO()
//                .setUserAccount(loginVo.getUserAccount())
//                .setSiteCode(siteCode));
//
//        if(ObjectUtil.isEmpty(userInfoVO) || ObjectUtil.isEmpty(userInfoVO.getMainCurrency())){
//            log.info("用户信息异常。:{}",userInfoVO);
//            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
//        }
//
//        if(UserStatusEnum.GAME_LOCK.getCode().equals(userInfoVO.getAccountStatus())){
//            return ResponseVO.fail(ResultCode.USER_GAME_LOCKED);
//        }
//        ResponseVO responseVO = ResponseVO.fail(ResultCode.OPEN_GAME_FREQUENTLY);
//        RLock lock = RedisUtil.getLock(String.format(ThirdRedisLockKey.THIRD_LOGIN_GAME_KEY, userId));
//        TransactionStatus transactionStatus = null;
//        try {
//            boolean isLock = lock.tryLock(10, 60 * 3, TimeUnit.SECONDS);
//            if (isLock) {
//                transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
//                CasinoMemberReq casinoMemberReq = new CasinoMemberReq();
//                casinoMemberReq.setVenueCode(venueInfoVO.getVenueCode());
//                casinoMemberReq.setSiteCode(siteCode);
//                casinoMemberReq.setUserId(userId);
//                loginVo.setCurrencyCode(userInfoVO.getMainCurrency());
//                CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(casinoMemberReq);
//                responseVO = thirdGameBusinessAdapter.loginRegisterMember(loginVo, venueInfoVO, casinoMemberVO);
//                if (!responseVO.isOk()) {
//                    return responseVO;
//                }
//                //查询最后一次登录的平台
//                CasinoMemberLoginVO casinoMemberLoginVO = casinoMemberLoginService.getCasinoMemberLogin(
//                        CasinoMemberReq.builder()
//                                .venueCode(venueInfoVO.getVenueCode())
//                                .userId(userId)
//                                .siteCode(siteCode)
//                                .build());
//                // 创建后需再次查询
//                if (Objects.isNull(casinoMemberVO)) {
//                    casinoMemberVO = casinoMemberService.getCasinoMember(casinoMemberReq);
//                }
//
//                if (Objects.isNull(casinoMemberLoginVO)) {
//                    //插入最后登录记录
//                    CasinoMemberLoginVO memberLoginVO = new CasinoMemberLoginVO();
//                    memberLoginVO.setVenuePlatform(venueInfoVO.getVenuePlatform());
//                    memberLoginVO.setVenueCode(venueInfoVO.getVenueCode());
//                    memberLoginVO.setLastLoginTime(System.currentTimeMillis());
//                    memberLoginVO.setCreatedTime(System.currentTimeMillis());
//                    memberLoginVO.setUserAccount(loginVo.getUserAccount());
//                    memberLoginVO.setVenueUserAccount(casinoMemberVO.getVenueUserAccount());
//                    memberLoginVO.setSiteCode(casinoMemberVO.getSiteCode());
//                    memberLoginVO.setUserId(CurrentRequestUtils.getCurrentOneId());
//                    casinoMemberLoginService.saveLastLoginTime(memberLoginVO);
//                } else {
//                    //更新最后登录时间和平台
//                    CasinoMemberReq memberParam = new CasinoMemberReq();
//                    memberParam.setId(casinoMemberLoginVO.getId());
//                    memberParam.setVenueCode(venueInfoVO.getVenueCode());
//                    memberParam.setVenuePlatform(venueInfoVO.getVenuePlatform());
//                    memberParam.setLoginTime(System.currentTimeMillis());
//                    memberParam.setVenueUserAccount(loginVo.getUserAccount());
//                    casinoMemberLoginService.updateLastLoginTimeById(memberParam);
//                }
//            }
//        } catch (Exception e) {
//            log.error("{} 玩家登录三方游戏失败: {},error:", venueInfoVO.getVenueCode(), loginVo.getUserAccount(), e);
//            if (Objects.nonNull(transactionStatus) && !transactionStatus.isCompleted()) {
//                transactionManager.rollback(transactionStatus);
//            }
//        } finally {
//            if (Objects.nonNull(transactionStatus) && !transactionStatus.isCompleted()) {
//                transactionManager.commit(transactionStatus);
//            }
//            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//                log.info("玩家登录三方游戏释放锁");
//            }
//        }
//        return responseVO;
//    }


    public ResponseVO<VenueInfoVO> checkVenueInfo(String venueCode, String currencyCode) {

        if (!siteVenueService.getSiteVenueIdsBySiteCodeAndByVenueCode(venueCode)) {
            log.info("该站:{} 没有分配:{} 场馆的权限", CurrReqUtils.getOneId(), venueCode);
            return ResponseVO.fail(ResultCode.VENUE_IS_DISABLE);
        }


        VenueInfoVO venueDetailVO = venueInfoService.getSiteVenueInfoByVenueCode(CurrReqUtils.getSiteCode(),venueCode, currencyCode);
        if (venueDetailVO == null) {
            return ResponseVO.fail(ResultCode.VENUE_CURRENCY_NOT);
        }

        if (Objects.equals(venueDetailVO.getStatus(), StatusEnum.CLOSE.getCode())) {
            return ResponseVO.fail(ResultCode.CASINO_IS_CLOSED);
        }

        if (Objects.equals(venueDetailVO.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            return ResponseVO.fail(ResultCode.CASINO_IS_MAINTAIN, venueDetailVO);
        }
        return ResponseVO.success(venueDetailVO);
    }

    @DistributedLock(name = ThirdRedisLockKey.THIRD_LOGIN_GAME_KEY, unique = "#loginVo.userId + ':' + #loginVo.venueCode", waitTime = 3, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO loginGame(LoginVO loginVo) {

        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();


        log.info("玩家登录三方游戏:{}", loginVo);
        if (ObjectUtil.isEmpty(loginVo.getVenueCode())) {
            return ResponseVO.fail(ResultCode.VENUE_IS_DISABLE);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());

        if (ObjectUtil.isEmpty(userInfoVO) || ObjectUtil.isEmpty(userInfoVO.getMainCurrency())) {
            log.info("用户信息异常。:{}", userInfoVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        /*if(!Objects.equals(loginVo.getVenueCode(), VenueEnum.SBA.getVenueCode())
                || !Objects.equals(loginVo.getVenueCode(), VenueEnum.ACELT.getVenueCode())
                || !Objects.equals(loginVo.getVenueCode(), VenueEnum.WP_ACELT.getVenueCode())) {
            CheckActivityVO checkActivityVO = new CheckActivityVO();
            checkActivityVO.setUserId(userId);
            checkActivityVO.setSiteCode(siteCode);
            checkActivityVO.setVenueCode(loginVo.getVenueCode());
            BigDecimal bigDecimal = gameInfoService.checkJoinActivity(checkActivityVO);
            if (bigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                log.info("用户:{} 参加了活动限制:{}", userId, checkActivityVO);
                GameLoginVo vo = GameLoginVo.builder().build();
                vo.setVenueCode(loginVo.getVenueCode());
                vo.setSource(bigDecimal.toString());
                return ResponseVO.fail(ResultCode.LOGIN_GAME_NOT_WAGERING,vo);
            }
        }*/

        if (!Objects.equals(loginVo.getVenueCode(), VenueEnum.SBA.getVenueCode()) && ObjectUtil.isNotEmpty(userInfoVO.getAccountStatus())
                && !Objects.equals(loginVo.getVenueCode(), VenueEnum.ACELT.getVenueCode())) {
            List<String> accountStatusList = Arrays.asList(userInfoVO.getAccountStatus().split(","));
            if (CollectionUtil.isNotEmpty(accountStatusList) && accountStatusList.contains(UserStatusEnum.GAME_LOCK.getCode())) {
                log.info("用户信息异常。锁定:{}", userInfoVO);
                throw new BaowangDefaultException(ResultCode.USER_STATUS_PAY_LOCK);
            }
        }
        ResponseVO<VenueInfoVO> vo;
        if(loginVo.getVenueCode().equals(VenueEnum.IM.getVenueCode()) && CurrencyEnum.USDT.getCode().equals(userInfoVO.getMainCurrency())){
            log.info("IM的场馆替换{},{}", loginVo.getVenueCode(),userInfoVO.getMainCurrency());
             vo = checkVenueInfo(loginVo.getVenueCode(), CurrencyEnum.USD.getCode());
        }else {
            vo = checkVenueInfo(loginVo.getVenueCode(), userInfoVO.getMainCurrency());
        }
        if (!vo.isOk()) {
            log.info("场馆校验失败,维护中");
            return vo;
        }



        //场馆不是沙巴跟彩票 并且不传游戏CODE
        if (!loginVo.getVenueCode().equals(VenueEnum.SBA.getVenueCode())
                && !loginVo.getVenueCode().equals(VenueEnum.ACELT.getVenueCode())
                && ObjectUtil.isEmpty(loginVo.getGameCode())) {

            VenueEnum venueEnum = VenueEnum.nameOfCode(loginVo.getVenueCode());

            if(venueEnum == null){
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            VenueJoinTypeEnum venueJoinTypeEnum = venueEnum.getVenueJoinTypeEnum();

            if(!venueJoinTypeEnum.getCode().equals(VenueJoinTypeEnum.VENUE.getCode())){
                log.info("{}:登陆游戏,不是单场馆游戏不允许直接用场馆登陆:{}", CurrReqUtils.getOneId(), loginVo.getVenueCode());
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }

            List<GameOneClassInfoVO> gameOneClassInfoVOList = gameOneClassInfoService.getGameOneClassInfoList(StatusEnum.OPEN.getCode());

            if (CollectionUtil.isEmpty(gameOneClassInfoVOList)) {
                log.info("{}:登陆游戏,未查到单场馆:{} 的一级分类", CurrReqUtils.getOneId(), loginVo);
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }

            gameOneClassInfoVOList = gameOneClassInfoVOList.stream().filter(x -> x.getModel().equals(GameOneModelEnum.SIGN_VENUE.getCode())).toList();

            if (CollectionUtil.isEmpty(gameOneClassInfoVOList)) {
                log.info("没传游戏code情况下的场馆只能是一级分类里面的单游戏场馆:{}", loginVo);
                return ResponseVO.fail(ResultCode.CASINO_IS_CLOSED);
            }
        }

        if (ObjectUtil.isNotEmpty(loginVo.getGameCode()) && !"null".equals(loginVo.getGameCode())) {
            GameInfoPO gameInfoPO = gameInfoService.getGameInfoByCode(loginVo.getGameCode(), loginVo.getVenueCode());
            if (ObjectUtil.isEmpty(gameInfoPO)) {
                log.info("游戏未找到:{}", loginVo);
                return ResponseVO.fail(ResultCode.GAME_ROOM_MAINTAIN);
            }

            if (Objects.equals(gameInfoPO.getStatus(), StatusEnum.CLOSE.getCode())) {
                return ResponseVO.fail(ResultCode.CASINO_IS_CLOSED);
            }


            if (Objects.equals(gameInfoPO.getStatus(), StatusEnum.MAINTAIN.getCode())) {
                VenueMaintainVO maintainVO = new VenueMaintainVO();
                maintainVO.setVenueName(gameInfoPO.getGameName());
                maintainVO.setVenueCode(gameInfoPO.getVenueCode());
                maintainVO.setStatus(gameInfoPO.getStatus());
                maintainVO.setMaintenanceStartTime(gameInfoPO.getMaintenanceStartTime());
                maintainVO.setMaintenanceEndTime(gameInfoPO.getMaintenanceEndTime());
                //return ResponseVO.fail(ResultCode.CASINO_IS_MAINTAIN, maintainVO);
            }
        }
        loginVo.setSiteCode(CurrReqUtils.getSiteCode());
        loginVo.setUserId(CurrReqUtils.getOneId());
        log.info("准备玩家登录三方游戏:{}", loginVo);

        VenueInfoVO venueInfoVO = vo.getData();
        /*if(venueInfoVO.getVenueCode().equals(VenueEnum.IM.getVenueCode())){
            if(CollectionUtil.isNotEmpty(venueInfoVO.getCurrencyCodeList()) && venueInfoVO.getCurrencyCodeList().size() > 0
                    && venueInfoVO.getCurrencyCodeList().get(0).equals(CurrencyEnum.USDT.getCode())){
                log.info("IM的场馆替换{},{}", loginVo.getVenueCode(),venueInfoVO.getCurrencyCodeList().get(0));
                ResponseVO<VenueInfoVO> usdVo = checkVenueInfo(loginVo.getVenueCode(), CurrencyEnum.USD.getCode());
                if (!usdVo.isOk()) {
                    log.info("场馆校验失败,维护中");
                    return usdVo;
                }
                venueInfoVO = usdVo.getData();
            }
        }*/

        loginVo.setCurrencyCode(userInfoVO.getMainCurrency());

        return processLoginTransaction(loginVo, venueInfoVO, userId, siteCode);
    }


    private ResponseVO processLoginTransaction(LoginVO loginVo, VenueInfoVO venueInfoVO, String userId, String siteCode) {
        CasinoMemberReq getCasinoMember = CasinoMemberReq.builder().venueCode(venueInfoVO.getVenueCode()).siteCode(siteCode).userId(userId).build();
        CasinoMemberVO casinoMemberVO = casinoMemberService.getCasinoMember(getCasinoMember);
        ResponseVO responseVO = thirdGameBusinessAdapter.loginRegisterMember(loginVo, venueInfoVO, casinoMemberVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        // 创建后需再次查询
        if (Objects.isNull(casinoMemberVO)) {
            casinoMemberVO = casinoMemberService.getLatestCasinoMember(getCasinoMember);
        }
        updateLastLoginRecord(loginVo, venueInfoVO, casinoMemberVO, userId, siteCode);
        return responseVO;
    }


    private void updateLastLoginRecord(LoginVO loginVo, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO, String userId, String siteCode) {
        CasinoMemberLoginVO casinoMemberLoginVO = casinoMemberLoginService.getCasinoMemberLogin(CasinoMemberReq
                .builder()
                .venueCode(venueInfoVO.getVenueCode())
                .userId(userId)
                .siteCode(siteCode)
                .build()
        );

        if (Objects.isNull(casinoMemberLoginVO)) {
            if (Objects.isNull(casinoMemberVO)){
                return;
            }
            CasinoMemberLoginVO casinoMemberLogin = new CasinoMemberLoginVO();
            casinoMemberLogin.setVenuePlatform(venueInfoVO.getVenuePlatform());
            casinoMemberLogin.setVenueCode(venueInfoVO.getVenueCode());
            casinoMemberLogin.setLastLoginTime(System.currentTimeMillis());
            casinoMemberLogin.setCreatedTime(System.currentTimeMillis());
            casinoMemberLogin.setUserAccount(loginVo.getUserAccount());
            casinoMemberLogin.setVenueUserAccount(casinoMemberVO.getVenueUserAccount());
            casinoMemberLogin.setSiteCode(casinoMemberVO.getSiteCode());
            casinoMemberLogin.setUserId(userId);
            // 插入新记录
            casinoMemberLoginService.saveLastLoginTime(casinoMemberLogin);
        } else {
            CasinoMemberReq casinoMemberLogin = new CasinoMemberReq();
            casinoMemberLogin.setId(casinoMemberLoginVO.getId());
            casinoMemberLogin.setVenueCode(venueInfoVO.getVenueCode());
            casinoMemberLogin.setVenuePlatform(venueInfoVO.getVenuePlatform());
            casinoMemberLogin.setLoginTime(System.currentTimeMillis());
            casinoMemberLogin.setVenueUserAccount(loginVo.getUserAccount());
            casinoMemberLogin.setUserId(userId);
            // 更新已有记录
            casinoMemberLoginService.updateLastLoginTimeById(casinoMemberLogin);
        }
    }

    public ResponseVO<Boolean> freeGame(FreeGameVO vo, VenueInfoVO venueInfoVO) {
        String siteCode = vo.getSiteCode();
        CasinoMemberReq casinoMemberReq = new CasinoMemberReq();
        casinoMemberReq.setVenueCode(vo.getVenueCode());
        casinoMemberReq.setUserAccountList(vo.getUserAccounts());
        casinoMemberReq.setSiteCode(siteCode);
        List<CasinoMemberVO> casinoMembers = casinoMemberService.getCasinoMembers(casinoMemberReq);
        if (CollectionUtil.isEmpty(casinoMembers)) {
            return ResponseVO.success();
        }
        GameService gameService = gameServiceFactory.getGameService(venueInfoVO.getVenueCode());
        log.info("免费选择添加记录：{}", JSONObject.toJSONString(vo));
        return gameService.freeGame(vo, venueInfoVO, casinoMembers);
    }
}

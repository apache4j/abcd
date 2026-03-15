package com.cloud.baowang.play.game.base;


import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.constants.ThirdConstants;
import com.cloud.baowang.play.game.factory.GameServiceFactory;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.service.VenueInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 三方api与业务适配层
 */
@Slf4j
@Service
public class ThirdGameBusinessAdapter {

    @Autowired
    GameServiceFactory gameServiceFactory;
    @Autowired
    private CasinoMemberService casinoMemberService;
    @Autowired
    private VenueUserAccountConfig venueUserAccountConfig;

    @Autowired
    private VenueInfoService venueInfoService;

    @Autowired
    private GameLogOutService gameLogOutService;


    /**
     * 登录注册二合一
     *
     * @param loginVO 登录信息
     * @return
     */
    public ResponseVO loginRegisterMember(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        String userId = CurrReqUtils.getOneId();
        String venueUserAccount = venueUserAccountConfig.addVenueUserAccountPrefix(userId,venueDetailVO.getVenueCode());
        GameService gameService = gameServiceFactory.getGameService(venueDetailVO.getVenueCode());
        boolean registerSaveFlag = Objects.isNull(casinoMemberVO); // 注册信息是否需要保存
        // 上次注册失败
        boolean lastRegisterFail = Objects.nonNull(casinoMemberVO) && casinoMemberVO.getStatus().equals(ThirdConstants.CREATE_MEMBER_FAIL);

//
//        if(VenueEnum.SBA.getVenueCode().equals(venueDetailVO.getVenueCode())){
//            venueUserAccount = venueUserAccountConfig.addVenueUserAccountPrefix(CurrentRequestUtils.getCurrentOneId());
//        }

        if(venueDetailVO.getVenueCode().startsWith(VenueEnum.JILIPLUS.getVenueCode())||venueDetailVO.getVenueCode().startsWith(VenueEnum.PGPLUS.getVenueCode())||
                venueDetailVO.getVenueCode().startsWith(VenueEnum.PPPLUS.getVenueCode())) {
            casinoMemberVO = Objects.isNull(casinoMemberVO) && registerSaveFlag? new CasinoMemberVO():casinoMemberVO;
            String casinoPassword = gameService.genVenueUserPassword();
            casinoMemberVO.setCasinoPassword(casinoPassword);
        }
        //没有创建或者之前创建失败
        if (registerSaveFlag) {
            // 兼容OMG 登录
            casinoMemberVO = Objects.isNull(casinoMemberVO)? new CasinoMemberVO():casinoMemberVO;
            // casinoMemberVO = new CasinoMemberVO();
            String casinoUserName = loginVO.getUserAccount();
            String casinoPassword = gameService.genVenueUserPassword();
            casinoMemberVO.setVenueUserAccount(venueUserAccount);
            casinoMemberVO.setUserAccount(casinoUserName);
            casinoMemberVO.setCasinoPassword(casinoPassword);
            casinoMemberVO.setVenuePlatform(venueDetailVO.getVenuePlatform());
            casinoMemberVO.setVenueCode(venueDetailVO.getVenueCode());
            casinoMemberVO.setStatus(ThirdConstants.CREATE_MEMBER_SUCCESS);
            casinoMemberVO.setSiteCode(CurrReqUtils.getSiteCode());
            casinoMemberVO.setUserId(userId);
            casinoMemberVO.setCurrencyCode(loginVO.getCurrencyCode());
            // 设置ip
            casinoMemberVO.setIp(Strings.isNotBlank(loginVO.getIp()) ? loginVO.getIp() : CommonConstant.DEFAULT_HOST);
            // 创建包网帐号与三方游戏平台帐号的映射关系
            ResponseVO<Boolean> createVO = gameService.createMember(venueDetailVO, casinoMemberVO);
            if (!createVO.isOk()) {
                casinoMemberVO.setStatus(ThirdConstants.CREATE_MEMBER_FAIL);
                casinoMemberService.addCasinoMember(casinoMemberVO);//创建失败也需入库
                log.warn("{} 创建包网帐号与三方游戏平台帐号的映射关系失败, userAccount: {}", venueDetailVO.getVenueName(), loginVO.getUserAccount());
                return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
            }
        }
        // 设置ip
        casinoMemberVO.setIp(Strings.isNotBlank(loginVO.getIp()) ? loginVO.getIp() : CommonConstant.DEFAULT_HOST);
        // 上次注册失败后,直接登录,登录失败后继续注册
        if (lastRegisterFail) {
            ResponseVO responseVO = gameService.login(loginVO, venueDetailVO, casinoMemberVO);
            if (responseVO.isOk()) {
                casinoMemberVO.setStatus(ThirdConstants.CREATE_MEMBER_SUCCESS);
                casinoMemberService.updateCasinoMember(casinoMemberVO);
            } else {
                casinoMemberVO.setCurrencyCode(loginVO.getCurrencyCode());
                ResponseVO<Boolean> createVO = gameService.createMember(venueDetailVO, casinoMemberVO);
                if (createVO.isOk()) {
                    responseVO = gameService.login(loginVO, venueDetailVO, casinoMemberVO);
                    if (!responseVO.isOk()) {
                        log.warn("{} 登录三方游戏平台失败, userAccount: {}", venueDetailVO.getVenueName(), loginVO.getUserAccount());
                        return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
                    }
                    casinoMemberVO.setStatus(ThirdConstants.CREATE_MEMBER_SUCCESS);
                    casinoMemberService.updateCasinoMember(casinoMemberVO);

                } else {
                    log.warn("{} 注册三方游戏平台失败, userAccount: {}", venueDetailVO.getVenueName(), loginVO.getUserAccount());
                }
            }
            return responseVO;
        }


        gameLogOutService.userLogOut(userId, venueDetailVO.getVenueCode());

        ResponseVO responseVO = gameService.login(loginVO, venueDetailVO, casinoMemberVO);
        if (!responseVO.isOk()) {
            casinoMemberVO.setStatus(ThirdConstants.CREATE_MEMBER_FAIL);
            CasinoMemberPO existPO = casinoMemberService.existCasinoMember(casinoMemberVO);
            log.info("{},登录三方游戏平台失败,校验是否已经保存注册信息:{},userAccount:{}", venueDetailVO.getVenueName(), casinoMemberVO, loginVO.getUserAccount());
            if (ObjectUtil.isEmpty(existPO) && casinoMemberVO.getId() == null) {
                boolean bool = casinoMemberService.addCasinoMember(casinoMemberVO);
                log.info("{},登录三方游戏平台失败,保存注册信息:{},userAccount:{}", venueDetailVO.getVenueName(), bool, loginVO.getUserAccount());
            }
            log.warn("{} 登录三方游戏平台失败, userAccount: {}", venueDetailVO.getVenueName(), loginVO.getUserAccount());
            return ResponseVO.fail(ResultCode.CREATE_MEMBER_FAIL);
        }

        if (!venueDetailVO.getVenueCode().equals(VenueEnum.SBA.getVenueCode()) &&
                !venueDetailVO.getVenueCode().equals(VenueEnum.ACELT.getVenueCode())) {
            String key = String.format(RedisConstants.VENUE_LOGIN, userId);
            RedisUtil.setValue(key, venueDetailVO.getVenueCode());
        }

        //保存用户信息
        if (registerSaveFlag && casinoMemberVO.getId() == null) {
            casinoMemberService.addCasinoMember(casinoMemberVO);
        }
        return responseVO;
    }


}

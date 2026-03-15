package com.cloud.baowang.play.game.zf.openApi.service;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.GameLoginTypeEnums;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.zf.openApi.ZfPublicApiDriver;
import com.cloud.baowang.play.game.zf.openApi.enums.ZFLang;
import com.cloud.baowang.play.game.zf.openApi.vo.ZFRespVO;
import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.UUID;

@Slf4j
public class ZfApiService extends GameBaseService implements GameService {

    protected final static String ERROR_CODE_SUCCESS = "0";
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {
        try {
            casinoMemberService.addCasinoMember(casinoMemberVO);
            return ResponseVO.success(true);
        } catch (Exception e) {
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }

//    public static void main(String[] args) {
//        VenueDetailVO venueDetailVO = new VenueDetailVO();
//        venueDetailVO.setMerchantNo("ZF1119_OKOK");
////        venueDetailVO.setMerchantKey("ee5d625e054a752b600c51df89f2c3dea13af9fc");
////        venueDetailVO.setApiUrl("https://uat-wb-api-2.kijl788du.com/api1/");
//        venueDetailVO.setMerchantKey("c7113d420ba71bfb3130fb802ba4c5353e146a5e");
//        venueDetailVO.setApiUrl("https://uat-wb-api.tadagaming.com/api1/");
//        CasinoMemberVO casinoMemberVO = new CasinoMemberVO();
//        casinoMemberVO.setVenueUserAccount("zf_test00001");
//        LoginVO loginVO = new LoginVO();
//        loginVO.setLanguageCode("zh-CN");
//        loginVO.setGameCode("1");
//
//        ZfPublicApiDriver publicApiDriver = new ZfPublicApiDriver(venueDetailVO);
//        LinkedHashMap<String, String> param = new LinkedHashMap<>();
////        param.put("Token",casinoMemberVO.getVenueUserAccount());
////        param.put("GameId",loginVO.getGameCode());
////        param.put("Lang", ZFLang.getLangByLocalLang(loginVO.getLanguageCode()));
//        param.put("AgentId",venueDetailVO.getMerchantNo());
//        String key = publicApiDriver.genKey(param);
//
//        param.put("Key",key);
//
//        String url = venueDetailVO.getApiUrl() + "GetGameList";
//        try {
//            String response = HttpClientHandler.post(url, param);
//            JSONObject jsonObject = JSONObject.parseObject(response);
//            JSONArray data = jsonObject.getJSONArray("Data");
//            for (int i = 0; i < data.size(); i++) {
//                JSONObject datum = data.getJSONObject(i);
//                String gameId = datum.getString("GameId");
//                JSONObject name = datum.getJSONObject("name");
//                String enName = name.getString("en-US");
//                String zhName = name.getString("zh-CN");
//                System.out.printf("gameId:%s,中文：%s, 英文：%s\n",gameId,zhName,enName);
//            }

//            System.out.println(response);
//        }catch (Exception e){
//
//        }
//        System.out.println(UUID.randomUUID().toString().replaceAll("-",""));
//    }


    @Override
    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO) {

        ZfPublicApiDriver publicApiDriver = new ZfPublicApiDriver(venueDetailVO);
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("Token",casinoMemberVO.getCasinoPassword());
        param.put("GameId",loginVO.getGameCode());
        param.put("Lang", ZFLang.getLangByLocalLang(loginVO.getLanguageCode()));
        param.put("AgentId",venueDetailVO.getMerchantNo());
        String key = publicApiDriver.genKey(param);
        param.put("Key",key);

        String url = venueDetailVO.getApiUrl() + "/singleWallet/LoginWithoutRedirect";
        try {
            log.info("{} 进入游戏请求url:{},请求参数:{}",venueDetailVO.getVenueCode(),url, JSONObject.toJSONString(param));
            String response = HttpClientHandler.post(url, param);
            if (StringUtils.isEmpty(response)){
                log.error("{}-进入游戏异常,当前用户:{}",venueDetailVO.getVenueCode(),casinoMemberVO.getVenueUserAccount());
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            log.info("{}-进入游戏response={}",venueDetailVO.getVenueCode(), response);
            ZFRespVO<String> responseVO = JSONObject.parseObject(response, ZFRespVO.class);
            if(!responseVO.getErrorCode().equals(ERROR_CODE_SUCCESS)){
                log.error("{}-进入游戏异常,当前用户:{}, 异常信息：{}",casinoMemberVO.getVenueUserAccount(),response);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            GameLoginVo gameLoginVo = GameLoginVo.builder().source(responseVO.getData()).venueCode(venueDetailVO.getVenueCode()).type(GameLoginTypeEnums.URL.getType()).userAccount(loginVO.getUserAccount()).build();
            return ResponseVO.success(gameLoginVo);
        } catch (Exception e) {
            log.error("{}-进入游戏异常【{}】",venueDetailVO.getVenueCode(), casinoMemberVO.getVenueUserAccount(), e);
        }
        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);

    }


    @Override
    public String genVenueUserPassword(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    @Override
    public ResponseVO<?> getBetRecordList(VenueInfoVO venueDetailVO, VenuePullParamVO venuePullParamVO) {
        return null;
    }

    /**
     * 踢线
     */
    @Override
    public ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount){
        ZfPublicApiDriver publicApiDriver = new ZfPublicApiDriver(venueDetailVO);
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("Account", venueUserAccount);
        String key = publicApiDriver.genKey(param);
        param.put("Key",key);

        String url = venueDetailVO.getApiUrl() + "/api/KickMember";
        try {
            log.info("{} 用户踢线url:{},请求参数:{}",venueDetailVO.getVenueCode(),url, JSONObject.toJSONString(param));
            String response = HttpClientHandler.post(url, param);
            if (StringUtils.isEmpty(response)){
                log.error("{}-用户踢线,当前用户:{}",venueDetailVO.getVenueCode(),venueUserAccount);
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
            }
            log.info("{}-用户踢线response={}",venueDetailVO.getVenueCode(), response);

        } catch (Exception e) {
            log.error("{}-用户踢线【{}】",venueDetailVO.getVenueCode(), venueUserAccount, e);
        }
        return ResponseVO.success(true);
    }
}

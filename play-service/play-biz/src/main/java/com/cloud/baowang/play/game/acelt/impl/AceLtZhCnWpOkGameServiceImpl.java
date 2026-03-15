//package com.cloud.baowang.play.game.acelt.impl;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.play.api.constants.VenueCodeConstants;
//import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
//import com.cloud.baowang.play.api.vo.third.LoginVO;
//import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
//import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
//import com.cloud.baowang.play.constants.ServiceType;
//import com.cloud.baowang.play.game.base.GameBaseService;
//import com.cloud.baowang.play.game.base.GameService;
//import com.cloud.baowang.play.vo.GameLoginVo;
//import com.cloud.baowang.play.vo.VenuePullParamVO;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Slf4j
//@Service(ServiceType.GAME_THIRD_API_SERVICE + VenueCodeConstants.WP_ACELT_ZHCN)
//@AllArgsConstructor
//public class AceLtZhCnWpOkGameServiceImpl extends GameBaseService implements GameService {
//
//    private final AceGameServiceImpl aceGameService;
//
//    @Override
//    public ResponseVO<Boolean> createMember(VenueInfoVO venueInfoRspVO, CasinoMemberVO casinoMemberVO) {
//        return aceGameService.createMember(venueInfoRspVO, casinoMemberVO);
//    }
//
//    @Override
//    public ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
//        return aceGameService.login(loginVO, venueInfoVO, casinoMemberVO);
//    }
//
//    @Override
//    public ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO) {
//        return aceGameService.getBetRecordList(venueInfoVO, venuePullParamVO);
//    }
//
//    public ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
//        return aceGameService.queryGameList(gameCategoryCodes, gameCodes, venueInfoVO);
//    }
//
//    public List<ShDeskInfoVO> queryGameList() {
//        return aceGameService.queryGameList();
//    }
//
//
//
//
//}

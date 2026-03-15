package com.cloud.baowang.play.game.base;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordVenueClientReqVO;
import com.cloud.baowang.play.api.vo.order.client.OrderRecordClientRespVO;
import com.cloud.baowang.play.api.vo.third.FreeGameVO;
import com.cloud.baowang.play.api.vo.third.LoginVO;
import com.cloud.baowang.play.api.vo.venue.ShDeskInfoVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.vo.ThirdGameInfoVO;
import com.cloud.baowang.play.vo.VenuePullParamVO;
import com.cloud.baowang.play.vo.GameLoginVo;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public interface GameService {

    /**
     * 创建玩家
     *
     * @param venueDetailVO
     * @param casinoMemberVO
     * @return
     */
    ResponseVO<Boolean> createMember(VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO);

    /**
     * 登入
     *
     * @param loginVO
     * @param venueDetailVO
     * @param casinoMemberVO
     * @return
     */
    ResponseVO<GameLoginVo> login(LoginVO loginVO, VenueInfoVO venueDetailVO, CasinoMemberVO casinoMemberVO);


    /**
     * 退出登入
     *
     * @param venueDetailVO
     * @param venueUserAccount
     * @return
     */
    default ResponseVO<Boolean> logOut(VenueInfoVO venueDetailVO, String venueUserAccount){
        return ResponseVO.success(true);
    }

    /**
     * 生成创建会员密码 各场馆密码
     */
    String genVenueUserPassword();


    /**
     * 注单拉取
     *
     * @param venueInfoVO
     * @param venuePullParamVO
     * @return
     */
    ResponseVO<?> getBetRecordList(VenueInfoVO venueInfoVO, VenuePullParamVO venuePullParamVO);

    /**
     * 下单时同步MQ注单处理
     *
     * @param orderRecordMqVOList
     * @return
     */
    default ResponseVO<?> orderListParse(List<OrderRecordMqVO> orderRecordMqVOList) {
        return ResponseVO.success(true);
    }


    /**
     * 客户端注单查询
     */
    default ResponseVO<OrderRecordClientRespVO> orderClientQuery(OrderRecordVenueClientReqVO reqVO, VenueInfoVO venueInfoVO, CasinoMemberVO casinoMemberVO) {
        return ResponseVO.success(new OrderRecordClientRespVO());
    }

    /**
     * 游戏免费旋转
     *
     * @return
     */
    default ResponseVO<Boolean> freeGame(FreeGameVO freeGameVO, VenueInfoVO venueInfoVO, List<CasinoMemberVO> casinoMembers) {
        return ResponseVO.success(true);
    }

    /**
     * 获取三方游戏详情配置
     * @param gameCategoryCodes 彩种
     * @param gameCodes 彩票游戏Code
     * @param venueInfoVO 场馆
     * @return
     */
    default ResponseVO<List<JSONObject>> queryGameList(List<String> gameCategoryCodes, List<String> gameCodes, VenueInfoVO venueInfoVO) {
        return ResponseVO.success(new ArrayList<>());
    }


    /**
     * 获取三方游戏详情配置
     */
    default List<ShDeskInfoVO> queryGameList() {
        return null;
    }

    /**
     * 游戏信息-获取最新游戏配置信息
     * @return
     */
    default List<ThirdGameInfoVO> gameInfo(VenueInfoVO venueInfoVO) {
        return Lists.newArrayList();
    }


}

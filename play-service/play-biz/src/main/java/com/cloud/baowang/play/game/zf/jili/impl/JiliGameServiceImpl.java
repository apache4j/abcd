package com.cloud.baowang.play.game.zf.jili.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.mq.OrderRecordMqVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.constants.ServiceType;
import com.cloud.baowang.play.game.zf.openApi.service.ZfApiService;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.po.GameInfoPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.play.service.GameOneClassInfoService;
import com.cloud.baowang.play.service.OrderRecordProcessService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service(ServiceType.GAME_THIRD_API_SERVICE + VenuePlatformConstants.JILI)
public class JiliGameServiceImpl extends ZfApiService {

    private final OrderRecordProcessService orderRecordProcessService;
    private final OrderRecordService orderRecordService;
    private final GameOneClassInfoService gameOneClassInfoService;

    private final VenueInfoService venueInfoService;

    @Override
    public ResponseVO<?> orderListParse(List<OrderRecordMqVO> orderRecordMqVOList) {
        if (CollectionUtil.isEmpty(orderRecordMqVOList)) {
            return ResponseVO.success();
        }
        Map<String, GameInfoPO> paramToGameInfo = getGameInfoByVenueCode(VenuePlatformConstants.JILI);

        // 场馆用户关联信息
        List<String> thirdUserName = orderRecordMqVOList.stream().map(OrderRecordMqVO::getCasinoUserName).distinct().toList();
        Map<String, CasinoMemberPO> casinoMemberMap = super.getCasinoMemberByUsers(thirdUserName, orderRecordMqVOList.get(0).getVenuePlatform());
        if (MapUtil.isEmpty(casinoMemberMap)) {
            log.info("{} 未找到用户信息", orderRecordMqVOList.get(0).getVenuePlatform());
            return null;
        }
        // 用户信息
        List<String> userIds = casinoMemberMap.values().stream().map(CasinoMemberPO::getUserId).toList();
        Map<String, UserInfoVO> userMap = super.getUserInfoByUserIds(userIds);
        if (CollUtil.isEmpty(userMap)) {
            log.info("JILI游戏用户账号不存在{}", userIds);
            return null;
        }
        List<String> orderIdList = Optional.of(orderRecordMqVOList).orElse(Lists.newArrayList()).stream()
                .map(OrderRecordMqVO::getThirdOrderId).collect(Collectors.toList());
        List<OrderRecordPO> orderRecordPOList = orderRecordService.list(Wrappers.<OrderRecordPO>lambdaQuery()
                .select(OrderRecordPO::getOrderId,OrderRecordPO::getThirdOrderId, OrderRecordPO::getBetAmount, OrderRecordPO::getBetTime)
                .in(OrderRecordPO::getThirdOrderId, orderIdList)
                .eq(OrderRecordPO::getVenueCode,VenueEnum.JILI.getVenueCode()));
        Map<String, OrderRecordPO> orderRecordPOMap = orderRecordPOList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, e -> e));

        // 用户登录信息
        Map<String, UserLoginInfoVO> loginVOMap = super.getLoginInfoByUserIds(userIds);
        // 场馆游戏配置
        Map<String, GameInfoPO> gameInfoMap = super.getGameInfoByVenueCode(VenuePlatformConstants.JILI);
        if (CollUtil.isEmpty(gameInfoMap)) {
            log.error("JILI游戏列表未配置");
            return null;
        }
        Integer venueType = venueInfoService.getVenueTypeByCode(VenueEnum.JILI.getVenueCode());
        Map<String, String> siteNameMap = getSiteNameMap();
        List<OrderRecordVO> orderRecordVOS = Lists.newArrayList();
        for (OrderRecordMqVO orderRecordMqVO : orderRecordMqVOList) {
            OrderRecordVO orderRecord = BeanUtil.toBean(orderRecordMqVO, OrderRecordVO.class);

            CasinoMemberPO casinoMemberVO = casinoMemberMap.get(orderRecordMqVO.getCasinoUserName());
            if (casinoMemberVO == null) {
                log.info("{} 三方关联账号 {} 不存在", orderRecordMqVO.getVenueCode(), orderRecordMqVO.getCasinoUserName());
                continue;
            }
            UserInfoVO userInfoVO = userMap.get(casinoMemberVO.getUserId());
            if (userInfoVO == null) {
                log.info("{} 用户账号{}不存在", orderRecordMqVO.getVenueCode(), casinoMemberVO.getUserId());
                continue;
            }

            OrderRecordPO orderRecordPO = orderRecordPOMap.get(orderRecordMqVO.getThirdOrderId());
            if (orderRecordPO != null && (orderRecordMqVO.getBetAmount() == null || orderRecordMqVO.getBetAmount().compareTo(BigDecimal.ZERO) == 0)) {
                orderRecord.setBetAmount(orderRecordPO.getBetAmount());
            }
            UserLoginInfoVO userLoginInfoVO = Optional.ofNullable(loginVOMap.get(userInfoVO.getUserId())).orElse(new UserLoginInfoVO());
            orderRecord.setUserAccount(userInfoVO.getUserAccount());
            orderRecord.setUserId(userInfoVO.getUserId());
            orderRecord.setUserName(userInfoVO.getUserName());
            orderRecord.setAccountType(Integer.valueOf(userInfoVO.getAccountType()));
            orderRecord.setAgentId(userInfoVO.getSuperAgentId());
            orderRecord.setAgentAcct(userInfoVO.getSuperAgentAccount());
            orderRecord.setSuperAgentName(userInfoVO.getSuperAgentName());
            orderRecord.setVenueType(venueType);
            orderRecord.setVipGradeCode(userInfoVO.getVipGradeCode());
            orderRecord.setVipRank(userInfoVO.getVipRank());
            orderRecord.setBetIp(userLoginInfoVO.getIp());
            orderRecord.setSiteCode(userInfoVO.getSiteCode());
            orderRecord.setSiteName(siteNameMap.get(orderRecord.getSiteCode()));
            if (userLoginInfoVO.getLoginTerminal() != null) {
                orderRecord.setDeviceType(Integer.valueOf(userLoginInfoVO.getLoginTerminal()));
            }
            BigDecimal validBetAmount = computerValidBetAmount(orderRecord.getBetAmount(), orderRecord.getWinLossAmount(), VenueTypeEnum.ELECTRONICS);
            orderRecord.setValidAmount(validBetAmount);
            String gameCode = orderRecord.getGameCode();
            GameInfoPO gameInfoPO = paramToGameInfo.get(gameCode);
            if (gameInfoPO != null) {
                orderRecord.setGameId(gameInfoPO.getGameId());
                orderRecord.setGameName(gameInfoPO.getGameI18nCode());
            }
            orderRecord.setThirdGameCode(gameCode);
            orderRecord.setOrderId(OrderUtil.getGameNo());
            if(CurrencyEnum.KVND.getCode().equalsIgnoreCase(orderRecord.getCurrency())){
                orderRecord.setCurrency(orderRecord.getCurrency().toUpperCase());
            }
            orderRecordVOS.add(orderRecord);
        }
        orderRecordProcessService.orderProcess(orderRecordVOS);
        return null;
    }

}

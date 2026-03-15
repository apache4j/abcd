package com.cloud.baowang.play.api.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.SystemParamVO;
import com.cloud.baowang.play.api.vo.user.PlayUserDataVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserDataVO;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.AgentGameRecordOrderEnum;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.PlayDateEnum;
import com.cloud.baowang.play.api.vo.third.CheckActivityVO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.repositories.VenueInfoRepository;
import com.cloud.baowang.play.service.GameInfoService;
import com.cloud.baowang.play.service.OrderRecordService;
import com.cloud.baowang.play.service.PlayServiceService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/17 19:37
 * @Version: V1.0
 **/
@Slf4j
@RestController
@AllArgsConstructor
public class PlayServiceApiImpl implements PlayServiceApi {

    private final OrderRecordService orderRecordService;

    private final PlayServiceService playServiceService;

    private final VenueInfoRepository venueInfoRepository;
    private final GameInfoService gameInfoService;

    @Override
    public PlayUserDataVO getUserDataDetail(String userAccount, String gameId) {
        return playServiceService.getUserDataDetail(userAccount,gameId);
    }


    @Override
    public Map<String, Object> agentGameSelect() {
        Map<String, Object> result = Maps.newHashMap();
        LambdaQueryWrapper<VenueInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VenueInfoPO::getStatus, BigDecimal.ONE.intValue());
        List<VenueInfoPO> list = venueInfoRepository.selectList(queryWrapper);
       list = list.stream()
                .filter(Objects::nonNull)  // 过滤 null 值
                .collect(Collectors.collectingAndThen(
                        //可能会存在相同的场馆code,但是币种不一样,这里做一下去重,保留一个场馆code就行
                        Collectors.toMap(VenueInfoPO::getVenueCode, v -> v, (existing, replacement) -> existing),
                        map -> new ArrayList<>(map.values())
                ));
        List<SystemParamVO> venueLists = Lists.newArrayList();
        list.forEach(obj -> venueLists.add(SystemParamVO.builder().code(String.valueOf(obj.getVenueCode()))
                .value(obj.getVenueName()).build()));
        result.put(CommonConstant.VENUE_CODE, venueLists);
        List<SystemParamVO> orderStatusList = List.of(SystemParamVO.builder().type(ClassifyEnum.NOT_SETTLE
                        .getCode().toString()).code(ClassifyEnum.NOT_SETTLE.getCode().toString())
                .value(ClassifyEnum.NOT_SETTLE.getName()).build(), SystemParamVO.builder().type(ClassifyEnum.SETTLED
                        .getCode().toString()).code(ClassifyEnum.SETTLED.getCode().toString())
                .value(ClassifyEnum.SETTLED.getName()).build(), SystemParamVO.builder().type(ClassifyEnum.CANCEL
                        .getCode().toString()).code(ClassifyEnum.CANCEL.getCode().toString())
                .value(ClassifyEnum.CANCEL.getName()).build());
        result.put(CommonConstant.ORDER_STATUS, orderStatusList);

        List<PlayDateEnum> PlayDateEnums = List.of(PlayDateEnum.TODAY, PlayDateEnum.YESTERDAY, PlayDateEnum.SEVEN_DAY, PlayDateEnum.CUSTOMIZE);
        List<SystemParamVO> agentDateList = Lists.newArrayList();
        for (PlayDateEnum dateEnum : PlayDateEnums) {
            agentDateList.add(SystemParamVO.builder().code(String.valueOf(dateEnum.getCode()))
                    .value(dateEnum.getName()).build());
        }
        result.put(CommonConstant.AGENT_DATE, agentDateList);

        List<SystemParamVO> agentOrderList = Lists.newArrayList();
        for (AgentGameRecordOrderEnum dateEnum : AgentGameRecordOrderEnum.values()) {
            agentOrderList.add(SystemParamVO.builder().code(String.valueOf(dateEnum.getCode()))
                    .value(dateEnum.getName()).build());
        }
        result.put("agent_order", agentOrderList);
        return result;
    }

    @Override
    public BigDecimal checkJoinActivity(CheckActivityVO checkActivityVO) {
        return gameInfoService.checkJoinActivity(checkActivityVO);
    }
}

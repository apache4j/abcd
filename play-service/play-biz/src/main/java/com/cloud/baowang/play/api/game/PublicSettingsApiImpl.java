package com.cloud.baowang.play.api.game;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.game.PublicSettingsApi;
import com.cloud.baowang.play.api.vo.game.*;
import com.cloud.baowang.play.po.SportFollowPO;
import com.cloud.baowang.play.service.PublicSettingsService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Service
@Slf4j
public class PublicSettingsApiImpl implements PublicSettingsApi {


    private final PublicSettingsService publicSettingsService;


    /**
     * 新增关注赛事
     */
    @Override
    public ResponseVO<Boolean> saveFollow(SportFollowReq vo, String userId) {
        return ResponseVO.success(publicSettingsService.saveSportFollow(vo, userId));
    }

    @Override
    public ResponseVO<Boolean> unFollow(SportUnFollowReq vo, String userId) {
        return ResponseVO.success(publicSettingsService.unFollow(vo, userId));
    }

    @Override
    public ResponseVO<Boolean> unFollowList(SportUnFollowListReq vo, String userId) {
        return ResponseVO.success(publicSettingsService.unFollowList(vo, userId));
    }

    /**
     * 新增赔率
     *
     * @param vo
     * @param userId
     * @return
     */
    @Override
    public ResponseVO<Boolean> saveSetting(PublicSettingsReq vo, String userId) {
        return ResponseVO.success(publicSettingsService.save(vo, userId));
    }


    @Override
    public List<SportFollowVO> getSportsFollowList(SportFollowReq req, String userId) {
        List<SportFollowPO> list = publicSettingsService.getSportsFollowList(req, userId);
        return list.stream().map(x -> {
            SportFollowVO vo = SportFollowVO.builder().build();
            BeanUtils.copyProperties(x, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SportFollowDetailVO> getSportsFollowMap(String userId) {
        List<SportFollowVO> list = getSportsFollowList(SportFollowReq.builder().build(), userId);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }

        List<SportFollowDetailVO> resultList = Lists.newArrayList();

        Map<String, List<SportFollowVO>> maps = list.stream().collect(Collectors.groupingBy(SportFollowVO::getType));

        for (Map.Entry<String, List<SportFollowVO>> item : maps.entrySet()) {
            resultList.add(SportFollowDetailVO.builder().list(item.getValue()).type(item.getKey()).build());
        }

        return resultList;
    }


    @Override
    public List<PublicSettingsVO> getPublicSetting(PublicSettingsReq publicSettingsReq, String userId) {

        return publicSettingsService.getPublicSettingsVO(publicSettingsReq, userId);
    }
}

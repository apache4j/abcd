package com.cloud.baowang.play.service;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.play.api.enums.PublicSettingTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.sb.SBPankouType;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.vo.game.*;
import com.cloud.baowang.play.po.PublicSettingsPO;
import com.cloud.baowang.play.po.SportFollowPO;
import com.cloud.baowang.play.repositories.PublicSettingsRepository;

import com.cloud.baowang.play.repositories.SportFollowRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PublicSettingsService {

    private final PublicSettingsRepository publicSettingsRepository;

    private final SportFollowRepository sportFollowRepository;


    @Transactional(rollbackFor = Exception.class)
    public boolean saveSportFollow(SportFollowReq req, String userId) {
        String siteCode = CurrReqUtils.getSiteCode();
        SportFollowPO po = SportFollowPO.builder().build();
        BeanUtils.copyProperties(req, po);
        po.setType(req.getType().toString());
        po.setSiteCode(siteCode);
        po.setUserId(userId);
        po.setSportType(null);

        if (SBPankouType.EVENT_SPORT_ID.getCode().equals(req.getType()) ||
                SBPankouType.CHAMPION_SPORT_ID.getCode().equals(req.getType())) {
            if (ObjectUtil.isEmpty(req.getSportType())) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }


        LambdaQueryWrapper<SportFollowPO> lambdaQueryWrapper = Wrappers.lambdaQuery(SportFollowPO.class)
                .eq(SportFollowPO::getUserId, userId)
                .eq(SportFollowPO::getThirdId, req.getThirdId())
                .eq(SportFollowPO::getSiteCode, siteCode)
                .eq(SportFollowPO::getType, req.getType());


        LambdaQueryWrapper<SportFollowPO> countWrapper = Wrappers.
                lambdaQuery(SportFollowPO.class)
                .eq(SportFollowPO::getUserId, userId)
                .eq(SportFollowPO::getSiteCode, siteCode)
                .eq(SportFollowPO::getType, req.getType());

        //皮肤2一定会传球类,如果传了球类则查询逻辑增加球类条件
        if (ObjectUtil.isNotEmpty(req.getSportType())) {
            lambdaQueryWrapper.eq(SportFollowPO::getSportType, req.getSportType());
            countWrapper.eq(SportFollowPO::getSportType, req.getSportType());
            po.setSportType(req.getSportType());
        }


        //相同的不存
        if (sportFollowRepository.selectCount(lambdaQueryWrapper) > 0) {
            return true;
        }

        //总条数 》10 则删除最旧的一条数据,不包括 champion_type
        Long count = sportFollowRepository.selectCount(countWrapper);

        if (count >= 10) {
            SportFollowPO sportFollowPO = sportFollowRepository.selectOne(countWrapper
                    .orderByAsc(SportFollowPO::getId)
                    .last(" limit 1 "));
            sportFollowRepository.deleteById(sportFollowPO.getId());
        }
        po.setCreatedTime(System.currentTimeMillis());
        if (sportFollowRepository.insert(po) <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean unFollow(SportUnFollowReq req, String userId) {
        if (CollectionUtils.isNotEmpty(req.getThirdId())) {
            sportFollowRepository.delete(Wrappers.lambdaQuery(SportFollowPO.class)
                    .in(SportFollowPO::getThirdId, req.getThirdId())
                    .eq(SportFollowPO::getUserId, userId)
                    .in(SportFollowPO::getType, List.of(SBPankouType.CHAMPION.getCode(), SBPankouType.EVENT.getCode())));
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean unFollowList(SportUnFollowListReq req, String userId) {
        for (SportFollowReq item : req.getThirdId()) {
            LambdaQueryWrapper<SportFollowPO> lambdaQueryWrapper = Wrappers.lambdaQuery(SportFollowPO.class)
                    .eq(SportFollowPO::getThirdId, item.getThirdId())
                    .eq(SportFollowPO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(SportFollowPO::getUserId, userId)
                    .eq(SportFollowPO::getType, item.getType());

            if (SBPankouType.EVENT_SPORT_ID.getCode().equals(item.getType())||
                    SBPankouType.CHAMPION_SPORT_ID.getCode().equals(item.getType())) {
                if (ObjectUtil.isEmpty(item.getSportType())) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                lambdaQueryWrapper.eq(SportFollowPO::getSportType, item.getSportType());
            }
            sportFollowRepository.delete(lambdaQueryWrapper);
        }
        return true;
    }


    /**
     * 查询客户端公共配置
     */
    public List<SportFollowPO> getSportsFollowList(SportFollowReq req, String userAccount) {
        LambdaQueryWrapper<SportFollowPO> wrapper = Wrappers.lambdaQuery(SportFollowPO.class)
                .eq(SportFollowPO::getUserId, userAccount)
                .eq(SportFollowPO::getSiteCode, CurrReqUtils.getSiteCode());

        if (ObjectUtil.isNotEmpty(req.getType())) {
            wrapper.eq(SportFollowPO::getType, req.getType());
        }

        if (!StringUtils.isBlank(req.getThirdId())) {
            wrapper.eq(SportFollowPO::getThirdId, req.getThirdId());
        }

        return sportFollowRepository.selectList(wrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean save(PublicSettingsReq publicSettingsReq, String userId) {

        String newValue = publicSettingsReq.getValue();
        String type = publicSettingsReq.getType();


        if (ObjectUtil.isEmpty(PublicSettingTypeEnum.nameOfCode(type))) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        PublicSettingsPO po = PublicSettingsPO.builder()
                .value(newValue)
                .userId(userId)
                .type(type)
                .build();

        //已存在则代表重复添加
        if (publicSettingsRepository.selectCount(Wrappers.lambdaQuery(PublicSettingsPO.class)
                .eq(PublicSettingsPO::getType, type)
                .eq(PublicSettingsPO::getUserId, userId)
                .eq(PublicSettingsPO::getValue, newValue)) > 0) {
            return Boolean.TRUE;
        }

        if (type.equals(PublicSettingTypeEnum.ODDS.getCode())) {
            publicSettingsRepository.delete(Wrappers.lambdaQuery(PublicSettingsPO.class)
                    .eq(PublicSettingsPO::getType, type)
                    .eq(PublicSettingsPO::getUserId, userId));
        }
        po.setValue(newValue);
        if (publicSettingsRepository.insert(po) <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return Boolean.TRUE;
    }


    public List<PublicSettingsVO> getPublicSettingsVO(PublicSettingsReq req, String userId) {
        List<PublicSettingsVO> resultList = Lists.newArrayList();

        List<PublicSettingsPO> list = publicSettingsRepository.selectList(Wrappers.lambdaQuery(PublicSettingsPO.class)
                .eq(PublicSettingsPO::getUserId, userId)
                .eq(PublicSettingsPO::getType, req.getType()));

        if (CollectionUtils.isEmpty(list)) {
            return resultList;
        }

        for (PublicSettingsPO item : list) {
            PublicSettingsVO vo = PublicSettingsVO.builder().build();
            BeanUtils.copyProperties(item, vo);
            resultList.add(vo);
        }


        return resultList;
    }


}

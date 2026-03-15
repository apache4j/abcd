package com.cloud.baowang.user.api;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserAvatarConfigApi;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddSortVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigPageQueryVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.user.service.SiteUserAvatarConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
public class SiteUserAvatarConfigApiImpl implements SiteUserAvatarConfigApi {
    private final SiteUserAvatarConfigService configService;

    @Override
    public ResponseVO<Page<SiteUserAvatarConfigRespVO>> pageQuery(SiteUserAvatarConfigPageQueryVO queryVO) {
        return configService.pageQuery(queryVO);
    }

    @Override
    public ResponseVO<Boolean> addConfig(SiteUserAvatarConfigAddVO addVO) {
        return configService.addConfig(addVO);
    }

    @Override
    public ResponseVO<Boolean> updConfig(SiteUserAvatarConfigAddVO addVO) {
        return configService.updConfig(addVO);
    }

    @Override
    public ResponseVO<Boolean> enableOrDisAble(SiteUserAvatarConfigAddVO addVO) {
        return configService.enableOrDisAble(addVO);
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return configService.del(id);
    }

    @Override
    public ResponseVO<List<SiteUserAvatarConfigRespVO>> getListBySiteCode(String siteCode) {
        ResponseVO<List<SiteUserAvatarConfigRespVO>> listRO = configService.getListBySiteCode(siteCode);
        List<SiteUserAvatarConfigRespVO> data = listRO.getData();
        if (CollectionUtil.isNotEmpty(data)){
            listRO.setData(data.stream().sorted(Comparator.comparing(SiteUserAvatarConfigRespVO::getSort)).limit(5).collect(Collectors.toList()));
        }
        return listRO;
    }

    @Override
    public ResponseVO<SiteUserAvatarConfigRespVO> getAvatarConfigByTXIdSiteCode(String siteCode, String avatarId) {
        return configService.getAvatarConfigByTXIdSiteCode(siteCode,avatarId);
    }

    @Override
    public SiteUserAvatarConfigRespVO getRandomUserAvatar(String siteCode) {
        return configService.getRandomUserAvatar(siteCode);
    }

    @Override
    public ResponseVO<Boolean> addSort(List<SiteUserAvatarConfigAddSortVO> addSortVOS) {
        return configService.addSort(addSortVOS);
    }

    @Override
    public ResponseVO<List<SiteUserAvatarConfigAddSortVO>> getSortList(String siteCode) {
        return configService.getSortList(siteCode);
    }
}

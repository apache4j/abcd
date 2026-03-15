package com.cloud.baowang.system.service.site.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.cloud.baowang.system.api.vo.site.agreement.MediaInfo;
import com.cloud.baowang.system.po.site.config.media.SiteMediaInfoPO;
import com.cloud.baowang.system.repositories.site.agreement.SiteMediaInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SiteMediaInfoService extends ServiceImpl<SiteMediaInfoRepository, SiteMediaInfoPO> {

    private final MinioFileService fileService;


    public boolean addMediaInfo(List<MediaInfo> reqVO) {
        List<SiteMediaInfoPO> list = new ArrayList<>();
        reqVO.forEach(item ->{
            SiteMediaInfoPO po = new SiteMediaInfoPO();
            BeanUtils.copyProperties(item, po);
            po.setUpdater(CurrReqUtils.getAccount());
            po.setUpdatedTime(System.currentTimeMillis());
            po.setSiteCode(CurrReqUtils.getSiteCode());
        });
        return this.saveBatch(list);

    }


    public List<MediaInfo> getMediaInfo() {
        LambdaQueryWrapper<SiteMediaInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteMediaInfoPO::getSiteCode, CurrReqUtils.getAccount());
        List<SiteMediaInfoPO> mediaInfoPOS = this.baseMapper.selectList(queryWrapper);
        List<MediaInfo> result = new ArrayList<>();
        String prefix = fileService.getMinioDomain();
        mediaInfoPOS.forEach(item -> {
            MediaInfo mediaInfo = new MediaInfo();
            BeanUtils.copyProperties(item, mediaInfo);
            mediaInfo.setImgFullUrl(prefix + item.getImgUrl());
            mediaInfo.setId(String.valueOf(item.getId()));
        });
        return result;
    }



    public Boolean delMediaInfo(MediaInfo reqVO) {
        LambdaQueryWrapper<SiteMediaInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteMediaInfoPO::getSiteCode, CurrReqUtils.getSiteCode());
        queryWrapper.eq(SiteMediaInfoPO::getId, reqVO.getId());
        SiteMediaInfoPO mediaInfoPO = this.baseMapper.selectOne(queryWrapper);
        if (mediaInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        this.baseMapper.deleteById(mediaInfoPO.getId());
        return Boolean.TRUE;
    }



}



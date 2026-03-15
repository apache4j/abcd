package com.cloud.baowang.system.service.site;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.seo.*;
import com.cloud.baowang.system.po.site.SiteSeoPO;
import com.cloud.baowang.system.repositories.site.SiteSeoRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SiteSeoService extends ServiceImpl<SiteSeoRepository, SiteSeoPO> {

    private final LanguageManagerApi languageManagerApi;

    public Page<SiteSeoResVO> findPage(SiteSeoQueryVO params) {
        Page<SiteSeoPO> page = new Page<>(params.getPageNumber(), params.getPageSize());
        LambdaQueryWrapper<SiteSeoPO> query = Wrappers.lambdaQuery(SiteSeoPO.class);
        query.eq(StrUtil.isNotEmpty(params.getLang()),SiteSeoPO::getLang, params.getLang());
        query.eq(StrUtil.isNotEmpty(params.getSiteCode()),SiteSeoPO::getSiteCode, params.getSiteCode());
        Page<SiteSeoPO> siteSeoPOPage = this.getBaseMapper().selectPage(page, query);
        Page<SiteSeoResVO> backPage = new Page<>(params.getPageNumber(), params.getPageSize());
        if (siteSeoPOPage.getTotal()>0){
            ResponseVO<List<LanguageManagerListVO>> langListRes = languageManagerApi.list();
            Map<String,String> langMap = new HashMap<>();
            if (langListRes.isOk()){
                langMap = langListRes.getData().stream().collect(Collectors.toMap(LanguageManagerListVO::getCode, LanguageManagerListVO::getName));
            }
            Map<String, String> finalLangMap = langMap;
            List<SiteSeoResVO> list = siteSeoPOPage.getRecords().stream().map(siteSeoPO -> {
                SiteSeoResVO siteSeoResVO = new SiteSeoResVO();
                BeanUtils.copyProperties(siteSeoPO, siteSeoResVO);
                siteSeoResVO.setLangName(finalLangMap.get(siteSeoResVO.getLang()));
                return siteSeoResVO;
            }).toList();
            backPage.setRecords(list);
            backPage.setTotal(siteSeoPOPage.getTotal());
            backPage.setPages(siteSeoPOPage.getPages());
        }

        return backPage;
    }

    public List<SiteSeoAppResVO> findList(SiteSeoQueryVO params){
        LambdaQueryWrapper<SiteSeoPO> query = Wrappers.lambdaQuery(SiteSeoPO.class);
        query.eq(StrUtil.isNotEmpty(params.getLang()),SiteSeoPO::getLang, params.getLang());
        query.eq(StrUtil.isNotEmpty(params.getSiteCode()),SiteSeoPO::getSiteCode, params.getSiteCode());
        List<SiteSeoPO> siteSeoPOS = this.getBaseMapper().selectList(query);

        return siteSeoPOS.stream().map(siteSeoPO -> BeanUtil.copyProperties(siteSeoPO, SiteSeoAppResVO.class)).toList();
    }

    public SiteSeoResVO findById(SiteSeoFindByIdVO siteSeoFindByIdVO) {
        SiteSeoPO siteSeoPO = this.getBaseMapper().selectById(siteSeoFindByIdVO.getId());
        SiteSeoResVO siteSeoResVO = new SiteSeoResVO();
        if (siteSeoPO==null){
            return siteSeoResVO;
        }
        BeanUtils.copyProperties(siteSeoPO, siteSeoResVO);
        ResponseVO<List<LanguageManagerListVO>> langListRes = languageManagerApi.list();
        if (langListRes.isOk()){
            List<LanguageManagerListVO> data = langListRes.getData();
            LanguageManagerListVO managerListVO = data.stream().filter(vo -> vo.getCode().equals(siteSeoResVO.getLang())).findFirst().orElse(new LanguageManagerListVO());
            siteSeoResVO.setLangName(managerListVO.getName());
        }
        return siteSeoResVO;
    }

    public ResponseVO<Boolean> add(SiteSeoAddReqVO siteSeoAddReqVO) {
        //NOTE 相同语言只能有一条记录
        Wrapper<SiteSeoPO> wrapper = Wrappers.lambdaQuery(SiteSeoPO.class).eq(SiteSeoPO::getSiteCode, siteSeoAddReqVO.getSiteCode())
                .eq(SiteSeoPO::getLang,siteSeoAddReqVO.getLang());
        if (this.getBaseMapper().selectCount(wrapper)>0){
            log.error("相同语言只能有一条记录");
            return ResponseVO.fail(ResultCode.SEO_LANG_REPEAT_ERROR);
        }
        return ResponseVO.success(save(BeanUtil.copyProperties(siteSeoAddReqVO, SiteSeoPO.class)));
    }

    public ResponseVO<Boolean> edit(SiteSeoEditReqVO siteSeoEditReqVO) {
        return ResponseVO.success(updateById(BeanUtil.copyProperties(siteSeoEditReqVO, SiteSeoPO.class)));
    }

    public boolean delete(SiteSeoFindByIdVO siteSeoFindByIdVO) {
        return this.getBaseMapper().deleteById(siteSeoFindByIdVO.getId())>0;
    }
}

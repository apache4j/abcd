package com.cloud.baowang.system.api.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteSeoApi;
import com.cloud.baowang.system.api.vo.site.seo.*;
import com.cloud.baowang.system.service.site.SiteSeoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
public class SiteSeoApiImpl implements SiteSeoApi {

    private final SiteSeoService siteSeoService;

    @Override
    public Page<SiteSeoResVO> findPage(SiteSeoQueryVO siteSeoQueryVO) {
        return siteSeoService.findPage(siteSeoQueryVO);
    }

    @Override
    public SiteSeoResVO findById(SiteSeoFindByIdVO siteSeoFindByIdVO) {
        return siteSeoService.findById(siteSeoFindByIdVO);
    }

    @Override
    public ResponseVO<Boolean> add(SiteSeoAddReqVO siteSeoAddReqVO) {
        return siteSeoService.add(siteSeoAddReqVO);
    }

    @Override
    public ResponseVO<Boolean> edit(SiteSeoEditReqVO siteSeoEditReqVO) {
        return siteSeoService.edit(siteSeoEditReqVO);
    }

    @Override
    public boolean delete(SiteSeoFindByIdVO siteSeoFindByIdVO) {
        return siteSeoService.delete(siteSeoFindByIdVO);
    }

    @Override
    public List<SiteSeoAppResVO> findList(SiteSeoQueryVO params) {
        return siteSeoService.findList(params);
    }
}

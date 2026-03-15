package com.cloud.baowang.activity.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.SystemActivityTemplateApi;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.service.SiteActivityTemplateService;
import com.cloud.baowang.activity.service.SystemActivityTemplateService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class SystemActivityTemplateApiImpl implements SystemActivityTemplateApi {


    private final SystemActivityTemplateService systemActivityTemplateService;

    private final SiteActivityTemplateService siteActivityTemplateService;

    @Override
    public ResponseVO<Page<SystemActivityTemplateVO>> getPage(SystemActivityTemplateReqVO vo) {
        return systemActivityTemplateService.getPage(vo);
    }

    @Override
    public ResponseVO<List<SystemActivityTemplateInfoVO>> getInfo(SystemActivityTemplateReqVO vo) {
        return systemActivityTemplateService.getInfo(vo);
    }

    @Override
    public ResponseVO<Boolean> batchBindAndUnBindActivityTemplate(SiteActivityTemplateSaveVO siteActivityTemplateSaveVO) {
        return siteActivityTemplateService.batchBindAndUnBindActivityTemplate(siteActivityTemplateSaveVO);
    }

    @Override
    public ResponseVO<List<SiteActivityTemplateVO>> querySiteActivityTemplate(String siteCode,Integer handicapMode) {
        return systemActivityTemplateService.querySiteActivityTemplate(siteCode,handicapMode);
    }

    @Override
    public ResponseVO<Boolean> checkBindFlag(SiteActivityTemplateCheckVO siteActivityTemplateCheckVO) {
        return siteActivityTemplateService.checkBindFlag(siteActivityTemplateCheckVO);
    }

}

package com.cloud.baowang.activity.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.SiteActivityLabApi;
import com.cloud.baowang.activity.api.vo.category.*;
import com.cloud.baowang.activity.service.SiteActivityLabsService;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteActivityLabApiImpl implements SiteActivityLabApi {

    private final SiteActivityLabsService activityLabsService;

    @Override
    public ResponseVO<Page<SiteActivityLabsVO>> pageQuery(SiteActivityLabPageQueryVO pageQueryVo) {
        return activityLabsService.pageQuery(pageQueryVo);
    }

    @Override
    public ResponseVO<List<SiteActivityLabsVO>> siteQueryList(String siteCode) {
        return activityLabsService.siteQueryList(siteCode);
    }

    @Override
    public ResponseVO<List<SiteActivityLabsVO>> siteQueryListV2(String siteCode) {
        return activityLabsService.siteQueryListV2(siteCode);
    }

    @Override
    public ResponseVO<Boolean> addActivityLab(SiteActivityLabRequestVO requestVo, String creator) {
        return activityLabsService.addActivityLab(requestVo, creator);
    }

    @Override
    public ResponseVO<Boolean> updActivityLab(SiteActivityLabRequestVO requestVo, String creator) {
        return activityLabsService.updActivityLab(requestVo, creator);
    }

    @Override
    public ResponseVO<Boolean> deleteById(Long id) {
        return activityLabsService.deleteById(id);
    }

    @Override
    public ResponseVO<SiteActivityLabsVO> detail(String id, String siteCode) {
        return activityLabsService.detail(id,siteCode);
    }

    @Override
    public ResponseVO<Boolean> enDisAbleLab(String id, Integer status) {
        return activityLabsService.enDisAbleLab(id,status);
    }

    @Override
    public List<CodeValueVO> getLabNameList(String siteCode) {
        return activityLabsService.getLabNameList(siteCode);
    }

    @Override
    public ResponseVO<List<AddActivityLabelSortVO>> getSort(String siteCode) {
        return activityLabsService.getSort(siteCode);
    }

    @Override
    public ResponseVO<Boolean> addSort(List<AddActivityLabelSortVO> labelSortVOS) {
        return activityLabsService.addSort(labelSortVOS);
    }
}

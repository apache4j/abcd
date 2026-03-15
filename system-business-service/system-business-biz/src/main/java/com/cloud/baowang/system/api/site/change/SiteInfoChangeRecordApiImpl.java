package com.cloud.baowang.system.api.site.change;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.api.site.change.SiteInfoChangeRecordApi;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.site.SiteInfoChangeRequestVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordVO;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author : mufan
 * @Date : 2025/4/4 16:13
 * @Version : 1.0
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteInfoChangeRecordApiImpl implements SiteInfoChangeRecordApi {

    private final SiteInfoChangeRecordService siteInfoChangeRecordService;

    @Override
    public Page<SiteInfoChangeRecordVO> querySiteInfoChangeRecord(SiteInfoChangeRequestVO siteInfoChangeRequestVO) {
        return siteInfoChangeRecordService.querySiteInfoChangeRecord(siteInfoChangeRequestVO);
    }

    @Override
    public void addInfo(SiteInfoChangeRecordReqVO siteInfoChangeRecordReqVO) {
        siteInfoChangeRecordService.addSiteInfoChangeRequestVO(siteInfoChangeRecordReqVO);
    }

    @Override
    public List<JsonDifferenceVO> getJsonDifferenceList(SiteInfoChangeBodyVO siteInfoChangeBodyVO) {
        return siteInfoChangeRecordService.getJsonDifferenceList(siteInfoChangeBodyVO);
    }

    @Override
    public List<JsonDifferenceVO> getJsonDifferenceListForRecharger(SiteInfoChangeBodyVO siteInfoChangeBodyVO) {
        return siteInfoChangeRecordService.getJsonDifferenceListForRecharger(siteInfoChangeBodyVO);
    }

    @Override
    public void addJsonDifferenceList(SiteInfoChangeRecordListReqVO vo) {
        siteInfoChangeRecordService.addJsonDifferenceList(vo);
    }


}

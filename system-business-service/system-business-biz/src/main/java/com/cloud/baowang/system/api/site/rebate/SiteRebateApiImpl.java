package com.cloud.baowang.system.api.site.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.*;
import com.cloud.baowang.system.service.site.rebate.SiteNonRebateConfigService;
import com.cloud.baowang.system.service.site.rebate.SiteRebateConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@AllArgsConstructor
public class SiteRebateApiImpl implements SiteRebateApi {

    private final SiteRebateConfigService configService;

    private final SiteNonRebateConfigService nonRebateConfigService;


    @Override
    public ResponseVO<List<SiteRebateConfigVO>> listPage(SiteRebateConfigQueryVO reqVO) {
        return ResponseVO.success(configService.listPage(reqVO));
    }

    @Override
    public ResponseVO saveRebateConfig(List<SiteRebateConfigAddVO> vo) {
        return configService.saveRebateConfig(vo);
    }

    @Override
    public ResponseVO saveNonRabate(SiteNonRebateConfigAddVO vo) {
        return nonRebateConfigService.saveNonRabate(vo);
    }

    @Override
    public ResponseVO updateNonRabate(SiteNonRebateConfigAddVO vo) {
        return nonRebateConfigService.updateNonRabate(vo);
    }

    @Override
    public ResponseVO<Page<SiteNonRebateConfigVO>> listNonRebatePage(SiteNonRebateQueryVO vo) {
        return ResponseVO.success(nonRebateConfigService.listPage(vo));
    }

    @Override
    public ResponseVO delNonRabate(SiteNonRebateConfigAddVO vo) {
        return ResponseVO.success(nonRebateConfigService.removeById(vo.getId()));
    }

    @Override
    public ResponseVO<Page<SiteNonRebateExportVO>> listExportPage(SiteNonRebateQueryVO vo) {
        return ResponseVO.success(nonRebateConfigService.listExportPage(vo));
    }

    @Override
    public ResponseVO<Long> NonRebateCount(SiteNonRebateQueryVO vo) {
        return ResponseVO.success(nonRebateConfigService.NonRebateCount(vo));
    }

    @Override
    public ResponseVO initRebateList(SiteRebateInitVO reqVO) {
        configService.initRebateList(reqVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<SiteRebateConfigWebVO>> webListPage(SiteRebateClientShowVO req) {
        return ResponseVO.success(configService.webListPage(req));
    }

    @Override
    public ResponseVO<List<SiteRebateConfigWebVO>> webQueryListByStatusPage(SiteRebateClientShowVO req) {
        return ResponseVO.success(configService.webQueryListByStatusPage(req));
    }


    @Override
    public ResponseVO<Map<Integer, Map<String, BigDecimal>>> rebateLabel(SiteRebateClientShowVO req) {
        return ResponseVO.success(configService.rebateLabel(req));
    }

    @Override
    public ResponseVO updateVipGradeRebateConfig(SiteRebateInitVO reqVO) {
        return ResponseVO.success(configService.updateVipGradeRebateConfig(reqVO));
    }
}

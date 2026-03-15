package com.cloud.baowang.site.controller.area;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.vo.area.*;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteManageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "基础设置-手机区号管理")
@RestController
@RequestMapping("/area-site-manage/api")
@AllArgsConstructor
public class AreaSiteManageController {
    private final AreaSiteManageApi areaSiteManageApi;


    @PostMapping("pageList")
    @Operation(summary = "总控手机区号管理分页查询")
    public  ResponseVO<Page<AreaSiteManageVO>> pageList(@RequestBody AreaCodeManageReqVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        return areaSiteManageApi.pageList(vo);
    }

    @PostMapping("statusChange")
    @Operation(summary = "总控手机区号管理状态变更")
    public ResponseVO<Boolean> statusChange(@RequestBody AreaStatusVO vo) {
        String updater = CurrReqUtils.getAccount();
        vo.setUpdater(updater);
        return areaSiteManageApi.statusChange(vo);
    }
}

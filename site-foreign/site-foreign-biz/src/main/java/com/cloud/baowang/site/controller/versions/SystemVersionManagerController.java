package com.cloud.baowang.site.controller.versions;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.versions.SystemVersionManagerApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerPageQueryVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerReqVO;
import com.cloud.baowang.system.api.vo.version.SystemVersionManagerRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "版本管理")
@RestController
@RequestMapping("/system-version-manager/api")
@AllArgsConstructor
@Slf4j
public class SystemVersionManagerController {
    private final SystemVersionManagerApi systemVersionManagerApi;
    private final SiteApi siteApi;
    private final SystemParamApi paramApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.VERSION_MOBILE_PLATFORM);
        param.add(CommonConstant.VERSION_UPDATE_STATUS);
        return paramApi.getSystemParamsByList(param);
    }

    @GetMapping("getSiteDownBox")
    @Operation(summary = "获取站点下拉")
    public ResponseVO<List<CodeValueNoI18VO>> getSiteDownBox() {
        ResponseVO<List<SiteVO>> resp = siteApi.allSiteInfo();
        List<CodeValueNoI18VO> result = new ArrayList<>();
        if (resp.isOk()) {
            List<SiteVO> data = resp.getData();
            if (CollectionUtil.isNotEmpty(data)) {
                result = data.stream()
                        .map(site -> new CodeValueNoI18VO(site.getSiteCode(), site.getSiteName()))
                        .toList();
            }
        }
        return ResponseVO.success(result);
    }


    @PostMapping("pageQuery")
    @Operation(summary = "分页列表")
    public ResponseVO<Page<SystemVersionManagerRespVO>> pageQuery(@RequestBody SystemVersionManagerPageQueryVO pageQueryVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        pageQueryVO.setSiteCode(siteCode);
        return systemVersionManagerApi.pageQuery(pageQueryVO);
    }
}

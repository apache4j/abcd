package com.cloud.baowang.admin.controller.site;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.versions.SystemVersionManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import com.cloud.baowang.system.api.vo.site.SiteMessageQueryVO;
import com.cloud.baowang.system.api.vo.version.SiteSystemInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "总台-站点-站点配置信息")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/siteMessage/api")
public class SiteMessageController {
    private final SystemVersionManagerApi systemVersionManagerApi;
    private final LanguageManagerApi languageManagerApi;

    @PostMapping("getSiteSystemInfo")
    @Operation(summary = "站点系统属性")
    ResponseVO<SiteSystemInfo> getSiteSystemInfo(@RequestBody SiteMessageQueryVO queryVO) {
        ResponseVO<SiteSystemInfo> resp = systemVersionManagerApi.getSiteSystemInfo(queryVO);
        if (resp.isOk() && resp.getData() != null) {
            SiteSystemInfo data = resp.getData();
            ResponseVO<List<LanguageValidListCacheVO>> langResp = languageManagerApi.validListBySiteCode(data.getSiteCode());
            if (langResp.isOk()) {
                data.setValidListCacheVOS(langResp.getData());
            }
        }

        return resp;
    }
}

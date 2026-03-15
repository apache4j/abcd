package com.cloud.baowang.site.controller.media;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.api.vo.site.agreement.MediaInfo;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.site.tutorial.OptionTypeConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "站点-媒体号配置")
@RestController
@RequestMapping("/site-media/api/")
@AllArgsConstructor
public class MediaManageController {
    private final HelpCenterManageApi helpCenterManageApi;

    @PostMapping("addMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<Boolean> addMediaInfo(@RequestBody List<MediaInfo> reqVO) {

        return helpCenterManageApi.addMediaInfo(reqVO);
    }

    @PostMapping("getMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<List<MediaInfo>> getMediaInfo() {
        return helpCenterManageApi.getMediaInfo();
    }

    @PostMapping("delMediaInfo")
    @Operation(summary = "媒体号配置 skin4")
    ResponseVO<Boolean> delMediaInfo(@RequestBody MediaInfo reqVO) {

        return helpCenterManageApi.delMediaInfo(reqVO);
    }

}

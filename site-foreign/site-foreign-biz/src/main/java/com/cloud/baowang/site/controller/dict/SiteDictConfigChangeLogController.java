package com.cloud.baowang.site.controller.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigChangeLogApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "站点-参数字典变更记录")
@AllArgsConstructor
@RestController
@RequestMapping("/siteDictConfigChangeLog/api")
public class SiteDictConfigChangeLogController {
    private final SystemDictConfigChangeLogApi logApi;
    private final SystemParamApi paramApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页列表")
    public ResponseVO<Page<SystemDictConfigChangeLogRespVO>> pageQuery(@RequestBody SystemDictConfigChangeLogPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return logApi.pageQuery(queryVO);
    }

    @GetMapping("getDownBox")
    @Operation(summary = "下拉")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return paramApi.getSystemParamByType(CommonConstant.DICT_CONFIG_CATEGORY);
    }
}

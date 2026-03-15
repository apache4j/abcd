package com.cloud.baowang.admin.controller.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
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

@RestController
@Tag(name = "参数字典变更记录查询")
@RequestMapping("/dictConfig/changeLog")
@AllArgsConstructor
public class SystemDictConfigChangeLogController {

    private final SystemDictConfigChangeLogApi logApi;
    private final SystemParamApi paramApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询")
    public ResponseVO<Page<SystemDictConfigChangeLogRespVO>> pageQuery(@RequestBody SystemDictConfigChangeLogPageQueryVO queryVO) {
        queryVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        return logApi.pageQuery(queryVO);
    }

    @GetMapping("getDownBox")
    @Operation(summary = "下拉")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return paramApi.getSystemParamByType(CommonConstant.DICT_CONFIG_CATEGORY);
    }


}

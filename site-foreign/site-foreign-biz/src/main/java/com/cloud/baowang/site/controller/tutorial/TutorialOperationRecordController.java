package com.cloud.baowang.site.controller.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialOperationRecordApi;
import com.cloud.baowang.system.api.enums.tutorial.ChangeDirectoryEnum;
import com.cloud.baowang.system.api.enums.tutorial.ChangeTypeEnum;

import com.cloud.baowang.system.api.enums.tutorial.TutorialGeneralEnum;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.operation.TutorialOperationRecordRspVO;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "教程配置操作记录")
@RestController
@RequestMapping(value = "/tutorial-record/api")
@AllArgsConstructor
@Validated
public class TutorialOperationRecordController {
    private final TutorialOperationRecordApi tutorialOperationRecordApi;

    @Operation(summary = "教程变更记录列表-分页")
    @PostMapping("listPage")
    public ResponseVO<Page<TutorialOperationRecordRspVO>> listPage(@RequestBody TutorialOperationRecordResVO resVo) {
        resVo.setSiteCode(CurrReqUtils.getSiteCode());
        return tutorialOperationRecordApi.listPage(resVo);
    }

    @Operation(summary = "教程操作操作记录页面-下拉框 changeCatalog对应变更目录")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        List<CodeValueVO> changeCatalog = ChangeDirectoryEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(String.valueOf(item.getCode())).value(item.getName()).build())
                .toList();
        result.put(TutorialGeneralEnum.CHANGE_CATALOG.getName(), changeCatalog);

        List<CodeValueVO> changeType = ChangeTypeEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(String.valueOf(item.getCode())).value(item.getName()).build())
                .toList();
        result.put(TutorialGeneralEnum.CHANGE_TYPE.getName(), changeType);
        return ResponseVO.success(result);
    }

   

}

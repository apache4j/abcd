package com.cloud.baowang.admin.controller.language;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerChangStatusReqVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerEditVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerInfoReqVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerInfoResVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerPageReqVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerSortVO;
import com.cloud.baowang.system.api.vo.language.LanguageManagerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "基础设置-语言管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/language-manager/api")
public class LanguageManagerController {
    private final LanguageManagerApi languageManagerApi;

    @Operation(summary = "语言管理列表查询")
    @PostMapping("list")
    public ResponseVO<List<LanguageManagerListVO>> list() {
        return languageManagerApi.list();
    }

    @Operation(summary = "语言管理列表排序")
    @PostMapping("sort")
    public ResponseVO<Void> sort(@Valid @RequestBody List<LanguageManagerSortVO> vo) {
        return languageManagerApi.sort(vo);
    }

    @Operation(summary = "语言管理分页查询")
    @PostMapping("/pageList")
    public ResponseVO<Page<LanguageManagerVO>> pageList(@RequestBody LanguageManagerPageReqVO vo) {
        return languageManagerApi.pageList(vo);
    }

    @Operation(summary = "语言管理编辑")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@Validated @RequestBody LanguageManagerEditVO vo) {
        return languageManagerApi.edit(vo);
    }

    @Operation(summary = "语言管理详情")
    @PostMapping("/info")
    public ResponseVO<LanguageManagerInfoResVO> info(@RequestBody LanguageManagerInfoReqVO vo) {
        return languageManagerApi.info(vo);
    }

    @Operation(summary = "语言管理状态管理-status禁用/停用")
    @PostMapping("/changeStatus")
    public ResponseVO<Void> changeStatus(@Validated @RequestBody LanguageManagerChangStatusReqVO vo) {
        return languageManagerApi.changeStatus(vo);

    }
}

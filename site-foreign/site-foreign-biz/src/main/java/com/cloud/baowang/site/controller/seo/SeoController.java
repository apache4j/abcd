package com.cloud.baowang.site.controller.seo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteSeoApi;
import com.cloud.baowang.system.api.vo.site.seo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "站点-运营-信息配置管理-检索信息配置")
@RestController
@RequestMapping("/site-seo/api")
@AllArgsConstructor
public class SeoController {

    private final SiteSeoApi siteSeoApi;

    @PostMapping("/findPage")
    @Operation(summary = "检索信息配置分页列表")
    public ResponseVO<Page<SiteSeoResVO>> findPage(@RequestBody SiteSeoQueryVO pageVO) {
        pageVO.setSiteCode(CurrReqUtils.getSiteCode());
        Page<SiteSeoResVO> page = siteSeoApi.findPage(pageVO);
        return ResponseVO.success(page);
    }

    @PostMapping("/findById")
    @Operation(summary = "检索信息配置分页列表")
    public ResponseVO<SiteSeoResVO> findById(@RequestBody @Validated SiteSeoFindByIdVO siteSeoFindByIdVO) {
        return ResponseVO.success(siteSeoApi.findById(siteSeoFindByIdVO));
    }

    @PostMapping("/add")
    @Operation(summary = "检索信息配置新增")
    public ResponseVO<Boolean> add(@RequestBody @Validated  SiteSeoAddReqVO siteSeoAddReqVO) {
        if (checkData(siteSeoAddReqVO.getTitle(),siteSeoAddReqVO.getMeta())){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (LanguageEnum.getList().stream().noneMatch(languageEnum -> languageEnum.getLang().equals(siteSeoAddReqVO.getLang()))){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        siteSeoAddReqVO.setSiteCode(CurrReqUtils.getSiteCode());

        siteSeoAddReqVO.setCreator(CurrReqUtils.getAccount());
        siteSeoAddReqVO.setCreatedTime(System.currentTimeMillis());
        siteSeoAddReqVO.setUpdater(CurrReqUtils.getAccount());
        siteSeoAddReqVO.setUpdatedTime(System.currentTimeMillis());
        return siteSeoApi.add(siteSeoAddReqVO);
    }

    @PostMapping("/edit")
    @Operation(summary = "检索信息配置编辑")
    public ResponseVO<Boolean> edit(@RequestBody @Validated  SiteSeoEditReqVO siteSeoEditReqVO) {

        siteSeoEditReqVO.setUpdater(CurrReqUtils.getAccount());
        siteSeoEditReqVO.setUpdatedTime(System.currentTimeMillis());
        //修改时，不修改语言
        siteSeoEditReqVO.setLang(null);
        if (checkData(siteSeoEditReqVO.getTitle(),siteSeoEditReqVO.getMeta())){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return siteSeoApi.edit(siteSeoEditReqVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "检索信息配置删除")
    public ResponseVO<Boolean> delete(@RequestBody @Validated  SiteSeoFindByIdVO siteSeoFindByIdVO) {
        return ResponseVO.success(siteSeoApi.delete(siteSeoFindByIdVO));
    }


    private boolean checkData(String title, String meta){
        return title.length() > 50 || meta.length() > 160;
    }

}

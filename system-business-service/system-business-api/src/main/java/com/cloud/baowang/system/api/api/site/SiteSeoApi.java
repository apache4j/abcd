package com.cloud.baowang.system.api.api.site;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.seo.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteSiteSeoApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - siteSeo")
public interface SiteSeoApi {

    String PREFIX = ApiConstants.PREFIX + "/siteSeo/api/";

    @PostMapping(PREFIX + "findPage")
    @Schema(description = "分页查询")
    Page<SiteSeoResVO> findPage(@RequestBody SiteSeoQueryVO siteSeoQueryVO);

    @PostMapping(PREFIX + "findById")
    @Schema(description = "查询findById")
    SiteSeoResVO findById(@RequestBody SiteSeoFindByIdVO siteSeoFindByIdVO);

    @PostMapping(PREFIX + "add")
    @Schema(description = "add")
    ResponseVO<Boolean> add(@RequestBody SiteSeoAddReqVO siteSeoAddReqVO);

    @PostMapping(PREFIX + "edit")
    @Schema(description = "edit")
    ResponseVO<Boolean> edit(@RequestBody SiteSeoEditReqVO siteSeoEditReqVO);

    @PostMapping(PREFIX + "findList")
    @Schema(description = "findList")
    List<SiteSeoAppResVO> findList(@RequestBody SiteSeoQueryVO siteSeoQueryVO);

    @PostMapping(PREFIX + "delete")
    @Schema(description = "删除ById")
    boolean delete(@RequestBody SiteSeoFindByIdVO siteSeoFindByIdVO);
}

package com.cloud.baowang.system.api.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.operations.SkinAddVO;
import com.cloud.baowang.system.api.vo.operations.SkinEditVO;
import com.cloud.baowang.system.api.vo.operations.SkinRequestVO;
import com.cloud.baowang.system.api.vo.operations.SkinResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSkinInfoApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - skinInfoApi")
public interface SkinInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/skinInfo/api";

    @Operation(summary = "查询皮肤列表")
    @PostMapping(PREFIX + "/querySkinPage")
    ResponseVO<Page<SkinResVO>> querySkinPage(@RequestBody SkinRequestVO skinRequestVO);

    @Operation(summary = "增加皮肤")
    @PostMapping(PREFIX + "/addSkin")
    ResponseVO<?> addSkin(@RequestBody SkinAddVO skinAddVO);

    @Operation(summary = "修改皮肤")
    @PostMapping(PREFIX + "/editSkin")
    ResponseVO<?> editSkin(@RequestBody SkinAddVO skinAddVO);

    @Operation(summary = "启用/禁用皮肤")
    @PostMapping(PREFIX + "/editSkinStatus")
    ResponseVO<?> editSkinStatus(@RequestBody SkinEditVO skinEditVO);

    @Operation(summary = "查询皮肤列表")
    @PostMapping(PREFIX + "/querySkinList")
    ResponseVO<List<SkinResVO>> querySkinList();
}

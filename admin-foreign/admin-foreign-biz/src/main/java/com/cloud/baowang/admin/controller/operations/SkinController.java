package com.cloud.baowang.admin.controller.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.SkinInfoApi;
import com.cloud.baowang.system.api.vo.operations.SkinAddVO;
import com.cloud.baowang.system.api.vo.operations.SkinEditVO;
import com.cloud.baowang.system.api.vo.operations.SkinRequestVO;
import com.cloud.baowang.system.api.vo.operations.SkinResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "皮肤管理")
@RestController
@RequestMapping("/skin/api")
@AllArgsConstructor
public class SkinController {

    private final SkinInfoApi skinInfoApi;

    @Operation(summary = "查询皮肤列表")
    @PostMapping("/querySkinPage")
    public ResponseVO<Page<SkinResVO>> querySkinPage(@RequestBody SkinRequestVO skinRequestVO) {
        return skinInfoApi.querySkinPage(skinRequestVO);
    }

    @Operation(summary = "增加皮肤")
    @PostMapping("/addSkin")
    public ResponseVO<?> addSkin(@Valid @RequestBody SkinAddVO skinAddVO) {
        skinAddVO.setUpdaterName(CurrReqUtils.getAccount());
        return skinInfoApi.addSkin(skinAddVO);
    }

    @Operation(summary = "修改皮肤")
    @PostMapping("/editSkin")
    public ResponseVO<?> editSkin(@Valid @RequestBody SkinAddVO skinAddVO) {
        skinAddVO.setUpdaterName(CurrReqUtils.getAccount());
        return skinInfoApi.editSkin(skinAddVO);
    }

    @Operation(summary = "启用/禁用皮肤")
    @PostMapping("/editSkinStatus")
    public ResponseVO<?> editSkinStatus(@Valid @RequestBody SkinEditVO skinEditVO){
        skinEditVO.setUpdaterName(CurrReqUtils.getAccount());
        return skinInfoApi.editSkinStatus(skinEditVO);
    }
}

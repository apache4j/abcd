package com.cloud.baowang.system.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.api.operations.SkinInfoApi;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.service.operations.CustomerChannelService;
import com.cloud.baowang.system.service.operations.SkinInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SkinInfoApiImpl implements SkinInfoApi {

    private final SkinInfoService skinInfoService;

    @Override
    public ResponseVO<Page<SkinResVO>> querySkinPage(SkinRequestVO skinRequestVO) {
        return skinInfoService.querySkinPage(skinRequestVO);
    }

    @Override
    public ResponseVO<List<SkinResVO>> querySkinList() {
        return skinInfoService.querySkinList();
    }

    @Override
    public ResponseVO<?> addSkin(SkinAddVO skinAddVO) {
        return skinInfoService.addSkin(skinAddVO);
    }

    @Override
    public ResponseVO<?> editSkin(SkinAddVO skinAddVO) {
        return skinInfoService.editSkin(skinAddVO);
    }

    @Override
    public ResponseVO<?> editSkinStatus(SkinEditVO skinEditVO) {
        return skinInfoService.editSkinStatus(skinEditVO);
    }
}

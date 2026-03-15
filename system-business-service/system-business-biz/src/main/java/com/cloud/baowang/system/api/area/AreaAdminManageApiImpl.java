package com.cloud.baowang.system.api.area;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.area.AreaAdminManageApi;
import com.cloud.baowang.system.api.vo.area.*;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.service.area.AreaAdminManageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AreaAdminManageApiImpl implements AreaAdminManageApi {

    private final AreaAdminManageService areaAdminManageService;

    @Override
    public ResponseVO<Page<AreaAdminManageVO>> pageList(AreaCodeManageReqVO vo) {
        return areaAdminManageService.pageList(vo);
    }

    @Override
    public ResponseVO<AreaCodeManageInfoVO> getInfo(IdVO idVO) {
        return ResponseVO.success(areaAdminManageService.getInfo(idVO));
    }

    @Override
    public ResponseVO<Void> edit(AreaCodeManageEditReqVO vo) {
        return areaAdminManageService.edit(vo);
    }

    @Override
    public ResponseVO<Boolean> statusChange(AreaStatusVO vo) {
        return ResponseVO.success(areaAdminManageService.statusChange(vo));
    }

    @Override
    public AreaSiteLangVO getAreaInfo(String areaCode) {
        return areaAdminManageService.getAreaInfo(areaCode);
    }
}


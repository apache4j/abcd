package com.cloud.baowang.admin.controller.area;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.area.AreaAdminManageApi;
import com.cloud.baowang.system.api.vo.area.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "基础设置-手机区号管理")
@RestController
@RequestMapping("/area-admin-manage/api")
@AllArgsConstructor
public class AreaAdminManageController {
    private final AreaAdminManageApi areaAdminManageApi;

    @PostMapping("pageList")
    @Operation(summary = "总控手机区号管理分页查询")
    public  ResponseVO<Page<AreaAdminManageVO>> pageList(@RequestBody AreaCodeManageReqVO vo) {
        return areaAdminManageApi.pageList(vo);
    }

    @PostMapping("edit")
    @Operation(summary = "总控手机区号管理信息编辑")
    public ResponseVO<Void> edit(@RequestBody AreaCodeManageEditReqVO vo) {
        String updater = CurrReqUtils.getAccount();
        vo.setUpdater(updater);
        if (vo.getMinLength() > vo.getMaxLength()) {
            throw new BaowangDefaultException(ResultCode.MIN_LENGTH_BIGGER);
        }
        return areaAdminManageApi.edit(vo);
    }

    @PostMapping("statusChange")
    @Operation(summary = "总控手机区号管理状态变更")
    public ResponseVO<Boolean> statusChange(@RequestBody AreaStatusVO vo) {
        String updater = CurrReqUtils.getAccount();
        vo.setUpdater(updater);
        return areaAdminManageApi.statusChange(vo);
    }

    @PostMapping("getInfo")
    @Operation(summary = "总控手机区号管理信息详情")
    public ResponseVO<AreaCodeManageInfoVO> getInfo(@RequestBody IdVO idVO) {
        return areaAdminManageApi.getInfo(idVO);
    }

}

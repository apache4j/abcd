package com.cloud.baowang.admin.controller.areaLimit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.areaLimit.AreaLimitApi;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerAddReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerEditReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerIdReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerStatusChangeReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "区域限制管理")
@RestController
@RequestMapping("/area-limit-manager/api")
@AllArgsConstructor
public class AreaLimitManagerController {
    private final AreaLimitApi areaLimitApi;


    @PostMapping("pageList")
    @Operation(summary = "区域限制管理分页查询")
    public ResponseVO<Page<AreaLimitManagerVO>> pageList(@RequestBody AreaLimitManagerReqVO vo) {
        return areaLimitApi.pageList(vo);
    }

    @PostMapping("add")
    @Operation(summary = "区域限制信息新增")
    public ResponseVO<Void> add(@Valid @RequestBody AreaLimitManagerAddReqVO vo) {
        String adminId = CurrReqUtils.getAccount();
        vo.setOperator(adminId);
        return areaLimitApi.add(vo);
    }

    @PostMapping("edit")
    @Operation(summary = "区域限制信息编辑")
    public ResponseVO<Void> edit(@Valid @RequestBody AreaLimitManagerEditReqVO vo) {
        String adminId = CurrReqUtils.getAccount();
        vo.setOperator(adminId);
        return areaLimitApi.edit(vo);
    }

    @PostMapping("statusChange")
    @Operation(summary = "状态变更")
    public ResponseVO<Void> statusChange(@Valid @RequestBody AreaLimitManagerStatusChangeReqVO vo) {
        String adminId = CurrReqUtils.getAccount();
        vo.setOperator(adminId);
        return areaLimitApi.statusChange(vo);
    }

    @PostMapping("del")
    @Operation(summary = "区域限制信息删除")
    public ResponseVO<Void> del(@Valid @RequestBody AreaLimitManagerIdReqVO vo) {
        return areaLimitApi.del(vo);
    }

    @PostMapping("info")
    @Operation(summary = "区域限制信息详情")
    public ResponseVO<AreaLimitManagerVO> info(@Valid @RequestBody AreaLimitManagerIdReqVO vo) {
        return areaLimitApi.info(vo);
    }

}

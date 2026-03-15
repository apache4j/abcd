package com.cloud.baowang.admin.controller.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigReqVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "参数字典配置")
@RequestMapping("/system/dictConfig")
@AllArgsConstructor
public class SystemDictConfigController {

    private final SystemDictConfigApi configApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询")
    public ResponseVO<Page<SystemDictConfigRespVO>> pageQuery(@RequestBody SystemDictConfigPageQueryVO queryVO) {
        queryVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        queryVO.setIsSyncSite(Integer.parseInt(YesOrNoEnum.NO.getCode()));
        return configApi.pageQuery(queryVO);
    }

    @PostMapping("upd")
    @Operation(summary = "修改")
    public ResponseVO<Boolean> upd(@RequestBody @Validated SystemDictConfigReqVO reqVO) {
        reqVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        reqVO.setOperator(CurrReqUtils.getAccount());
        return configApi.upd(reqVO);
    }

    @GetMapping("getByCode")
    @Operation(summary = "根据code获取配置信息")
    public ResponseVO<SystemDictConfigRespVO> getByCode(@RequestParam("dictCode") Integer dictCode) {
        return configApi.getByCode(dictCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
    }

}

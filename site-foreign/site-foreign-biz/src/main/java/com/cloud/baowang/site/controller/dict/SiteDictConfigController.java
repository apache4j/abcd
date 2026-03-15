package com.cloud.baowang.site.controller.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigReqVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "站点-参数字典配置")
@AllArgsConstructor
@RestController
@RequestMapping("/siteDictConfig/api")
public class SiteDictConfigController {
    private final SystemDictConfigApi configApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页列表")
    public ResponseVO<Page<SystemDictConfigRespVO>> pageQuery(@RequestBody SystemDictConfigPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return configApi.pageQuery(queryVO);
    }

    @PostMapping("upd")
    @Operation(summary = "编辑")
    public ResponseVO<Boolean> upd(@RequestBody @Validated SystemDictConfigReqVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setOperator(CurrReqUtils.getAccount());
        return configApi.upd(reqVO);
    }

    @PostMapping("getByCode")
    @Operation(summary = "获取其中一个字典配置")
    public ResponseVO<SystemDictConfigRespVO> getByCode(@RequestBody SystemDictConfigPageQueryVO queryVO) {
        if(queryVO.getDictCode()==null){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return configApi.getByCode(queryVO.getDictCode(),CurrReqUtils.getSiteCode());
    }

    /*@GetMapping("queryWithdrawSwitch")
    @Operation(summary = "查询当前站点推送提款开关")
    public ResponseVO<SystemDictConfigRespVO> queryWithdrawSwitch() {
        return configApi.queryWithdrawSwitch(CurrReqUtils.getSiteCode(), DictCodeConfigEnums.WITHDRAW_SOUND_SWITCH.getCode());
    }*/
}

package com.cloud.baowang.site.controller.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalInfoApi;
import com.cloud.baowang.user.api.api.medal.SiteMedalInfoApi;
import com.cloud.baowang.user.api.vo.IdReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoDetailRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoStatusReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoUpdateReqVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "会员-会员勋章管理-勋章信息")
@RequestMapping("/siteMedalInfo/api/")
@AllArgsConstructor
public class SiteMedalInfoController {

    private final SiteMedalInfoApi siteMedalInfoApi;

    private final MedalInfoApi medalInfoApi;


    @PostMapping("selectPage")
    @Operation(summary = "勋章信息分页查询")
    ResponseVO<Page<SiteMedalInfoRespVO>> selectPage(@RequestBody @Validated SiteMedalInfoReqVO siteMedalInfoReqVO){
        siteMedalInfoReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteMedalInfoApi.listPage(siteMedalInfoReqVO);
    }

    @PostMapping("info")
    @Operation(summary = "勋章详情信息")
    ResponseVO<SiteMedalInfoDetailRespVO> info(@RequestBody @Validated IdReqVO idReqVO){
        return siteMedalInfoApi.info(idReqVO);
    }


    @PostMapping("update")
    @Operation(summary = "勋章信息修改")
    ResponseVO<Void> update(@RequestBody @Validated SiteMedalInfoUpdateReqVO siteMedalInfoUpdateReqVO){
        siteMedalInfoUpdateReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        siteMedalInfoUpdateReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteMedalInfoApi.update(siteMedalInfoUpdateReqVO);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "勋章信息启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SiteMedalInfoStatusReqVO siteMedalInfoStatusReqVO){
        siteMedalInfoStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteMedalInfoApi.enableOrDisable(siteMedalInfoStatusReqVO);
    }

    @Operation(summary = "下拉框 ENABLE_DISABLE_TYPE 启用禁用状态")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        Map<String, Object> result = Maps.newHashMap();
        String languageCode= CurrReqUtils.getLanguage();
        ResponseVO<List<MedalInfoRespVO>> medalRespVo= medalInfoApi.listAll();
        if(medalRespVo.isOk()){
            List<CodeValueVO> medalCondLists = Lists.newArrayList();
            List<MedalInfoRespVO> medalInfoRespVOS=medalRespVo.getData().stream().filter(o->o.getLanguageCode().equals(languageCode)).toList();
            for(MedalInfoRespVO medalInfoRespVO:medalInfoRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(medalInfoRespVO.getLanguageCode());
                codeValueVO.setCode(medalInfoRespVO.getMedalCode());
                codeValueVO.setValue(medalInfoRespVO.getUnlockCondName());
                medalCondLists.add(codeValueVO);
            }
            result.put("medalCondLists", medalCondLists);
        }

        return ResponseVO.success(result);
    }



}

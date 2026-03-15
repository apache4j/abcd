package com.cloud.baowang.user.api.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.IdReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoDetailRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoStatusReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteSiteMedalInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC-勋章信息api")
public interface SiteMedalInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/siteMedalIfo/api";

    @Operation(summary = "勋章信息分页查询")
    @PostMapping(value = PREFIX+"/listPage")
    ResponseVO<Page<SiteMedalInfoRespVO>> listPage(@RequestBody SiteMedalInfoReqVO siteMedalInfoReqVO);


    @Operation(summary = "勋章信息排序查询")
    @PostMapping(value = PREFIX+"/listAllBySort/{siteCode}")
    ResponseVO<List<SiteMedalInfoRespVO>> listAllBySort(@PathVariable("siteCode") String siteCode);

    @Operation(summary = "勋章信息查询")
    @PostMapping(value = PREFIX+"/selectBySiteCode/{siteCode}")
    ResponseVO<List<SiteMedalInfoRespVO>> selectBySiteCode(@PathVariable("siteCode") String siteCode);


    @Operation(summary = "按照条件查询勋章")
    @PostMapping(value = PREFIX+"/selectByCond")
    ResponseVO<SiteMedalInfoRespVO> selectByCond(@RequestBody SiteMedalInfoCondReqVO siteMedalInfoCondReqVO);


    @Operation(summary = "站点勋章初始化")
    @PostMapping(value = PREFIX+"/init/{siteCode}")
    ResponseVO<Boolean> init(@PathVariable("siteCode")String siteCode);


    @Operation(summary = "勋章信息修改")
    @PostMapping(value = PREFIX+"/update")
    ResponseVO<Void> update(@RequestBody SiteMedalInfoUpdateReqVO siteMedalInfoUpdateReqVO);

    @Operation(summary = "勋章信息 启用禁用")
    @PostMapping(value = PREFIX+"/enableOrDisable")
    ResponseVO<Void> enableOrDisable(@RequestBody SiteMedalInfoStatusReqVO siteMedalInfoStatusReqVO);

    @Operation(summary = "勋章详情信息")
    @PostMapping(value = PREFIX+"/info")
    ResponseVO<SiteMedalInfoDetailRespVO> info(@RequestBody IdReqVO idReqVO);
}

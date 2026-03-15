package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddSortVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigPageQueryVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteUserAvatarConfig", value = ApiConstants.NAME)
@Tag(name = "RPC 会员注册登录 服务")
public interface SiteUserAvatarConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/siteUserAvatarConfig/api";

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页查询头像列表")
    ResponseVO<Page<SiteUserAvatarConfigRespVO>> pageQuery(@RequestBody SiteUserAvatarConfigPageQueryVO queryVO);

    @PostMapping(PREFIX + "addConfig")
    @Operation(summary = "新增头像配置")
    ResponseVO<Boolean> addConfig(@RequestBody SiteUserAvatarConfigAddVO addVO);

    @PostMapping(PREFIX + "updConfig")
    @Operation(summary = "修改头像配置")
    ResponseVO<Boolean> updConfig(@RequestBody SiteUserAvatarConfigAddVO addVO);

    @PostMapping(PREFIX + "enableOrDisAble")
    @Operation(summary = "启用/禁用")
    ResponseVO<Boolean> enableOrDisAble(@RequestBody SiteUserAvatarConfigAddVO addVO);

    @GetMapping(PREFIX + "del")
    @Operation(summary = "删除")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @GetMapping(PREFIX + "getListBySiteCode")
    @Operation(summary = "获取站点下全部未禁用头像列表")
    ResponseVO<List<SiteUserAvatarConfigRespVO>> getListBySiteCode(@RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "getAvatarConfigByTXIdSiteCode")
    @Operation(summary = "根据头像id,站点code,获取用户头像信息")
    ResponseVO<SiteUserAvatarConfigRespVO> getAvatarConfigByTXIdSiteCode(@RequestParam("siteCode") String siteCode,
                                                                         @RequestParam("avatarId") String avatarId);

    @GetMapping(PREFIX + "getRandomUserAvatar")
    @Operation(summary = "获取站点下随机一个启用的头像")
    SiteUserAvatarConfigRespVO getRandomUserAvatar(@RequestParam("siteCode") String siteCode);

    @PostMapping("addSort")
    @Operation(summary = "添加排序")
    ResponseVO<Boolean> addSort(@RequestBody List<SiteUserAvatarConfigAddSortVO> addSortVOS);

    @GetMapping("getSortList")
    @Operation(summary = "获取排序列表")
    ResponseVO<List<SiteUserAvatarConfigAddSortVO>> getSortList(@RequestParam("siteCode") String siteCode);
}
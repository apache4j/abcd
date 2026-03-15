package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserAvatarConfigApi;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddSortVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigAddVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigPageQueryVO;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 新增会员审核
 *
 * @author aomiao
 */
@Tag(name = "会员头像配置")
@RestController
@RequestMapping("/user-avatarConfig/api")
@RequiredArgsConstructor
public class SiteUserAvatarConfigController {
    private final SiteUserAvatarConfigApi configApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询")
    public ResponseVO<Page<SiteUserAvatarConfigRespVO>> pageQuery(@RequestBody SiteUserAvatarConfigPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return configApi.pageQuery(queryVO);
    }

    @PostMapping("addConfig")
    @Operation(summary = "新增配置")
    public ResponseVO<Boolean> addConfig(@RequestBody @Valid SiteUserAvatarConfigAddVO addVO) {
        addVO.setSiteCode(CurrReqUtils.getSiteCode());
        addVO.setCreator(CurrReqUtils.getAccount());
        addVO.setCreatedTime(System.currentTimeMillis());
        addVO.setUpdater(CurrReqUtils.getAccount());
        addVO.setUpdatedTime(System.currentTimeMillis());
        return configApi.addConfig(addVO);
    }

    @PostMapping("updConfig")
    @Operation(summary = "修改配置")
    public ResponseVO<Boolean> updConfig(@RequestBody SiteUserAvatarConfigAddVO addVO) {
        addVO.setSiteCode(CurrReqUtils.getSiteCode());
        addVO.setUpdater(CurrReqUtils.getAccount());
        addVO.setUpdatedTime(System.currentTimeMillis());
        return configApi.updConfig(addVO);
    }

    @GetMapping("del")
    @Operation(summary = "删除")
    public ResponseVO<Boolean> del(@RequestParam("id") String id) {
        return configApi.del(id);
    }

    @GetMapping("enableOrDisAble")
    @Operation(summary = "启用/禁用")
    public ResponseVO<Boolean> enableOrDisAble(@RequestParam("id") String id, @RequestParam("status") Integer status) {
        SiteUserAvatarConfigAddVO addVO = new SiteUserAvatarConfigAddVO();
        addVO.setId(id);
        addVO.setStatus(status);
        addVO.setSiteCode(CurrReqUtils.getSiteCode());
        addVO.setUpdater(CurrReqUtils.getAccount());
        addVO.setUpdatedTime(System.currentTimeMillis());
        return configApi.enableOrDisAble(addVO);
    }

    @PostMapping("addSort")
    @Operation(summary = "添加排序")
    public ResponseVO<Boolean> addSort(@RequestBody List<SiteUserAvatarConfigAddSortVO> addSortVOS) {
        String account = CurrReqUtils.getAccount();
        long updTime = System.currentTimeMillis();
        for (SiteUserAvatarConfigAddSortVO addSortVO : addSortVOS) {
            addSortVO.setUpdater(account);
            addSortVO.setUpdatedTime(updTime);
        }
        return configApi.addSort(addSortVOS);
    }

    @GetMapping("getSortList")
    @Operation(summary = "获取排序列表")
    public ResponseVO<List<SiteUserAvatarConfigAddSortVO>> getSortList() {
        String siteCode = CurrReqUtils.getSiteCode();
        return configApi.getSortList(siteCode);
    }
}

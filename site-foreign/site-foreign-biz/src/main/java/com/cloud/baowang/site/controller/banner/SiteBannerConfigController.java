package com.cloud.baowang.site.controller.banner;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayGameClassInfoApi;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.banner.SiteBannerConfigApi;
import com.cloud.baowang.system.api.vo.banner.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/site-banner")
@Tag(name = "SiteBannerConfig", description = "站点轮播图配置管理")
@RequiredArgsConstructor
public class SiteBannerConfigController {

    private final SiteBannerConfigApi configApi;
    private final SystemParamApi systemParamApi;
    private final PlayGameClassInfoApi gameClassInfoApi;
    private final GameInfoApi gameInfoApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        //展示位置(场馆类型),选择区域,时效,是否跳转,启用状态,跳转目标
        param.add(CommonConstant.VENUE_TYPE);
        param.add(CommonConstant.BANNER_AREA);
        param.add(CommonConstant.BANNER_DURATION);
        param.add(CommonConstant.YES_NO);
        param.add(CommonConstant.ENABLE_DISABLE_TYPE);
        param.add(CommonConstant.BANNER_LINK_TARGET);
        param.add(CommonConstant.HOME_PAGE_TOP);
        GameSortRequestVO gameSortRequestVO = new GameSortRequestVO();
        gameSortRequestVO.setHomeSort(true);
        ResponseVO<Map<String, List<CodeValueVO>>> result = systemParamApi.getSystemParamsByList(param);
        Map<String, List<CodeValueVO>> data = result.getData();

        ResponseVO<List<GameOneClassInfoVO>> resp = gameClassInfoApi.getGameOneClassInfoList(gameSortRequestVO);


        if (resp.isOk()) {
            List<GameOneClassInfoVO> gameData = resp.getData();
            if (CollectionUtil.isNotEmpty(gameData)) {
                List<CodeValueVO> codeValueVOS = gameData.stream()
                        .map(datum -> {
                            CodeValueVO codeValueVO = new CodeValueVO();
                            String directoryI18nCode = datum.getDirectoryI18nCode();
                            codeValueVO.setCode(datum.getId());
                            codeValueVO.setValue(directoryI18nCode);
                            return codeValueVO;
                        }).toList();
                List<CodeValueVO> homeTop = data.get(CommonConstant.HOME_PAGE_TOP);
                homeTop.addAll(codeValueVOS);
                data.put(CommonConstant.HOME_PAGE_TOP, homeTop);
                result.setData(data);
            }
        }
        return result;
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询站点轮播图配置")
    public ResponseVO<Page<SiteBannerConfigPageRespVO>> getPage(@RequestBody SiteBannerConfigPageQueryReqVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return configApi.getPage(reqVO);
    }

    @PostMapping("/getListBySiteCode")
    @Operation(summary = "根据站点编码获取轮播图配置列表")
    public ResponseVO<List<SiteBannerConfigPageRespVO>> getListBySiteCode(@RequestBody SiteBannerConfigAppQueryVO queryVO) {
        return configApi.getListBySiteCode(queryVO);
    }

    @GetMapping("getGameList")
    @Operation(summary = "选择跳转类型为游戏时,加载此下拉框-保存时,传入gameId字段")
    public ResponseVO<List<SiteGameInfoVO>> getGameList() {
        GameInfoRequestVO query = new GameInfoRequestVO();
        return gameInfoApi.siteGameInfoList(query);
    }

    @PostMapping("/createConfig")
    @Operation(summary = "新增站点轮播图配置")

    public ResponseVO<Boolean> createConfig(@RequestBody @Validated SiteBannerConfigReqVO bannerConfigVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String account = CurrReqUtils.getAccount();
        bannerConfigVO.setSiteCode(siteCode);
        bannerConfigVO.setOperator(account);

        return configApi.createConfig(bannerConfigVO);
    }

    @PostMapping("/updateConfig")
    @Operation(summary = "更新站点轮播图配置")
    public ResponseVO<Boolean> updateConfig(@RequestBody @Validated SiteBannerConfigReqVO bannerConfigVO) {
        bannerConfigVO.setSiteCode(CurrReqUtils.getSiteCode());
        bannerConfigVO.setOperator(CurrReqUtils.getAccount());
        return configApi.updateConfig(bannerConfigVO);
    }

    @GetMapping("/enableAndDisableStatus")
    @Operation(summary = "禁用/启用轮播图")
    public ResponseVO<Boolean> enableAndDisableStatus(@RequestParam("id") String id,
                                                      @RequestParam("status") Integer status) {
        SiteBannerConfigReqVO reqVO = new SiteBannerConfigReqVO();
        reqVO.setOperator(CurrReqUtils.getAccount());
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setId(id);
        reqVO.setStatus(status);
        return configApi.enableAndDisableStatus(reqVO);
    }

    @GetMapping("/deleteConfigById")
    @Operation(summary = "根据ID删除站点轮播图配置")
    public ResponseVO<Boolean> deleteConfigById(@Parameter(description = "轮播图配置ID") @RequestParam("id") String id) {
        return configApi.deleteConfigById(id);
    }

    @GetMapping("/getConfigById")
    @Operation(summary = "根据ID获取站点轮播图配置详情")
    public ResponseVO<SiteBannerConfigRespVO> getConfigById(@Parameter(description = "轮播图配置ID") @RequestParam("id") String id) {
        return configApi.getConfigById(id);
    }

    @GetMapping("/querySortList")
    @Operation(summary = "获取排序列表")
    public ResponseVO<List<SiteBannerConfigAddSortVO>> querySortList(@RequestParam("gameOneClassId") String gameOneClassId) {
        String siteCode = CurrReqUtils.getSiteCode();
        return configApi.querySortList(siteCode, gameOneClassId);
    }

    @PostMapping("updSortList")
    @Operation(summary = "编辑排序")
    public ResponseVO<Boolean> updSortList(@RequestBody @Validated List<SiteBannerConfigAddSortVO> sortVOS) {
        String account = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        String gameOneClassId = sortVOS.get(0).getGameOneClassId();
        return configApi.updSortList(gameOneClassId, sortVOS, account, siteCode);
    }
}

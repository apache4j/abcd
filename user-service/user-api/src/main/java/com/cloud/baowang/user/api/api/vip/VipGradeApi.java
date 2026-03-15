package com.cloud.baowang.user.api.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPVenueExeVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeUpdateVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "vipGradeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - vipGrade")
public interface VipGradeApi {

    String PREFIX = ApiConstants.PREFIX + "/vipGrade/api/";

    @PostMapping(PREFIX + "getVipGrade")
    @Operation(summary = "VIP等级-下拉框")
    List<CodeValueNoI18VO> getVipGrade();

    @PostMapping(PREFIX + "updateVIPGrade")
    @Operation(summary = "会员VIP等级配置保存")
    ResponseVO<?> updateVIPGrade(@RequestBody VIPGradeUpdateVO vipGradeUpdateVO);

    @PostMapping(PREFIX + "queryVIPGrade")
    @Operation(summary = "查询会员VIP等级配置")
    ResponseVO<Page<SiteVIPGradeVO>> queryVIPGradePage(@RequestBody PageVO pageVO);

    @PostMapping(PREFIX + "queryVIPGradeByGrade")
    @Operation(summary = "根据VIP等级查询相关VIP配置相关信息")
    SiteVIPGradeVO queryVIPGradeByGrade(@RequestParam("vipRankCode") String vipRankCode);

    @GetMapping(PREFIX + "queryAllVIPGrade")
    @Operation(summary = "查询所有VIP配置相关信息")
    List<SiteVIPGradeVO> queryAllVIPGrade(@RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "queryAllVIPGradeBySiteCode")
    @Operation(summary = "查询所有VIP配置相关信息")
    List<SiteVIPGradeVO> queryAllVIPGradeBySiteCode(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "查询场馆经验值比例")
    @GetMapping(value = "/getVenueExperience")
    List<SiteVIPVenueExeVO> getVenueExperience();

    @Operation(summary = "根据vip段位code+siteCode，获取当前vip段位下所有vip等级")
    @GetMapping("getSiteVipGradeList")
    List<SiteVIPGradeVO> getSiteVipGradeList(@RequestParam("siteCode") String siteCode, @RequestParam("vipRankCode") String vipRankCode);

    @Operation(summary = "批量获取当前站点下对应的gradeCodes")
    @PostMapping("getSiteVipGradeListByCodes")
    List<SiteVIPGradeVO> getSiteVipGradeListByCodes(@RequestParam("siteCode") String siteCode, @RequestBody List<Integer> vipGradeCodes);

    @Operation(summary = "批量获取当前站点下对应的gradeCodes")
    @GetMapping("getSiteVipGradeListByCodes")
    SiteVIPGradeVO getSiteVipGradeByCodeAndSiteCode(@RequestParam("siteCode") String siteCode, @RequestParam("vipGradeCode") Integer vipGradeCode);


    @Operation(summary = "保存场馆经验值比例")
    @PostMapping("addVenueExperience")
    Boolean addVenueExperience(@RequestParam("siteCode") String siteCode, @RequestBody List<SiteVIPVenueExeVO> vos);

    @GetMapping("getSystemVipGradeList")
    @Operation(summary = "批量获取总台vip等级列表")
    List<VIPGradeVO> getSystemVipGradeList();

    @GetMapping("getFirstSiteVipGrade")
    @Operation(summary = "获取当前站点最低vip等级")
    SiteVIPGradeVO getFirstSiteVipGrade(@RequestParam("siteCode") String siteCode);

    @GetMapping("getLastSiteVipGrade")
    @Operation(summary = "获取当前站点最大vip等级")
    SiteVIPGradeVO getLastSiteVipGrade(@RequestParam("siteCode") String siteCode);

    @GetMapping("getAllSiteVipGrade")
    @Operation(summary = "获取所有站点的所有已配置vip等级-根据siteCode分组")
    Map<String, List<SiteVIPGradeVO>> getAllSiteVipGrade();


    @GetMapping("getVipGradeTopTen")
    @Operation(summary = "获取前10个VIP等级")
    List<CodeValueNoI18VO> getVipGradeTopTen();


    @GetMapping("queryAllVIPGradeNameMap")
    @Operation(summary = "获取当前站点所有VIP等级MAP")
    Map<Integer,String> queryAllVIPGradeNameMap(@RequestParam("siteCode") String siteCode);

}

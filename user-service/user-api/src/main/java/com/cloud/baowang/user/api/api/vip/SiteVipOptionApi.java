package com.cloud.baowang.user.api.api.vip;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.SiteVipFeeRateVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "SiteVipOptionApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - SiteVipOptionApi")
public interface SiteVipOptionApi {
    String PREFIX = ApiConstants.PREFIX + "/siteVipOptionApi/api/";

    @Operation(summary = "VIP奖励记录查询")
    @PostMapping(value = PREFIX + "init")
    ResponseVO<Boolean> initVIP(@RequestParam("siteCode") String siteCode, @RequestBody List<String> currency);

    @Operation(summary = "VIP奖励记录查询")
    @PostMapping(value = PREFIX + "update")
    ResponseVO<Void> updateSiteVipOptionVO(@RequestBody SiteVipOptionVO vo);

    @Operation(summary = "VIP奖励记录查询")
    @PostMapping(value = PREFIX + "getList")
    ResponseVO<List<SiteVipOptionVO>> getList(@RequestParam("siteCode") String siteCode,@RequestParam("currency") String currency);

    @Operation(summary = "获取所有VIP等级")
    @PostMapping(value = PREFIX + "getCnVipGradeList")
    ResponseVO<List<VIPGradeVO>> getCnVipGradeList();

    @Operation(summary = "获取所有VIP等级")
    @PostMapping(value = PREFIX + "cnVipUpDownAllSiteCode")
    ResponseVO<Void> cnVipUpDownAllSiteCode(@RequestParam(value = "timezone" ,required = false) String timezone);

    @Operation(summary = "获取大陆盘VIP等级MAP")
    @PostMapping(value = PREFIX + "getCnVipGradeMap")
    Map<Integer,String> getCnVipGradeMap();


    @Operation(summary = "获取VIP等级相关费率信息")
    @PostMapping(value = PREFIX + "getVipGradeSiteCodeAndCurrency")
    SiteVipFeeRateVO getVipGradeSiteCodeAndCurrency(@RequestParam("siteCode")String siteCode, @RequestParam("vipGradeCode")Integer vipGradeCode, @RequestParam("currencyCode")String currencyCode,@RequestParam("withdrawWayId") String withdrawWayId);

    @Operation(summary = "获取VIP等级信息")
    @PostMapping(value = PREFIX + "getVipGradeInfoByCode")
    SiteVipOptionVO getVipGradeInfoByCode(@RequestParam("siteCode")String siteCode, @RequestParam("vipGradeCode")Integer vipGradeCode, @RequestParam("currencyCode")String currencyCode);

    @Operation(summary = "初始化VIP等级")
    @PostMapping(value = PREFIX + "initVIPGrade")
    ResponseVO<Void> initVIPGrade(@RequestParam(value = "siteCode" ) String siteCode);

}

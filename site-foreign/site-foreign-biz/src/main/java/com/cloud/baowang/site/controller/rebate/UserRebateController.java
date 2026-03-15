package com.cloud.baowang.site.controller.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.rebate.UserVenueRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.user.RebateListVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditRspVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateDetailsRspVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name = "站点-返水审核列表")
@RequestMapping("/user-rebate/api/")
@AllArgsConstructor
@Slf4j
public class UserRebateController {

    private final UserVenueRebateApi userVenueRebateApi;

    private final VipRankApi vipRankApi;

    @PostMapping("userRebatePage")
    @Operation(summary = "返水审核列表")
    ResponseVO<Page<UserRebateAuditRspVO>> userRebatePage(@RequestBody UserRebateAuditQueryVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setTimeZone(CurrReqUtils.getTimezone());
        return userVenueRebateApi.userRebatePage(vo);
    }

    @PostMapping("userRebateDetails")
    @Operation(summary = "返水明细 只要userId,orderNo")
    ResponseVO<List<UserRebateDetailsRspVO>> userRebateDetails(@RequestBody UserRebateAuditQueryVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userVenueRebateApi.userRebateDetails(vo);
    }

    @Operation(summary = "锁单或解锁<批量> ")
    @PostMapping(value = "lockRebate")
    public ResponseVO<Boolean> lockRebate(@Valid @RequestBody RebateListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        return userVenueRebateApi.lockRebate(vo);
    }

    @Operation(summary = "拒绝<批量> ")
    @PostMapping(value = "rejectRebate")
    public ResponseVO<Boolean> rejectRebate(@RequestBody @Valid RebateListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        return userVenueRebateApi.rejectRebate(vo);
    }

    @Operation(summary = "发放<批量>")
    @PostMapping(value = "issueRebate")
    public ResponseVO<Boolean> issueRebate(@RequestBody @Valid RebateListVO vo) {
        vo.setOperatorName(CurrReqUtils.getAccount());
        return userVenueRebateApi.issueRebate(vo);
    }

    @Operation(summary = "vip段位下拉框")
    @PostMapping(value = "getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox(){
        List<SiteVIPRankVO> vipRankListBySiteCode = vipRankApi.getVipRankListBySiteCode(CurrReqUtils.getSiteCode()).getData();

        List<CodeValueVO> vipRankNameEnums = Lists.newArrayList();

        for (SiteVIPRankVO respVo : vipRankListBySiteCode) {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setType(respVo.getVipRankCode().toString());
            codeValueVO.setCode(respVo.getVipRankCode().toString());
            codeValueVO.setValue(respVo.getVipRankNameI18nCode());
            vipRankNameEnums.add(codeValueVO);
        }
        return ResponseVO.success(vipRankNameEnums);
    }
}

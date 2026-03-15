package com.cloud.baowang.site.controller.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPVenueExeVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/7 11:24
 * @Version : 1.0
 */
@Tag(name = "站点后台vip等级相关配置")
@RestController
@RequestMapping("/vip/api")
@AllArgsConstructor
public class VipGradeController {

    private final VipGradeApi vipGradeApi;

    @Operation(summary = "VIP等级查询")
    @PostMapping(value = "/queryVIPGrade")
    public ResponseVO<Page<SiteVIPGradeVO>> queryVIPGrade(@RequestBody PageVO pageVO) {
        return vipGradeApi.queryVIPGradePage(pageVO);
    }

    @GetMapping("detail")
    @Operation(summary = "详情")
    public ResponseVO<SiteVIPGradeVO> detail(@RequestParam("vipGradeCode") Integer vipGradeCode) {
        return ResponseVO.success(vipGradeApi.getSiteVipGradeByCodeAndSiteCode(CurrReqUtils.getSiteCode(), vipGradeCode));
    }

    @Operation(summary = "VIP等级编辑")
    @PostMapping(value = "/updateVIPGrade")
    public ResponseVO<?> updateVIPGrade(@Valid @RequestBody VIPGradeUpdateVO vipGradeUpdateVO) {
        return vipGradeApi.updateVIPGrade(vipGradeUpdateVO);
    }

    @Operation(summary = "站点后台VIP等级下拉查询")
    @PostMapping(value = "/getVipGrade")
    public ResponseVO<List<CodeValueNoI18VO>> getVipGrade() {
        List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(CurrReqUtils.getSiteCode());
        List<CodeValueNoI18VO> list = siteVIPGradeVOS.stream()
                .map(siteVIPGrade -> new CodeValueNoI18VO(String.valueOf(siteVIPGrade.getVipGradeCode()), siteVIPGrade.getVipGradeName()))
                .toList();
        return ResponseVO.success(list);
    }

    @Operation(summary = "站点后台vip等级")
    @GetMapping(value = "/getNotVipGrade")
    public ResponseVO<List<CodeValueNoI18VO>> getNotVipGrade(@RequestParam("vipRankCode") Integer vipRankCode) {
        List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(CurrReqUtils.getSiteCode());
        List<CodeValueNoI18VO> list = new ArrayList<>();
        String code = YesOrNoEnum.YES.getCode();
        String noCode = YesOrNoEnum.NO.getCode();
        for (SiteVIPGradeVO siteVIPGradeVO : siteVIPGradeVOS) {
            CodeValueNoI18VO codeValueNoI18VO = new CodeValueNoI18VO(String.valueOf(siteVIPGradeVO.getVipGradeCode()), siteVIPGradeVO.getVipGradeName());
            if (siteVIPGradeVO.getVipRankCode() != null && !siteVIPGradeVO.getVipRankCode().equals(vipRankCode)) {
                codeValueNoI18VO.setType(noCode);
            } else {
                codeValueNoI18VO.setType(code);
            }
            list.add(codeValueNoI18VO);
        }
        return ResponseVO.success(list);
    }

    @Operation(summary = "查询场馆经验值比例")
    @GetMapping(value = "/getVenueExperience")
    public ResponseVO<List<SiteVIPVenueExeVO>> getVenueExperience() {
        return ResponseVO.success(vipGradeApi.getVenueExperience());
    }

    @Operation(summary = "保存场馆经验值比例")
    @PostMapping("addVenueExperience")
    public ResponseVO<Boolean> addVenueExperience(@RequestBody List<SiteVIPVenueExeVO> vos) {
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(vipGradeApi.addVenueExperience(siteCode, vos));
    }

}

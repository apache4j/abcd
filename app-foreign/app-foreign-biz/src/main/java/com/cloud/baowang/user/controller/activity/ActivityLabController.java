package com.cloud.baowang.user.controller.activity;

import com.cloud.baowang.activity.api.api.SiteActivityLabApi;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabsAPPVO;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabsVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "活动页签相关")
@RestController
@RequestMapping("/activityLabs/api")
@AllArgsConstructor
public class ActivityLabController {
    private final SiteActivityLabApi activityLabApi;

    @GetMapping("queryList")
    @Operation(summary = "分页查询所有站点活动页签列表，status的值同system_param enable_disable_status code值")
    public ResponseVO<List<SiteActivityLabsVO>> siteQueryList() {
        String siteCode = CurrReqUtils.getSiteCode();
        return activityLabApi.siteQueryList(siteCode);
    }


}

package com.cloud.baowang.user.controller.activityV2;

import com.cloud.baowang.activity.api.api.SiteActivityLabApi;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabsVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "活动页签相关V2")
@RestController
@RequestMapping("/activityLabs/v2/api")
@AllArgsConstructor
public class ActivityLabV2Controller {
    private final SiteActivityLabApi activityLabApi;

    @GetMapping("queryList")
    @Operation(summary = "分页查询所有站点活动页签列表")
    public ResponseVO<List<SiteActivityLabsVO>> siteQueryList() {
        String siteCode = CurrReqUtils.getSiteCode();
        return activityLabApi.siteQueryListV2(siteCode);
    }


}

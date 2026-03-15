package com.cloud.baowang.user.api.api.push;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.site.IPTop10ReqVO;
import com.cloud.baowang.user.api.vo.site.VisitFromResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @className: SiteStatistics
 * @author: wade
 * @description: 站点首页统计
 * @date: 12/8/24 10:05
 */
@FeignClient(contextId = "remotePushApi", value = ApiConstants.NAME)
@Tag(name = "站点首页统计信息服务")
public interface PushApi {
    String PREFIX = ApiConstants.PREFIX + "/pushApi/api";



    @Operation(summary = "访问来源")
    @PostMapping(value = PREFIX + "/getVisitFrom")
    ResponseVO<List<VisitFromResVO>> getVisitFrom(IPTop10ReqVO vo);
}

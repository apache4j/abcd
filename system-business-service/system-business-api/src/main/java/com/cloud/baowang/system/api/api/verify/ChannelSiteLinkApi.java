package com.cloud.baowang.system.api.api.verify;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.verify.ChannelSiteLinkVO;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:23
 */
@FeignClient(contextId = "remoteChannelLinkApi", value = ApiConstants.NAME)
@Tag(name = "RPC 邮箱短信站点关联服务 - ChannelSiteLinkApi")
public interface ChannelSiteLinkApi {
    String PREFIX = ApiConstants.PREFIX + "/channelLinkConfig/api/";

    @PostMapping(PREFIX +"mailSiteAddBatch")
    @Operation(summary ="邮箱站点关联批量插入")
    ResponseVO<Boolean> mailSiteAddBatch(@RequestParam("siteCode")String siteCode,@RequestBody ChannelSiteLinkVO mailSiteLinkVO,@RequestParam("siteName") String siteName);

    @PostMapping(PREFIX +"smsSiteAddBatch")
    @Operation(summary ="短信站点关联批量插入")
    ResponseVO<Boolean> smsSiteAddBatch(@RequestParam("siteCode")String siteCode, @RequestBody ChannelSiteLinkVO smsSiteLinkVO,@RequestParam("siteName") String siteName);

    @PostMapping(PREFIX +"editMailStatus")
    @Operation(summary ="站点邮箱通道状态修改")
    ResponseVO editMailStatus(@RequestBody ChannelStatusVO channelStatusVO);

    @PostMapping(PREFIX +"editSmsStatus")
    @Operation(summary ="站点短信通道状态修改")
    ResponseVO editSmsStatus(@RequestBody ChannelStatusVO channelStatusVO);
}

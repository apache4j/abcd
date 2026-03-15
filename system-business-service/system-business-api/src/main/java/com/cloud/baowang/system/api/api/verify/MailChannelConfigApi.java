package com.cloud.baowang.system.api.api.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:23
 */
@FeignClient(contextId = "remoteMailChannelConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 短信通道配置服务 - MailChannelConfigApi")
public interface MailChannelConfigApi {
    String PREFIX = ApiConstants.PREFIX + "/MailChannelConfig/api/";

    @PostMapping(PREFIX +"getMailConfigPage")
    @Operation(summary ="查询短信通道配置列表分页")
    ResponseVO<Page<MailChannelConfigPageVO>> getMailConfigPage(@RequestBody MailChannelQueryVO mailChannelQueryVO);

    @PostMapping(PREFIX +"queryEmailChannel")
    @Operation(summary ="查询邮箱通道配置列表分页")
    ResponseVO<SiteEmailChannelVO> queryEmailChannel(@RequestBody MailChannelQueryVO reqVO);

    @PostMapping(PREFIX +"editStatus")
    @Operation(summary ="邮箱通道状态修改")
    ResponseVO editStatus(@RequestBody ChangeStatusVO changeStatusVO);

    @PostMapping(PREFIX +"getSiteMailConfigPage")
    @Operation(summary ="站点后台查询邮箱通道列表")
    Page<SiteBackEmailChannelPageVO> getSiteMailConfigPage(@RequestBody MailChannelQueryVO reqVO);

    @GetMapping(PREFIX +"mailAuthorList")
    @Operation(summary ="站点后台查询邮箱通道列表")
    ResponseVO<List<SMSAuthorVO>> mailAuthorList(@RequestParam("id") String id);
}

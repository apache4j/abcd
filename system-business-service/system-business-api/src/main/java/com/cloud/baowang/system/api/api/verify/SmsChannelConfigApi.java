package com.cloud.baowang.system.api.api.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:23
 */
@FeignClient(contextId = "remoteSmsChannelConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 短信通道配置服务 - SmsChannelConfigApi")
public interface SmsChannelConfigApi {
    String PREFIX = ApiConstants.PREFIX + "/smsChannelConfig/api/";

    @PostMapping(PREFIX +"getSmsConfigPage")
    @Operation(summary ="查询短信通道配置列表分页")
    ResponseVO<Page<SmsChannelConfigPageVO>> getSmsConfigPage(@RequestBody SmsChannelQueryVO smsChannelQueryVO);

    @PostMapping(PREFIX +"querySmsChannel")
    @Operation(summary ="新增站点查询短信通道配置列表分页")
    ResponseVO<SiteSmsChannelVO> querySmsChannel(@RequestBody SmsChannelQueryVO reqVO);

    @PostMapping(PREFIX +"editStatus")
    @Operation(summary ="短信通道状态修改")
    ResponseVO editStatus(@RequestBody ChangeStatusVO changeStatusVO);

    @PostMapping(PREFIX +"getAddressDownBox")
    @Operation(summary ="使用地区下拉框")
    List<CodeValueNoI18VO> getAddressDownBox();

    @PostMapping(PREFIX +"getSiteSmsConfigPage")
    @Operation(summary ="站点后台查询短信通道配置列表分页")
    Page<SiteBackSmsChannelConfigPageVO> getSiteSmsConfigPage(@RequestBody SmsChannelQueryVO reqVO);

    @PostMapping(PREFIX +"smsAuthorList")
    @Operation(summary ="授权短信列表")
    ResponseVO<List<SMSAuthorVO>> smsAuthorList(@RequestParam("id") String id);
}

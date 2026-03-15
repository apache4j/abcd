package com.cloud.baowang.site.controller.resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.ChannelSiteLinkApi;
import com.cloud.baowang.system.api.api.verify.SmsChannelConfigApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import com.cloud.baowang.system.api.vo.verify.SiteBackSmsChannelConfigPageVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigPageVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:37
 * @description:
 */
@Tag(name = "运营-资源管理-短信通道管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/sms-channel/api")
public class SmsChannelConfigController {
    private final SmsChannelConfigApi smsChannelConfigApi;
    private final ChannelSiteLinkApi channelSiteLinkApi;

    @Operation(summary = "下拉框-使用地区")
    @PostMapping(value = "/getAddressDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getAddressDownBox() {
        return ResponseVO.success(smsChannelConfigApi.getAddressDownBox());
    }

    @PostMapping("getSiteSmsConfigPage")
    @Operation(summary ="查询短信通道配置列表")
    ResponseVO<Page<SiteBackSmsChannelConfigPageVO>> getSiteSmsConfigPage(@RequestBody SmsChannelQueryVO smsChannelQueryVO) {
        smsChannelQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(smsChannelConfigApi.getSiteSmsConfigPage(smsChannelQueryVO));
    }

    @PostMapping("updateStatus")
    @Operation(summary ="短信通道状态修改")
    ResponseVO smsEditStatus(@RequestBody ChannelStatusVO channelStatusVO) {
        String adminName  = CurrReqUtils.getAccount();
        channelStatusVO.setUpdater(adminName);
        return channelSiteLinkApi.editSmsStatus(channelStatusVO);
    }

}

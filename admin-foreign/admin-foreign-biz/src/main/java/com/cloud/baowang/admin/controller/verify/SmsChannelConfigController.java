package com.cloud.baowang.admin.controller.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.SmsChannelConfigApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.SMSAuthorVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigPageVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:37
 * @description:
 */
@Tag(name = "资源管理-短信通道管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/sms-channel/api")
public class SmsChannelConfigController {
    private final SmsChannelConfigApi smsChannelConfigApi;

    @Operation(summary = "下拉框-使用地区")
    @PostMapping(value = "/getAddressDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getAddressDownBox() {
        return ResponseVO.success(smsChannelConfigApi.getAddressDownBox());
    }

    @PostMapping("getSmsConfigPage")
    @Operation(summary ="查询短信通道配置列表")
    ResponseVO<Page<SmsChannelConfigPageVO>> getSmsConfigPage(@RequestBody SmsChannelQueryVO smsChannelQueryVO) {
        return smsChannelConfigApi.getSmsConfigPage(smsChannelQueryVO);
    }

    @PostMapping("smsEditStatus")
    @Operation(summary ="短信通道状态修改")
    ResponseVO smsEditStatus(@RequestBody ChangeStatusVO changeStatusVO) {
        String adminName  = CurrReqUtils.getAccount();
        changeStatusVO.setUpdater(adminName);
        return smsChannelConfigApi.editStatus(changeStatusVO);
    }

    @GetMapping("smsAuthorList")
    @Operation(summary ="授权短信列表")
    ResponseVO<List<SMSAuthorVO>> smsAuthorList(@RequestParam("id") String id) {
        return smsChannelConfigApi.smsAuthorList(id);
    }

}

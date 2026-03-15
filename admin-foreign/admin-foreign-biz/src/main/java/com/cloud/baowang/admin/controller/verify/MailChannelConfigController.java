package com.cloud.baowang.admin.controller.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.MailChannelConfigApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigPageVO;
import com.cloud.baowang.system.api.vo.verify.MailChannelQueryVO;
import com.cloud.baowang.system.api.vo.verify.SMSAuthorVO;
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
@Tag(name = "资源管理-邮箱通道管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/mail-channel/api")
public class MailChannelConfigController {
    private final MailChannelConfigApi mailChannelConfigApi;

    @PostMapping("getMailConfigPage")
    @Operation(summary ="查询邮箱通道配置列表")
    ResponseVO<Page<MailChannelConfigPageVO>> getMailConfigPage(@RequestBody MailChannelQueryVO mailChannelQueryVO) {
        return mailChannelConfigApi.getMailConfigPage(mailChannelQueryVO);
    }

    @PostMapping("mailEditStatus")
    @Operation(summary ="邮箱通道状态修改")
    ResponseVO mailEditStatus(@RequestBody ChangeStatusVO changeStatusVO) {
        String adminName  = CurrReqUtils.getAccount();
        changeStatusVO.setUpdater(adminName);
        return mailChannelConfigApi.editStatus(changeStatusVO);
    }

    @GetMapping("mailAuthorList")
    @Operation(summary ="授权邮箱列表")
    ResponseVO<List<SMSAuthorVO>> mailAuthorList(@RequestParam("id") String id) {
        return mailChannelConfigApi.mailAuthorList(id);
    }

}

package com.cloud.baowang.site.controller.resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.ChannelSiteLinkApi;
import com.cloud.baowang.system.api.api.verify.MailChannelConfigApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigPageVO;
import com.cloud.baowang.system.api.vo.verify.MailChannelQueryVO;
import com.cloud.baowang.system.api.vo.verify.SiteBackEmailChannelPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:37
 * @description:
 */
@Tag(name = "运营-资源管理-邮箱通道管理")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/mail-channel/api")
public class MailChannelConfigController {
    private final MailChannelConfigApi mailChannelConfigApi;
    private final ChannelSiteLinkApi channelSiteLinkApi;

    @PostMapping("getSiteMailConfigPage")
    @Operation(summary ="查询邮箱通道配置列表")
    ResponseVO<Page<SiteBackEmailChannelPageVO>> getSiteMailConfigPage(@RequestBody MailChannelQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(mailChannelConfigApi.getSiteMailConfigPage(reqVO));
    }

    @PostMapping("updateStatus")
    @Operation(summary ="邮箱通道状态修改")
    ResponseVO mailEditStatus(@RequestBody ChannelStatusVO channelStatusVO) {
        String adminName  = CurrReqUtils.getAccount();
        channelStatusVO.setUpdater(adminName);
        return channelSiteLinkApi.editMailStatus(channelStatusVO);
    }

}

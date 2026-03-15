package com.cloud.baowang.system.api.verify;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.ChannelSiteLinkApi;
import com.cloud.baowang.system.api.vo.verify.ChannelSiteLinkVO;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import com.cloud.baowang.system.service.verify.MailSiteLinkService;
import com.cloud.baowang.system.service.verify.SmsSiteLinkService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:22
 * @description:
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class ChannelSiteLinkApiImpl implements ChannelSiteLinkApi {
    private final MailSiteLinkService mailSiteLinkService;
    private final SmsSiteLinkService smsSiteLinkService;

    @Override
    public ResponseVO<Boolean> mailSiteAddBatch(String siteCode,ChannelSiteLinkVO channelSiteLinkVO,String siteName) {
        return ResponseVO.success(mailSiteLinkService.addBatch(siteCode,channelSiteLinkVO,siteName));
    }

    @Override
    public ResponseVO<Boolean> smsSiteAddBatch(String siteCode,ChannelSiteLinkVO channelSiteLinkVO,String siteName) {
        return ResponseVO.success(smsSiteLinkService.addBatch(siteCode,channelSiteLinkVO,siteName));
    }

    @Override
    public ResponseVO editMailStatus(ChannelStatusVO channelStatusVO) {
        return ResponseVO.success(mailSiteLinkService.editStatus(channelStatusVO));
    }

    @Override
    public ResponseVO editSmsStatus(ChannelStatusVO channelStatusVO) {
        return ResponseVO.success(smsSiteLinkService.editStatus(channelStatusVO));
    }
}

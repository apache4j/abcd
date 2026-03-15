package com.cloud.baowang.system.api.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.MailChannelConfigApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.service.verify.MailChannelConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 16:22
 * @description:
 */
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class MailChannelConfigApiImpl implements MailChannelConfigApi {
    private final MailChannelConfigService mailChannelConfigService;

    @Override
    public ResponseVO<Page<MailChannelConfigPageVO>> getMailConfigPage(MailChannelQueryVO mailChannelQueryVO) {
        return ResponseVO.success(mailChannelConfigService.getMailConfigPage(mailChannelQueryVO));
    }

    @Override
    public ResponseVO<SiteEmailChannelVO> queryEmailChannel(MailChannelQueryVO reqVO) {
        return mailChannelConfigService.queryEmailChannel(reqVO);
    }

    @Override
    public ResponseVO editStatus(ChangeStatusVO changeStatusVO) {
        return ResponseVO.success(mailChannelConfigService.editStatus(changeStatusVO));
    }

    @Override
    public Page<SiteBackEmailChannelPageVO> getSiteMailConfigPage(MailChannelQueryVO reqVO) {
        return mailChannelConfigService.getSiteMailConfigPage(reqVO);
    }

    @Override
    public ResponseVO<List<SMSAuthorVO>> mailAuthorList(String id) {
        return mailChannelConfigService.mailAuthorList(id);
    }
}

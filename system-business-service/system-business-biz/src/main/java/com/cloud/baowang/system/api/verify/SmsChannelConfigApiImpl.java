package com.cloud.baowang.system.api.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.SmsChannelConfigApi;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.service.verify.SmsChannelConfigService;
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
public class SmsChannelConfigApiImpl implements SmsChannelConfigApi {
    private final SmsChannelConfigService smsChannelConfigService;

    @Override
    public ResponseVO<Page<SmsChannelConfigPageVO>> getSmsConfigPage(SmsChannelQueryVO smsChannelQueryVO) {
        return ResponseVO.success(smsChannelConfigService.getSmsConfigPage(smsChannelQueryVO));
    }

    @Override
    public ResponseVO<SiteSmsChannelVO> querySmsChannel(final SmsChannelQueryVO reqVO) {
        return smsChannelConfigService.querySmsChannel(reqVO);
    }

    @Override
    public ResponseVO editStatus(ChangeStatusVO changeStatusVO) {
        return ResponseVO.success(smsChannelConfigService.editStatus(changeStatusVO));
    }

    @Override
    public List<CodeValueNoI18VO> getAddressDownBox() {
        return smsChannelConfigService.getAddressDownBox();
    }

    @Override
    public Page<SiteBackSmsChannelConfigPageVO> getSiteSmsConfigPage(SmsChannelQueryVO reqVO) {
        return smsChannelConfigService.getSiteSmsConfigPage(reqVO);
    }

    @Override
    public ResponseVO<List<SMSAuthorVO>> smsAuthorList(String id) {
        return smsChannelConfigService.smsAuthorList(id);
    }
}

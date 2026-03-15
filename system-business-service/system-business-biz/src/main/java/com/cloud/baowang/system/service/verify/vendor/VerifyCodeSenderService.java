package com.cloud.baowang.system.service.verify.vendor;


import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;

/**
 * @author: fangfei
 * @createTime: 2024/05/08 23:36
 * @description:
 */

public interface VerifyCodeSenderService {
    void sendSms(SmsChannelConfigVO configVO);
    void sendMail(MailChannelConfigVO configVO);
}

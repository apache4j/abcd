package com.cloud.baowang.user.service;

import com.aliyun.cloudauth_intl20220809.models.BankMetaVerifyIntlRequest;
import com.aliyun.cloudauth_intl20220809.models.BankMetaVerifyIntlResponse;
import com.aliyun.cloudauth_intl20220809.models.Mobile2MetaVerifyIntlRequest;
import com.aliyun.cloudauth_intl20220809.models.Mobile2MetaVerifyIntlResponse;
import com.cloud.baowang.user.properties.AliCloudAuthConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @className: AliAuthService
 * @author: wade
 * @description: ali
 * @date: 18/9/25 14:33
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AliAuthService {

    private static final String BIZCODE_SUCCESS = "1"; // 核验一致（计费）
    private static final String BIZCODE_FAIL = "2";    // 核验不一致（计费）
    private static final String BIZCODE_NO_RECORD = "3"; // 查无记录（不计费）
    private final com.aliyun.cloudauth_intl20220809.Client aliCloudClient;

    private final AliCloudAuthConfigProperties aliCloudAuthConfigProperties;

    /**
     * 银行卡实名认证（国际版）
     *
     * @param userName 用户姓名
     * @param bankCard 银行卡号
     * @return true = 核验一致，false = 核验不一致或异常
     */
    public boolean bankVerification(String userName, String bankCard) {
        // 如果环境配置了false，则直接返回成功
        if (!aliCloudAuthConfigProperties.isApnsProduction()) {
            // 非生产环境，直接返回成功
            return true;
        }
        if (userName == null || userName.isEmpty() || bankCard == null || bankCard.isEmpty()) {
            log.warn("银行卡核验参数非法, userName: {}, bankCard: {}", userName, bankCard);
            return false;
        }

        try {
            BankMetaVerifyIntlRequest request = new BankMetaVerifyIntlRequest()
                    .setProductCode("BANK_CARD_N_META")
                    .setParamType("normal")
                    .setVerifyMode("VERIFY_BANK_CARD")
                    .setProductType("BANK_CARD_2_META")
                    .setBankCard(bankCard)
                    .setUserName(userName);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            BankMetaVerifyIntlResponse response =
                    aliCloudClient.bankMetaVerifyIntlWithOptions(request, runtime);

            log.info("银行卡核验响应结果：{}", com.aliyun.teautil.Common.toJSONString(response));

            var body = response.getBody();
            if (body != null && body.getResultObject() != null) {
                String bizCode = body.getResultObject().getBizCode();
                String subCode = body.getResultObject().getSubCode();
                log.info("bizCode: {}, subCode: {}", bizCode, subCode);
                // bizCode 含义 1：校验一致（计费）2：校验不一致（计费）3：查无记录（不计费）
                return BIZCODE_SUCCESS.equals(bizCode);
            } else {
                log.warn("银行卡核验返回体为空或 resultObject 为 null, userName: {}, bankCard: {}", userName, bankCard);
                return false;
            }

        } catch (Exception e) {
            log.error("阿里云银行卡核验异常, userName: {}, bankCard: {}, error: {}", userName, bankCard, e.getMessage(), e);
            return false;
        }
    }

    public boolean phoneVerify(String userName, String phone) {
        if (!aliCloudAuthConfigProperties.isApnsProduction()) {
            // 非生产环境，直接返回成功
            return true;
        }
        if (userName == null || userName.isEmpty() || phone == null || phone.isEmpty()) {
            log.warn("手机号码核验参数非法, userName: {}, phone: {}", userName, phone);
            return false;
        }


        try {
            // 2. 构建请求参数对象
            Mobile2MetaVerifyIntlRequest mobile2MetaVerifyIntlRequest = new Mobile2MetaVerifyIntlRequest()
                    // 用户姓名
                    .setUserName(userName)
                    // 用户手机号（建议使用国际标准格式，例如：中国手机号前面加 +86）
                    .setMobile(phone)
                    // 产品编码，固定写法，根据阿里云控制台文档确认使用的产品
                    .setProductCode("MOBILE_2META")
                    // 参数类型，normal 表示普通验证
                    .setParamType("normal");
            // 3. 运行时配置（可选），用于设置超时、重试等参数
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            // 4. 调用接口方法
            // 注意：接口返回的是一个响应对象，这里建议接收并打印出来以便调试
            Mobile2MetaVerifyIntlResponse response =
                    aliCloudClient.mobile2MetaVerifyIntlWithOptions(mobile2MetaVerifyIntlRequest, runtime);
            var body = response.getBody();
            log.info("手机号核验响应结果：{}", com.aliyun.teautil.Common.toJSONString(response));
            if (body != null && body.getResult() != null) {
                String bizCode = body.getResult().getBizCode();
                String ispName = body.getResult().getIspName();
                // CMCC：移动, CUCC：联通,CTCC：电信
                log.info("bizCode: {}, ispName: {}", bizCode, ispName);
                // bizCode 含义 1：校验一致（计费）2：校验不一致（计费）3：查无记录（不计费）
                return BIZCODE_SUCCESS.equals(bizCode);
            } else {
                log.warn("调用手机号核验接口返回体为空或 resultObject 为 null, userName: {}, phone: {}", userName, phone);
                return false;
            }
        } catch (Exception error) {
            // 6. 捕获 SDK 抛出的 TeaException
            log.error("调用手机号核验接口发生错误： userName: {}, phone: {}, error:", userName, phone, error);
            return false;

        }

    }
}

package com.cloud.baowang.system.api.vo.site.agreement;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "站点-帮助中心")
@AllArgsConstructor
@NoArgsConstructor
public class UserAgreementVO extends BaseVO {
    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(description = "语言")
    private String language;
    @Schema(description = "名称")
    private String languageName;
    @Schema(description = "协议")
    private String agreement;

    @Schema(description = "关于我们")
    private String aboutUs;

    @Schema(description = "隐私政策")
    private String privacyPolicy;

    @Schema(description = "规则与条款")
    private String termsCondition;

    @Schema(description = "联系我们")
    private String contactUs;

    @Schema(description = "ios下载")
    private String iosDownload;
    @Schema(description = "安卓下载")
    private String androidDownload;

    @Schema(description = "合规监管")
    private String compliance;

    @Schema(description = "投诉邮箱")
    private String complaintEmail;

    @Schema(description = "客服邮箱")
    private String customerServiceEmail;

    @Schema(description = "客服邮箱")
    private String unLoginPic;

    @Schema(description = "code排序")
    private String code;

    private String option_value;
    private String option_value2;
}

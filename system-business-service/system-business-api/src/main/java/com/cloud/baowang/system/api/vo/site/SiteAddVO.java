package com.cloud.baowang.system.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/27 11:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点列表新增传入参数")
public class SiteAddVO {

    @Schema(description = "站点列表新增步骤(1:基础信息,2:站点配置,3:场馆授权,4:存款授权,5:提款授权,6:短信通道授权,7:邮箱授权" +
            "，8:客服授权)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lastStep;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "基础信息新增实体类")
    private SiteBasicVO siteBasic;

    @Schema(description = "基础信息新增实体类")
    private SiteConfigVO siteConfig;

    @Schema(description = "场馆授权新增实体类")
    private List<SiteVenueAuthorizeVO> siteVenue;

    @Schema(description = "存款通道新增实体类")
    private List<SiteDepositVO> siteDeposit;

    @Schema(description = "提款通道新增实体类")
    private List<SiteWithdrawVO> siteWithdraw;

    @Schema(description = "短信通道新增实体类")
    private SiteSmsVO siteSms;

    @Schema(description = "邮箱通道新增实体类")
    private SiteEmailVO siteEmail;

    @Schema(description = "客服通道新增实体类")
    private SiteCustomerVO siteCustomer;

    @Schema(description = "创建人", hidden = true)
    private String creator;

}

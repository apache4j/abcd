package com.cloud.baowang.wallet.api.vo.user;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 11/5/23 7:45 PM
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户详情传入参数")
public class WalletUserBasicRequestVO extends PageVO implements Serializable{

    //@NotNull(message = "userAccount can not be empty")
    @Schema(description ="会员账号")
    private String userAccount;

    @Schema(description ="站点编号")
    private String siteCode;
    @Schema(description ="取款方式",hidden = true)
    private String depositWithdrawTypeCode;

    @Schema(description ="风控类别",hidden = true)
    private String riskType;



    private Boolean dataDesensitization = true;

}

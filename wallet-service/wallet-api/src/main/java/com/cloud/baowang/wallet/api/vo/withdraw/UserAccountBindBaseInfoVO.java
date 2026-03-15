package com.cloud.baowang.wallet.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.wallet.api.vo.bank.BankManageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "会员收款信息绑定基础信息")
@I18nClass
public class UserAccountBindBaseInfoVO {


    @Schema(description = "银行列表")
    private List<BankManageVO> bankList;

    @Schema(description = "协议类型列表")
    private List<CodeValueVO> netWorkTypeList;


}

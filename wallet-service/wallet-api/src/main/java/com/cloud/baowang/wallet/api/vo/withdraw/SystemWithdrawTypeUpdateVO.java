package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: qiqi
 **/
@Data
@Schema(description = "提现类型修改")
public class SystemWithdrawTypeUpdateVO {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "操作人 ")
    private String operatorUserNo;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 提现类型
     */
    @Schema(description = "提现类型")
    private String withdrawType;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String memo;

    /**
     * 提现类型 多语言
     */
    @Schema(description = "提现类型 多语言List")
    private List<I18nMsgFrontVO> withdrawTypeI18List;
}

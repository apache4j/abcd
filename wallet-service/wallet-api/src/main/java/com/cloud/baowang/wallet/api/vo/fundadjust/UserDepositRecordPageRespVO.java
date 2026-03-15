package com.cloud.baowang.wallet.api.vo.fundadjust;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: aomiao
 */
@Data
@Schema(description = "会员存款记录 返回")
@I18nClass
public class UserDepositRecordPageRespVO {
    private Page<UserDepositRecordResponseVO> pages;
    private UserDepositRecordResponseVO totalOrder;
    private UserDepositRecordResponseVO smallOrder;
}

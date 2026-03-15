package com.cloud.baowang.user.api.vo.user.invite;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@ExcelIgnoreUnannotated
@Schema(description = "邀请码配置响应VO")
public class SiteUserInviteRecordResVO extends BaseVO implements Serializable {
    @Schema(description = "邀请人账号")
    @ExcelProperty(value = "邀请人账号", order = 1)
    @ColumnWidth(25)
    private String userAccount;

    @Schema(description = "邀请人id")
    private String userId;

    @ExcelProperty(value = "邀请码", order = 2)
    @ColumnWidth(25)
    @Schema(description = "邀请码")
    private String inviteCode;

    @ExcelProperty(value = "被邀请会员账号", order = 3)
    @ColumnWidth(25)
    @Schema(description = "被邀请会员账号")
    private String targetAccount;

    @Schema(description = "被邀请会员id")
    private String targetUserId;

    @Schema(description = "是否有效邀请 0 不是  1 是")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String validInvite;

    @Schema(description = "是否有效邀请文本")
    @ExcelProperty(value = "有效邀请", order = 4)
    @ColumnWidth(25)
    private String validInviteText;

    @Schema(description = "币种")
    @ExcelProperty(value = "币种", order = 5)
    @ColumnWidth(25)
    private String currency;

    @Schema(description = "首存金额")
    @ExcelProperty(value = "首存金额", order = 6)
    @ColumnWidth(25)
    private BigDecimal firstDepositAmount;

    @Schema(description = "累计存款金额")
    @ExcelProperty(value = "累计存款金额", order = 7)
    @ColumnWidth(25)
    private BigDecimal depositAmountTotal;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "注册时间-导出使用")
    @ExcelProperty(value = "注册时间", order = 9)
    @ColumnWidth(25)
    private String registerTimeStr;

    @Schema(description = "首存时间-导出使用")
    @ExcelProperty(value = "首存时间", order = 8)
    @ColumnWidth(25)
    private String firstDepositTimeStr;

    public String getRegisterTimeStr() {
        return null == registerTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(registerTime, CurrReqUtils.getTimezone());
    }

    public String getFirstDepositTimeStr() {
        return null == firstDepositTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(firstDepositTime, CurrReqUtils.getTimezone());
    }
}

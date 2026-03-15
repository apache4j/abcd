package com.cloud.baowang.user.api.vo.vip;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ValidList;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @Author 小智
 * @Date 4/5/23 11:14 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP权益配置单个入参对象")
@Slf4j
public class VIPBenefitConfigRequestVO implements Serializable {

    /* vip等级 */
    @Schema(title = "vip等级")
    @NotEmpty(message = "vip等级不能为空")
    private String vipRank;

    /* 日提款次数 */
    @Schema(title = "日提款次数")
    @NotEmpty(message = "日提款次数不能为空")
    @Pattern(regexp = "^[1-9]\\d*$", message = "日提款次数必须是正整数")
    private String dailyWithdrawals;

    /* 每日累计提款额度 */
    @Schema(title = "每日累计提款额度")
    @NotEmpty(message = "每日累计提款额度不能为空")
    @Pattern(regexp = "^[1-9]\\d*$", message = "每日累计提款额度必须是正整数")
    private String dayWithdrawLimit;

    /* 每周返还奖金比例(%) */
    @Schema(title = "每周返还奖金比例(%)")
    @NotEmpty(message = "每周返还奖金比例不能为空")
//    @Pattern(regexp = "^[0-9]\\d*$", message = "升级礼金必须是整数")
    private String weekRebate;

    @Schema(title = "每周最低下注金额")
    @NotEmpty(message = "每周最低下注金额不能为空")
//    @Pattern(regexp = "^[0-9]\\d*$", message = "升级礼金必须是整数")
    private String weekMinBetAmount;

    /* 每月返还奖金比例(%) */
    @Schema(title = "每月返还奖金比例(%)")
    @NotEmpty(message = "每月返还奖金比例不能为空")
//    @Pattern(regexp = "^[0-9]\\d*$", message = "生日礼金必须是整数")
    private String monthRebate;

    @Schema(title = "每月最低下注金额")
    @NotEmpty(message = "每月最低下注金额不能为空")
//    @Pattern(regexp = "^[0-9]\\d*$", message = "升级礼金必须是整数")
    private String monthMinBetAmount;

    /* 升级奖金 */
    @Schema(title = "升级奖金")
    @NotEmpty(message = "升级奖金不能为空")
//    @Pattern(regexp = "^[0-9]\\d*$", message = "上半月礼金必须是整数")
    private String upgrade;

    /* 幸运转盘 */
    @Schema(title = "幸运转盘")
    @NotEmpty(message = "幸运转盘不能为空")
//    @Pattern(regexp = "^[0-9]\\d*$", message = "下半月礼金必须是整数")
    private String luckTime;

    public static boolean haveError(ValidList<VIPBenefitConfigRequestVO> lists,
                                    Set<String> errors) {
        if (ObjectUtil.isEmpty(lists)) {
            errors.add(ResultCode.PARAM_ERROR.getMessageCode());
        }
        for (VIPBenefitConfigRequestVO vo : lists) {
            if ("VIP0".equals(vo.getVipRank())) {
                VIPBenefitConfigRequestVO validateVO = new VIPBenefitConfigRequestVO();
                BeanUtils.copyProperties(vo, validateVO);
                validateVO.setWeekRebate(BigDecimal.ONE.toString());
                validateVO.setMonthRebate(BigDecimal.ONE.toString());
                validateVO.setUpgrade(BigDecimal.ONE.toString());
                validateVO.setLuckTime(BigDecimal.ONE.toString());
                String error = ValidateUtil.validate(validateVO);
                if (ObjectUtil.isNotEmpty(error)) {
                    errors.add(error);
                }
                continue;
            }
            String error = ValidateUtil.validate(vo);
            if (ObjectUtil.isNotEmpty(error)) {
                errors.add(error);
            }
        }
        return ObjectUtil.isNotEmpty(errors);
    }

}

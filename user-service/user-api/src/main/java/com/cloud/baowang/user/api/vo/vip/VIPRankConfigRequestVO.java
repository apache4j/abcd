package com.cloud.baowang.user.api.vo.vip;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @Author 小智
 * @Date 2/5/23 8:00 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP等级配置单个入参对象")
public class VIPRankConfigRequestVO implements Serializable {

    @Schema(title = "vip等级")
    @NotEmpty(message = "vip等级不能为空")
    private String vipRank;

    @Schema(title = "vip等级名称")
    @NotEmpty(message = "vip等级名称不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$",
            message = "vip等级只能是中文，数字，和英文大小写字母")
    private String vipRankName;

//    @ApiModelProperty("累计存款")
//    @NotNull(message = "累计存款不能为空")
//    @Pattern(regexp = "^[1-9]\\d*$", message = "累计存款必须是正整数")
//    private String depositUpgrade;

    @Schema(title = "累计有效流水")
    @NotNull(message = "累计有效流水不能为空")
    @Pattern(regexp = "^[1-9]\\d*$", message = "累计有效流水必须是正整数")
    private String betAmountUpgrade;



    public static boolean haveError(List<VIPRankConfigRequestVO> requestVO, Set<String> errors) {
        if (ObjectUtil.isEmpty(requestVO.size())) {
            errors.add(ResultCode.PARAM_ERROR.getMessageCode());
            return true;
        }
        // list 校验参数返回错误结果
        for (VIPRankConfigRequestVO vo : requestVO) {
            if ("VIP0".equals(vo.getVipRank())) {
                VIPRankConfigRequestVO validateVO = new VIPRankConfigRequestVO();
                BeanUtils.copyProperties(vo, validateVO);
//                validateVO.setDepositUpgrade(BigDecimal.ONE.toString());
                validateVO.setBetAmountUpgrade(BigDecimal.ONE.toString());
                String error = ValidateUtil.validate(validateVO);
                if (ObjectUtil.isNotEmpty(error)) {
                    errors.add(error);
                }
                continue;
            }
            String error = ValidateUtil.validate(vo);
            if (ObjectUtil.isNotEmpty(error)) {
                errors.add(error);
                return true;
            }
        }
        List<BigDecimal> list = requestVO.stream()
                .sorted(Comparator.comparing(obj -> Integer.parseInt(obj.getVipRank().split("VIP")[1])))
                .map(VIPRankConfigRequestVO::getBetAmountUpgrade).map(BigDecimal::new).toList();
        if (!Ordering.natural().isOrdered(list)) {
            errors.add(ResultCode.VIP_BET_AMOUNT_UPGRADE_ERROR.getMessageCode());
            return true;
        }
//        list = requestVO.stream()
//                .sorted(Comparator.comparing(obj -> Integer.parseInt(obj.getVipRank().split("VIP")[1])))
//                .map(VIPRankConfigRequestVO::getDepositUpgrade).map(BigDecimal::new).toList();
//        if (!Ordering.natural().isOrdered(list)) {
//            errors.add(ResultCode.VIP_DEPOSIT_UPGRADE_ERROR.getMessage());
//            return true;
//        }
        return false;
    }
}

package com.cloud.baowang.wallet.api.vo.recharge;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/12 10:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="充值通道返回参数 SiteRechargeChannelResVO")
@I18nClass
public class SiteRechargeChannelResVO {

    @Schema(description = "选中充值通道集合")
    private List<String> chooseID;

    @Schema(description = "全部充值通道集合")
    private List<String> allID;

    @Schema(description = "充值通道返回分页参数")
    private Page<RechargeChannelResVO> pageVO;
}

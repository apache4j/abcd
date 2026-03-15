package com.cloud.baowang.wallet.api.vo.withdraw;

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
 * @Date : 2024/8/12 15:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点提款授权返回参数")
@I18nClass
public class SiteWithdrawChannelResVO {

    @Schema(description = "选中提款通道集合")
    private List<String> chooseID;

    @Schema(description = "全部提款通道集合")
    private List<String> allID;

    @Schema(description ="站点提款授权分页对象")
    private Page<WithdrawChannelResVO> pageVO;
}

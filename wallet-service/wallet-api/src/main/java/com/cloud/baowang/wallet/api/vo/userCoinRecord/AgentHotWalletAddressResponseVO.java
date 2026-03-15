package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理链上资金管理返回对象")
@I18nClass
public class AgentHotWalletAddressResponseVO {

    /**
     * 小计
     */
    private AgentHotWalletAddressVO currentPage;

    /**
     * 总计
     */
    private AgentHotWalletAddressVO totalPage;
    /**
     * 分页数据
     */
    private Page<AgentHotWalletAddressVO> agentHotWalletAddressVOPage;
}

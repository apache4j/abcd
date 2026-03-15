package com.cloud.baowang.agent.api.vo.manualup;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理人工减额记录 返回对象")
@I18nClass
public class AgentManualDownRecordResponseVO extends Page<AgentManualDownRecordVO> {

    /**
     * 小计
     */
    private AgentManualDownRecordVO currentPage;

    /**
     * 总计
     */
    private AgentManualDownRecordVO totalPage;
}

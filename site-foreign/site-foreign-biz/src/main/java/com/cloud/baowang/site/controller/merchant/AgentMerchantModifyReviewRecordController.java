package com.cloud.baowang.site.controller.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantModifyReviewApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyReviewVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author: ford
 * @Description: 代理相关
 */
@Tag(name = "商务信息变更记录")
@RestController
@RequestMapping("/merchantModifyRecord/api")
@AllArgsConstructor
public class AgentMerchantModifyReviewRecordController {
    private final AgentMerchantModifyReviewApi merchantApi;
    private final SystemParamApi paramApi;

    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        ResponseVO<List<CodeValueVO>> resp = paramApi.getSystemParamByType(CommonConstant.AGENT_STATUS);
        if (resp.isOk()) {
            List<CodeValueVO> data = resp.getData();
            String normalCode = AgentStatusEnum.NORMAL.getCode();
            String loginLockCode = AgentStatusEnum.LOGIN_LOCK.getCode();
            data = data.stream()
                    .filter(item -> normalCode.equals(item.getCode()) || loginLockCode.equals(item.getCode()))
                    .toList();
            resp.setData(data);
        }
        return resp;
    }

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询")
    public ResponseVO<Page<AgentMerchantModifyReviewVO>> pageQuery(@RequestBody AgentMerchantModifyPageQueryVO queryVO) {
        queryVO.setReviewStatus(ReviewStatusEnum.REVIEW_PASS.getCode());
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        queryVO.setOperator(CurrReqUtils.getAccount());
        return merchantApi.pageQuery(queryVO);
    }

}

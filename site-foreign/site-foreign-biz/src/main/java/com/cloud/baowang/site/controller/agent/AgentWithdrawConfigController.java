package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawConfigApi;
import com.cloud.baowang.agent.api.vo.withdrawConfig.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author: kimi
 */
@Tag(name = "资金-提款设置-代理提款设置")
@AllArgsConstructor
@RestController
@RequestMapping("/agentWithdrawConfig")
@Slf4j
public class AgentWithdrawConfigController {

    private final AgentWithdrawConfigApi agentWithdrawConfigApi;
    private final SystemParamApi paramApi;

    @Operation(summary = "新增")
    @PostMapping("/add")
    public ResponseVO<Void> agentWithdrawConfigAdd(@Valid @RequestBody AgentWithdrawConfigAddVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentWithdrawConfigApi.add(vo);
    }

    @Operation(summary = "删除")
    @PostMapping("/del")
    public ResponseVO<Void> agentWithdrawConfigDel(@Valid @RequestBody IdVO vo) {
        return agentWithdrawConfigApi.del(vo);
    }

    @Operation(summary = "修改")
    @PostMapping("/edit")
    public ResponseVO<Void> edit(@Valid @RequestBody AgentWithdrawConfigEditVO vo) {
        return agentWithdrawConfigApi.edit(vo);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/pageList")
    public ResponseVO<Page<AgentWithdrawConfigPageVO>> pageList(@RequestBody AgentWithdrawConfigPageQueryVO vo) {
        return agentWithdrawConfigApi.pageList(vo);
    }

    @Operation(summary = "详情")
    @PostMapping("/detail")
    public ResponseVO<AgentWithdrawConfigDetailResVO> detail(@Valid @RequestBody IdVO vo) {
        return agentWithdrawConfigApi.detail(vo);
    }


    @Operation(summary = "查询币种下的提款方式")
    @GetMapping("/queryWithdrawWay")
    public ResponseVO<List<AgentWithdrawWayRspVO>> queryWithdrawWay() {
        return agentWithdrawConfigApi.queryWithdrawWay();
    }

    @GetMapping("getDownBox")
    @Operation(summary = "手续费类型")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        Map<String, List<CodeValueVO>> result = new HashMap<>();
        ResponseVO<List<CodeValueVO>> resp = paramApi.getSystemParamByType(CommonConstant.FEE_TYPE);
        List<CodeValueVO> data = resp.getData();
        Iterator<CodeValueVO> iterator = data.iterator();
        while (iterator.hasNext()) {
            CodeValueVO codeValueVO = iterator.next();
            if (codeValueVO.getCode().equals(CommonConstant.business_two_str)) {
                iterator.remove();
                break;
            }
        }
        result.put(CommonConstant.FEE_TYPE, data);
        return ResponseVO.success(result);
    }

}

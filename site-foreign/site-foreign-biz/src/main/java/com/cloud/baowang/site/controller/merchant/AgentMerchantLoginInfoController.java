package com.cloud.baowang.site.controller.merchant;

import com.cloud.baowang.agent.api.api.AgentMerchantLoginInfoApi;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoRespVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "商务登录信息")
@RestController
@RequestMapping("/agentMerchantLoginInfo/api")
@AllArgsConstructor
public class AgentMerchantLoginInfoController {
    private final AgentMerchantLoginInfoApi loginInfoApi;
    private final SystemParamApi systemParamApi;

    @GetMapping("getDownBox")
    @Operation(summary = "下拉框")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return systemParamApi.getSystemParamByType(CommonConstant.LOGIN_TYPE);
    }

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询")
    public ResponseVO<AgentMerchantLoginInfoRespVO> pageQuery(@RequestBody AgentMerchantLoginInfoPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return loginInfoApi.pageQuery(queryVO);
    }

}

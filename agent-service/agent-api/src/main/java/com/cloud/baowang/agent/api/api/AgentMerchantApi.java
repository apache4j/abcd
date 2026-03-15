package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantResultVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.MerchantAgentInfoVO;
import com.cloud.baowang.agent.api.vo.merchant.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "agentMerchantApi", value = ApiConstants.NAME)
@Tag(name = "RPC 商务基础信息 服务")
public interface AgentMerchantApi {
    String PREFIX = ApiConstants.PREFIX + "/agentMerchantApi/api/";

    @Operation(summary = "根据站点账号和站点编码获取商务后台信息")
    @PostMapping(value = PREFIX + "getAdminByMerchantAccountAndSite")
    AgentMerchantVO getAdminByMerchantAccountAndSite(@RequestParam("merchantAccount") String merchantAccount
            , @RequestParam("siteCode") String siteCode);

    @Operation(summary = "修改商务后台")
    @PostMapping(value = PREFIX + "updateAgentMerchantLoginInfo")
    void updateAgentMerchantLoginInfo(@RequestBody AgentMerchantResultVO resultVO);

    @Operation(description = "商务列表")
    @PostMapping(value = PREFIX +"pageQuery")
    ResponseVO<Page<AgentMerchantPageRespVO>> pageQuery(@RequestBody AddMerchantPageQueryVO queryVO);


    @GetMapping(value = PREFIX + "getList")
    @Operation(summary = "获取站点下所有商务信息")
    List<AgentMerchantVO> getList(@RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "getListByAccounts")
    @Operation(summary = "获取站点下所有商务信息-新增通知用")
    List<AgentMerchantVO> getListByAccounts(@RequestParam("siteCode") String siteCode,@RequestBody List<String> merchantAccounts);


    @PostMapping(PREFIX + "getMerchantAgentInfo")
    @Operation(summary = "获取商务包含总代人数信息")
    MerchantAgentInfoVO getMerchantAgentInfo(@RequestParam("siteCode") String siteCode,
                                             @RequestParam("merchantAccount") String merchantAccount);

    @PostMapping(PREFIX + "getTeamNum")
    @Operation(summary = "获取商务团队代理人数")
    Long getTeamNum(@RequestParam("siteCode") String siteCode,
                                             @RequestParam("merchantAccount") String merchantAccount);


    @PostMapping(PREFIX + "updateRiskInfo")
    @Operation(summary = "修改风控信息")
    ResponseVO<Boolean> updateRiskInfo(@RequestBody MerchantRiskUpdateVO updateVO);

    @PostMapping(value = PREFIX + "validate")
    @Operation(summary = "校验密码是否正确")
    boolean validate(@RequestBody AgentMerchantVO merchantVO, @RequestParam String password);

    @PostMapping(value = PREFIX + "sendMail")
    @Operation(summary = "发送邮箱")
    ResponseVO<?> sendMail(@RequestBody MerchantLoginGetMailCodeVO vo);

    @PostMapping(value = PREFIX + "updatePassword")
    @Operation(summary = "修改密码")
    boolean updatePassword(@RequestBody AgentMerchantVO agentMerchantVO, @RequestParam String newPassword);

    @PostMapping(value = PREFIX + "bindEmail")
    @Operation(summary = "绑定邮箱")
    ResponseVO<?> bindEmail(@RequestParam String merchantAccount, @RequestParam String email, @RequestParam String siteCode);

    @PostMapping(value = PREFIX + "bindGoogle")
    @Operation(summary = "绑定谷歌")
    ResponseVO<?> bindGoogle(@RequestBody AgentMerchantVO agentMerchantVO, @RequestParam String googleAuthKey);

    @PostMapping(value = PREFIX + "column")
    @Operation(summary = "绑定谷歌")
    ResponseVO<MerchantSecuritySetVO> column(@RequestParam String siteCode, @RequestParam String merchatAccount);

    @PostMapping(value = PREFIX + "countByAccountAndSite")
    @Operation(summary = "countByAccountAndSite")
    long countByAccountAndSite(@RequestParam String email, @RequestParam String siteCode, @RequestParam String id);
}

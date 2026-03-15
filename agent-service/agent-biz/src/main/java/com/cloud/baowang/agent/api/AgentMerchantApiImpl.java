package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantResultVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.MerchantAgentInfoVO;
import com.cloud.baowang.agent.api.vo.merchant.*;
import com.cloud.baowang.agent.service.AgentMerchantService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.verify.SenderServiceApi;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentMerchantApiImpl implements AgentMerchantApi {
    private final AgentMerchantService merchantService;

    private final SenderServiceApi senderServiceApi;

    @Override
    public AgentMerchantVO getAdminByMerchantAccountAndSite(String merchantAccount, String siteCode) {
        log.info("getAdminByMerchantAccountAndSite, merchantAccount: {}, siteCode: {}", merchantAccount, siteCode);
        // 获取商务后台信息
        return merchantService.getAdminByMerchantAccountAndSite(merchantAccount, siteCode);
    }

    @Override
    public void updateAgentMerchantLoginInfo(AgentMerchantResultVO resultVO) {
        merchantService.updateAgentMerchantLoginInfo(resultVO);
    }

    @Override
    public ResponseVO<Page<AgentMerchantPageRespVO>> pageQuery(AddMerchantPageQueryVO queryVO) {
        return merchantService.pageQuery(queryVO);
    }


    @Override
    public List<AgentMerchantVO> getList(String siteCode) {
        return merchantService.getList(siteCode);
    }

    @Override
    public MerchantAgentInfoVO getMerchantAgentInfo(String siteCode, String merchantAccount) {
        return merchantService.getMerchantAgentInfo(siteCode,merchantAccount);
    }

    @Override
    public Long getTeamNum(String siteCode, String merchantAccount) {
        return merchantService.getTeamNum(siteCode,merchantAccount);
    }

    @Override
    public ResponseVO<Boolean> updateRiskInfo(MerchantRiskUpdateVO updateVO) {
        return merchantService.updateRiskInfo(updateVO);
    }

    @Override
    public List<AgentMerchantVO> getListByAccounts(String siteCode, List<String> merchantAccounts) {
        return merchantService.getListByAccounts(siteCode,merchantAccounts);
    }

    @Override
    public boolean validate(AgentMerchantVO agentMerchantVO,String password) {
        return merchantService.validate(agentMerchantVO,password);
    }

    @Override
    public ResponseVO<?> sendMail(MerchantLoginGetMailCodeVO vo) {
        VerifyCodeSendVO verifyCodeSendVO = new VerifyCodeSendVO();
        verifyCodeSendVO.setSiteCode(vo.getSiteCode());
        verifyCodeSendVO.setAccount(vo.getEmail());
        verifyCodeSendVO.setUserAccount(vo.getMerchantAccount());
        ResponseVO responseVO = senderServiceApi.sendMail(verifyCodeSendVO);
        if (!responseVO.isOk()) {
            return responseVO;
        }
        return ResponseVO.success();
    }


    /**
     * @param id 商务主键
     * @param newPassword 新密码
     */
    @Override
    public boolean updatePassword(AgentMerchantVO agentMerchantVO, String newPassword) {
        return merchantService.updatePassword(agentMerchantVO,newPassword);
    }

    @Override
    public ResponseVO<?> bindEmail(String merchantAccount, String email, String siteCode) {
        return merchantService.bindEmail(merchantAccount,email,siteCode);
    }

    @Override
    public ResponseVO<?> bindGoogle(AgentMerchantVO agentMerchantVO, String googleAuthKey) {
        return merchantService.bindGoogle(agentMerchantVO,googleAuthKey);
    }

    @Override
    public ResponseVO<MerchantSecuritySetVO> column(String siteCode, String merchatAccount) {
        return merchantService.column(siteCode,merchatAccount);
    }

    @Override
    public long countByAccountAndSite(String email, String siteCode, String id) {
        return merchantService.countByAccountAndSite(email, siteCode, id);
    }

}

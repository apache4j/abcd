package com.cloud.baowang.site.controller.agent.commission;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/21 22:33
 * @description:
 */
@Tag(name = "代理-代理配置-佣金方案")
@AllArgsConstructor
@RestController
@RequestMapping("/agentCommissionPlan/api")
public class AgentCommissionPlanController {

    public final AgentCommissionPlanApi agentCommissionPlanApi;
    public final PlayVenueInfoApi playVenueInfoApi;

    /**
     * 过滤出来当前站点的
     *
     * @return
     */
    @Operation(summary = "查询当前站点下所有的场馆")
    @PostMapping("getAllVenueList")
    public ResponseVO<List<CommissionVenueFeeVO>> getAllVenueList() {
        List<String> venueCodeList = VenueEnum.getVenueCodeList();
        // VenueInfoRequestVO paramVO=new VenueInfoRequestVO();
        // List<VenueInfoVO> venueInfoVOS=playVenueInfoApi.venueInfoListByParam(paramVO).getData();
        // venueInfoVOS=venueInfoVOS.stream().sorted(Comparator.comparing(VenueInfoVO::getId)).toList();
        List<CommissionVenueFeeVO> list = new ArrayList<>();
        // for (VenueInfoVO venueInfoVO : venueInfoVOS) {
        for (String venueCode : venueCodeList) {
            CommissionVenueFeeVO vo = new CommissionVenueFeeVO();
            vo.setVenueCode(venueCode);
            list.add(vo);
        }
        return ResponseVO.success(list);
    }

    @Operation(summary = "查询所有佣金方案")
    @PostMapping("listAllCommissionPlan")
    public ResponseVO<List<AgentCommissionPlanVO>> listAllCommissionPlan() {
        return agentCommissionPlanApi.listAllCommissionPlan(CurrReqUtils.getSiteCode());
    }


    @Operation(summary = "佣金方案分页查询")
    @PostMapping("getCommissionPlanPage")
    public ResponseVO<Page<AgentCommissionPlanPageVO>> getCommissionPlanPage(@RequestBody CommissionPlanReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        reqVO.setSiteCode(siteCode);
        return agentCommissionPlanApi.getCommissionPlanPage(reqVO);
    }

    @Operation(summary = "新增佣金方案")
    @PostMapping("addPlanInfo")
    public ResponseVO addPlanInfo(@RequestBody AgentCommissionPlanAddVO addVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String creator = CurrReqUtils.getAccount();
        addVO.setSiteCode(siteCode);
        addVO.setCreator(creator);

        checkParam(addVO);

        return agentCommissionPlanApi.addPlanInfo(addVO);
    }

    @Operation(summary = "编辑佣金方案")
    @PostMapping("editPlanInfo")
    ResponseVO editPlanInfo(@RequestBody AgentCommissionPlanInfoVO infoVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String creator = CurrReqUtils.getAccount();
        infoVO.setSiteCode(siteCode);
        infoVO.setCreator(creator);

        //校验 临时
        // try {
        AgentCommissionPlanAddVO addVO = new AgentCommissionPlanAddVO();
        addVO.setLadderConfig(infoVO.getLadderConfig());
        addVO.setPlanConfigVO(infoVO.getPlanConfigVO());
        addVO.setRebateConfig(infoVO.getRebateConfig());
        addVO.setVenueFeeList(infoVO.getVenueFeeList());
        checkParam(addVO);
      /*  } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }*/
        return agentCommissionPlanApi.editPlanInfo(infoVO);
    }

    public void checkParam(AgentCommissionPlanAddVO vo) {
        if (vo == null ||
                vo.getPlanConfigVO() == null ||
                vo.getLadderConfig() == null ||
                vo.getRebateConfig() == null ||
                vo.getVenueFeeList() == null) {
            throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }
        PlanConfigVO planConfigVO = vo.getPlanConfigVO();
        if (ObjectUtil.isEmpty(planConfigVO.getActiveBet()) ||
                ObjectUtil.isEmpty(planConfigVO.getActiveDeposit()) ||
                ObjectUtil.isEmpty(planConfigVO.getValidBet()) ||
                ObjectUtil.isEmpty(planConfigVO.getValidDeposit())) {
            throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }

        if (planConfigVO.getActiveBet().compareTo(BigDecimal.ZERO) < 0 ||
                planConfigVO.getActiveDeposit().compareTo(BigDecimal.ZERO) < 0 ||
                planConfigVO.getValidBet().compareTo(BigDecimal.ZERO) < 0 ||
                planConfigVO.getValidDeposit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaowangDefaultException(ResultCode.AMOUNT_GREATER_ZERO);
        }

        List<CommissionVenueFeeVO> venueFeeList = vo.getVenueFeeList();
        for (CommissionVenueFeeVO feeVO : venueFeeList) {
            if (StringUtils.isEmpty(feeVO.getRate()) ||
                    (new BigDecimal(feeVO.getRate()).compareTo(BigDecimal.ZERO) < 0)) {
                throw new BaowangDefaultException(ResultCode.VENUE_FEE_ZERO);
            }
            if (StringUtils.isEmpty(feeVO.getValidRate()) ||
                    (new BigDecimal(feeVO.getValidRate()).compareTo(BigDecimal.ZERO) < 0)) {
                throw new BaowangDefaultException(ResultCode.VENUE_VALID_FEE_ZERO);
            }
        }

        LadderConfigVO ladderConfig = vo.getLadderConfig();
        List<LadderConfigDetailVO> ladderConfigDetailVO = ladderConfig.getLadderConfigDetailVO();
        if (ObjectUtil.isEmpty(ladderConfig.getLadderConfigDetailVO()) ||
                ObjectUtil.isEmpty(ladderConfig.getSettleCycle()) ||
                ObjectUtil.isEmpty(ladderConfigDetailVO)
        ) {
            throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }

        if (ladderConfigDetailVO.size() > 20) {
            throw new BaowangDefaultException(ResultCode.PLAN_CONFIG_COUNT_ERROR);
        }

        for (LadderConfigDetailVO detailVO : ladderConfigDetailVO) {
            if (ObjectUtil.isEmpty(detailVO.getRate()) ||
                    ObjectUtil.isEmpty(detailVO.getActiveNumber()) ||
                    ObjectUtil.isEmpty(detailVO.getLevelName()) ||
                    ObjectUtil.isEmpty(detailVO.getNewValidNumber()) ||
                    ObjectUtil.isEmpty(detailVO.getWinLossAmount()) ||
                    ObjectUtil.isEmpty(detailVO.getRate())
            ) {
                throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
            }

            if (new BigDecimal(detailVO.getRate()).compareTo(BigDecimal.ZERO) < 0 ||
                    detailVO.getActiveNumber() < 0 ||
                    detailVO.getNewValidNumber() < 0 ||
                    detailVO.getWinLossAmount().compareTo(BigDecimal.ZERO) < 0 ||
                    detailVO.getValidAmount().compareTo(BigDecimal.ZERO) < 0
            ) {
                throw new BaowangDefaultException(ResultCode.AMOUNT_GREATER_ZERO);
            }
        }

        RebateConfigVO rebateConfig = vo.getRebateConfig();
        if (ObjectUtil.isEmpty(rebateConfig.getChessRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getCockfightRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getEsportsRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getLiveRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getLotteryRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getSlotRate()) ||
                ObjectUtil.isEmpty(ladderConfig.getSettleCycle()) ||
                ObjectUtil.isEmpty(rebateConfig.getSportsRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getFishRate()) ||
                ObjectUtil.isEmpty(rebateConfig.getNewUserAmount())) {
            throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }

        if (new BigDecimal(rebateConfig.getChessRate()).compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getCockfightRate()).compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getEsportsRate()).compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getLiveRate()).compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getSlotRate()).compareTo(BigDecimal.ZERO) < 0 ||
                rebateConfig.getNewUserAmount().compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getSportsRate()).compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getLotteryRate()).compareTo(BigDecimal.ZERO) < 0 ||
                new BigDecimal(rebateConfig.getFishRate()).compareTo(BigDecimal.ZERO) < 0
        ) {
            throw new BaowangDefaultException(ResultCode.REBATE_RATE_ZERO);
        }


    }

    @Operation(summary = "查看佣金方案")
    @PostMapping("getPlanInfo")
    ResponseVO<AgentCommissionPlanInfoVO> getPlanInfo(@RequestBody CommissionAgentReqVO reqVO) {
        if (StrUtil.isEmpty(reqVO.getPlanCode())) {
            if (StrUtil.isEmpty(reqVO.getId())) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            return agentCommissionPlanApi.getPlanInfo(new IdVO(reqVO.getId()));
        } else {
            return ResponseVO.success(agentCommissionPlanApi.getPlanInfoByPlanCode(reqVO.getPlanCode()));
        }
    }

    @Operation(summary = "查看代理人数")
    @PostMapping("getAgentByPlan")
    ResponseVO<CommissionPlanAgentVO> getAgentByPlan(@RequestBody CommissionAgentReqVO reqVO) {
        return agentCommissionPlanApi.getAgentByPlan(reqVO);
    }

    @Operation(summary = "删除方案")
    @PostMapping("removePlanInfo")
    ResponseVO removePlanInfo(@RequestBody IdVO idVO) {
        return agentCommissionPlanApi.removePlanInfo(idVO);
    }

    @Operation(summary = "佣金方案分页查询-有效流水")
    @PostMapping("planTurnoverPageList")
    ResponseVO<Page<CommissionPlanTurnoverPageListVO>> planTurnoverPageList(@RequestBody CommissionPlanTurnoverPageQueryVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        reqVO.setSiteCode(siteCode);
        return agentCommissionPlanApi.planTurnoverPageList(reqVO);
    }

    @Operation(summary = "佣金方案配置详情-有效流水")
    @PostMapping("planTurnoverDetail")
    ResponseVO<CommissionPlanTurnoverDetailVO> planTurnoverDetail(@RequestParam("planCode") String planCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        return agentCommissionPlanApi.planTurnoverDetail(siteCode, planCode);
    }

    @Operation(summary = "新增佣金方案-有效流水")
    @PostMapping("addPlanTurnover")
    ResponseVO<Void> addPlanTurnover(@RequestBody CommissionPlanTurnoverAddVO addVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String creator = CurrReqUtils.getAccount();
        addVO.setSiteCode(siteCode);
        addVO.setCreator(creator);
        return agentCommissionPlanApi.addPlanTurnover(addVO);
    }

    @Operation(summary = "编辑佣金方案-有效流水")
    @PostMapping("editPlanTurnover")
    ResponseVO<Void> editPlanTurnover(@RequestBody CommissionPlanTurnoverUpdateVO updateVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String updater = CurrReqUtils.getAccount();
        updateVO.setSiteCode(siteCode);
        updateVO.setUpdater(updater);
        return agentCommissionPlanApi.editPlanTurnover(updateVO);
    }

    @Operation(summary = "删除佣金方案-有效流水")
    @PostMapping("removePlanTurnover")
    ResponseVO<Void> removePlanTurnover(@RequestBody IdVO idVO) {
        return agentCommissionPlanApi.removePlanTurnover(idVO.getId());
    }

}

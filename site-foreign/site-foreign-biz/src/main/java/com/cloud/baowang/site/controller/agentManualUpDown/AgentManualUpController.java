package com.cloud.baowang.site.controller.agentManualUpDown;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownAccountResultVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpSubmitVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceQueryVO;
import com.cloud.baowang.agent.api.vo.manualup.GetAgentBalanceVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.common.excel.userNotifyExcel.AgentManualDownAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.AgentManualDownAccountReadExcelDTO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: kimi
 */
@Tag(name = "代理人工添加额度")
@RestController
@AllArgsConstructor
@RequestMapping("/agent-manual-up/api")
public class AgentManualUpController {

    private final SystemParamApi systemParamApi;
    private final AgentManualUpApi agentManualUpApi;

    @Operation(summary = "提交")
    @PostMapping(value = "/agentSubmit")
    public ResponseVO<?> agentSubmit(@Valid @RequestBody AgentManualUpSubmitVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        return agentManualUpApi.agentSubmit(vo, CurrReqUtils.getAccount());
    }

    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        //代理钱包类型
        param.add(CommonConstant.AGENT_WALLET_TYPE);
        //审核状态
        param.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        return systemParamApi.getSystemParamsByList(param);
    }

    @GetMapping("getAdjustTypeListByWalletType")
    @Operation(summary = "根据选择的钱包类型，获取加额类型下拉")
    public ResponseVO<List<CodeValueVO>> getAdjustTypeListByWalletType(@RequestParam("walletType") Integer walletType) {
        //加额类型
        ResponseVO<List<CodeValueVO>> resp = systemParamApi.getSystemParamByType(CommonConstant.AGENT_MANUAL_ADJUST_TYPE);
        if (resp.isOk()) {
            List<CodeValueVO> list = resp.getData();
            List<AgentManualAdjustTypeEnum> enums = AgentManualAdjustTypeEnum.listByWalletType(walletType);
            List<String> enumCodes = enums.stream()
                    .map(AgentManualAdjustTypeEnum::getCode) // 假设 getCode() 返回 code
                    .toList();
            return ResponseVO.success(list.stream()
                    .filter(codeValue -> enumCodes.contains(codeValue.getCode())) // 假设 getCode() 返回 code
                    .toList());
        }
        return ResponseVO.success();
    }

    @Operation(summary = "代理账号查询-输入框查询-第一步")
    @PostMapping(value = "/getAgentBalance")
    public ResponseVO<List<GetAgentBalanceVO>> getAgentBalance(@Valid @RequestBody GetAgentBalanceQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentManualUpApi.getAgentBalance(vo);
    }

    @Operation(summary = "代理账号查询加额-excel模版导出")
    @GetMapping("excelImport")
    public void excelImport(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "代理人工加额") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @Operation(summary = "代理账号查询减额-excel模版导出")
    @GetMapping("excelImportDown")
    public void excelImportDown(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "代理人工减额") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @PostMapping("getAgentBalanceByExcel")
    @Operation(summary = "代理账号查询-excel上传查询-第一步")
    public ResponseVO<List<AgentManualUpDownAccountResultVO>> getAgentBalanceByExcel(MultipartFile file) {
        try {
            List<AgentManualDownAccountReadExcelDTO> agentList = new ArrayList<>();
            ExcelUtil.read(file.getInputStream(), AgentManualDownAccountReadExcelDTO.class, new AgentManualDownAccountExcelConsumerListener(agentList)).sheet().doRead();
            if (CollectionUtil.isNotEmpty(agentList)) {
                List<AgentManualUpDownAccountResultVO> list =  ConvertUtil.entityListToModelList(agentList, AgentManualUpDownAccountResultVO.class);
                return agentManualUpApi.checkAgentInfo(list);
            }
            return ResponseVO.success();
        } catch (IOException e) {
            throw new BaowangDefaultException("解析excel失败");
        }
    }
}

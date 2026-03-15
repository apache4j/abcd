package com.cloud.baowang.site.controller.agentManualUpDown;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.activity.api.api.SiteActivityLabApi;
import com.cloud.baowang.activity.api.vo.category.SiteActivityLabsVO;
import com.cloud.baowang.agent.api.api.AgentManualDownApi;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentManualAdjustTypeEnum;
import com.cloud.baowang.agent.api.vo.manualup.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.site.vo.export.agentManual.AgentManualDownExcelVO;
import com.cloud.baowang.site.vo.export.agentManual.AgentManualUpRecordExcelVO;
import com.cloud.baowang.common.excel.userNotifyExcel.AgentManualDownAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.AgentManualDownAccountReadExcelDTO;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountReadExcelDTO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.enums.AgentManualDownAdjustTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: kimi
 */
@Tag(name = "资金-资金调整-代理人工扣除额度")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-manual-down/api")
public class AgentManualDownController {


    private final AgentManualDownApi agentManualDownApi;

    private final AgentManualUpApi agentManualUpApi;

    private final MinioUploadApi minioUploadApi;
    private final SystemParamApi systemParamApi;

    @Operation(summary = "下拉框")
    @PostMapping(value = "/queryManualDownSelect")
    public ResponseVO<List<CodeValueVO>> queryManualDownSelect() {
        return systemParamApi.getSystemParamByType(CommonConstant.AGENT_WALLET_TYPE);
    }

    @GetMapping("getDownBox")
    @Operation(summary = "审核状态下拉")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        return systemParamApi.getSystemParamByType(CommonConstant.AGENT_MANUAL_ADJUST_DOWN_TYPE);
    }

    @GetMapping("getAdjustTypeListByWalletType")
    @Operation(summary = "根据选择的钱包类型，获取减额类型下拉")
    public ResponseVO<List<CodeValueVO>> getAdjustTypeListByWalletType(@RequestParam("walletType") Integer walletType) {
        //加额类型
        ResponseVO<List<CodeValueVO>> resp = systemParamApi.getSystemParamByType(CommonConstant.AGENT_MANUAL_ADJUST_DOWN_TYPE);
        if (resp.isOk()) {
            List<CodeValueVO> list = resp.getData();
            List<AgentManualDownAdjustTypeEnum> enums = AgentManualDownAdjustTypeEnum.listByWalletType(walletType);
            List<String> enumCodes = enums.stream()
                    .map(AgentManualDownAdjustTypeEnum::getCode) // 假enums = {ArrayList@21358}  size = 3设 getCode() 返回 code
                    .toList();
            return ResponseVO.success(list.stream()
                    .filter(codeValue -> enumCodes.contains(codeValue.getCode())) // 假设 getCode() 返回 code
                    .toList());
        }
        return ResponseVO.success();
    }

    @Operation(summary = "校验代理账号-第一步")
    @PostMapping(value = "checkAgentAccount")
    public ResponseVO<List<GetAgentBalanceVO>> checkAgentAccount(@Valid @RequestBody GetAgentBalanceQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentManualUpApi.getAgentBalance(vo);
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
                return agentManualDownApi.checkAgentInfo(list);
            }
            return ResponseVO.success();
        } catch (IOException e) {
            throw new BaowangDefaultException("解析excel失败");
        }
    }

    @Operation(summary = "校验代理余额-第二步")
    @PostMapping(value = "/getAgentBalance")
    public ResponseVO<List<GetAgentBalanceVO>> getAgentBalance(@Valid @RequestBody GetAgentBalanceQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        //校验代理账号
        ResponseVO<List<GetAgentBalanceVO>> resultResp = agentManualUpApi.getAgentBalance(vo);
        if (!resultResp.isOk()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<GetAgentBalanceVO> data = resultResp.getData();

        /*for (GetAgentBalanceVO agentBalanceVO : data) {
            BigDecimal adjustAmount = new BigDecimal(vo.getAdjustAmount());
            if (BigDecimal.ZERO.compareTo(adjustAmount) >= 0) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_NOT_LT_ZREO);
            }
            BigDecimal adjustAmountFmt = adjustAmount.stripTrailingZeros();
            if (adjustAmountFmt.scale() > CommonConstant.business_two) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_SCALE_GT_TWO);
            }
            if ((adjustAmountFmt.precision() - adjustAmountFmt.scale()) > CommonConstant.business_eleven) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_MAX_LENGTH);
            }

            GetAgentBalanceQueryVO getAgentBalanceQueryVO = new GetAgentBalanceQueryVO();
            getAgentBalanceQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
            getAgentBalanceQueryVO.setAgentAccountList(vo.getAgentAccountList());
            if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
                getAgentBalanceQueryVO.setWalletTypeCode(Integer.parseInt(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode()));

            } else if (AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode().equals(String.valueOf(vo.getWalletTypeCode()))) {
                getAgentBalanceQueryVO.setWalletTypeCode(Integer.parseInt(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode()));
            }
            BigDecimal balance = new BigDecimal(agentBalanceVO.getAgentBalance());
            if (balance.compareTo(BigDecimal.ZERO) <= 0 || balance.compareTo(adjustAmount) < 0) {
                throw new BaowangDefaultException(ResultCode.AGENT_MANUAL_DOWN_COIN_AMOUNT_NOT_ENOUGH);
            }
        }*/

        return ResponseVO.success(data);
    }

    @Operation(summary = "代理人工扣除额度保存")
    @PostMapping(value = "/saveManualDown")
    public ResponseVO<Boolean> saveManualDown(@Valid @RequestBody AgentManualDownAddVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        GetAgentBalanceQueryVO queryVO = new GetAgentBalanceQueryVO();
        List<String> agentAccountList = vo.getAgentManualUpDownAccountVOS().stream().map(AgentManualUpDownAccountVO::getAgentAccount).collect(Collectors.toList());
        queryVO.setAgentAccountList(agentAccountList);
        queryVO.setWalletTypeCode(vo.getWalletTypeCode());
        getAgentBalance(queryVO);
        return agentManualDownApi.saveManualDown(vo, CurrReqUtils.getAccount());
    }


    @Operation(summary = "代理人工扣除记录")
    @PostMapping(value = "/listAgentManualDownRecordPage")
    public ResponseVO<AgentManualDownRecordResponseVO> listAgentManualDownRecordPage(@RequestBody AgentManualDownRecordRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentManualDownApi.listAgentManualDownRecordPage(vo);
    }

    @PostMapping("export")
    @Operation(summary = "代理人工扣除记录导出")
    public ResponseVO<?> export(@RequestBody AgentManualDownRecordRequestVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::agentManualDownRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentManualDownApi.listAgentManualDownRecordPageExportCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentManualDownExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(listAgentManualDownRecordPage(param).getData().getRecords(), AgentManualDownExcelVO.class));


        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_MANUAL_DOWN_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}

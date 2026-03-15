package com.cloud.baowang.site.controller.deposit;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.agent.api.api.AgentDepositRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositAllRes;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordReq;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalStatisticsVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.agentManual.AgentDepositRecordExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: aoMiao
 */
@Tag(name = "资金-代理资金记录-代理存款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agentDeposit")
public class AgentDepositRecordController {

    private final SystemParamApi systemParamApi;

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final AgentDepositRecordApi agentDepositRecordApi;
    private final SystemRechargeWayApi wayApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @GetMapping("getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        //时间类型(申请,完成时间),订单来源,订单状态,客户端状态,币种,存款钱包,存款方式
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.APPLY_TIME_TYPE);
        //来源
        param.add(CommonConstant.DEVICE_TYPE);
        //审核状态
        param.add(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
        //客户端状态
        param.add(CommonConstant.DEPOSIT_WITHDRAWAL_ORDER_CUSTOMER_STATUS);

        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            //币种
            List<CodeValueVO> currencyDownBox = currencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode());
            //充值方式
            List<CodeValueVO> rechargeWayListBySiteCode = wayApi.getRechargeWayListBySiteCode(CurrReqUtils.getSiteCode());
            Map<String, List<CodeValueVO>> data = resp.getData();
            data.put("currency_code", currencyDownBox);
            data.put("deposit_withdraw_way", rechargeWayListBySiteCode);
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.DEPOSIT_WITHDRAW_STATUS);
            if (CollectionUtil.isNotEmpty(codeValueVOS)) {
                //订单状态筛选 处理中 21 取消订单 98 失败100 成功101
                List<String> statusList = new ArrayList<>();
                statusList.add(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
                statusList.add(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                Set<String> statusSet = Set.copyOf(statusList);
                codeValueVOS = codeValueVOS.stream()
                        .filter(codeValue -> statusSet.contains(codeValue.getCode()))
                        .toList();
                data.put(CommonConstant.DEPOSIT_WITHDRAW_STATUS, codeValueVOS);
            }
            resp.setData(data);
        }


        return resp;
    }

    @Operation(summary = "分页记录")
    @PostMapping(value = "/listPage")
    public ResponseVO<AgentDepositAllRes> listPage(@Valid @RequestBody AgentDepositRecordReq requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentDepositRecordApi.depositListPage(requestVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody AgentDepositRecordReq vo) {
        String uniqueKey = "tableExport::agent::deposit::" + CommonConstant.ADMIN_CENTER_SITE_CODE + CurrReqUtils.getAccount();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentDepositRecordApi.depositExportCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentDepositRecordExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(agentDepositRecordApi.depositListPage(param).getData().getPages().getRecords(), AgentDepositRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_DEPOSIT_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

    @PostMapping("getDepositTotal")
    @Operation(summary = "统计代理存款")
    public ResponseVO<AgentWithdrawalStatisticsVO> getDepositTotal(@RequestBody AgentDepositRecordReq recordReq) {
        String siteCode = CurrReqUtils.getSiteCode();
        recordReq.setSiteCode(siteCode);
        String currencyCode = recordReq.getCurrencyCode();
        if (StringUtils.isBlank(currencyCode)) {
            throw new BaowangDefaultException(ResultCode.CURRENCY_CODE_NOT_EXIT);
        }
        return ResponseVO.success(agentDepositRecordApi.getDepositTotal(recordReq));
    }
}

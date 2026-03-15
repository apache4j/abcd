package com.cloud.baowang.admin.controller.deposit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.wallet.api.api.UserRechargeReviewRecordApi;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetUserRechargeRecordResponseVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: wade
 */
@AllArgsConstructor
@Tag(name = "会员充值人工确认审核记录")
@RestController
@RequestMapping("/user-recharge-review-record/api")
public class UserRechargeReviewRecordController {

    private final UserRechargeReviewRecordApi userRechargeReviewRecordApi;


    private final MinioUploadApi minioUploadApi;



    @Operation(summary ="下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> getDownBox() {
        // 订单状态-下拉框
        List<CodeValueVO> orderStatus = Lists.newArrayList();
        CodeValueVO oneFail = new CodeValueVO(
                null,
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getCode(),
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT_REJECT.getName());
        CodeValueVO success = new CodeValueVO(
                null,
                DepositWithdrawalOrderStatusEnum.SUCCEED.getCode(),
                ResultCode.MONEY_DEPOSITED.getMessageCode());

        orderStatus.add(oneFail);
        orderStatus.add(success);

        Map<String, Object> result = Maps.newHashMap();
        result.put("orderStatus", orderStatus);

        return ResponseVO.success(result);
    }

    @Operation(summary ="分页列表")
    @PostMapping(value = "/getRechargeRecordPage")
    public ResponseVO<Page<GetUserRechargeRecordResponseVO>> getRechargeRecordPage(@RequestBody GetUserRechargeRecordPageVO vo) {
        return ResponseVO.success(userRechargeReviewRecordApi.getRechargeRecordPage(vo));
    }

    @Operation(summary ="导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody GetUserRechargeRecordPageVO vo, HttpServletResponse response) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userRechargeReviewRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);

            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = userRechargeReviewRecordApi.getTotalCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        String fileName = "会员充值审核记录" + DateUtils.dateToyyyyMMddHHmmss(new Date());
        ExcelUtil.writeForParallel(response, fileName, GetUserRechargeRecordResponseVO.class, vo, 4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()), param -> userRechargeReviewRecordApi.getRechargeRecordPage(param).getRecords());

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                GetUserRechargeRecordResponseVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(userRechargeReviewRecordApi.getRechargeRecordPage(param).getRecords(), GetUserRechargeRecordResponseVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}

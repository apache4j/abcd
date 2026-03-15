package com.cloud.baowang.site.controller.userCoin;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.ChainTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.wallet.api.api.HotWalletAddressApi;
import com.cloud.baowang.wallet.api.vo.recharge.HotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.BatchCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.HotWalletAddressBatchCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.HotWalletAddressSingleCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.SingleCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author qiqi
 */
@RestController
@Tag(name = "链上资金-会员链上资金管理")
@RequestMapping("/userHotWalletAddress/api")
@Slf4j
@AllArgsConstructor
public class UserHotWalletAddressController {

    private HotWalletAddressApi hotWalletAddressApi;

    private MinioUploadApi minioUploadApi;



    @PostMapping("listUserHotAddress")
    @Operation(summary = "会员链上资金管理列表")
    public ResponseVO<UserHotWalletAddressResponseVO> listUserHotAddress(@RequestBody UserHotWalletAddressRequestVO userHotWalletAddressRequestVO) {
        userHotWalletAddressRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return hotWalletAddressApi.listUserHotAddress(userHotWalletAddressRequestVO);
    }

    @PostMapping("trcSingleCollect")
    @Operation(summary = "trc归集")
    public ResponseVO<Void> trcSingleCollect(@Valid @RequestBody HotWalletAddressSingleCollectVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userSingleHotWalletAddress::" + adminId+ChainTypeEnum.TRON.getCode();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.EXECUTE_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey,CommonConstant.business_two.longValue(), TimeUnit.SECONDS);
        }
        //查询会员地址
        HotWalletAddressVO hotWalletAddressVO = hotWalletAddressApi.queryHotWalletAddressByOutAddressNo(vo.getOutAddressNo());
        SingleCollectVO singleCollectVO = new SingleCollectVO();
        singleCollectVO.setPlatNo(CurrReqUtils.getSiteCode());
        singleCollectVO.setAddressNo(hotWalletAddressVO.getAddress());
        singleCollectVO.setChainType(ChainTypeEnum.TRON.getCode());
        return hotWalletAddressApi.singleCollect(singleCollectVO);
    }

    @PostMapping("ercSingleCollect")
    @Operation(summary = "erc归集")
    public ResponseVO<Void> trcCollect(@Valid @RequestBody HotWalletAddressSingleCollectVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userSingleHotWalletAddress::" + adminId+ChainTypeEnum.ETH.getCode();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.EXECUTE_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey,CommonConstant.business_two.longValue(), TimeUnit.SECONDS);
        }
        //查询会员地址
        HotWalletAddressVO hotWalletAddressVO = hotWalletAddressApi.queryHotWalletAddressByOutAddressNo(vo.getOutAddressNo());
        SingleCollectVO singleCollectVO = new SingleCollectVO();
        singleCollectVO.setPlatNo(CurrReqUtils.getSiteCode());
        singleCollectVO.setAddressNo(hotWalletAddressVO.getAddress());
        singleCollectVO.setChainType(ChainTypeEnum.ETH.getCode());
        return hotWalletAddressApi.singleCollect(singleCollectVO);
    }

    @PostMapping("trcBatchCollect")
    @Operation(summary = "trc批量归集")
    public ResponseVO<Void> trcBatchCollect(@Valid @RequestBody HotWalletAddressBatchCollectVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userBatchHotWalletAddress::" + adminId+ChainTypeEnum.TRON.getCode();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.EXECUTE_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey,CommonConstant.THIRTY_MINUTE_SECONDS, TimeUnit.SECONDS);
        }
        BatchCollectVO batchCollectVO = new BatchCollectVO();
        batchCollectVO.setPlatNo(CurrReqUtils.getSiteCode());
        batchCollectVO.setChainType(ChainTypeEnum.TRON.getCode());
        batchCollectVO.setOwnerUserType(OwnerUserTypeEnum.USER.getCode());
        batchCollectVO.setCollectMinAmount(vo.getCollectMinAmount());
        return hotWalletAddressApi.batchCollect(batchCollectVO);
    }

    @PostMapping("ercBatchCollect")
    @Operation(summary = "erc批量归集")
    public ResponseVO<Void> ercBatchCollect(@Valid @RequestBody HotWalletAddressBatchCollectVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userBatchHotWalletAddress::" + adminId+ChainTypeEnum.ETH.getCode();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.EXECUTE_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey,CommonConstant.THIRTY_MINUTE_SECONDS, TimeUnit.SECONDS);
        }
        BatchCollectVO batchCollectVO = new BatchCollectVO();
        batchCollectVO.setPlatNo(CurrReqUtils.getSiteCode());
        batchCollectVO.setChainType(ChainTypeEnum.ETH.getCode());
        batchCollectVO.setOwnerUserType(OwnerUserTypeEnum.USER.getCode());
        batchCollectVO.setCollectMinAmount(vo.getCollectMinAmount());
        return hotWalletAddressApi.batchCollect(batchCollectVO);
    }




    @PostMapping("export")
    @Operation(summary = "会员链上资金管理列表导出")
    public ResponseVO<?> export(@RequestBody UserHotWalletAddressRequestVO userHotWalletAddressRequestVO){
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userHotWalletAddress::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        userHotWalletAddressRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        userHotWalletAddressRequestVO.setPageSize(10000);
        userHotWalletAddressRequestVO.setExportFlag(true);
        ResponseVO<Long> responseVO= hotWalletAddressApi.userHotWalletAddressPageCount(userHotWalletAddressRequestVO);

        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserHotWalletAddressVO.class,
                userHotWalletAddressRequestVO,
                4,
                ExcelUtil.getPages(userHotWalletAddressRequestVO.getPageSize(), responseVO.getData()),
                param -> hotWalletAddressApi.listUserHotAddress(param).getData().getUserHotWalletAddressVOPage().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_HOT_WALLET_ADDRESS)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }




}

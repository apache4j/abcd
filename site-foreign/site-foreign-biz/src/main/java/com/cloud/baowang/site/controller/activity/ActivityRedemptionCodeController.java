package com.cloud.baowang.site.controller.activity;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityRedemptionCodeApi;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.free.FreeGameReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.vo.export.ActivityOrderRecordRespExportVO;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Tag(name = "兑换码管理")
@RestController
@RequestMapping(value = "/activityRedemptionCode/api/")
@Slf4j
public class ActivityRedemptionCodeController {

    @Autowired
    private UserInfoApi userInfoApi;

    @Autowired
    private ActivityRedemptionCodeApi activityRedemptionCodeApi;
    @Autowired
    private MinioUploadApi minioUploadApi;

    @Operation(summary = "添加兑换码基础")
    @PostMapping("save")
    ResponseVO<Boolean> save(@RequestBody ActivityRedemptionCodeConfigVO  vo){
        check(vo);
        return activityRedemptionCodeApi.save(vo);
    }

    @Operation(summary = "编辑兑换码信息")
    @PostMapping("update")
    ResponseVO<Boolean> update(@RequestBody ActivityRedemptionCodeConfigVO vo){

        return activityRedemptionCodeApi.update(vo);
    }

    @Operation(summary = "兑换码基础信息列表")
    @PostMapping("redemptionCodeBasePageList")
    ResponseVO<Page<SiteActivityRedemptionCodeBaseRespVO>> redemptionCodeBasePageList(@RequestBody ActivityRedemptionCodeReqVO vo){

        return activityRedemptionCodeApi.redemptionCodeBasePageList(vo);
    }

    @Operation(summary = "兑换码详情")
    @PostMapping("info")
    ResponseVO<SiteActivityRedemptionCodeBaseRespVO> info(@RequestBody ActivityIdReqVO activityIdReqVO){
        return null;
    }

    @Operation(summary = "删除兑换码-基础信息,详情,兑换码均需要删除")
    @PostMapping("delete")
    ResponseVO<Boolean> delete(@RequestParam("activityId") String activityId){

        if (StrUtil.isEmptyIfStr(activityId)){
            throw new BaowangDefaultException(ResultCode.PARAM_NOT_VALID);
        }
        return activityRedemptionCodeApi.delete(activityId);
    }

    @Operation(summary = "兑换码保存修改参数校验")
    @PostMapping("check")
    ResponseVO<Boolean> check(@RequestBody ActivityRedemptionCodeConfigVO vo){
        return this.activityRedemptionCodeApi.check(vo);
    }

    @Operation(summary = "会员领取兑换码奖励")
    @PostMapping("exchange")
    ResponseVO<Boolean> exchange(@RequestParam("userId") String userId,@RequestParam("code") String code){

        SiteActivityRedemptionCodeBaseRespVO baseVO;
        SiteActivityRedemptionCodeDetailVO detailVO;
        SiteActivityRedemptionCodeExchangeVO vo;
        SiteActivityRedemptionGenCodeVO genCodeVo;
        SiteActivityRedemptionCodeExchangeVO exchangeVO;
        GetByUserAccountVO userInfo;
        ActivityIdReqVO activityIdReqVO;

        Long activityDetailId;
        String batchNo;

        //校验参数是否为空
        if(StrUtil.isEmptyIfStr(userId) || StrUtil.isEmptyIfStr(code)){
            throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }

        userInfo = userInfoApi.getByUserInfoId(userId);
        genCodeVo  = activityRedemptionCodeApi.getGenCodeByCode(code).getData();

        //校验兑换码是否存在
        if (Objects.isNull(genCodeVo)){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //兑换码详情Id
        activityDetailId = genCodeVo.getActivityDetailId();
        //批次号
        batchNo = genCodeVo.getBatchNo();
        //校验会员身份
        if (Objects.isNull(userInfo)){
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_NOT_EXIST_);
        }

        activityIdReqVO = new ActivityIdReqVO();
        detailVO = this.activityRedemptionCodeApi.getActivityRedemptionCodeDetailVOById(activityDetailId).getData();
        activityIdReqVO.setId(detailVO.getActivityId()+"");
        baseVO = this.activityRedemptionCodeApi.info(activityIdReqVO).getData();
        vo = SiteActivityRedemptionCodeExchangeVO.builder().build();

        vo.setCode(code);
        vo.setUserId(userId);
        exchangeVO = activityRedemptionCodeApi.getRedemptionCodeExchangeVO(vo).getData();
        //基础配置不存在
        if (Objects.isNull(baseVO)){
            throw new BaowangDefaultException("未找到兑换码的基础配置,请联系管理员处理!");
        }
        //客户端兑换入口是否打开,
        if (baseVO.getClientSwitch() == CommonConstant.business_zero){
            throw new BaowangDefaultException("兑换入口已关闭,目前无法兑换!");
        }
        //同批次同一个会员只能兑换一次
        if (Objects.nonNull(exchangeVO)){
            if(StrUtil.isNotBlank(exchangeVO.getUserId()) && StrUtil.isNotBlank(exchangeVO.getBatchNo())){
                throw new BaowangDefaultException("会员："+userId+"所使用的兑换码："+code+"已兑换换过了，请勿重复兑换!");
            }
        }
        //调用兑换接口
        return this.activityRedemptionCodeApi.exchange(userId,code);
    }

    @Operation(summary = "兑换码列表-导出")
    @PostMapping("/export")
    ResponseVO<String> export(@RequestBody ActivityRedemptionCodeReqVO reqVO) {
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::activityOrderRecord::" + adminId;

        /**
        if (CurrReqUtils.getHandicapMode()==null||CurrReqUtils.getHandicapMode()== 0){
            if (RedisUtil.isKeyExist(uniqueKey)) {
                long remain = RedisUtil.getRemainExpireTime(uniqueKey);
                return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
            } else {
                RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
            }
            reqVO.setPageSize(10000);
            Long count = this.activityRedemptionCodeApi.countRedemption(reqVO).getData();


            return minioUploadApi.uploadXlsxAndFileExport(
                    UploadXlsxVO.builder()
                            .bucket(ExcelUtil.BAOWANG_BUCKET)
                            .byteArray(byteArray)
                            .pageName(CommonConstant.ACTIVITY_ORDER_RECORD)
                            .adminId(CurrReqUtils.getAccount())
                            .siteCode(CurrReqUtils.getSiteCode())
                            .build());
        }else {
            if (RedisUtil.isKeyExist(uniqueKey)) {
                long remain = RedisUtil.getRemainExpireTime(uniqueKey);
                return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
            } else {
                RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
            }
            reqVO.setPageSize(10000);
            Long count = activityParticipateV2Api.getActivityOrderRecordCount(reqVO).getData();

            byte[] byteArray = ExcelUtil.writeForParallel(
                    ActivityOrderRecordRespExportVO.class,
                    reqVO,
                    4,
                    ExcelUtil.getPages(reqVO.getPageSize(), count),
                    param -> ConvertUtil.entityListToModelList(activityParticipateV2Api.queryPageActivityOrderRecord(param).getData().getRecords(),
                            ActivityOrderRecordRespExportVO.class));

            return minioUploadApi.uploadXlsxAndFileExport(
                    UploadXlsxVO.builder()
                            .bucket(ExcelUtil.BAOWANG_BUCKET)
                            .byteArray(byteArray)
                            .pageName(CommonConstant.ACTIVITY_ORDER_RECORD)
                            .adminId(CurrReqUtils.getAccount())
                            .siteCode(CurrReqUtils.getSiteCode())
                            .build());
        }

            */
        return ResponseVO.success();
    }
}

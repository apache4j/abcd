package com.cloud.baowang.user.api.medal;

import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalAcquireApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordNewReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.service.MedalAcquireRecordService;
import com.cloud.baowang.user.service.SiteMedalInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/6 11:26
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class MedalAcquireApiImpl implements MedalAcquireApi {

    private final SiteMedalInfoService siteMedalInfoService;

    private final MedalAcquireRecordService medalAcquireRecordService;

    @Override
    public ResponseVO<SiteMedalInfoRespVO> findByMedalCode(MedalAcquireCondReqVO medalAcquireCondReqVO) {
        SiteMedalInfoCondReqVO siteMedalInfoCondReqVO=new SiteMedalInfoCondReqVO();
        siteMedalInfoCondReqVO.setSiteCode(medalAcquireCondReqVO.getSiteCode());
        siteMedalInfoCondReqVO.setMedalCode(medalAcquireCondReqVO.getMedalCodeEnum().getCode());
        return siteMedalInfoService.selectByCond(siteMedalInfoCondReqVO);
    }

    /**
     * 勋章是否可以被解锁
     * @param medalAcquireCondVO 解锁原始值
     * @return true:可以解锁 false: 不可以解锁
     */
    @Override
    public ResponseVO<Boolean> canLockMedal(MedalAcquireCondVO medalAcquireCondVO) {
        MedalCodeEnum medalCodeEnum=medalAcquireCondVO.getMedalCodeEnum();
        if (medalCodeEnum == null) {
            log.info("勋章参数为空 不能解锁");
            return ResponseVO.success(Boolean.FALSE);
        }
        SiteMedalInfoCondReqVO siteMedalInfoCondReqVO=new SiteMedalInfoCondReqVO();
        siteMedalInfoCondReqVO.setSiteCode(medalAcquireCondVO.getSiteCode());
        siteMedalInfoCondReqVO.setMedalCode(medalCodeEnum.getCode());
        ResponseVO<SiteMedalInfoRespVO>  siteMedalInfoRespVOResponseVO=siteMedalInfoService.selectByCond(siteMedalInfoCondReqVO);
        if(!siteMedalInfoRespVOResponseVO.isOk()){
            SiteMedalInfoRespVO siteMedalInfoRespVO=siteMedalInfoRespVOResponseVO.getData();
            if(medalCodeEnum.getCondNum()>=1){
                String cond1=siteMedalInfoRespVO.getCondNum1();
                if(ObjectUtils.nullSafeEquals(cond1,medalAcquireCondVO.getOriginVal1())){
                    log.info("勋章参数1:{}匹配,符合要求,可以解锁",medalAcquireCondVO);
                    return ResponseVO.success(Boolean.TRUE);
                }
            }
            if(medalCodeEnum.getCondNum()>=2){
                String cond2=siteMedalInfoRespVO.getCondNum2();
                if(ObjectUtils.nullSafeEquals(cond2,medalAcquireCondVO.getOriginVal2())){
                    log.info("勋章参数2:{}匹配,符合要求,可以解锁",medalAcquireCondVO);
                    return ResponseVO.success(Boolean.TRUE);
                }
            }
        }
        return ResponseVO.success(Boolean.FALSE);
    }

    /**
     * 只负责解锁勋章,如何获得勋章,勋章解锁条件是否满足由调用方提供
     * @param medalAcquireReqVO 解锁条件
     * @return
     */
    @Override
    public ResponseVO<Boolean> unLockMedal(MedalAcquireReqVO medalAcquireReqVO) {
         //判断勋章是否配置
        SiteMedalInfoCondReqVO siteMedalInfoCondReqVO=new SiteMedalInfoCondReqVO();
        siteMedalInfoCondReqVO.setSiteCode(medalAcquireReqVO.getSiteCode());
        siteMedalInfoCondReqVO.setMedalCode(medalAcquireReqVO.getMedalCode());
        ResponseVO<SiteMedalInfoRespVO>  siteMedalInfoRespVOResponseVO=siteMedalInfoService.selectByCond(siteMedalInfoCondReqVO);
        if(!siteMedalInfoRespVOResponseVO.isOk()) {
            log.info("当前站点:{}勋章:{}尚未配置",medalAcquireReqVO.getSiteCode(),medalAcquireReqVO.getMedalCode());
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        SiteMedalInfoRespVO siteMedalInfoRespVO = siteMedalInfoRespVOResponseVO.getData();
        if(!EnableStatusEnum.ENABLE.getCode().equals(siteMedalInfoRespVO.getStatus())){
            log.info("当前站点:{}勋章:{}状态尚未启用,无法解锁",medalAcquireReqVO.getSiteCode(),medalAcquireReqVO.getMedalCode());
            return ResponseVO.success(Boolean.FALSE);
        }
        MedalAcquireRecordNewReqVO medalAcquireRecordNewReqVO=new MedalAcquireRecordNewReqVO();
        medalAcquireRecordNewReqVO.setSiteCode(medalAcquireReqVO.getSiteCode());
        medalAcquireRecordNewReqVO.setMedalCode(medalAcquireReqVO.getMedalCode());
        medalAcquireRecordNewReqVO.setUserId(medalAcquireReqVO.getUserId());
        medalAcquireRecordNewReqVO.setUserAccount(medalAcquireReqVO.getUserAccount());
        medalAcquireRecordService.insert(medalAcquireRecordNewReqVO,siteMedalInfoRespVO);
        log.info("当前用户:{}获得勋章:{}成功",medalAcquireReqVO.getUserId(),medalAcquireReqVO.getMedalCode());
        return ResponseVO.success(Boolean.TRUE);
    }



}

package com.cloud.baowang.user.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalAcquireRecordApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordRespVO;
import com.cloud.baowang.user.po.MedalAcquireRecordPO;
import com.cloud.baowang.user.service.MedalAcquireRecordService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/31 16:03
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class MedalAcquireRecordApiImpl implements MedalAcquireRecordApi {
    @Resource
    private MedalAcquireRecordService medalAcquireRecordService;

    @Override
    public ResponseVO<Page<MedalAcquireRecordRespVO>> listPage(MedalAcquireRecordReqVO medalAcquireRecordReqVO) {
        return medalAcquireRecordService.listPage(medalAcquireRecordReqVO);
    }

    @Override
    public ResponseVO<List<MedalAcquireRecordRespVO>> listByCond(MedalAcquireRecordCondReqVO medalAcquireRecordCondReqVO) {
        return medalAcquireRecordService.listByCond(medalAcquireRecordCondReqVO);
    }

    @Override
    public ResponseVO<Long> countByCond(MedalAcquireRecordCondReqVO medalAcquireRecordCondReqVO) {
        return medalAcquireRecordService.countByCond(medalAcquireRecordCondReqVO);
    }

    @Override
    public ResponseVO<List<MedalAcquireRecordRespVO>> getRecordByUserAccountAndMedalType(List<String> userAccount, String siteCode, String medalCode) {
        return medalAcquireRecordService.getRecordByUserAccountAndMedalType(userAccount,siteCode,medalCode);
    }
}

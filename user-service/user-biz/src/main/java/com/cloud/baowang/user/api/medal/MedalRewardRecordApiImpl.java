package com.cloud.baowang.user.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.MedalRewardRecordApi;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordRespVO;
import com.cloud.baowang.user.service.MedalRewardRecordService;
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
public class MedalRewardRecordApiImpl implements MedalRewardRecordApi {
    @Resource
    private MedalRewardRecordService medalRewardRecordService;

    @Override
    public ResponseVO<Page<MedalRewardRecordRespVO>> listPage(MedalRewardRecordReqVO medalRewardRecordReqVO) {
        return medalRewardRecordService.listPage(medalRewardRecordReqVO);
    }

    @Override
    public ResponseVO<List<MedalRewardRecordRespVO>> listByCond(MedalRewardRecordCondReqVO medalRewardRecordCondReqVO) {
       // return medalRewardRecordService.listByCond(medalRewardRecordCondReqVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Long> countByCond(MedalRewardRecordCondReqVO medalRewardRecordCondReqVO) {
      //  return medalRewardRecordService.countByCond(medalRewardRecordCondReqVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<MedalRewardRecordRespVO>> getRecordByUserAccountAndMedalType(List<String> userAccount, String siteCode, String medalCode) {
       // return medalRewardRecordService.getRecordByUserAccountAndMedalType(userAccount,siteCode,medalCode);
        return ResponseVO.success();
    }
}

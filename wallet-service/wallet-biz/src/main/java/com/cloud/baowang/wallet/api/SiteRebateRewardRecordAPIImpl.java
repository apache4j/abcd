package com.cloud.baowang.wallet.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteRebateRewardRecordAPI;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteRebateRewardRecordVO;
import com.cloud.baowang.wallet.service.SiteRebateRewardRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteRebateRewardRecordAPIImpl implements SiteRebateRewardRecordAPI {
    private SiteRebateRewardRecordService siteRebateRewardRecordService;


//    @Override
//    public ResponseVO saveSiteRebateRewardRecord(String siteCode) {
//        List<SiteRebateRewardRecordVO> data=new ArrayList<>();
//        SiteRebateRewardRecordVO vo=new SiteRebateRewardRecordVO();
//        vo.setRewardAmount(BigDecimal.ONE);
//        vo.setUserId("43903257");
//        vo.setOrderNo(System.currentTimeMillis()+"");
//        data.add(vo);
//        siteRebateRewardRecordService.bachAddSiteRebateRewardRecordS(data,siteCode);
//        return ResponseVO.success();
//    }

    @Override
    public ResponseVO rebateReward(String id) {
        return ResponseVO.success(siteRebateRewardRecordService.rebateReward(id));
    }

    @Override
    public ResponseVO<Boolean> rebateUserReward(String userId) {
        siteRebateRewardRecordService.rebateUserReward(userId);
        return ResponseVO.success(true);
    }
}

package com.cloud.baowang.user.api;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.user.api.api.UserJobHandlerApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.service.SiteMedalInfoService;
import com.cloud.baowang.user.service.UserInfoService;
import com.cloud.baowang.user.task.UserOfflineDaysTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserJobHandlerApiImpl implements UserJobHandlerApi {

    private final UserOfflineDaysTask userOfflineDaysTask;
    private final UserInfoService userInfoService;
    private final SiteMedalInfoService siteMedalInfoService;

    @Override
    public void userOfflineDays() {
        userOfflineDaysTask.userOfflineDays();
    }

    @Override
    public void userRegisterDays(String siteCode) {
        List<UserInfoVO> userList = userInfoService.getUsersBySiteCode(siteCode);
        SiteMedalInfoCondReqVO reqVO = new SiteMedalInfoCondReqVO();
        reqVO.setMedalCode(MedalCodeEnum.MEDAL_1015.getCode());
        reqVO.setSiteCode(siteCode);

        ResponseVO<SiteMedalInfoRespVO> responseVO = siteMedalInfoService.selectByCond(reqVO);
        SiteMedalInfoRespVO siteMedalInfoRespVO = responseVO.getData();

        List<MedalAcquireReqVO> medalDetailList = new ArrayList<>();
        for (UserInfoVO userInfoVO : userList) {
           Long days = DateUtil.between(DateUtil.beginOfDay(new Date(userInfoVO.getRegisterTime())), DateUtil.endOfDay(new Date()), DateUnit.DAY) + 1;
           if (days >= Long.parseLong(siteMedalInfoRespVO.getCondNum1())) {

               MedalAcquireReqVO detail = new MedalAcquireReqVO();
               detail.setSiteCode(siteCode);
               detail.setMedalCode(MedalCodeEnum.MEDAL_1015.getCode());
               detail.setUserId(userInfoVO.getUserId());
               detail.setUserAccount(userInfoVO.getUserAccount());
               medalDetailList.add(detail);
           }
        }
        if (medalDetailList.size() > 0) {
            MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
            medalAcquireBatchReqVO.setSiteCode(siteCode);
            medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalDetailList);
            KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
        }
    }

}

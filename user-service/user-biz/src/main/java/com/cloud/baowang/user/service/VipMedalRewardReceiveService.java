package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.user.api.api.medal.MedalAcquireApi;
import com.cloud.baowang.user.api.api.medal.MedalAcquireRecordApi;
import com.cloud.baowang.user.api.vo.medal.*;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VipMedalRewardReceiveService {
    private final MedalAcquireApi medalAcquireApi;
    private final MedalAcquireRecordApi recordApi;
    private final UserInfoRepository userInfoRepository;

    /**
     * @param userAccounts
     * @param siteCode
     * @return
     */
    public ResponseVO<Boolean> receiveMedal(List<UserInfoVO> userInfoVOS, String siteCode) {
        //获取勋章配置条件
        MedalAcquireCondReqVO vo = new MedalAcquireCondReqVO();
        vo.setSiteCode(siteCode);
        vo.setMedalCodeEnum(MedalCodeEnum.MEDAL_1004);

        if (CollectionUtil.isNotEmpty(userInfoVOS)) {
            List<String> userAccounts = userInfoVOS.stream().map(UserInfoVO::getUserAccount).toList();
            //判断哪一些领取过独山高楼勋章了
            ResponseVO<List<MedalAcquireRecordRespVO>> recordResp = recordApi.getRecordByUserAccountAndMedalType(userAccounts, siteCode, MedalCodeEnum.MEDAL_1004.getCode());
            if (!recordResp.isOk()) {
                log.info("vip升级领取勋章,获取会员派发记录失败,原因:{}", recordResp.getMessage());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            List<MedalAcquireRecordRespVO> data = recordResp.getData();
            Map<String, MedalAcquireRecordRespVO> map = new HashMap<>();
            if (CollectionUtil.isNotEmpty(data)) {
                map = data.stream().collect(Collectors.toMap(MedalAcquireRecordRespVO::getUserAccount, record -> record));
            }

            Map<String, MedalAcquireRecordRespVO> finalMap = map;
            userInfoVOS = userInfoVOS.stream()
                    .filter(userAccount -> {
                        if (finalMap.containsKey(userAccount.getUserAccount())) {
                            log.info("vip升级领取勋章,当前会员:{}已派发过勋章,不需要再次领取", userAccount);
                            return false;
                        }
                        return true;
                    }).toList();
            //没有派发过,并且也都满足条件后,剩下的会员,准备批量派发
            if (CollectionUtil.isNotEmpty(userInfoVOS)) {
                MedalAcquireBatchReqVO reqVO = new MedalAcquireBatchReqVO();
                reqVO.setSiteCode(siteCode);
                List<MedalAcquireReqVO> reqVOList = new ArrayList<>();
                String code = MedalCodeEnum.MEDAL_1004.getCode();
                for (UserInfoVO userInfoPO : userInfoVOS) {
                    MedalAcquireReqVO acquireReqVO = new MedalAcquireReqVO();
                    acquireReqVO.setMedalCode(code);
                    acquireReqVO.setUserAccount(userInfoPO.getUserAccount());
                    acquireReqVO.setUserId(userInfoPO.getUserId());
                    acquireReqVO.setSiteCode(siteCode);
                    reqVOList.add(acquireReqVO);
                }
                reqVO.setMedalAcquireReqVOList(reqVOList);
                log.info("发起批量领取会员勋章,当前会员账号数组:{}", JSON.toJSONString(userAccounts));
                KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, reqVO);
            }

        }


        return null;
    }
}

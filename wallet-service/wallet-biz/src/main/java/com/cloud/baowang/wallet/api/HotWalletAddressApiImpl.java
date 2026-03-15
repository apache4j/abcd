package com.cloud.baowang.wallet.api;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.HotWalletAddressApi;
import com.cloud.baowang.wallet.api.vo.recharge.GenHotWalletAddressReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.HotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.BatchCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.SingleCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressResponseVO;
import com.cloud.baowang.wallet.service.HotWalletAddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class HotWalletAddressApiImpl implements HotWalletAddressApi {


    private HotWalletAddressService hotWalletAddressService;

    public ResponseVO<String> getHotWalletAddress(GenHotWalletAddressReqVO genHotWalletAddressReqVO){
        return hotWalletAddressService.getHotWalletAddress(genHotWalletAddressReqVO);
    }

    @Override
    public HotWalletAddressVO queryHotWalletAddress(String address) {
        return hotWalletAddressService.queryHotWalletAddress(address);
    }

    @Override
    public List<HotWalletAddressVO> queryHotWalletAddressByUserId(String userId) {
        return hotWalletAddressService.queryHotWalletAddressByUserId(userId);
    }

    @Override
    public ResponseVO<UserHotWalletAddressResponseVO> listUserHotAddress(UserHotWalletAddressRequestVO userHotWalletAddressRequestVO) {
        return hotWalletAddressService.listUserHotAddress(userHotWalletAddressRequestVO);
    }

    public ResponseVO<Long> userHotWalletAddressPageCount(UserHotWalletAddressRequestVO vo) {
        return ResponseVO.success(hotWalletAddressService.userHotWalletAddressPageCount(vo));
    }

    @Override
    public ResponseVO<AgentHotWalletAddressResponseVO> listAgentHotAddress(AgentHotWalletAddressRequestVO userHotWalletAddressRequestVO) {
        return hotWalletAddressService.listAgentHotAddress(userHotWalletAddressRequestVO);
    }

    @Override
    public ResponseVO<Long> agentHotWalletAddressPageCount(AgentHotWalletAddressRequestVO vo) {
        return ResponseVO.success(hotWalletAddressService.agentHotWalletAddressPageCount(vo));
    }

    @Override
    public HotWalletAddressVO queryHotWalletAddressByOutAddressNo(String outAddressNo) {
        return hotWalletAddressService.queryHotWalletAddressByOutAddressNo(outAddressNo);
    }

    @Override
    public ResponseVO<Void> singleCollect(SingleCollectVO singleCollectVO) {
        return hotWalletAddressService.singleCollect(singleCollectVO);
    }

    @Override
    public ResponseVO<Void> batchCollect(BatchCollectVO batchCollectVO) {
        return hotWalletAddressService.batchCollect(batchCollectVO);
    }

}

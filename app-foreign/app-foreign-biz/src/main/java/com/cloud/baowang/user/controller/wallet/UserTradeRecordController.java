package com.cloud.baowang.user.controller.wallet;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserTradeRecordApi;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "客户端-我的-交易记录")
@RestController
@AllArgsConstructor
@RequestMapping("/userTradeRecord/api")
public class UserTradeRecordController {

    private final UserTradeRecordApi userTradeRecordApi;

    private final SystemParamApi systemParamApi;


    @Operation(summary = "下拉框 交易类型:trade_type ,状态:deposit_withdraw_customer_status,日期：trade_date_num ")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {

        List<String> list = new ArrayList<>();
        list.add(CommonConstant.TRADE_TYPE);
        list.add(CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS);
        list.add(CommonConstant.TRADE_DATE_NUM);
        Map<String, List<CodeValueVO>> map = systemParamApi.getSystemParamsByList(list).getData();
        List<CodeValueVO> typeList = map.get(CommonConstant.TRADE_TYPE);
        typeList = typeList.stream()
                .filter(obj -> !obj.getCode().equals(CommonConstant.business_three_str))
                .collect(Collectors.toList());
        map.put(CommonConstant.TRADE_TYPE,typeList);
        return ResponseVO.success(map);
    }

    /**
     * 获取充值，提款，平台币兑换 交易记录
     */

    @Operation(summary = "获取充值，提款，平台币兑换 交易记录")
    @PostMapping("tradeRecordList")
    public ResponseVO<Page<UserTradeRecordResponseVO>> tradeRecordList(@RequestBody UserTradeRecordRequestVO vo){
        String userId = CurrReqUtils.getOneId();
        vo.setUserId(userId);
        return ResponseVO.success(userTradeRecordApi.tradeRecordList(vo));

    }

    @Operation(summary = "交易记录详情")
    @PostMapping("tradeRecordDetail")
    public ResponseVO<UserTradeRecordDetailResponseVO> tradeRecordDetail(@RequestBody UserTradeRecordDetailRequestVO vo){
        String userId = CurrReqUtils.getOneId();
        vo.setUserId(userId);
        return ResponseVO.success(userTradeRecordApi.tradeRecordDetail(vo));

    }

}

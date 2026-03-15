package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.api.UserTradeRecordApi;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserTradeRecordResponseVO;
import com.cloud.baowang.wallet.service.UserTradeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@AllArgsConstructor
@RestController
public class UserTradeRecordApiImpl implements UserTradeRecordApi {

    private final UserTradeRecordService userTradeRecordService;
    @Override
    public Page<UserTradeRecordResponseVO> tradeRecordList(UserTradeRecordRequestVO vo) {


        return userTradeRecordService.tradeRecordList(vo);
    }

    @Override
    public UserTradeRecordDetailResponseVO tradeRecordDetail(UserTradeRecordDetailRequestVO vo) {
        return userTradeRecordService.tradeRecordDetail(vo);
    }
}

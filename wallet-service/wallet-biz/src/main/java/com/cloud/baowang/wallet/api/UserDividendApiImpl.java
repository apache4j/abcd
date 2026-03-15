package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserDividendApi;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendPageVO;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendRequestVO;
import com.cloud.baowang.wallet.service.UserCoinRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : 小智
 * @Date : 18/6/24 2:35 PM
 * @Version : 1.0
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserDividendApiImpl implements UserDividendApi {

    private final UserCoinRecordService userCoinRecordService;

    @Override
    public ResponseVO<Page<UserDividendPageVO>> userDividendPage(UserDividendRequestVO requestVO) {
        return userCoinRecordService.userDividendPage(requestVO);
    }
}

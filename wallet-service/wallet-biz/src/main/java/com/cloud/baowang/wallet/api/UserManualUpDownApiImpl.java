package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.api.UserManualUpDownApi;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import com.cloud.baowang.wallet.service.UserManualUpDownRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/19 17:46
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserManualUpDownApiImpl implements UserManualUpDownApi {

    private UserManualUpDownRecordService userManualUpDownRecordService;


    @Override
    public Map<String, UserManualDownRecordVO> listStaticData(List<String> userIds) {
        return userManualUpDownRecordService.listStaticData(userIds);
    }

    @Override
    public Page<UserManualDownRecordVO> listPage(UserManualDownRecordRequestVO userManualDownRecordRequestVO) {
        return userManualUpDownRecordService.listPage(userManualDownRecordRequestVO);
    }

    @Override
    public BigDecimal getActivityAmountByUserId(String userId) {
        return userManualUpDownRecordService.getActivityAmountByUserId(userId);
    }
}

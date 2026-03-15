package com.cloud.baowang.system.api.member;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.member.BusinessLoginInfoApi;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.service.member.BusinessLoginInfoService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiqi
 */
@Validated
@AllArgsConstructor
@RestController
public class BusinessLoginInfoApiImpl implements BusinessLoginInfoApi {

    private final BusinessLoginInfoService businessLoginInfoService;


    @Override
    public String addLoginInfo(BusinessLoginInfoAddVO businessLoginInfoAddVO){
       return businessLoginInfoService.addLoginInfo(businessLoginInfoAddVO);
    }

    @Override
    public ResponseVO<Page<BusinessLoginInfoVO>> queryBusinessLoginInfoPage(UserLoginRequestVO businessRoleQueryVO) {
        return ResponseVO.success(businessLoginInfoService.queryBusinessLoginInfoPage(businessRoleQueryVO));
    }

}

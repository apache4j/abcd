package com.cloud.baowang.admin.service;

import com.cloud.baowang.system.api.api.member.BusinessLoginInfoApi;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiqi
 */
@AllArgsConstructor
@Service
public class LoginInfoService {

    private final BusinessLoginInfoApi businessLoginInfoApi;


    public void recordLoginInfo(String userName, Integer status, String errMsg, String ip, String loginLocation) {
        BusinessLoginInfoAddVO businessLoginInfoAddVO = new BusinessLoginInfoAddVO();
        businessLoginInfoAddVO.setUserName(userName);
        businessLoginInfoAddVO.setStatus(status);
        businessLoginInfoAddVO.setMsg(errMsg);
        businessLoginInfoAddVO.setIpaddr(ip);
        businessLoginInfoAddVO.setLoginLocation(loginLocation);
        businessLoginInfoApi.addLoginInfo(businessLoginInfoAddVO);
    }

    public void recordLoginInfoRecord(BusinessLoginInfoAddVO businessLoginInfoAddVO) {
        businessLoginInfoApi.addLoginInfo(businessLoginInfoAddVO);
    }
}

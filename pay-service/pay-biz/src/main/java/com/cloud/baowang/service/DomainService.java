package com.cloud.baowang.service;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainQueryVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/3/10 15:08
 * @Version: V1.0
 **/
@Component
@Slf4j
public class DomainService {


    @Autowired
    private DomainInfoApi domainInfoApi;


    /**
     * 按照类型获取跳转地址
     * @param domainType 域名类型;1-代理端,2-H5端,3-app端,4-后端 字典CODE:site_domain_type
     * @return
     */
    public String getReturnUrl(String siteCode,Integer domainType){
        String returnUrl=null;
        DomainQueryVO domainQueryVO = new DomainQueryVO();
        domainQueryVO.setSiteCode(siteCode);
        domainQueryVO.setStatus(1);
        domainQueryVO.setDomainType(domainType);
        //获取域名
        DomainVO domainVO = domainInfoApi.getDomainByType(domainQueryVO);
        if (domainVO == null) {
            throw new BaowangDefaultException(ResultCode.DOMAIN_NULL);
        }
        String url = CurrReqUtils.getBizCustom();
        log.info("MidPay获取的bizCustom:{}", url);
        if (url == null) {
            url = domainVO.getDomainAddr();
        }
        if (url.startsWith("http")) {
            returnUrl = url;
        } else {
            returnUrl = "http://" + url;
        }
        return returnUrl;
    }
}

package com.cloud.baowang.system.api.ipinfo;

import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.system.api.api.ipinfo.IpInfoOwnerApi;
import com.cloud.baowang.system.util.IpOwnerUtils;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.util.CollectionUtils;

public class IpInfoOwnerApiImpl implements IpInfoOwnerApi {
    @Override
    public IPRespVO parseIpAddr(String ipAddr) {
        CityResponse cityResponse=IpOwnerUtils.parseIpAddr(ipAddr);
        if(cityResponse!=null){
            IPRespVO ipRespVO=new IPRespVO();
            ipRespVO.setCountryCode(cityResponse.getCountry().getIsoCode());
            ipRespVO.setCountryName(cityResponse.getCountry().getName());
            if(!CollectionUtils.isEmpty(cityResponse.getSubdivisions())){
                ipRespVO.setProvinceName(cityResponse.getSubdivisions().get(0).getName());
            }
            ipRespVO.setCityName(cityResponse.getCity().getName());
            return ipRespVO;
        }
        return null;
    }
}

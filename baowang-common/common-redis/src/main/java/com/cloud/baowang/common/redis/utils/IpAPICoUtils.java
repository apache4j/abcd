package com.cloud.baowang.common.redis.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.IpAddressUtils;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author : 小智
 * @Date : 2025/7/30 15:09
 * @Version : 1.0
 */
@Slf4j
public class IpAPICoUtils {

    public static IPRespVO getIp(String ip){
        log.info("根据ip查询归属地:{}",ip);
        IPRespVO ipRespVO= new IPRespVO();
        if (StrUtil.isEmpty(ip)) {
            return ipRespVO;
        }
        String key= CacheConstants.SYSTEM_IPAPI_INFO_IP.concat(ip);
        IPRespVO ipCacheVal = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(ipCacheVal)){
            log.info("根据ip查询归属地:{},命中缓存:{}",ip,ipCacheVal);
            return ipCacheVal;
        }
        try {
            IPResponse ipResponse= IpAddressUtils.queryIpRegion(ip);
            log.info("根据ip查询归属地,国内获取结果:ip:{}-->{}",ip,ipResponse);
            if (!ObjectUtils.isEmpty(ipResponse)){
                ipRespVO.setCityName(ipResponse.getCity());
                ipRespVO.setCountryCode(ipResponse.getCountryCode());
                ipRespVO.setCountryName(ipResponse.getCountryName());
                ipRespVO.setProvinceName(ipResponse.getRegion());
            }
            //国内走ipApi 海外走本地自建IP库
//            if(!"CN".equals(ipResponse.getCountryCode())){
//                String urlStr = "http://ipgeo.wintosys.com/json/" + ip ;
//                String response= HttpClient4Util.get(urlStr);
//                log.info("根据ip查询归属地,自建IP库获取结果:ip:{}-->{}",ip,response);
//                if(!StringUtils.hasText(response)){
//                    JSONObject ipGeoResponse=JSONObject.parseObject(response);
//                    if(ipGeoResponse.containsKey("country_code")){
//                        ipRespVO.setCountryName(ipGeoResponse.getString("country_name"));
//                        ipRespVO.setProvinceName(ipGeoResponse.getString("region_name"));
//                        ipRespVO.setCityName(ipGeoResponse.getString("city"));
//                    }
//                }
//            }
            if(StringUtils.hasText(ipRespVO.getCountryCode())){
                RedisUtil.setValue(key,ipRespVO, 1L, TimeUnit.DAYS);
            }
            return ipRespVO;
        } catch (Exception e) {
            log.info("根据ip查询归属地,发生异常:{}", e);
        }
        return ipRespVO;
    }

    public static void main(String[] args) {
        getIp("103.20.81.126");
    }
}

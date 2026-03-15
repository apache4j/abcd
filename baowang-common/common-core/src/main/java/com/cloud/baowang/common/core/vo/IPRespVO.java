package com.cloud.baowang.common.core.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 根据ip获取国家地区 统一返回
 */
@Data
public class IPRespVO {

    /**
     * 国家代码
     */
    private String countryCode;
    /**
     * 国家名称
     */
    private String countryName;

    /**
     * 省份 州
     */
    private String provinceName;
    /**
     * 城市 地区
     */
    private String cityName;
    /**
     * 街道
     */
    private String street;


    public String getAddress() {
        StringBuilder stb=new StringBuilder();
        if(StringUtils.hasText(this.getCityName())){
            stb.append(this.getCityName()).append(" ");
        }

        if(StringUtils.hasText(this.getProvinceName())){
            stb.append(this.getProvinceName()).append(" ");
        }

        if(StringUtils.hasText(this.getCountryName())){
            stb.append(this.getCountryName()).append(" ");
        }
       /* if(StringUtils.hasText(this.getStreet())){
            stb.append(this.getStreet()).append(" ");
        }*/
        return stb.toString();
    }



}

package com.cloud.baowang.common.core.vo;

import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @Author : 小智
 * @Date : 2025/7/30 15:32
 * @Version : 1.0
 */
@Data
public class IpApiVO {

    private String ip;

    private String network;

    private String version;

    private String city;

    private String region;

    private String regionCode;

    private String country;

    private String countryName;

    private String countryCode;

    private String countryCodeIso3;

    private String countryCapital;

    private String countryTld;

    private String continentCode;

    private String in_eu;

    private String postal;

    private String latitude;

    private String longitude;

    private String timezone;

    private String utcOffset;

    private String countryCallingCode;

    private String currency;

    private String currencyName;

    private String languages;

    private String countryArea;

    private String countryPopulation;

    private String asn;

    private String org;

    private String address;

    public String getAddress() {
        StringBuilder stb=new StringBuilder();
        if(StringUtils.hasText(this.getCountryName())){
            stb.append(this.getCountryName()).append(" ");
        }
        if(StringUtils.hasText(this.getCity())){
            stb.append(this.getCity());
        }
        return stb.toString();
    }
}

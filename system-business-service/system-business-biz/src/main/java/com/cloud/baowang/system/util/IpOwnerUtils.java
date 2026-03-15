package com.cloud.baowang.system.util;

import com.alibaba.fastjson2.JSON;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;

/**
 *
 * https://www.maxmind.com/en/account/sign-in
 * ford20250501@gmail.com/!@#1232025Qwe
 */
public class IpOwnerUtils {
    private static final Logger logger= LoggerFactory.getLogger(IpOwnerUtils.class);
    public static CityResponse parseIpAddr(String ipAddr) {
        try{
            File database = new File("/opt/geodb/GeoLite2-City.mmdb");
            // File database = new File("/Users/joke/Desktop/opt/geodb/GeoLite2-ASN.mmdb");
            //     File database = new File("/Users/joke/Desktop/opt/geodb/GeoLite2-Country.mmdb");
            // This reader object should be reused across lookups as creation of it is
            // expensive.
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            // If you want to use caching at the cost of a small (~2MB) memory overhead:
            // new DatabaseReader.Builder(file).withCache(new CHMCache()).build();
            InetAddress ipAddress = InetAddress.getByName(ipAddr);
           /* AsnResponse response = reader.asn(ipAddress);
            System.out.println("response:"+JSON.toJSONString(response));*/
          /*  CountryResponse response = reader.country(ipAddress);
            System.out.println("response:"+JSON.toJSONString(response));*/
            CityResponse response = reader.city(ipAddress);
            logger.info("base on ip:{},find info:{}",ipAddr,JSON.toJSONString(response));
          //  Country country = response.getCountry();
           // City city=response.getCity();
          //  Location location = response.getLocation();
           /* System.out.println("country:"+JSON.toJSONString(country));
            System.out.println("city:"+JSON.toJSONString(city));
            System.out.println("location:"+JSON.toJSONString(location));
            System.out.println("postal:"+JSON.toJSONString(response.getPostal()));
            System.out.println(country.getIsoCode());
            System.out.println(country.getName());
            System.out.println(city.getConfidence());
            System.out.println(city.getName());
            System.out.println(location.getTimeZone());*/
            return response;
        }catch (Exception e){
           logger.error("base on ip:{},find info error:{}",ipAddr,e);
        }
        return null;
    }
}

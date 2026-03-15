package com.cloud.baowang.play.config;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class VenueUserAccountConfig {

    @Value("${play.server.userAccountSuffix:A}")
    private String userAccountSuffix;

    @Value("${play.server.userAccountPrefix:Utest_}")
    private String userAccountPrefix;

    /**
     * 沙巴体育匿名登陆账号
     */
    @Value("${play.server.anonymousAccount:anonymous}")
    private String anonymousAccount;


    @Value("${play.server.evn:like}")
    private String evn;

    public String getAnonymousAccount() {
        return anonymousAccount;
    }


    /**
     * 获取当前环境 ,true = 产线环境 false = 测试环境
     */
    public Boolean getEvn() {
        //如果是空代表没有配置 没配置的代表是产线环境
        if (StringUtils.isBlank(evn)) {
            return true;
        }
        return !evn.equals("test");
    }


    /**
     * 增加场馆用户后缀
     *
     * @return
     */
    public String addVenueUserAccountSuffix(String userAccount) {
        return userAccount + userAccountSuffix;
    }

    /**
     * 增加场馆用户前缀
     *
     * @return
     */
    public String addVenueUserAccountPrefix(String userAccount) {
        return userAccountPrefix + userAccount;
    }

    /**
     * 增加场馆用户前缀
     * 根据场馆来确定前缀.因为部分场馆不支持特殊字符
     *
     * @return
     */
    public String addVenueUserAccountPrefix(String userAccount, String venueCode) {
        //目前针对SA场馆不用下划线
        if (StringUtils.isNotBlank(venueCode) && venueCode.equals(VenuePlatformConstants.SA)) {
            return (userAccountPrefix + userAccount).replace("_", "");
        }
        return addVenueUserAccountPrefix(userAccount);
    }


    /**
     * 去除用户前缀,得到用户真实账号
     */
    public String getVenueUserAccount(String userAccount, String venueCode) {
        String accountPrefix = userAccountPrefix;
        if(venueCode.equals(VenuePlatformConstants.SA)){//SA场馆不能使用下划线
            accountPrefix = accountPrefix.replace("_","");
        }
        if (StringUtils.isBlank(userAccount) || userAccount.length() <= accountPrefix.length()) {
            return userAccount;
        }
        String prefix = userAccount.substring(0, accountPrefix.length());
        if (accountPrefix.equals(prefix)) {
            return userAccount.substring(accountPrefix.length());
        }
        return userAccount;
    }


    /**
     * 去除用户前缀,得到用户真实账号
     * 统一转成小写处理,返回用户ID
     */
    public String getVenueUserAccount(String userAccount) {
        if (StringUtils.isBlank(userAccount) || userAccount.length() <= userAccountPrefix.length()) {
            return userAccount;
        }
        userAccount = userAccount.toLowerCase();
        String config = userAccountPrefix.toLowerCase();
        String prefix = userAccount.substring(0, config.length());
        if (config.equals(prefix)) {
            return userAccount.substring(config.length());
        }
        return userAccount;
    }

    public List<String> getVenueUserAccountList(List<String> userAccountList) {
        List<String> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(userAccountList)) {
            return resultList;
        }
        for (String item : userAccountList) {
            String account = getVenueUserAccount(item);
            if (!StringUtils.isBlank(account)) {
                resultList.add(account);
            }
        }
        return resultList;
    }


    /**
     * 删除场馆用户后缀
     *
     * @return
     */
    public String removeVenueUserAccountSuffix(String venueUserAccount) {
        if (venueUserAccount.endsWith(userAccountSuffix)) {
            return venueUserAccount.substring(0, venueUserAccount.length() - userAccountSuffix.length());
        }
        return venueUserAccount;
    }


    public static String replaceChar(String original, char charToReplace, char replacementChar) {
        return original.replace(charToReplace, replacementChar);
    }


    public static void main(String[] args) {

        String userAccount = "Utest_123456".toLowerCase();
        String config = "Utest_".toLowerCase();
        System.out.println("VenueUserAccountConfig.main - "+config);
        String prefix = userAccount.substring(0, config.length());
        if (config.equals(prefix)) {
            System.out.println("VenueUserAccountConfig.main - "+userAccount.substring(config.length()));
        }
        System.out.println("VenueUserAccountConfig.main- "+userAccount);
    }

}

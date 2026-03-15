package com.cloud.baowang.common.core.utils;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.CurrentRequestBasicInfoVO;
import com.cloud.baowang.common.core.vo.base.RequestBasicInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;

import static com.cloud.baowang.common.core.constants.CommonConstant.*;

/**
 * @Desciption: 当前登录用户信息
 * @Author: Ford
 * @Date: 2024/7/26 15:29
 * @Version: V1.0
 **/
public class CurrReqUtils {

    public static void init() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            String language = request.getHeader(LANGUAGE_HEAD);
            String userAgent = request.getHeader(USER_AGENT);
            String referer = request.getHeader(X_CUSTOM);
            String userId = request.getHeader(RequestBasicInfo.Fields.oneId);
            String userAccount = request.getHeader(RequestBasicInfo.Fields.userAccount);
            String loginIp = request.getHeader(RequestBasicInfo.Fields.requestIp);
            String siteCode = request.getHeader(RequestBasicInfo.Fields.siteCode);
            String timezone = request.getHeader(RequestBasicInfo.Fields.timezone);
            String deviceType = request.getHeader(RequestBasicInfo.Fields.reqClientSource);
            String platCurrencyName = request.getHeader(RequestBasicInfo.Fields.platCurrencyName);
            String platCurrencySymbol = request.getHeader(RequestBasicInfo.Fields.platCurrencySymbol);
            String platCurrencyIcon = request.getHeader(RequestBasicInfo.Fields.platCurrencyIcon);
            String handicapMode = request.getHeader(RequestBasicInfo.Fields.handicapMode);
            Boolean dataDesensitization = Boolean.parseBoolean(request.getHeader(RequestBasicInfo.Fields.dataDesensitization));

            String deviceTypeVersion = request.getHeader(DEVICE_TYPE_VERSION);


            RequestBasicInfo requestBasicInfo = new RequestBasicInfo();
            requestBasicInfo.setOneId(userId);
            requestBasicInfo.setUserAccount(userAccount);
            requestBasicInfo.setRequestIp(loginIp);
            requestBasicInfo.setLanguage(language);
            requestBasicInfo.setSiteCode(siteCode);
            requestBasicInfo.setReferer(referer);
            requestBasicInfo.setTimezone(timezone);
            requestBasicInfo.setUserAgent(userAgent);
            if(StringUtils.hasText(handicapMode)){
                requestBasicInfo.setHandicapMode(Integer.valueOf(handicapMode));
            }
            requestBasicInfo.setPlatCurrencyName(platCurrencyName);
            requestBasicInfo.setPlatCurrencySymbol(platCurrencySymbol);
            requestBasicInfo.setPlatCurrencyIcon(platCurrencyIcon);
            requestBasicInfo.setReqClientSource(StringUtils.hasLength(deviceType) ? Integer.parseInt(deviceType) : 1);
            requestBasicInfo.setDataDesensitization(dataDesensitization);
            requestBasicInfo.setDeviceId(request.getHeader(RequestBasicInfo.Fields.deviceId));
            requestBasicInfo.setVersion(request.getHeader(RequestBasicInfo.Fields.version));
            requestBasicInfo.setDeviceTypeVersion(deviceTypeVersion);
            RequestContextHolder.getRequestAttributes().setAttribute(REQUEST_BASIC_INFO, requestBasicInfo, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static String getOneId() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return ServletUtil.urlDecode(requestBasicInfo.getOneId());
            }
        }
        return null;
    }

    public static String getAccount() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return ServletUtil.urlDecode(requestBasicInfo.getUserAccount());
            }
        }
        return null;
    }


    public static String getReqIp() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getRequestIp();
            }
        }
        return null;
    }

    public static String getTimezone() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return ServletUtil.urlDecode(requestBasicInfo.getTimezone());
            }
        }
        return null;
    }

    public static RequestAttributes getHolder() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            return RequestContextHolder.getRequestAttributes();
        }
        return null;
    }

    public static void setHolder(RequestAttributes holder) {
        RequestContextHolder.setRequestAttributes(holder);
    }


    public static void resetHolder() {
        RequestContextHolder.resetRequestAttributes();
    }

    public static void setTimezone(String timezone) {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                requestBasicInfo.setTimezone(timezone);
            }
        }
    }

    public static String getSiteCode() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getSiteCode();
            }
        }
        return null;
    }
    /**
     *  盘口模式 0:国际盘 1:大陆盘
     *  总控后台为null
     *  其他默认为0
     */
    public static Integer getHandicapMode() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getHandicapMode();
            }
        }
        return null;
    }


    public static String getLanguage() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getLanguage();
            }
        }
        return null;
    }

    public static String getReferer() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getReferer();
            }
        }
        return null;
    }

    /**
     * {@link com.cloud.baowang.common.core.enums.DeviceType}
     *
     * @return DeviceType
     */
    public static Integer getReqDeviceType() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getReqClientSource();
            }
        }
        return null;
    }

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    public static Boolean getDataDesensity() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getDataDesensitization();
            }
        }
        return false;
    }

    public static String getPlatCurrencyCode() {
       return CommonConstant.PLAT_CURRENCY_CODE;
    }


    public static String getPlatCurrencyName() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getPlatCurrencyName();
            }
        }
        return null;
    }

    public static String getPlatCurrencySymbol() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                if(StringUtils.hasText(requestBasicInfo.getPlatCurrencySymbol())){
                    return StringUtil.currencyHexToString(requestBasicInfo.getPlatCurrencySymbol());
                }
            }
        }
        return null;
    }

    public static String getPlatCurrencyIcon() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                if(StringUtils.hasText(requestBasicInfo.getPlatCurrencyIcon())){
                    return URLDecoder.decode(requestBasicInfo.getPlatCurrencyIcon());
                }
            }
        }
        return null;
    }

    public static String getUserAgent() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getUserAgent();
            }
        }
        return null;
    }

    public static String getBizCustom() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getBizCustom();
            }
        }
        return null;
    }


    public static CurrentRequestBasicInfoVO getCurrentBasicInfo(){
        CurrentRequestBasicInfoVO currentRequestBasicInfoVO=new CurrentRequestBasicInfoVO();
        currentRequestBasicInfoVO.setTimezone(getTimezone());
        currentRequestBasicInfoVO.setPlatCurrencyCode(getPlatCurrencyCode());
        currentRequestBasicInfoVO.setPlatCurrencyName(getPlatCurrencyName());
        currentRequestBasicInfoVO.setPlatCurrencySymbol(getPlatCurrencySymbol());
        currentRequestBasicInfoVO.setPlatCurrencyIcon(getPlatCurrencyIcon());
        currentRequestBasicInfoVO.setReferer(getReferer());
        currentRequestBasicInfoVO.setLanguage(getLanguage());
        currentRequestBasicInfoVO.setBizCustom(getBizCustom());
        currentRequestBasicInfoVO.setSiteCode(getSiteCode());
        currentRequestBasicInfoVO.setUserId(getOneId());
        currentRequestBasicInfoVO.setUserAccount(getAccount());
        currentRequestBasicInfoVO.setDeviceType(getReqDeviceType());
        currentRequestBasicInfoVO.setLoginIp(getReqIp());
        currentRequestBasicInfoVO.setDataDesensitization(getDataDesensity());
        return currentRequestBasicInfoVO;
    }

    public static String getReqDeviceId() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getDeviceId();
            }
        }
        return null;
    }

    public static String getReqVersion() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getVersion();
            }
        }
        return null;
    }

    public static String getDeviceTypeVersion() {
        if (RequestContextHolder.getRequestAttributes() != null) {
            Object reqObj = RequestContextHolder.getRequestAttributes().getAttribute(REQUEST_BASIC_INFO, RequestAttributes.SCOPE_REQUEST);
            if (reqObj != null) {
                RequestBasicInfo requestBasicInfo = (RequestBasicInfo) reqObj;
                return requestBasicInfo.getDeviceTypeVersion();
            }
        }
        return null;
    }
}

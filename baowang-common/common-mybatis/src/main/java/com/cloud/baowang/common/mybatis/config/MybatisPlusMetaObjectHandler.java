package com.cloud.baowang.common.mybatis.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * MybatisPlus 自动填充配置
 *
 * @author L.cm
 */
@Slf4j
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * en:英文,vi:越南,zh:中文,pt:巴西:th:泰语: 默认中文
     */
    public static final String LANG = "lang";
    public static final String LANG_HTTP2 = "Lang";

    /**
     * 登陆的平台id
     */
    public static final String PLATFORM_ID = "platformId";
    public static final String PLATFORM_ID_LOWERCASE = "platformid";
    public static final String PLATFORM_ID_HTTP2 = "Platformid";
    public static final String UPDATE_BY = "updater";
    private String creator;
    @TableField(value = "created_time")
    private Long createdTime;

    /**
     * 填充值，先判断是否有手动设置，优先手动设置的值，例如：job必须手动设置
     *
     * @param fieldName  属性名
     * @param fieldVal   属性值
     * @param metaObject MetaObject
     * @param isCover    是否覆盖原有值,避免更新操作手动入参
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject, boolean isCover) {
        // 1. 没有 get 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 2. 如果用户有手动设置的值
        Object userSetValue = metaObject.getValue(fieldName);
        String setValueStr = StrUtil.str(userSetValue, Charset.defaultCharset());
        if (StrUtil.isNotBlank(setValueStr) && !isCover) {
            return;
        }

        // 3. field 类型相同时设置
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("mybatis plus start insert fill ....");
        Long nowTime = System.currentTimeMillis();
        fillValIfNullByName("createdTime", nowTime, metaObject, false);
        fillValIfNullByName("updatedTime", nowTime, metaObject, false);
        if (ObjectUtil.isNull(getUserName())) {
            fillValIfNullByName("creator", null, metaObject, false);
            fillValIfNullByName("updater", null, metaObject, false);
        } else {
            fillValIfNullByName("creator", getUserName(), metaObject, false);
            fillValIfNullByName("updater", getUserName(), metaObject, false);
        }

        //String lang = getHeader(LANG);
        //if (StrUtil.isNotEmpty(lang)) {
        //    fillValIfNullByName("lang", lang, metaObject, false);
        //}
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("mybatis plus start update fill ....");
        Long nowTime = System.currentTimeMillis();
        fillValIfNullByName("updatedTime", nowTime, metaObject, false);

        if (ObjectUtil.isNull(getUserName())) {
            fillValIfNullByName("updater", null, metaObject, false);
        } else {
            fillValIfNullByName("updater", getUserName(), metaObject, false);
        }

    }

    /**
     * 获取 spring security 当前的用户名
     *
     * @return 当前用户名
     */
    private String getUserName() {
        return CurrReqUtils.getAccount();
    }

    private String getHeader(String key) {

        if (ObjectUtil.isNull(RequestContextHolder.getRequestAttributes())) {
            String strKey = "";
            if (key.equals(PLATFORM_ID)) {
                strKey = "0";
            }

            if (key.equals(LANG)) {
                strKey = "zh";
            }

            return strKey;
        }

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String strKey = request.getHeader(key);

        if (StrUtil.isEmpty(strKey)) {
            if (key.equalsIgnoreCase(PLATFORM_ID)) {
                strKey = request.getHeader(PLATFORM_ID_LOWERCASE);
                if (ObjectUtil.isNull(strKey)) {
                    strKey = request.getHeader(PLATFORM_ID_HTTP2);
                }
            } else if (key.equalsIgnoreCase(LANG)) {
                if (ObjectUtil.isNull(strKey)) {
                    strKey = request.getHeader(LANG_HTTP2);
                }
            }
        }


        if (StrUtil.isEmpty(strKey) && key.equalsIgnoreCase(PLATFORM_ID)) {
            strKey = "1";
        }

        if (StrUtil.isEmpty(strKey) && key.equalsIgnoreCase(LANG)) {
            strKey = "zh";
        }

        return strKey;
    }

}

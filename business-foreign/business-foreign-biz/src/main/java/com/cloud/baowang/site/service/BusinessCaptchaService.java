package com.cloud.baowang.site.service;

import com.cloud.baowang.common.core.configuration.SpecCaptcha;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.config.BusinessCaptchaProperties;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: fangfei
 * @createTime: 2024/06/18 9:03
 * @description: 验证码配置类
 */
@Service
public class BusinessCaptchaService {

    private final BusinessCaptchaProperties properties;

    public BusinessCaptchaService(final BusinessCaptchaProperties properties) {
        this.properties = properties;
    }

    public void create(String  codeKey, HttpServletResponse response) throws Exception {
        setHeader(response, properties.getType());

        Captcha captcha = createCaptcha();

        //缓存到redis中
        RedisUtil.setValue(codeKey, captcha.text(), 2L, TimeUnit.MINUTES);

        captcha.out(response.getOutputStream());
    }

    public boolean check(String key, String value) {
        // 获取redis中的验证码
        String redisCode = RedisUtil.getValue(key);
        return StringUtils.equalsIgnoreCase(value, redisCode);
    }

    private Captcha createCaptcha() {
        Captcha captcha = new SpecCaptcha(properties.getWidth(), properties.getHeight(), properties.getLength());

        captcha.setCharType(properties.getCharType());
        try {
            captcha.setFont(new Font("楷体", Font.PLAIN, 28));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return captcha;
    }

    private void setHeader(HttpServletResponse response, String type){
        if (StringUtils.equalsIgnoreCase(type, "gif")) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}

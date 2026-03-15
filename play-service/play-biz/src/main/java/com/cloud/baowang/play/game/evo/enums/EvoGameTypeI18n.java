package com.cloud.baowang.play.game.evo.enums;

import cn.hutool.core.util.ObjectUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @className: EvoGameTypeI18n
 * @author: wade
 * @description: 类型解析
 * @date: 25/8/25 09:17
 */
public class EvoGameTypeI18n {
    private static Properties enProperties = new Properties();
    private static Properties zhProperties = new Properties();

    static {
        try (InputStream zhInput = EvoGameTypeI18n.class.getClassLoader()
                .getResourceAsStream("evoGameType_zh_CN.properties");
             InputStream enInput = EvoGameTypeI18n.class.getClassLoader()
                     .getResourceAsStream("evoGameType_en_US.properties")) {

            if (zhInput != null) zhProperties.load(new InputStreamReader(zhInput, StandardCharsets.UTF_8));
            if (enInput != null) enProperties.load(new InputStreamReader(enInput, StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // key = gameType, value = {语言 -> 名称}
    public static String getName(String gameType, String lang) {
        if (ObjectUtil.isEmpty(gameType)) {
            return gameType; // gameType为空，直接返回

        }
        if ("zh_CN".equalsIgnoreCase(lang) || "zh-CN".equalsIgnoreCase(lang)) {
            return enProperties.getProperty(gameType, gameType);
        } else {
            return enProperties.getProperty(gameType, gameType);
        }
    }


}

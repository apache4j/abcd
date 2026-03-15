package com.cloud.baowang.user.util;

import com.cloud.baowang.common.core.constants.CommonConstant;
import org.apache.commons.lang3.StringUtils;

public class SystemMessageConvertUtil {
    public static String convertSystemMessage(String source, String replacement) {
        String[] replacements = replacement.split(CommonConstant.COMMA);
        /*
        int replacementIndex = 0;

        while (source.contains("%s") && replacementIndex < replacements.length) {
            source = source.replaceFirst("%s", replacements[replacementIndex]);

            replacementIndex++;
        }
        return source.replaceAll("\\{(.*?)}", "$1");*/
        return convertSystemMessageData(source,  replacements);
    }

    public static String convertSystemMessageData(String source, String... replacements) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        source = String.format(source, (Object[]) replacements);
        return source.replaceAll("\\{(.*?)}", "$1");
    }

    public static void main(String[] args) {
        System.out.println(convertSystemMessage("%s%s%s%s%s", "1a1,2b2,3c3,4d4,5e5"));  // 正确传递展开的数组
        System.out.println(convertSystemMessageData("%s", "123"));  // 使用单个参数
    }


}

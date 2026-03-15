package com.cloud.baowang.system.api.file;

import org.springframework.util.ObjectUtils;

/**
 * @Author kimi
 **/
public class FileServerUtil {

    public static boolean checkNull(String... args) {
        if (args == null || args.length == 0) {
            return true;
        }

        for (String arg : args) {
            if (ObjectUtils.isEmpty(arg)) {
                return true;
            }
        }

        return false;
    }


}

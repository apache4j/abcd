package com.cloud.baowang.common.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GsonUtil {
    private static Gson gson = null;
    private static final ReentrantLock lock = new ReentrantLock();

    private GsonUtil() {
        // 私有构造方法防止实例化
    }

    public static Gson getInstance() {
        if (gson == null) {
            lock.lock();
            try {
                if (gson == null) {
                    gson = new GsonBuilder().create();
                }
            } finally {
                lock.unlock();
            }
        }
        return gson;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getInstance().fromJson(json, classOfT);
    }

    public static String toJson(Object src) {
        return getInstance().toJson(src);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> classOfT) {
        Type type = TypeToken.getParameterized(List.class, classOfT).getType();
        return getInstance().fromJson(json, type);
    }

    public static String toJsonFromList(List<?> list) {
        return getInstance().toJson(list);
    }

    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        Type type = TypeToken.getParameterized(Map.class, keyClass, valueClass).getType();
        return getInstance().fromJson(json, type);
    }

    public static String toJsonFromMap(Map<?, ?> map) {
        return getInstance().toJson(map);
    }
}



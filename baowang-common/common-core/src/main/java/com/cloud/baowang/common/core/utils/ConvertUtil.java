package com.cloud.baowang.common.core.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.convert.CustomerConvert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class ConvertUtil {

    public static String convertObjectToJson(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public static <T> T convertJsonToObject(final String json, final Class<T> clazz) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, clazz);
    }

    public static <T> List<List<T>> subListToList(final List<T> list, int len) {
        final List<List<T>> resultList = new ArrayList<>();
        if (!list.isEmpty() && len > 0) {
            int size = list.size();
            int count = (size + len - 1) / len;
            for (int i = 0; i < count; i++) {
                final List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
                resultList.add(subList);
            }
        }
        return resultList;
    }

    public static <E, T> List<E> convertListToList(List<T> objectList, E targetParam) throws Exception {
        Class<?> clazz = targetParam.getClass();
        List<E> targetList = objectList.stream()
                .map(item -> {
                    E managePO = null;
                    try {
                        managePO = (E) CustomerConvert.convert(item, clazz);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    isValidItem(managePO);
                    return managePO;
                }).toList();
        return targetList;
    }

    private static <E> void isValidItem(E managePO) {
        if (Objects.isNull(managePO)) {
            throw new RuntimeException("参数不能空");
        }
    }


    /**
     * 将entityList转换成modelList
     *
     * @param fromList
     * @param tClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> List<T> entityListToModelList(List<F> fromList, Class<T> tClass) {
        if (fromList == null || fromList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> tList = new ArrayList<>();
        for (F f : fromList) {
            T t = entityToModel(f, tClass);
            tList.add(t);
        }
        return tList;
    }

    /**
     * 将entityList转换成modelList
     *
     * @param fromList
     * @param tClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> List<T> entityListToModelList(List<F> fromList, Class<T> tClass, Consumer<F> consumer) {
        if (fromList == null || fromList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> tList = new ArrayList<>();
        for (F f : fromList) {
            consumer.accept(f);
            T t = entityToModel(f, tClass);
            tList.add(t);
        }
        return tList;
    }

    public static <F, T> T entityToModel(F entity, Class<T> modelClass) {
        Object model = null;
        if (entity == null || modelClass == null) {
            return null;
        }

        try {
            model = modelClass.newInstance();
        } catch (InstantiationException e) {
            log.error("entityToModel : 实例化异常", e);
        } catch (IllegalAccessException e) {
            log.error("entityToModel : 安全权限异常", e);
        }
        BeanUtils.copyProperties(entity, model);
        return (T) model;
    }

    /**
     * 获取小数位数
     *
     * @param point
     * @return
     */
    public static int getDecimalPlace(String point) {
        int index = point.indexOf(".");
        if (index < 0) {
            return 0;
        }
        return point.substring(index + 1).length();
    }

    /**
     * 按指定大小，分隔集合，将集合按规定个数分为n个部分
     *
     * @param list
     * @param len
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.isEmpty() || len < 1) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        if (result.isEmpty()) {
            return new ArrayList<>();
        }
        return result;
    }


    public static <T> Page<T> toConverPage(IPage<T> iPage){
        Page<T> page = new Page<T>();
        page.setCurrent(iPage.getCurrent());
        page.setSize(iPage.getSize());
        page.setPages(iPage.getPages());
        page.setRecords(iPage.getRecords());
        page.setTotal(iPage.getTotal());
        return page;
    }

}

package com.cloud.baowang.common.data.transfer.dictionary;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.cache.SystemDictCache;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 字典工具类
 */
@Slf4j
//@Aspect
@Component
public class DictionaryAspect {

    //字典表 system_param规则
    private final static String DICT_I18N_BASE = "LOOKUP_";
    @Autowired
    SystemDictCache systemDictCache;

    /**
     * 环绕通知.
     *
     * @param pjp pjp
     * @return obj
     * @throws Throwable exception
     */
//    @Around("execution(* com.cloud.baowang..controller..*.*(..)) && @annotation(Method)") // 仅对controller目录下文件生效
    //@Around("@annotation(Method)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        transDict(result);
        return result;
    }

    public void transDict(Object result) {
        try {
            if (Objects.isNull(result)) {
                return;
            }
            // 普通返回
            if (result instanceof ResponseVO<?> vo) {
                Object data = vo.getData();
                if (Objects.nonNull(data)) {
                    transDict(data);
                }
                // 分页对象
            } else if (result instanceof Page<?> page) {
                List<?> records = page.getRecords();
                if (CollUtil.isEmpty(records)) return;
                for (Object record : records) {
                    // 字段转换
                    transDictByField(record);//分页对象不搜索嵌套对象
                }
                // 普通对象
            } else if (result instanceof Serializable) {
                Class<?> aClass = result.getClass();
                Field[] fields = aClass.getDeclaredFields();
                boolean isContains = false;//非自建对象是否包含注解
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object o = field.get(result);
                    if (o instanceof Number || o instanceof String) {
                        isContains = field.isAnnotationPresent(Dict.class);
                    } else {
                        transDict(o);
                    }
                }
                if (isContains) {
                    transDictByField(result);
                }
                // 集合
            } else if (result instanceof List<?> list) {
                if (CollUtil.isEmpty(list)) return;
                for (Object o : list) {
                    transDict(o);
                }
            }
        } catch (Exception e) {
            log.error("字典表字段映射失败,error:", e);
        }
    }

    private void transDictByField(Object record) throws NoSuchFieldException, IllegalAccessException {
        Class<?> aClass = record.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Dict.class)) {
                field.setAccessible(true);
                Object obj = field.get(record);
                if (Objects.isNull(obj)) {
                    continue;
                }
                String code = obj.toString();
                Dict dict = field.getAnnotation(Dict.class);
                //替换值
                String type = dict.value();
                String param = getSystemParamByTypeAndCode(type, code);
                if (StrUtil.isNotBlank(param)) {
                    Field declaredField = aClass.getDeclaredField(field.getName() + "Text");
                    declaredField.setAccessible(true);
                    declaredField.set(record, param);
                }

            }
        }
    }


    public String getSystemParamByTypeAndCode(String type, String code) {
        if (StrUtil.isBlank(type) || StrUtil.isBlank(code)) {
            return null;
        }
        List<CodeValueVO> systemParamByType = systemDictCache.getSystemParamByType(type);
        if (CollUtil.isNotEmpty(systemParamByType)) {
            //多状态处理
            if (code.contains(CommonConstant.COMMA)) {
                String[] split = code.split(CommonConstant.COMMA);
                List<String> list = systemParamByType.stream().filter(s -> Arrays.asList(split).contains(s.getCode()))
                        // 如果是 "LOOKUP_" 起始 则翻译，其他配置值 不翻译
                        .map(e -> e.getValue() != null && e.getValue().startsWith(DICT_I18N_BASE) ? I18nMessageUtil.getI18NMessageInAdvice(e.getValue()) : e.getValue())
                        .toList();
                return String.join(CommonConstant.COMMA, list);
            }
            return systemParamByType.stream().filter(s -> s.getCode().equals(code)).findFirst()
                    // 如果是 "LOOKUP_" 起始 则翻译，其他配置值 不翻译
                    .map(e -> e.getValue() != null && e.getValue().startsWith(DICT_I18N_BASE) ? I18nMessageUtil.getI18NMessageInAdvice(e.getValue()) : e.getValue())
                    .orElse(null);
        }
        return null;
    }
}

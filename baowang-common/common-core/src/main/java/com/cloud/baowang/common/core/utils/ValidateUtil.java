package com.cloud.baowang.common.core.utils;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.utils.tool.vo.Comparison;
import com.google.common.collect.Sets;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author 小智
 * @Date 2/5/23 8:17 PM
 * @Version 1.0
 */
@Slf4j
public class ValidateUtil {

    /**
     * 用于验证list集合error信息
     *
     * @param bindingResult
     * @return
     */
    public static Set<String> getError(BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        Set<String> error = Sets.newHashSet();
        for (ObjectError item : allErrors) {
            error.add(item.getDefaultMessage());
        }
        return error;
    }

    public static String validate(@Valid Object obj) {
        Set<ConstraintViolation<@Valid Object>> validateSet = Validation
                .buildDefaultValidatorFactory().getValidator().validate(obj);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            return messages;
        }
        return null;
    }

    public static String validate(@Valid Object obj, Class clas) {
        Set<ConstraintViolation<@Valid Object>> validateSet = Validation
                .buildDefaultValidatorFactory().getValidator().validate(obj, clas);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            return messages;
        }
        return null;
    }

    public static List<Comparison> compareObj(Object beforeObj, Object afterObj) {
        List<Comparison> diffs = new ArrayList<>();

        if (beforeObj == null) {
            throw new RuntimeException("原对象不能为空");
        }
        if (afterObj == null) {
            throw new RuntimeException("新对象不能为空");
        }
        if (!beforeObj.getClass().isAssignableFrom(afterObj.getClass())) {
            throw new RuntimeException("两个对象不相同，无法比较");
        }

        //取出属性
        try {
            Field[] beforeFields = beforeObj.getClass().getDeclaredFields();
            Field[] afterFields = afterObj.getClass().getDeclaredFields();
            Field.setAccessible(beforeFields, true);
            Field.setAccessible(afterFields, true);

            //遍历取出差异值
            if (beforeFields != null && beforeFields.length > 0) {
                for (int i = 0; i < beforeFields.length; i++) {
                    Object beforeValue = beforeFields[i].get(beforeObj);
                    Object afterValue = afterFields[i].get(afterObj);
                    if ((ObjectUtil.isNotEmpty(beforeValue) && !ObjectUtil.equal(beforeValue, afterValue))
                            || (ObjectUtil.isEmpty(beforeValue) && afterValue != null)) {
                        Comparison comparison = new Comparison();
                        comparison.setField(beforeFields[i].getName());
                        comparison.setBefore(beforeValue);
                        comparison.setAfter(afterValue);
                        diffs.add(comparison);
                    }
                }
            }

            return diffs;
        } catch (Exception e) {
            log.error("compare obj have error", e);
        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        VIPBenefitConfigRequestVO a = new VIPBenefitConfigRequestVO();
//        VIPBenefitConfigRequestVO b = new VIPBenefitConfigRequestVO();
//        a.setVipRank("123");
//        a.setBirthdayBonus(new BigDecimal(122));
//        a.setUpgradeBonus(new BigDecimal(222));
//        b.setVipRank("1232");
//        b.setBirthdayBonus(new BigDecimal(122));
//        System.out.println(compareObj(a, b));
//    }

}

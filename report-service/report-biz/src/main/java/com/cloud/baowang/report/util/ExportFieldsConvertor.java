package com.cloud.baowang.report.util;

import java.util.*;

public class ExportFieldsConvertor {
    private static final Map<String, List<String>> fieldMapping = new HashMap<>();

    static {
        fieldMapping.put("staticDate",List.of("staticDate"));

        fieldMapping.put("siteName",List.of("siteName"));

        fieldMapping.put("currencyCode",List.of("currencyCode"));
        // 初始化映射规则
        fieldMapping.put("memberRegister", Arrays.asList(
                "registerTotal",
                "registerBacked",
                "registerPc",
                "registerAndroidH5",
                "registerAndroidAPP",
                "registerIosAPP",
                "registerIosH5"
        ));

        fieldMapping.put("memberLogin", Arrays.asList(
                "loginTotal",
                "loginBacked",
                "loginPc",
                "loginAndroidH5",
                "loginAndroidAPP",
                "loginIosAPP",
                "loginIosH5"
        ));
        //会员总存款
        fieldMapping.put("memberTotalDeposit", Arrays.asList(
                "totalDeposit",
                "depositPeopleNums",
                "depositNums"
        ));

        //会员总取款
        fieldMapping.put("memberTotalWithdraw", Arrays.asList(
                "totalWithdraw",
                "withdrawPeopleNums",
                "withdrawNums",
                "largeWithdrawPeopleNums",
                "largeWithdrawNums"
        ));
        //会员存取差
        fieldMapping.put("memberAccessDifference", List.of("memberAccessDifference"));

        //会员首存
        fieldMapping.put("memberFirstDeposit", Arrays.asList(
                "firstDepositPeopleNums",
                "firstDepositAmount"
        ));

        //会员投注
        fieldMapping.put("memberBetInfo", Arrays.asList(
                "betAmount",
                "effectiveBetAmount",
                "bettorNums",
                "bettingOrderAmount"
        ));

        //会员输赢
        fieldMapping.put("memberWinOrLose", List.of("memberWinOrLose","tipsAmount"));


        //会员vip福利
        fieldMapping.put("vipWelfare", Arrays.asList(
                "vipWelfareAmount",
                "vipWelfarePeopleNums"
        ));

        //会员优惠活动
        fieldMapping.put("memberPromotion", Arrays.asList(
                "promotionAmount",
                "promotionPeopleNums"
        ));

        //已使用优惠
        fieldMapping.put("usedPromotion", List.of("usedPromotion"));


        //会员调整
        fieldMapping.put("memberAdjustment", Arrays.asList(
                "totalAdjust",
                "addAmount",
                "addAmountPeopleNum",
                "reduceAmount",
                "reduceAmountPeopleNums",

                "platformTotalAdjust",
                "platformAddAmount",
                "platformAddPeopleNum",
                "platformReduceAmount",
                "platformReducePeopleNums"
        ));

        //代理注册人数
        fieldMapping.put("agentRegister", Arrays.asList(
                "agentRegisterTotal",
                "agentRegisterBacked",
                "agentRegisterPc",
                "agentRegisterAndroidH5",
                "agentRegisterAndroidAPP",
                "agentRegisterIosAPP",
                "agentRegisterIosH5"
        ));

        //代理总存款
        fieldMapping.put("agentTotalDeposit", Arrays.asList(
                "agentTotalDeposit",
                "agentDepositPeopleNums",
                "agentDepositNums"
        ));

        //代理总取款
        fieldMapping.put("agentTotalWithdraw", Arrays.asList(
                "agentTotalWithdraw",
                "agentWithdrawPeopleNums",
                "agentWithdrawNums",
                "agentLargeWithdrawPeopleNums",
                "agentLargeWithdrawNums"
        ));

        //代存会员
        fieldMapping.put("agentDepositInfo", Arrays.asList(
                "agentCredit",
                "agentCreditPeopleNums",
                "agentCreditTimes"
        ));

    }


    public static List<String> mapFields(List<String> inputFields) {
        List<String> result = new ArrayList<>();

        for (String field : inputFields) {
            if (fieldMapping.containsKey(field)) {
                result.addAll(fieldMapping.get(field));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(fieldMapping.keySet());
        List<String> inputFields = Arrays.asList();
        for (String field : list) {
            System.out.println("ExportFieldsMapper.main field: " + field);
        }
        ExportFieldsConvertor mapper = new ExportFieldsConvertor();
        List<String> mappedFields = mapper.mapFields(list);

        // 输出映射后的字段数组
        System.out.println(mappedFields);
    }
}

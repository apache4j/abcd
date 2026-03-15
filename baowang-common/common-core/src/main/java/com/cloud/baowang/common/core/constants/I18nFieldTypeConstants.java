package com.cloud.baowang.common.core.constants;

/**
 * 缓存常量信息
 *
 * @author qiqi
 */
public class I18nFieldTypeConstants {

    /**
     * 字典值
     * 支持单个
     * 支持多个中间逗号分割
     */
    public static final int DICT = 1;
    /**
     * 列表字典值 + List
     */
    public static final int DICT_LIST = 2;
    /**
     * spel 表达式
     */
    public static final int SPEL = 3;
    /**
     * 某个字段为codes字符串数组时--ids的多语言数组
     */
    public static final int DICT_CODE_ARR = 4;

    /**
     * 某个字段为codes字符串数组时--ids的当前语言的多语言数组 ，字段名为filed+CurrentFrontList
     */
    public static final int DICT_CURRENT_CODE_ARR = 5;

    /**
     * 某个字段为codes时，直接返回多语言字符串，字段名为 filed+Text
     */
    public static final int DICT_CODE_TO_STR = 6;


    /**
     * 文件 自动拼接域名 字段名为 filed+FileUrl
     */
    public static final int FILE = 7;

    /**
     * 多语言文件 自动拼接域名 字段名为 filed+List
     */
    public static final int FILE_LIST = 8;




}

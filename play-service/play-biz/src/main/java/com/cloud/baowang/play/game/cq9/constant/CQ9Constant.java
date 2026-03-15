package com.cloud.baowang.play.game.cq9.constant;

/**
 * CQ9常量类
 *
 * @author: lavine
 * @creat: 2023/8/31 09:49
 */
public final class CQ9Constant {

    /**
     * 成功状态码
     */
    public static final String SUCC_CODE = "0";

    /**
     * 数据未找到
     */
    public static final String ERROR_CODE_DATA_NOT_FOUND = "8";

    /**
     * 会员已存在
     */
    public static final Integer ALREADY_HAS_SAME_ACCOUNT = 6;

    /**
     * 存取款，等待状态码
     */
    public static final Integer PROGRESS_CODE = 33;

    /**
     * 缺省的币种
     */
    public static final String DEF_CURRENCY = "CNY";

    /**
     * 一页拉取最大记录数
     */
    public static final Integer MAX_PAGE_SIZE = 2000;

    /**
     * 拉取的时间间隔
     */
    public static final Integer DEFAULT_TIME_INTERVAL = 10 * 60 * 1000;


}

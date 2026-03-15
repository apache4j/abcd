package com.cloud.baowang.common.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum I18MsgKeyEnum {


    VENUE_JOIN_TYPE(I18NMessageType.LOOKUP, "venue_join_type_", "场馆接入类型"),

    SBA_BET_TEAM_NAME(I18NMessageType.LOOKUP, "sba_bet_team_name_", "沙巴投注内容"),

    VENUE_INIT_NAME(I18NMessageType.LOOKUP, "VENUE_INIT_NAME", "场馆名称初始化"),

    VENUE_DESC(I18NMessageType.LOOKUP, "VENUE_DESC", "场馆描述"),

    SIGN_VENUE_NAME_I18N_NAME(I18NMessageType.BUSINESS, "SIGN_VENUE_NAME_I18N_", "游戏一级分类-目录名称"),

    ACELT_GAME(I18NMessageType.LOOKUP, "ACELT_GAME", "彩票游戏名称"),

    SBA_SPORT(I18NMessageType.LOOKUP, "SPORT", "沙巴体育大类"),

    SH_GAME_TYPE(I18NMessageType.LOOKUP, "SH_GAME_TYPE", "SH视讯游戏大类"),

    SA_GAME_TYPE(I18NMessageType.LOOKUP, "SA_GAME_TYPE", "SA视讯游戏大类"),

    SA_GAME_TABLE_NUMBER_NAME(I18NMessageType.LOOKUP, "sa_game_table_number_name", "SA视讯游戏桌子名称"),


    SEXY_GAME_TYPE(I18NMessageType.LOOKUP, "SEXY_GAME_TYPE", "SEXY视讯游戏大类"),


    EVO_GAME_TYPE(I18NMessageType.LOOKUP, "EVO_GAME_TYPE", "EVO视讯游戏大类"),

    ES_GAME_TYPE(I18NMessageType.LOOKUP, "ES_GAME_TYPE", "电竞游戏大类"),

    COCKFIGHTING_GAME_TYPE(I18NMessageType.LOOKUP, "CF_GAME_TYPE", "斗鸡游戏大类 只有一类 用于搜索"),

    SA_GAME_BAC(I18NMessageType.LOOKUP, "SA_GAME_bac_", "SA-视讯百家乐-游戏详情"),

    GAME_ONE_DIRECTORY(I18NMessageType.BUSINESS, "GAME_ONE_DIRECTORY", "游戏一级分类-目录名称"),

    GAME_ONE_HOME(I18NMessageType.BUSINESS, "GAME_ONE_HOME", "游戏一级分类-首页名称"),

    GAME_ONE_ICON(I18NMessageType.BUSINESS, "GAME_ONE_ICON", "游戏一级分类-图片"),

    SITE_FLOAT_NAME_I18N_CODE(I18NMessageType.BUSINESS, "SITE_FLOAT_NAME_I18N_CODE", "游戏一级分类-悬浮名称"),

    SITE_LOGO_ICON_I18N_CODE(I18NMessageType.BUSINESS, "SITE_LOGO_ICON_I18N_CODE", "游戏一级分类-品牌图标"),

    SITE_MEDIUM_ICON_I18N_CODE(I18NMessageType.BUSINESS, "SITE_MEDIUM_ICON_I18N_CODE", "游戏一级分类-中图标"),

    GAME_TWO_TYPE(I18NMessageType.BUSINESS, "GAME_TWO_TYPE", "分类名称"),

    GAME_TWO_HT_ICON(I18NMessageType.BUSINESS, "GAME_TWO_HT_ICON", "皮肤4:游戏横版图标"),

    GAME_NAME(I18NMessageType.BUSINESS, "GAME_NAME", "游戏名称"),

    GAME_DESC(I18NMessageType.BUSINESS, "GAME_DESC", "游戏备注"),

    GAME_ICON(I18NMessageType.BUSINESS, "GAME_ICON", "游戏图片"),

    SE_GAME_ICON(I18NMessageType.BUSINESS, "SE_GAME_ICON", "正方形-游戏图片"),

    VT_GAME_ICON(I18NMessageType.BUSINESS, "VT_GAME_ICON", "竖版-游戏图片"),

    HT_GAME_ICON(I18NMessageType.BUSINESS, "HT_GAME_ICON", "横版-游戏图片"),



    PC_VENUE_ICON(I18NMessageType.BUSINESS, "PC_VENUE_ICON", "总控-PC-游戏平台名称"),
    H5_VENUE_ICON(I18NMessageType.BUSINESS, "H5_VENUE_ICON", "总控-H5-游戏平台名称"),
    PC_LOGO_CODE(I18NMessageType.BUSINESS, "PC_LOGO_CODE", "总控-pc_场馆LOGO"),
    PC_BACKGROUND_ICON(I18NMessageType.BUSINESS, "PC_BACKGROUND_CODE", "总控-pc_场馆背景图"),


    INIT_VENUE_NAME(I18NMessageType.BUSINESS, "INIT_VENUE_NAME", "总控-场馆名称"),

    SMALL_ICON1(I18NMessageType.BUSINESS, "SMALL_ICON1", "总控-小图标1-多语言"),

    SMALL_ICON2(I18NMessageType.BUSINESS, "SMALL_ICON2", "总控-小图标2-多语言"),

    SMALL_ICON3(I18NMessageType.BUSINESS, "SMALL_ICON3", "总控-小图标3-多语言"),

    SMALL_ICON4(I18NMessageType.BUSINESS, "SMALL_ICON4", "总控-小图标4-多语言"),

    SMALL_ICON5(I18NMessageType.BUSINESS, "SMALL_ICON5", "总控-小图标5-多语言"),

    SMALL_ICON6(I18NMessageType.BUSINESS, "SMALL_ICON6", "总控-小图标6-多语言"),

    HT_ICON(I18NMessageType.BUSINESS, "HT_ICON", "总控-H5-游戏平台名称"),

    MIDDLE_HT_ICON(I18NMessageType.BUSINESS, "MIDDLE_HT_ICON", "总控-中等图"),


    SITE_MIDDLE_HT_ICON(I18NMessageType.BUSINESS, "SITE_MIDDLE_HT_ICON", "站点-中等图"),

    SITE_PC_VENUE_ICON(I18NMessageType.BUSINESS, "SITE_PC_VENUE_ICON", "站点-PC-游戏平台名称"),

    SITE_H5_VENUE_ICON(I18NMessageType.BUSINESS, "SITE_H5_VENUE_ICON", "站点-H5-游戏平台名称"),

    SITE_HT_ICON(I18NMessageType.BUSINESS, "SITE_HT_ICON", "站点-H5-游戏平台名称"),

    SITE_INIT_VENUE_NAME(I18NMessageType.BUSINESS, "SITE_INIT_VENUE_NAME", "站点-场馆名称"),

    SITE_VENUE_DESC(I18NMessageType.BUSINESS, "SITE_VENUE_DESC", "站点-场馆备注"),

    SITE_SMALL_ICON1(I18NMessageType.BUSINESS, "SITE_SMALL_ICON1", "站点-小图标1-多语言"),

    SITE_SMALL_ICON2(I18NMessageType.BUSINESS, "SITE_SMALL_ICON2", "站点-小图标2-多语言"),

    SITE_SMALL_ICON3(I18NMessageType.BUSINESS, "SITE_SMALL_ICON3", "站点-小图标3-多语言"),

    SITE_SMALL_ICON4(I18NMessageType.BUSINESS, "SITE_SMALL_ICON4", "站点-小图标4-多语言"),

    SITE_SMALL_ICON5(I18NMessageType.BUSINESS, "SITE_SMALL_ICON5", "站点-小图标5-多语言"),

    SITE_SMALL_ICON6(I18NMessageType.BUSINESS, "SITE_SMALL_ICON6", "站点-小图标6-多语言"),

    SITE_PC_BACKGROUND_ICON(I18NMessageType.BUSINESS, "SITE_PC_BACKGROUND_CODE", "站点pc_场馆背景图"),

    SITE_PC_LOGO_CODE(I18NMessageType.BUSINESS, "SITE_PC_LOGO_CODE", "站点pc_场馆LOGO"),

    BANK_CARD_SHOW_NAME(I18NMessageType.BUSINESS, "BANK_SHOW_NAME", "银行卡前端展示名称"),

    MEDAL_NAME(I18NMessageType.BUSINESS, "MEDAL_NAME", "勋章名称"),

    MEDAL_DESC(I18NMessageType.BUSINESS, "MEDAL_DESC", "勋章描述"),

    MEDAL_UNLOCK(I18NMessageType.BUSINESS, "MEDAL_UNLOCK", "勋章解锁条件"),

    MEDAL_COND_LABEL1(I18NMessageType.BUSINESS, "MEDAL_COND_LABEL1", "达成条件1标签名"),

    MEDAL_COND_LABEL2(I18NMessageType.BUSINESS, "MEDAL_COND_LABEL2", "达成条件2标签名"),

    RECHARGE_TYPE(I18NMessageType.BUSINESS, "RECHARGE_TYPE", "充值类型"),

    CURRENCY_NAME(I18NMessageType.BUSINESS, "CURRENCY_NAME", "货币名称"),

    RECHARGE_WAY(I18NMessageType.BUSINESS, "RECHARGE_WAY", "充值方式"),

    WITHDRAW_TYPE(I18NMessageType.BUSINESS, "WITHDRAW_TYPE", "提款类型"),

    WITHDRAW_WAY(I18NMessageType.BUSINESS, "WITHDRAW_WAY", "提款方式"),

    VIP_RANK_NAME(I18NMessageType.BUSINESS, "VIP_RANK_NAME", "vip段位名称"),

    SYSTEM_VIP_RANK_NAME(I18NMessageType.BUSINESS, "SYSTEM_VIP_RANK_NAME", "总台vip段位名称"),

    ACTIVITY_LAB_NAME(I18NMessageType.BUSINESS, "ACTIVITY_LAB_NAME", "活动页签名称"),

    ACTIVITY_NAME(I18NMessageType.BUSINESS, "ACTIVITY_NAME", "活动名称"),

    ACTIVITY_DESCRIPTION(I18NMessageType.BUSINESS, "ACTIVITY_DESCRIPTION", "活动描述"),

    ACTIVITY_BASE_RULE(I18NMessageType.BUSINESS, "ACTIVITY_BASE_RULE", "活动基础配置-返利规则"),

    ACTIVITY_BASE_DESC(I18NMessageType.BUSINESS, "ACTIVITY_BASE_DESC", "活动基础配置-返利描述"),
    ACTIVITY_ENTRANCE_PICTURE(I18NMessageType.BUSINESS, "ACTIVITY_ENTRANCE_PICTURE", "活动基础配置-入口图-移动端"),
    ACTIVITY_ENTRANCE_PICTURE_BLACK(I18NMessageType.BUSINESS, "ACTIVITY_ENTRANCE_PICTURE_BLACK", "活动基础配置-入口图-移动端-黑夜"),
    ACTIVITY_ENTRANCE_PICTURE_PC(I18NMessageType.BUSINESS, "ACTIVITY_ENTRANCE_PICTURE_PC", "活动基础配置-入口图-PC端"),
    ACTIVITY_ENTRANCE_PICTURE_PC_BLACK(I18NMessageType.BUSINESS, "ACTIVITY_ENTRANCE_PICTURE_PC_BLACK", "活动基础配置-入口图-PC端-黑夜"),
    ACTIVITY_HEAD_PICTURE(I18NMessageType.BUSINESS, "ACTIVITY_HEAD_PICTURE", "活动头图-移动端-移动端"),
    ACTIVITY_HEAD_PICTURE_BLACK(I18NMessageType.BUSINESS, "ACTIVITY_HEAD_PICTURE_BLACK", "活动头图-移动端-移动端-黑夜"),
    ACTIVITY_HEAD_PICTURE_PC(I18NMessageType.BUSINESS, "ACTIVITY_HEAD_PICTURE_PC", "活动基础配置-活动头图-PC端"),
    ACTIVITY_HEAD_PICTURE_PC_BLACK(I18NMessageType.BUSINESS, "ACTIVITY_HEAD_PICTURE_PC_BLACK", "活动基础配置-活动头图-PC端-黑夜"),
    ACTIVITY_DETAIL_DAIL_COMPETITION(I18NMessageType.BUSINESS, "ACTIVITY_DETAIL_DAIL_COMPETITION", "活动-每日竞技详情名称配置"),
    ACTIVITY_INTRODUCE(I18NMessageType.BUSINESS, "ACTIVITY_INTRODUCE", "活动基础配置-活动简介"),
    ACTIVITY_ThIRD_A_DAY_APP(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_A_DAY_APP", "活动赛事三方A白天app端图片"),
    ACTIVITY_ThIRD_A_DAY_PC(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_A_DAY_PC", "活动赛事三方A白天PC端图片"),
    ACTIVITY_ThIRD_A_NIGHT_PC(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_A_NIGHT_PC", "活动赛事三方A夜间PC端图片"),
    ACTIVITY_ThIRD_A_NIGHT_APP(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_A_NIGHT_APP", "活动赛事三方A夜间APP端图片"),
    ACTIVITY_ThIRD_B_DAY_APP(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_B_DAY_APP", "活动赛事三方B白天app端图片"),
    ACTIVITY_ThIRD_B_DAY_PC(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_B_DAY_PC", "活动赛事三方B白天PC端图片"),
    ACTIVITY_ThIRD_B_NIGHT_PC(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_B_NIGHT_PC", "活动赛事三方B夜间PC端图片"),
    ACTIVITY_ThIRD_B_NIGHT_APP(I18NMessageType.BUSINESS, "ACTIVITY_ThIRD_B_NIGHT_APP", "活动赛事三方B夜间APP端图片"),

    TASK_NAME(I18NMessageType.BUSINESS, "TASK_NAME", "任务名称"),

    TASK_DESCRIPTION(I18NMessageType.BUSINESS, "TASK_DESCRIPTION", "任务说明"),

    TASK_DESC_DESCRIPTION(I18NMessageType.BUSINESS, "TASK_DESC_DESCRIPTION", "任务描述"),

    TASK_PICTURE(I18NMessageType.BUSINESS, "TASK_PICTURE", "移动端任务图标"),

    TASK_PICTURE_PC(I18NMessageType.BUSINESS, "TASK_PICTURE_PC", "PC任务图标"),

    SITE_BANNER_IMAGE(I18NMessageType.BUSINESS, "SITE_BANNER_IMAGE", "站点banner多语言图片"),
    NOTICE_TITLE(I18NMessageType.BUSINESS, "NOTICE_TITLE", "消息名称"),
    NOTICE_CONTENT(I18NMessageType.BUSINESS, "NOTICE_CONTENT", "消息内容"),

    NOTICE_PIC(I18NMessageType.BUSINESS, "NOTICE_PIC", "消息内容"),
    VERSION_MANAGER_DESC(I18NMessageType.BUSINESS, "VERSION_MANAGER_DESC", "版本管理描述多语言"),

    ABOUT_US(I18NMessageType.BUSINESS, "ABOUT_US", "关于我们"),
    PRIVACY_POLICY(I18NMessageType.BUSINESS, "PRIVACY_POLICY", "隐私政策"),
    TERMS_CONDITION(I18NMessageType.BUSINESS, "TERMS_CONDITION", "规则与条款"),
    WITHOUT_LOGIN_PICTURE(I18NMessageType.BUSINESS, "WITHOUT_LOGIN_PICTURE", "首页未登录图"),
    COMPLIANCE_REGULATION(I18NMessageType.BUSINESS, "COMPLIANCE_REGULATION", "合规监管"),
    USER_AGREEMENT(I18NMessageType.BUSINESS, "USER_AGREEMENT", "用户协议"),
    BUSINESS_ADDRESS(I18NMessageType.BUSINESS, "BUSINESS_ADDRESS", "经营地址"),

    IOS_ANDROID_DOWNLOAD_URL(I18NMessageType.BUSINESS, "IOS_DOWNLOAD_URL", "下载背景图"),

    BUSINESS_BASIC_INFO(I18NMessageType.BUSINESS, "BUSINESS_BASIC_INFO", "商务信息"),


    TUTORIAL_CATEGORY(I18NMessageType.BUSINESS, "CATEGORY_%s", "教程大类"),
    TUTORIAL_CLASS(I18NMessageType.BUSINESS, "CLASS_%s", "教程分类"),
    TUTORIAL_TABS(I18NMessageType.BUSINESS, "TABS_%s", "教程页签"),
    TUTORIAL_CONTENT(I18NMessageType.BUSINESS, "CONTENT_%s", "教程内容"),
    TUTORIAL_CONTENT_DETAIL(I18NMessageType.BUSINESS, "CONTENT_DETAIL_%s", "教程内容富文本"),

    SITE_SPLASH_IMAGE(I18NMessageType.BUSINESS, "SITE_SPLASH_IMAGE", "站点闪屏多语言图片"),

    SH_ORDER_RESULT_TITLE_WIN(I18NMessageType.LOOKUP, "sh_order_result_title_win", "视讯指定结果牌,赢"),

    SH_ORDER_RESULT_TITLE_TIE(I18NMessageType.LOOKUP, "sh_order_result_title_tie", "视讯指定结果牌,和局"),

    SH_ORDER_RESULT_TITLE_LOSE(I18NMessageType.LOOKUP, "sh_order_result_title_lose", "视讯指定结果牌,输"),

    SH_ORDER_RESULT_TITLE_WIN_OR_LOSE_RESULTS(I18NMessageType.LOOKUP, "sh_order_result_title_win_or_lose_results", "视讯指定结果牌,输赢结果"),

    SH_ORDER_RESULT_TITLE_LIGHTNING(I18NMessageType.LOOKUP, "sh_order_result_title_lightning", "视讯指定结果牌,闪电"),

    SH_ORDER_RESULT_GAME_RESULTS(I18NMessageType.LOOKUP, "sh_order_result_game_results", "视讯指定结果牌,局结果"),

    SH_ORDER_RESULT_TITLE_TABLE_NUMBER(I18NMessageType.LOOKUP, "sh_order_result_title_table_number", "视讯指定标题:桌号"),

    SH_ORDER_RESULT_TITLE_TABLE_BET(I18NMessageType.LOOKUP, "sh_order_result_title_table_bet", "视讯指定标题:下注"),

    SH_ORDER_RESULT_GAME_RESULTS_TITLE(I18NMessageType.LOOKUP, "sh_order_result_game_results_title", "视讯指定结果牌,结果"),

    SH_ORDER_RESULT_GAME_TYPE_NAME(I18NMessageType.LOOKUP, "sh_order_result_game_type_name", "视讯指定结果牌,游戏名称"),

    SH_ORDER_RESULT_GAME_TABLE_TYPE_NAME(I18NMessageType.LOOKUP, "sh_order_result_game_table_type_name", "视讯指定结果牌,游戏桌台号"),

    SH_ORDER_RESULT_GAME_RESULTS_NUMBER(I18NMessageType.LOOKUP, "sh_order_result_game_results", "视讯指定结果牌,局号"),

    ORDER_RESULT_LOTTERY_RESULTS(I18NMessageType.LOOKUP, "order_result_lottery_results", "指定标题,开奖结果"),

    ACE_ORDER_RESULT_TITLE_TABLE_NUMBER(I18NMessageType.LOOKUP, "ace_order_result_title_table_number", "指定标题:期号"),

    ACE_ORDER_RESULT_TITLE_GAME_NAME(I18NMessageType.LOOKUP, "ace_order_result_title_game_name", "指定标题:彩种名称"),

    ACE_ORDER_RESULT_TITLE_ODDS(I18NMessageType.LOOKUP, "ace_order_result_title_odds", "指定标题:赔率"),

    ACE_ORDER_RESULT_TITLE_MULTIPLE(I18NMessageType.LOOKUP, "ace_order_result_title_multiple", "指定标题:倍数"),

    ACE_ORDER_RESULT_TITLE_BET_COUNT(I18NMessageType.LOOKUP, "ace_order_result_title_bet_count", "指定标题:注数"),

    ORDER_RESULT_TITLE_PLAY_TYPE(I18NMessageType.LOOKUP, "order_result_title_play_type", "指定标题:玩法"),

    ACTIVITY_SHOW_UP_PIC_PC(I18NMessageType.BUSINESS, "ACTIVITY_SHOW_UP_PIC_PC", "未登录状态弹窗推荐宣传图PC"),
    ACTIVITY_SHOW_UP_PIC_APP(I18NMessageType.BUSINESS, "ACTIVITY_SHOW_UP_PIC_APP", "未登录状态弹窗推荐宣传图APP"),

    ACTIVITY_RECOMMEND_TERMINALS_PIC(I18NMessageType.BUSINESS, "ACTIVITY_RECOMMEND_TERMINALS_PIC", "注册成功弹窗展示图:移动"),
    ACTIVITY_RECOMMEND_TERMINALS_PIC_PC(I18NMessageType.BUSINESS, "ACTIVITY_RECOMMEND_TERMINALS_PIC_PC", "注册成功弹窗展示图:PC"),

    ACTIVITY_DETAIL_SHOW_PIC(I18NMessageType.BUSINESS, "ACTIVITY_DETAIL_SHOW_PIC", "活动细节展示图:移动白天"),
    ACTIVITY_DETAIL_SHOW_PIC_PC(I18NMessageType.BUSINESS, "ACTIVITY_DETAIL_SHOW_PIC_PC", "活动细节展示图:PC"),

    ACTIVITY_DETAIL_SHOW_PIC_DARK(I18NMessageType.BUSINESS, "ACTIVITY_DETAIL_SHOW_PIC_DARK", "活动细节展示图:移动黑夜"),

    ACTIVITY_DETAIL_SHOW_PIC_PC_DARK(I18NMessageType.BUSINESS, "ACTIVITY_DETAIL_SHOW_PIC_PC_DARK", "活动细节展示图:PC黑夜"),

    ACTIVITY_FLOAT_ICON_APP(I18NMessageType.BUSINESS, "ACTIVITY_FLOAT_ICON_APP", "未登录首页浮动图标(移动端)"),
    ACTIVITY_FLOAT_ICON_PC(I18NMessageType.BUSINESS, "ACTIVITY_FLOAT_ICON_PC", "未登录首页浮动图标(PC端)"),


    ;

    private I18NMessageType i18NMessageType;
    private String code;
    private String name;

    I18MsgKeyEnum(I18NMessageType i18NMessageType, String code, String name) {
        this.code = i18NMessageType.getCode() + "_" + code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static I18MsgKeyEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        I18MsgKeyEnum[] types = I18MsgKeyEnum.values();
        for (I18MsgKeyEnum type : types) {
            if (code.equals(Integer.valueOf(type.getCode()))) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(Integer code) {
        I18MsgKeyEnum statusEnum = nameOfCode(code);
        if (statusEnum == null) {
            return null;
        }
        return statusEnum.getName();
    }


    public static List<I18MsgKeyEnum> getList() {
        return Arrays.asList(values());
    }


}

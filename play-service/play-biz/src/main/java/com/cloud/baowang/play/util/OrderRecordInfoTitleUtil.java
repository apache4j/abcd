package com.cloud.baowang.play.util;

import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;

public class OrderRecordInfoTitleUtil {


    /**
     * 下注:标题
     */
    public static void setBetTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_TABLE_BET.getCode())).append(": ");
    }

    /**
     * 结果:标题
     */
    public static void setResultTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_RESULTS_TITLE.getCode())).append(": ");
    }


    /**
     * 桌号:标题
     */
    public static void setBetTableTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_TABLE_NUMBER.getCode())).append(": ");
    }


    /**
     * 游戏名称:标题
     */
    public static void setGameNameTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_TYPE_NAME.getCode())).append(": ");
    }

    /**
     * 游戏桌台号:标题
     */
    public static void setGameTableNameTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_TABLE_TYPE_NAME.getCode())).append(": ");
    }


    /**
     * 局号:标题
     */
    public static void setTableNumberTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_GAME_RESULTS_NUMBER.getCode())).append(": ");
    }


    /**
     * 输赢结果:标题
     */
    public static void setWinOrLoseResultTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_WIN_OR_LOSE_RESULTS.getCode())).append(": ");
    }

    /**
     * 赢:标题
     */
    public static String getWinTitle() {
        return I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_WIN.getCode());
    }

    /**
     * 赢:标题
     */
    public static void setWinTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_WIN.getCode())).append(": ");
    }

    /**
     * 输:标题
     */
    public static String getLoseTitle() {
        return I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_LOSE.getCode());
    }

    /**
     * 输:标题
     */
    public static void setLoseTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_LOSE.getCode())).append(": ");
    }

    /**
     * 和:标题
     */
    public static String getTieTitle() {
        return I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_TIE.getCode());
    }

    /**
     * 和:标题
     */
    public static void setTieTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.SH_ORDER_RESULT_TITLE_TIE.getCode())).append(": ");
    }

    /**
     * 开奖结果:标题
     */
    public static void setLotteryResultsTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ORDER_RESULT_LOTTERY_RESULTS.getCode())).append(": ");
    }


    /**
     * 期号:标题
     */
    public static void setAceTableNumberTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ACE_ORDER_RESULT_TITLE_TABLE_NUMBER.getCode())).append(": ");
    }


    /**
     * 玩法:标题
     */
    public static void setPlayTypeTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ORDER_RESULT_TITLE_PLAY_TYPE.getCode())).append(": ");
    }


    /**
     * 彩种名称:标题
     */
    public static void setAceGameNameTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ACE_ORDER_RESULT_TITLE_GAME_NAME.getCode())).append(": ");
    }


    /**
     * 赔率:标题
     */
    public static void setOddsTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ACE_ORDER_RESULT_TITLE_ODDS.getCode())).append(": ");
    }

    /**
     * 倍数:标题
     */
    public static void setMultipleTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ACE_ORDER_RESULT_TITLE_MULTIPLE.getCode())).append(": ");
    }

    /**
     * 注数:标题
     */
    public static void setBetCountTitle(StringBuilder stringBuilder) {
        stringBuilder.append(I18nMessageUtil.getI18NMessage(I18MsgKeyEnum.ACE_ORDER_RESULT_TITLE_BET_COUNT.getCode())).append(": ");
    }


}

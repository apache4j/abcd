package com.cloud.baowang.play.api.vo.cq9.request;

import cn.hutool.core.util.ObjectUtil;
//import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 入参
 * 投注/扣款
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CQ9PayoffReq {


    /**
     * 必填
     */
    private String account;


    /**
     * 事件時間 格式為 RFC3339
     * 如 2017-01-19T22:56:30-04:00
     * ※最大長度為35字元
     */
    private String eventTime;


    /**
     * 交易代碼
     * ※mtcode為唯一值
     * ※最大長度為70字元
     */
    private String mtcode;





    /**
     * 下注金額，takeall 接口字段为空
     * 下注金額(實際從玩家錢包扣除的金額)
     * ※最大長度為16位數，及小數後4位
     * ※金額不得為負值
     */
    private BigDecimal amount;



    /**
     * SessionID
     * ※貴司自行定義，請參考單一錢包 Gamboy文件
     * Player 取得遊戲大廳連結 API
     * Player 取得遊戲連結 API
     */
    private String session;
    /**
     * 活動id
     */
    private String promoid;

    private String wtoken;
    /**
     * 全部是其他扣除调整，OTHER_SUBTRACT("20", "其他扣除调整",BusinessCoinTypeEnum.OTHER_ADJUSTMENTS),
     * 投注与部分转账使用 GAME_BET("12", "投注",BusinessCoinTypeEnum.GAME_BET)
     * WalletEnum.CoinTypeEnum
     */
    private String type;

    /**
     * 是否全部转出 默认是false
     */
    private Boolean takeAllFlag = Boolean.FALSE;
    /**
     * 请求方式
     */
    private String callType;

    /**
     * 收支类型1收入,2支出
     */
    private String balanceType;
    /**
     * 备注
     */
    private String remark;

    /**
     * 投注 ，转入，支出类默认是true，其他false
     */
    private Boolean actionFlag;

    public Boolean valid() {
        if (!ObjectUtil.isAllNotEmpty(account, mtcode)) {
            return false;
        }
        if (!takeAllFlag) {
            return amount.compareTo(BigDecimal.ZERO) >= 0;
        } else {
            return true;
        }
    }
    public boolean isRFC3339TimeFormat(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return false;
        }

        try {
            OffsetDateTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


}

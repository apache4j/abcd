package com.cloud.baowang.play.wallet.vo.res.bti;

import com.cloud.baowang.play.wallet.enums.BtiRespErrEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BtiTokenRsp implements Serializable {

    /**
     * 错误代码
     */
    private Integer errorCode;

    /**
     *  状态信息, 回复此次请求之状态内容
     */
    private String errorMessage ;

    /**
     *  唯一标识玩家的 ID
     * 代理商的客户帐户将在 Web 应用程序上为客
     * 户创建（如果尚未创建），cust_id 将是
     * 回传
     * 存储在其他指令。
     */
    private String custId;
    /**
     * 玩家的余额按照玩家的货币
     */
    private BigDecimal balance;

    /**
     * CMD 请求之日期, Ticks 数据
     */

    private String custLogin;

    /**
     * 玩家城市
     */
    private String city;

    /**
     * country
     */
    private String country;

    private String currencyCode;



    public static String success(BtiTokenRsp request){
        StringBuffer sb= new StringBuffer();
        request.setErrorCode(BtiRespErrEnums.SUCCESS.getCode());
        BigDecimal balance= request.getBalance().setScale(2, RoundingMode.DOWN);
        request.setErrorMessage(BtiRespErrEnums.SUCCESS.getDescription());
        sb.append("error_code="+request.getErrorCode()).append("\\r\\n");
        sb.append("error_message="+request.getErrorCode()).append("\\r\\n");
        sb.append("cust_id="+request.getCustId()).append("\\r\\n");
        sb.append("balance="+ balance).append("\\r\\n");
        sb.append("cust_login="+request.getCustLogin()).append("\\r\\n");
        sb.append("city="+request.getCity()).append("\\r\\n");
        sb.append("country="+request.getCountry()).append("\\r\\n");
        sb.append("currency_code="+request.getCurrencyCode());
        return sb.toString();
    }

    public static String err(BtiRespErrEnums btiRespErrEnums){
        String sb = "error_code=" + btiRespErrEnums.getCode() + "\\r\\n" +
                "error_message=" + btiRespErrEnums.getDescription() + "\\r\\n";
        return sb;
    }

}

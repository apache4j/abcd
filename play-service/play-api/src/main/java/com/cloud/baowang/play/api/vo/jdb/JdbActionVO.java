package com.cloud.baowang.play.api.vo.jdb;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdbActionVO implements Serializable {

    private Integer action;
    /** 当前系统时间 */
    private Long ts;

    /** 交易序号 */
    private String parent;

    /** 玩家账号 */
    private String uid;

    /** 语系 */
    private String lang;

    /** 游戏类型 */
    @JSONField(name = "gType")
    private String gType;

    /** 机台类型 */
    @JSONField(name = "mType")
    private String mType;

    /** 使用 JDB 游戏大厅 ?*/
    private String windowMode;

    /** 是否为手机 APP 进入游戏 */
    private Boolean isAPP;

    /** 游戏大厅网址 windowMode 为 2 时，此参数才会有作用 */
    private String lobbyURL;

    /** 默认音效开关 */
    private Integer mute;

    /** 棋牌游戏群组 */
    private String cardGameGroup;

    /** 设定彩金开关*/
    private Integer jackpotFlag;

    /** 是否显示币别符号 */
    private Boolean isShowDollarSign;

    /** 币种 */
    protected String currency;


    protected String systemSessionId;

}

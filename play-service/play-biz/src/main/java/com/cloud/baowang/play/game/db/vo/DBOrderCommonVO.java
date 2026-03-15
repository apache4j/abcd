package com.cloud.baowang.play.game.db.vo;

import lombok.Data;

@Data
public class DBOrderCommonVO {
    //注单 id
    private String bi;
    //商户 id
    private Integer mi;
    //玩家账号(用户名)
    private String mmi;
    //投注时间
    private Long st;
    //结算时间
    private Long et;
    //游戏桌号
    private Integer gd;
    //游戏ID
    private Integer gi;
    //游戏名称
    private String gn;
    //房间类型
    private Integer gt;
    //游戏房间
    private String gr;
    //输赢金额
    private Integer mw;
    //抽水金额
    private Integer mp;
    //有效投注
    private Integer bc;
    //局号
    private String cn;
    //总投注金额
    private Integer tb;
    //游戏分类标记(0:游戏类,100活动类)
    private Integer gf;
    //币种
    private String cur;
    //游戏品牌
    private String gb;
    //注单类型(1,免费旋转,2,免费游戏,3,特殊旋转,5,优惠活动,6多次下注)
    private Integer brt;
    //注单时间
    private Long bt;

    //终端设备类型( 0:windows, 1:mac, 2:ios, 3:android, 4:未知)
    private Integer dt;


}


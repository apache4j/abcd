package com.cloud.baowang.user.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 勋章代码
 *
 */
@Getter
public enum MedalCodeEnum {
    MEDAL_1001("1001","铁粉", "累计登录平台n天",1),
    MEDAL_1002("1002","孤勇者", "任意单个自然月全平台负盈利最多的一个人",0),
    MEDAL_1003("1003","功勋卓著", "任意单个自然月全平台流水最多的一个人",0),
    MEDAL_1004("1004","独上高楼", "达到最高等级≥vip n的人",1),
    MEDAL_1005("1005","雨神", "参与官方的红包雨活动并收到有效红包n个 ",1),
    MEDAL_1006("1006","旋转之星", "参与轮盘旋转总计n次",1),
    MEDAL_1007("1007","无敌幸运星","平台任意游戏单笔注单盈利 n WTC以上",1),
    MEDAL_1008("1008","赌神","每日竞赛(真人排行)连续n天排名均在前m",2),
    MEDAL_1009("1009","电子霸王","每日竞赛(电子排行)连续n天排名均在前m",2),
    MEDAL_1010("1010","运动健将","体育场馆连续n个自然日均有投注,单日流水m WTC以上",2),
    MEDAL_1011("1011","乐透达人","彩票场馆连续n个自然日均有投注,单日流水m WTC以上",2),
    MEDAL_1012("1012","招财猫","会员累积盈利达到n WTC",1),
    MEDAL_1013("1013","叫我有钱人","平台投注量达到n WTC",1),
    MEDAL_1014("1014","小有所成","平台投注量达到n WTC",1),
    MEDAL_1015("1015","老前辈","会员平台注册后满n天",1),
    MEDAL_1016("1016","元宇宙大富翁","任意单个自然月虚拟币充值最多的一个人",0),
    MEDAL_1017("1017","任务大师","任意单个自然周(周一至周日)完成全部每日任务",0),
    MEDAL_1018("1018","富甲天下","任意单个自然月全平台累积充值金额(非虚拟币)最多的一个人",0),
    MEDAL_1019("1019","大老板","任意单个自然月全平台累积提款金额最多的一个人",0),
    MEDAL_1020("1020","呼朋唤友","通过个人邀请好友链接邀请好友n人(好友需注册并有充值记录)",1);
    /**
     * 勋章代码
     */
    private final String code;
    /**
     * 勋章名称
     */
    private final String name;
    /**
     * 解锁条件
     */
    private final String desc;
    /**
     * 条件数量
     */
    private final int condNum;

    MedalCodeEnum(String code, String name,String desc,int condNum) {
        this.code = code;
        this.name = name;
        this.desc=desc;
        this.condNum=condNum;
    }

    public static MedalCodeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        MedalCodeEnum[] types = MedalCodeEnum.values();
        for (MedalCodeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<MedalCodeEnum> getList() {
        return Arrays.asList(values());
    }


}

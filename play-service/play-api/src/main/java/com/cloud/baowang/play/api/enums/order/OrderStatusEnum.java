package com.cloud.baowang.play.api.enums.order;

import com.cloud.baowang.play.api.enums.ClassifyEnum;
import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    //通用
    NOT_SETTLE(0, "未结算", ClassifyEnum.NOT_SETTLE.getCode()),
    SETTLED(1, "已结算", ClassifyEnum.SETTLED.getCode()),
    CANCEL(2, "已取消", ClassifyEnum.CANCEL.getCode()),
    RESETTLED(4, "重新结算", ClassifyEnum.RESETTLED.getCode()),

    //真人特殊状态
    SKIP_ISSUE(3,"跳局", ClassifyEnum.CANCEL.getCode()),
    SURRENDER(17, "投降", ClassifyEnum.SETTLED.getCode()),
    //彩票/电竞
    PRE_LOTTERY(5, "待开奖", ClassifyEnum.NOT_SETTLE.getCode()),
    WIN(6, "已中奖", ClassifyEnum.SETTLED.getCode()),
    LOSS(7, "未中奖", ClassifyEnum.SETTLED.getCode()),
    HANG_UP(8, "挂起", ClassifyEnum.NOT_SETTLE.getCode()),
    LOTTERY_CANCEL(20, "撤销", ClassifyEnum.CANCEL.getCode()),
    DRAW(21, "和局", ClassifyEnum.SETTLED.getCode()),

    //电竞
    PRE_SETTLE(9, "待结算", ClassifyEnum.NOT_SETTLE.getCode()),
    REVOKE(10, "撤销", ClassifyEnum.NOT_SETTLE.getCode()),

    //体育 电竞 拒单
    PRE_PROCESS(11, "待处理", ClassifyEnum.NOT_SETTLE.getCode()),
    MANUAL_CANCEL(12, "手动取消", ClassifyEnum.CANCEL.getCode()),
    PRE_CONFIRM(13, "待确认", ClassifyEnum.NOT_SETTLE.getCode()),
    RISK_REJECT(14, "风控拒单", ClassifyEnum.CANCEL.getCode()),
    GAME_REVOKE(15, "赛事取消", ClassifyEnum.CANCEL.getCode()),
    CONFIRM(16, "已确认", ClassifyEnum.NOT_SETTLE.getCode()),

    ABERRANT(17, "异常(请联系客服)", ClassifyEnum.NOT_SETTLE.getCode());


    private Integer code;
    private String name;
    private Integer classifyCode;

    OrderStatusEnum(Integer code, String name, Integer classifyCode) {
        this.code = code;
        this.name = name;
        this.classifyCode = classifyCode;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getClassifyCode() {
        return classifyCode;
    }

    public void setClassifyCode(Integer classifyCode) {
        this.classifyCode = classifyCode;
    }

    public static OrderStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        OrderStatusEnum[] types = OrderStatusEnum.values();
        for (OrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static Integer getClassifyCodeByCode(Integer code) {
        if (null == code) {
            return null;
        }
        OrderStatusEnum[] types = OrderStatusEnum.values();
        for (OrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getClassifyCode();
            }
        }
        return null;
    }
}

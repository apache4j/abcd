package com.cloud.baowang.play.api.vo.dbPanDaSport;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.enums.dbPanDaSport.DbPanDaSportBizTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbPanDaSportBetReqVO {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 业务类型，账变来源，具体可见参数字段映射
     */
    private String bizType;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * Long型(小于等于19位)，交易的讯息号(每次账变都是唯一的)
     */
    private String transferId;

    /**
     * Double类型金额
     */
    private BigDecimal amount;

    /**
     * 账变类型(1加款,2扣款)
     */
    private String transferType;


    /**
     * Long型时间戳(13位)
     */
    private String timestamp;

    /**
     * 签名
     */
    private String signature;

    /**
     * 商户方的用户会话
     */
    private String stoken;

    /**
     * 二次结算标识. 值为2时为二次结算 值为空或1为单次结算
     */
    private String secondSettleFlag;

    /**
     * version=1是为结算1.0服务，version=2时为结算2.0
     */
    private Integer version;

    /**
     * 商户端注单ID (字符串数组)
     */
    private String ticketId;
    /**
     * 订单列表(json字符串) , 具体格式请查看下方
     */
    private String orderStr;


    private List<DbPanDaBetDetailVO> orderList;

    public List<DbPanDaBetDetailVO> getOrderList() {
        return JSONArray.parseArray(orderStr, DbPanDaBetDetailVO.class);
    }


    public boolean valid() {
        boolean boolType = ObjectUtil.isAllNotEmpty(userName, bizType, merchantCode, transferId,
                amount, transferType, orderStr, timestamp,
                signature, orderStr);

        if (!boolType) {
            return false;
        }
        return CollectionUtil.isNotEmpty(orderList);
    }


}

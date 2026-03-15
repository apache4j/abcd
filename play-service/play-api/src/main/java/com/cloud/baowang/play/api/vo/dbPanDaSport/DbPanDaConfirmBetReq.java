package com.cloud.baowang.play.api.vo.dbPanDaSport;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbPanDaConfirmBetReq {

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 交易的讯息号, 唯一标示, 不可重复, 19位数字类型字符串
     */
    private String transferId;

    /**
     * Long型时间戳 (13位)
     */
    private String timestamp;

    /**
     * 加扣款状态 (0:失败, 1:成功)
     */
    private String status;

    /**
     * 加扣款状态信息描述
     */
    private String msg;

    /**
     * 交易涉及注单
     */
    private String orderList;

    private List<DbPanDaBetDetailVO> betOrderList;

    public List<DbPanDaBetDetailVO> getBetOrderList() {
        return JSONArray.parseArray(orderList, DbPanDaBetDetailVO.class);
    }

    public boolean valid() {
        boolean type = StrUtil.isAllNotEmpty(merchantCode, transferId, timestamp, status);
        if (!type) {
            return false;
        }
        return CollectionUtil.isNotEmpty(betOrderList);
    }

}

package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("vip_rank")
public class VipRankPO extends BasePO implements Serializable {
    private Integer vipRankCode;
    private String vipRankName;
    private String vipRankNameI18nCode;
}

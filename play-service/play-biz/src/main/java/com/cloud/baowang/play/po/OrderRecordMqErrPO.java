package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("order_record_mq_err")
public class OrderRecordMqErrPO extends BasePO {

    /**
     * 平台编码
     */
    private String venueCode;
    /**
     * 内容
     */
    private String jsonStr;

    /**
     * 已经尝试次数
     */
    private Integer times;


    /**
     * 状态 1成功; 0 失败
     */
    private Integer status;


}

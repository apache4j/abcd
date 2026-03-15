package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("order_pull_params")
public class OrderPullParamsPO implements Serializable {

    public final static String COLLECTION_NAME = "order_pull_params";

   // @TableId(type = IdType.ASSIGN_ID)
    @TableId
    private String id;

    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 拉单参数
     */
    private String paramsJson;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 修改时间
     */
    private Long updatedTime;

    public String getId(){
        if(!StringUtils.hasText(id)){
            this.id= SnowFlakeUtils.getSnowId();
        }
        return id;
    }
}

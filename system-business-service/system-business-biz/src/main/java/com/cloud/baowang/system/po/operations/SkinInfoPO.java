package com.cloud.baowang.system.po.operations;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("skin_info")
public class SkinInfoPO extends BasePO {

    private String skinCode;

    private String skinName;

    //private String pcAddr;

    //private String h5Addr;

    private Integer status;

    private String remark;

    private String creatorName;

    private String updaterName;
//    private Integer handicapMode;
}

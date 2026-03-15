package com.cloud.baowang.system.po.timezone;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("system_timezone")
public class SystemTimezonePO extends BasePO {

    /**
     * 时区代码
     */
    private String timezoneCode;
}

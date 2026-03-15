package com.cloud.baowang.user.api.vo.vip;

import lombok.Data;

import java.io.Serializable;
@Data
public class VIPGradeVO implements Serializable {

    /* VIP等级code */
    private Integer vipGradeCode;

    /* VIP等级名称 */
    private String vipGradeName;

}

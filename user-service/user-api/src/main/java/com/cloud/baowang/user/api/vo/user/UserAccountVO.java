package com.cloud.baowang.user.api.vo.user;


import lombok.Builder;
import lombok.Data;


/**
 * 会员信息
 */
@Data
@Builder
public class UserAccountVO {
    private Long id;
    //会员账号
    private String userAccount;
    //会员ID
    private String userId;
    //账号类型 1测试 2正式
    private String accountType;

}

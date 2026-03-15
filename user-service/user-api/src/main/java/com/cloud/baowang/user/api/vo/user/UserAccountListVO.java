package com.cloud.baowang.user.api.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会员列表请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountListVO {
    private List<String> accountList;
}

package com.cloud.baowang.user.api.vo.user.request;

import com.cloud.baowang.common.core.vo.base.PageVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/12/05 23:37
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdPageVO extends PageVO {
    private List<String> userIdList;
}

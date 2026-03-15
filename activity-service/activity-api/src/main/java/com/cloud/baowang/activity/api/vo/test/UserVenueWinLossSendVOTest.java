package com.cloud.baowang.activity.api.vo.test;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/11 11:11
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class UserVenueWinLossSendVOTest extends MessageBaseVO {
    private List<UserVenueWinLossMqVOTest> voList;
}

package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.UserVIPFlowRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserVIPFlowRecordRepository extends BaseMapper<UserVIPFlowRecordPO> {
    UserVIPFlowRecordPO selectLastOne(@Param("userId") String userId,
                                      @Param("vipGradeCode") Integer vipGradeCode);
}

package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.api.vo.vip.UserVipFlowRecordReqVO;
import com.cloud.baowang.user.po.UserVipFlowRecordCnPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: mufan
 * @description:
 */
@Mapper
public interface UserVipFlowRecordCnRepository extends BaseMapper<UserVipFlowRecordCnPO> {
    List<UserVipFlowRecordCnPO> lastSiteVipChangeRecordCnPOs(@Param("vo") UserVipFlowRecordReqVO vo);
}

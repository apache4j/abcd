package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.SystemMessageConfigPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 消息配置表 Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2024-10-31
 */
@Mapper
public interface SystemMessageConfigRepository extends BaseMapper<SystemMessageConfigPO> {

}

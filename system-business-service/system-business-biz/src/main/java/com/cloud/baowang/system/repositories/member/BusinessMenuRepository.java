package com.cloud.baowang.system.repositories.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.member.BusinessMenuPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 中控菜单 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface BusinessMenuRepository extends BaseMapper<BusinessMenuPO> {

}

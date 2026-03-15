package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.vip.VIPChangeRequestVO;
import com.cloud.baowang.user.api.vo.vip.VIPChangeVO;
import com.cloud.baowang.user.po.VIPChangePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 小智
 * @Date 8/5/23 5:22 PM
 * @Version 1.0
 */
@Mapper
public interface VIPChangeRepository extends BaseMapper<VIPChangePO> {
    Page<VIPChangeVO> selectVIPChangePage(Page<VIPChangePO> page,
                                          @Param("vo") VIPChangeRequestVO requestVO);
}

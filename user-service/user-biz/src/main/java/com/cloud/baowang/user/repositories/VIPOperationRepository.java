package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.vip.VIPOperationRequestVO;
import com.cloud.baowang.user.api.vo.vip.VIPOperationVO;
import com.cloud.baowang.user.po.SiteVIPOperationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 小智
 * @Date 6/5/23 1:48 PM
 * @Version 1.0
 */
@Mapper
public interface VIPOperationRepository extends BaseMapper<SiteVIPOperationPO> {
    Page<VIPOperationVO> selectVIPOperationPage(
            Page<SiteVIPOperationPO> page, @Param("vo") VIPOperationRequestVO requestVO);
}

package com.cloud.baowang.system.repositories.site;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteLoginInfoVO;
import com.cloud.baowang.system.po.site.SiteLoginInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface SiteLoginInfoRepository extends BaseMapper<SiteLoginInfoPO> {

    Page<SiteLoginInfoVO> querySiteLoginInfoPage(Page<SiteLoginInfoPO> page, @Param("vo") UserLoginRequestVO dto);

}

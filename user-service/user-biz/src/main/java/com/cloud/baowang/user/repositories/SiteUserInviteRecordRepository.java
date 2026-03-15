package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordResVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordTaskReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordTaskResVO;
import com.cloud.baowang.user.po.SiteUserInviteRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 23:44
 * @description:
 */
@Mapper
public interface SiteUserInviteRecordRepository extends BaseMapper<SiteUserInviteRecordPO> {
    Page<SiteUserInviteRecordResVO> getInviteRecordPage(Page<SiteUserInviteRecordResVO> page, @Param("vo") SiteUserInviteRecordReqVO vo);

    List<SiteUserInviteRecordTaskResVO> getInviteRecord(@Param("vo") SiteUserInviteRecordTaskReqVO vo);

}

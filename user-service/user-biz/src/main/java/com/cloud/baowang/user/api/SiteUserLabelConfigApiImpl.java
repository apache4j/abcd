package com.cloud.baowang.user.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.vo.userlabel.*;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.service.SiteUserLabelConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 会员标签配置 服务类
 *
 * @author wade
 * @since 2023-05-04 10:00:00
 */
@Slf4j
@AllArgsConstructor
@RestController
public class SiteUserLabelConfigApiImpl implements SiteUserLabelConfigApi {

    private final SiteUserLabelConfigService siteUserLabelConfigService;

    @Override
    public List<GetAllUserLabelVO> getAllUserLabel(String siteCode) {
        return siteUserLabelConfigService.getAllUserLabel(siteCode);
    }

    @Override
    public GetAllUserLabelVO getByLabelId(IdVO idVO) {
        return siteUserLabelConfigService.getByLabelId(idVO);
    }

    @Override
    public GetUserLabelByIdsVO getInfoById(Long id) {
        return siteUserLabelConfigService.getInfoById(id);
    }




    @Override
    public List<GetUserLabelByIdsVO> getUserLabelByIds(List<String> ids) {
        return siteUserLabelConfigService.getUserLabelByIds(ids);
    }

    @Override
    public List<GetUserLabelByIdsVO> getUserLabel(UserLabelIdReqVO userLabelIdReqVO) {
        return siteUserLabelConfigService.getUserLabel(userLabelIdReqVO);
    }


    @Override
    public ResponseVO<?> addLabel(UserLabelAddRequestVO vo) {
        return siteUserLabelConfigService.addLabel(vo);
    }

    @Override
    public Page<UserLabelConfigPageResponseVO> getLabelConfigPage(UserLabelConfigPageRequestVO vo) {
        return siteUserLabelConfigService.getLabelConfigPage(vo);
    }

    @Override
    public ResponseVO<?> editLabel(UserLabelEditRequestVO vo) {
        return siteUserLabelConfigService.editLabel(vo);
    }

    @Override
    public ResponseVO<?> delLabel(UserLabelDelRequestVO vo) {
        return siteUserLabelConfigService.delLabel(vo);
    }

    @Override
    public Page<GetUserPageByLabelIdVO> getUserPageByLabelId(GetUserPageByLabelIdRequestVO vo) {
        return siteUserLabelConfigService.getUserPageByLabelId(vo);
    }

    @Override
    public ResponseVO<Long> getTotalCount(UserLabelConfigPageRequestVO reqVO) {
        return siteUserLabelConfigService.getTotalCount(reqVO);
    }

    @Override
    public Boolean existsLabelId(String ids, String labelId) {
        return siteUserLabelConfigService.existsLabelId(ids,labelId);
    }
}

package com.cloud.baowang.user.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserInformationDownVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserLabelVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserListInformationDownVO;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.vo.UserDetails.*;
import com.cloud.baowang.user.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @className: UserDetailsApiImpl
 * @author: wade
 * @description: 会员-会员详情
 * @date: 2024/3/23 17:24
 */
@RestController
@Validated
@Slf4j
public class UserDetailsApiImpl implements UserDetailsApi {

    private final UserDetailsService userDetailsService;

    public UserDetailsApiImpl(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    public ResponseVO updateInformation(UserDetailsReqVO userDetailsReqVO, String adminId, String adminName) {
        return userDetailsService.updateInformation(userDetailsReqVO,adminId,adminName);
    }


    @Override
    public ResponseVO<UserInformationDownVO> getInformationDown(UserInfoDwonReqVO userInfoDwonReqVO) {
        return userDetailsService.getInformationDown(userInfoDwonReqVO);
    }

    public ResponseVO<UserListInformationDownVO> getUserListInformationDown(UserInfoListDownReqVO vo) {
        return userDetailsService.getUserListInformationDown(vo);
    }

    @Override
    public void bathUpdateLabel(BathUserReqVO vo, String adminId,  String adminName) {
        userDetailsService.bachUpdateUserLable(vo,adminId,adminName);
    }

    @Override
    public void bathUpdateRemark(BathUserRemarkReqVO vo) {
        userDetailsService.bathUpdateRemark(vo);
    }

    @Override
    public ResponseVO<String> checkUsers(CheckUserReqVO vo) {
        return userDetailsService.checkUsers(vo);
    }

    @Override
    public ResponseVO<List<UserLabelVO>> getBathUsersLabel(CheckUserReqVO vo) {
        return userDetailsService.getBathUsersLabel(vo);
    }

    @Override
    public void bathDeleteLabel(BathUserReqVO vo, String adminId, String adminName) {
         userDetailsService.bathDeleteLabel(vo,adminId,adminName);
    }

    @Override
    public ResponseVO<Boolean> addUserReceiveAccountReview(UserDetailsReqVO vo) {
        return userDetailsService.addUserReceiveAccountReview(vo);
    }

    @Override
    public List<UserAccountUpdateReviewResVO> getReviewingList(List<String> ids) {
        return userDetailsService.getReviewingList(ids);
    }
}

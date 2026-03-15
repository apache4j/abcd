package com.cloud.baowang.user.controller.user;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeUnreadNumRspVO;
import com.cloud.baowang.user.api.vo.notice.user.request.NoticeUpdateVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeSetReadStateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "客户端-我的-用户通知")
@RestController
@AllArgsConstructor
@RequestMapping("/client/user/notice")
public class UserNoticeController {


    private final UserNoticeApi userNoticeApi;


    private final SystemParamApi systemParamApi;


//    @Operation(summary ="获取用户通知Tab页列表")
//    @PostMapping("/getUserNoticeTabList")
//    public ResponseVO<List<CodeValueVO>> getUserNoticeTabList() {
//        ResponseVO<List<CodeValueVO>> userNoticeTabList = systemParamApi.getSystemParamByType(CommonConstant.NOTIFICATION_TYPE);
//        return userNoticeTabList;
//    }
    @Operation(summary ="获取未读消息数量 skin4")
    @GetMapping("/getUserNoticeUnreadNums")
    public ResponseVO<UserNoticeUnreadNumRspVO> getUserNoticeUnreadNums() {
    //        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
    //        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        ResponseVO<UserNoticeUnreadNumRspVO> noticeUnreadNums = userNoticeApi.getUserNoticeUnreadNums();
        return noticeUnreadNums;
    }


    @Operation(summary ="获取用户通知列表,未读消息数量在/user-info/api/getIndexInfo返回对象取unReadNoticeNums字段")
    @PostMapping("/getUserNoticeList")
    public ResponseVO<Page<UserNoticeRespVO>> getUserNoticeList(@Valid @RequestBody UserNoticeReqVO userNoticeReqVO) {
//        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
//        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        ResponseVO<Page<UserNoticeRespVO>> userNoticeList = userNoticeApi.getUserNoticeList(userNoticeReqVO);
        return userNoticeList;
    }


    @Operation(summary ="标记已读通知 ,只需传消息id,和status,其他字段忽略")
    @PostMapping("/setReadState")
    public ResponseVO setReadState(@Valid @RequestBody UserNoticeSetReadStateReqVO userNoticeSetReadStateReqVO) {
        userNoticeSetReadStateReqVO.setUserAccount(CurrReqUtils.getAccount());
        userNoticeSetReadStateReqVO.setUserId(CurrReqUtils.getOneId());
        return userNoticeApi.setReadState(userNoticeSetReadStateReqVO);
    }

    @Operation(summary ="一键已读通知 只需传noticeType,其他字段忽略")
    @PostMapping("/setReadStateAll")
    public ResponseVO setReadStateAll(@Valid @RequestBody UserNoticeReqVO userNoticeReqVO) {
        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        return userNoticeApi.setReadStateAll(userNoticeReqVO);
    }

    @Operation(summary ="一键删除 只需传noticeType,其他字段忽略")
    @PostMapping("/setDelStateAll")
    public ResponseVO<Boolean> setDelStateAll(@Valid @RequestBody UserNoticeReqVO userNoticeReqVO) {
        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        return userNoticeApi.setDelStateAll(userNoticeReqVO);
    }


    @Operation(summary ="批量删除 ")
    @PostMapping("/deleteBatch")
    public ResponseVO<Boolean> deleteBatch(@Valid @RequestBody NoticeUpdateVO reqVO) {
        reqVO.setUserId(CurrReqUtils.getOneId());
        return userNoticeApi.deleteBatch(reqVO);
    }


    @Operation(summary ="获取跑马灯通知列表-公告")
    @PostMapping("/getUserNoticeHeadList")
    public ResponseVO<List<UserNoticeRespVO>> getUserNoticeHeadList() {
        UserNoticeHeadReqVO userNoticeReqVO = new UserNoticeHeadReqVO();
//        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
//        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        if (CurrReqUtils.getReqDeviceType() == null){
            throw  new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        userNoticeReqVO.setDeviceTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        return userNoticeApi.getUserNoticeHeadList(userNoticeReqVO);
    }

    @Operation(summary ="获取弹窗通知列表-公告")
    @PostMapping("/getForceUserNoticeHeadList")
    public ResponseVO<List<UserNoticeRespVO>> getForceUserNoticeHeadList() {
        UserNoticeHeadReqVO userNoticeReqVO = new UserNoticeHeadReqVO();
//        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
//        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        if (CurrReqUtils.getReqDeviceType() == null){
            throw  new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        userNoticeReqVO.setDeviceTerminal(String.valueOf(CurrReqUtils.getReqDeviceType()));
        return userNoticeApi.getForceUserNoticeHeadList(userNoticeReqVO);
    }


}

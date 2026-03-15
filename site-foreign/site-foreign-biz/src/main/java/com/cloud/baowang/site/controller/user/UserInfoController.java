package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.reponse.UserListInformationDownVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.UserInfoExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserDetails.UserInfoListDownReqVO;
import com.cloud.baowang.user.api.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: kimi
 */
@Tag(name = "会员列表")
@RestController
@RequestMapping("/user-info/api")
@AllArgsConstructor
public class UserInfoController {

    private final UserInfoApi userInfoApi;
    private final MinioUploadApi minioUploadApi;
    private final UserDetailsApi userDetailsApi;

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getPage")
    public ResponseVO<Page<UserInfoResponseVO>> getPage(@RequestBody UserInfoPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userInfoApi.getPage(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserInfoPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::userInfo::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> responseVO = userInfoApi.getTotalCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        vo.setPageSize(responseVO.getData().intValue());
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }


        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserInfoExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(getPage(param).getData().getRecords(), UserInfoExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_LIST)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    @Operation(summary = "会员基本信息")
    @PostMapping(value = "/queryBasicUser")
    //@Cacheable(cacheNames = CacheConstants.USER_BASIC, key = "#requestVO.userAccount"+"requestVO.userRegister")
    public ResponseVO<UserBasicVO> queryBasicUser(@RequestBody UserBasicRequestVO requestVO) {
        checkRequestVo(requestVO);
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userInfoApi.queryBasicUser(requestVO);
    }

    private void checkRequestVo(UserBasicRequestVO requestVO) {
        if (StringUtils.isBlank(requestVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.PLEASE_RE_ENTER_USER_ACCOUNT);
        }
    }

    @Operation(summary = "根据注册信息查询会员 ")
    @PostMapping(value = "/getUserInfoList")
    public ResponseVO<List<UserInfoQueryVO>> getUserInfoList(@RequestBody UserBasicRequestVO requestVO) {
        //commonAdminService.getLoginAdmin();
        if (StringUtils.isBlank(requestVO.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return userInfoApi.getUserInfoList(requestVO);
    }

    @Operation(summary = "会员备注信息")
    @PostMapping(value = "/queryUserRemark")
    public ResponseVO<Page<UserRemarkVO>> queryUserRemark(
            @RequestBody UserBasicRequestVO requestVO) {
        checkRequestVo(requestVO);
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userInfoApi.queryUserRemark(requestVO);
    }

    @Operation(summary = "修改会员备注历史信息")
    @PostMapping(value = "/updateRemarkHistory")
    public ResponseVO<Boolean> updateRemarkHistory(@RequestBody UserRemarkRequestVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setOperator(CurrReqUtils.getAccount());
        return userInfoApi.updateRemarkHistory(requestVO);
    }




    @Operation(summary = "根据会员账号查询登录日志")
    @PostMapping(value = "/queryUserLoginInfo")
    public ResponseVO<Page<UserLoginInfoVO>> queryUserLoginInfo(@RequestBody UserBasicRequestVO requestVO) {
        checkRequestVo(requestVO);
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userInfoApi.queryUserLoginInfo(requestVO);
    }

    @Operation(summary = "会员详情获取验证码")
    @PostMapping(value = "/queryVerifyCode")
    public ResponseVO<String> queryVerifyCode(@RequestBody UserDetailQueryVO requestVO) {
        if (requestVO.getPhone() == null) {
            return ResponseVO.fail(ResultCode.PHONE_IS_NULL);
        }
        if (requestVO.getUserAccount() == null) {
            return ResponseVO.fail(ResultCode.ACCOUNT_NOT_NULL);
        }
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        //GetByUserAccountVO getByUserAccountVO = userInfoApi.getByUserAccount(requestVO.getUserAccount());
        UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
        userBasicRequestVO.setUserAccount(requestVO.getUserAccount());
        userBasicRequestVO.setSiteCode(requestVO.getSiteCode());
        UserInfoVO userInfoVO = userInfoApi.getUserInfoVOByAccountOrRegister(userBasicRequestVO);

        String codeKey = String.format(RedisConstants.VERIFY_CODE_CACHE, userInfoVO.getSiteCode(), userInfoVO.getUserAccount());

        String verifyCode = RedisUtil.getValue(codeKey);

        if (verifyCode == null) {

            return ResponseVO.success("验证码已过期");
        }

        return ResponseVO.success(verifyCode);
    }

    @Operation(summary = "会员列表信息编辑-下拉框")
    @PostMapping("/getUserListInformationDown")
    public ResponseVO<UserListInformationDownVO> getUserListInformationDown() {
        UserInfoListDownReqVO vo = new UserInfoListDownReqVO();
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userDetailsApi.getUserListInformationDown(vo);

    }
}

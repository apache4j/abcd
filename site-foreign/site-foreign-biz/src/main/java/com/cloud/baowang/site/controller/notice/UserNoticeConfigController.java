package com.cloud.baowang.site.controller.notice;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountReadExcelDTO;
import com.cloud.baowang.user.api.api.notice.UserNoticeConfigApi;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.*;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.SiteHomeNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticeconfig.response.UserNoticeConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetGetVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Tag(name = "通知配置")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/user-notice-config/api")
@Slf4j
public class UserNoticeConfigController {

    private final UserNoticeConfigApi userNoticeConfigApi;


    @Operation(summary = "通知配置列表查询")
    @PostMapping("/pageList")
    public ResponseVO<Page<UserNoticeConfigVO>> pageList(@RequestBody UserNoticeConfigGetVO userNoticeConfigGetVO) {
        userNoticeConfigGetVO.setSiteCode(CurrReqUtils.getSiteCode());
        return userNoticeConfigApi.getUserNoticeConfigPage(userNoticeConfigGetVO);
    }


    @Operation(summary = "通知配置新增-中控后台")
    @PostMapping("/addUserNoticeConfig")
    public ResponseVO<?> addUserNoticeConfig(@Valid UserNoticeConfigAddVO userNoticeConfigAddVO) {
        userNoticeConfigAddVO.setOperator(CurrReqUtils.getAccount());
        userNoticeConfigAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        UserNoticeConfigAddModifyVO reqVO = new UserNoticeConfigAddModifyVO();
        BeanUtils.copyProperties(userNoticeConfigAddVO, reqVO);
        List<String> combinedAccounts = new ArrayList<>();
        if (userNoticeConfigAddVO.getFile() != null && !userNoticeConfigAddVO.getFile().isEmpty()) {
            combinedAccounts.addAll(userListOfParseExcel(userNoticeConfigAddVO.getFile()));
        }
        if (StringUtils.isNotBlank(userNoticeConfigAddVO.getAccounts())) {
            List<String> accounts = convertStringToList(userNoticeConfigAddVO.getAccounts());
            combinedAccounts.addAll(accounts);
        }
        reqVO.setFileAccount(combinedAccounts.stream().distinct().collect(Collectors.toList()));
        // 转换
        if (StringUtils.isNotBlank(userNoticeConfigAddVO.getNoticeTitleI18nCodeList())) {
            JSONArray jsonArray = JSONArray.parseArray(userNoticeConfigAddVO.getNoticeTitleI18nCodeList());
            // Convert to List<JSONObject>
            List<I18nMsgFrontVO> jsonObjectList = jsonArray.toJavaList(I18nMsgFrontVO.class);
            reqVO.setNoticeTitleI18nCodeList(jsonObjectList);
        }
        if (StringUtils.isNotBlank(userNoticeConfigAddVO.getMessageContentI18nCodeList())) {
            JSONArray jsonArray = JSONArray.parseArray(userNoticeConfigAddVO.getMessageContentI18nCodeList());
            // Convert to List<JSONObject>
            List<I18nMsgFrontVO> jsonObjectList = jsonArray.toJavaList(I18nMsgFrontVO.class);
            reqVO.setMessageContentI18nCodeList(jsonObjectList);
        }
        if (StringUtils.isNotBlank(userNoticeConfigAddVO.getPicIconI18nCodeList())) {
            JSONArray jsonArray = JSONArray.parseArray(userNoticeConfigAddVO.getPicIconI18nCodeList());
            // Convert to List<JSONObject>
            List<I18nMsgFrontVO> jsonObjectList = jsonArray.toJavaList(I18nMsgFrontVO.class);
            reqVO.setPicIconI18nCodeList(jsonObjectList);
        }
        return userNoticeConfigApi.addUserNoticeConfig(reqVO);
    }

    public List<String> convertStringToList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return List.of(); // 返回空列表
        }

        // 使用逗号进行分割，并去除空格和空字符串
        return Arrays.stream(input.split(","))
                .map(String::trim)              // 去除每个元素的前后空格
                .filter(item -> !item.isEmpty()) // 过滤空字符串
                .collect(Collectors.toList());  // 转换为列表

    }

    private List<String> userListOfParseExcel(MultipartFile file) {
        List<String> userList = new ArrayList<>();
        try {
            ExcelUtil.read(file.getInputStream(), UserAccountReadExcelDTO.class, new UserAccountExcelConsumerListener(userList::add)).sheet().doRead();
        } catch (Exception e) {
            log.error("新增用户通知解析会员账号excel异常,error:", e);
            throw new BaowangDefaultException(ResultCode.ACCOUNT_NOT_NULL);
        }
        return userList;
    }

    @Operation(summary = "通知系统消息配置新增-系统消息")
    @PostMapping("/addUserSysNoticeConfig")
    public ResponseVO<?> addUserSysNoticeConfig(@RequestBody @Valid UserSysNoticeConfigAddVO sysNoticeConfigAddVO) {
        return userNoticeConfigApi.addUserSysNoticeConfig(sysNoticeConfigAddVO);
    }

    @Operation(summary = "通知配置撤回通知")
    @PostMapping("/update")
    public ResponseVO<?> update(@RequestBody UserNoticeConfigGetVO userNoticeConfigGetVO) {
        return userNoticeConfigApi.updateUserNoticeConfig(userNoticeConfigGetVO);
    }

    @Operation(summary = "通知配置编辑")
    @PostMapping("/edit")
    public ResponseVO<Boolean> edit(@Valid UserNoticeConfigEditVO userNoticeConfigEditVO) {
        return userNoticeConfigApi.edit(userNoticeConfigEditVO);
    }

    @Operation(summary = "通知配置刪除")
    @PostMapping("/del/{id}")
    public ResponseVO<Boolean> del(@PathVariable("id") Long id) {
        return userNoticeConfigApi.del(id);
    }

    @Operation(summary = "点击查看更多会员")
    @PostMapping("/pageAccount")
    public ResponseVO<Page<UserNoticeTargetVO>> pageAccount(@RequestBody UserNoticeTargetGetVO userNoticeTargetGetVO) {
        return userNoticeConfigApi.pageAccount(userNoticeTargetGetVO);
    }

    @Operation(summary = "查看排序")
    @PostMapping("/sortNoticeSelect")
    public ResponseVO<List<NoticeSortSelectResponseVO>> sortNoticeSelect(@RequestBody SortNoticeSelectVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userNoticeConfigApi.sortNoticeSelect(vo);

    }

    @Operation(summary = "排序")
    @PostMapping("/sortNotice")
    public ResponseVO sortNotice(@RequestBody NoticeConfigResortVO noticeConfigResortVO) {

        return userNoticeConfigApi.sortNotice(noticeConfigResortVO);

    }

    @Operation(summary = "通知消息：会员/代理账号查询-excel模版导出")
    @GetMapping("/excelImport")
    public void excelImport(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员或代理或商务账号") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }


}

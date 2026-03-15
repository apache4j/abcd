package com.cloud.baowang.site.controller.userManualUpDown;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.activity.api.vo.free.AddFreeGameConfigDTO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.common.excel.freeGame.AddFreeGameConsumerListener;
import com.cloud.baowang.common.excel.freeGame.AddFreeGameReadExcelDTO;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountReadExcelDTO;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualAccountReadExcelDTO;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualDownAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualDownAccountReadExcelDTO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserManualDownRecordApi;
import com.cloud.baowang.wallet.api.api.UserManualUpApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpSubmitVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: kimi
 */
@Tag(name = "会员额度加额/减额相关")
@AllArgsConstructor
@RestController
@RequestMapping("/user-manual/api")
public class UserManualUpController {
    private final SystemParamApi systemParamApi;
    private final UserManualDownRecordApi userManualDownRecordApi;
    private final UserManualUpApi userManualUpApi;


    @Operation(summary = "会员人工加额发起")
    @PostMapping(value = "/submit")
    public ResponseVO<Boolean> submit(@Valid @RequestBody UserManualUpSubmitVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userManualUpApi.submit(vo, CurrReqUtils.getAccount());
    }


    @Operation(summary = "会员人工扣除额度发起")
    @PostMapping(value = "/saveManualDown")
    public ResponseVO<Boolean> saveManualDown(@Valid @RequestBody UserManualDownSubmitVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userManualDownRecordApi.saveManualDown(vo);
    }

    @Operation(summary = "会员账号查询-输入框查询")
    @PostMapping(value = "/getUserBalance")
    public ResponseVO<GetUserBalanceVO> getUserBalance(@Valid @RequestBody GetUserBalanceQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUserAccount(vo.getUserAccount().replaceAll(CommonConstant.SEMICOLON, CommonConstant.COMMA));
        return userManualUpApi.getUserBalance(vo);
    }

    @Operation(summary = "会员账号查询加额-excel模版导出")
    @GetMapping("excelImport")
    public void excelImport(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员人工加额") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @Operation(summary = "会员账号查询减额-excel模版导出")
    @GetMapping("excelImportDown")
    public void excelImportDown(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员人工减额") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @PostMapping("getUserBalanceByExcel")
    @Operation(summary = "会员账号查询-excel上传查询 加额")
    public ResponseVO<UserManualAccountResponseVO> getUserBalanceByExcel(MultipartFile file) {
        try {
            List<UserManualAccountReadExcelDTO> userList = new ArrayList<>();
            ExcelUtil.read(file.getInputStream(), UserManualAccountReadExcelDTO.class, new UserManualAccountExcelConsumerListener(userList)).sheet().doRead();
            if (CollectionUtil.isNotEmpty(userList)) {
                List<UserManualAccountResultVO> list =  ConvertUtil.entityListToModelList(userList, UserManualAccountResultVO.class);
                return userManualUpApi.checkUpUserAccountInfo(list);


            }
            return ResponseVO.success();
        } catch (IOException e) {
            throw new BaowangDefaultException("解析excel失败");
        }
    }

    @PostMapping("getUserBalanceByExcelForDown")
    @Operation(summary = "会员账号查询-excel上传查询 减额")
    public ResponseVO<UserManualDownAccountResponseVO> getUserBalanceByExcelForDown(MultipartFile file) {
        try {
            List<UserManualDownAccountReadExcelDTO> userList = new ArrayList<>();
            ExcelUtil.read(file.getInputStream(), UserManualDownAccountReadExcelDTO.class, new UserManualDownAccountExcelConsumerListener(userList)).sheet().doRead();
            if (CollectionUtil.isNotEmpty(userList)) {

                List<UserManualDownAccountResultVO> list =  ConvertUtil.entityListToModelList(userList, UserManualDownAccountResultVO.class);
                return userManualUpApi.checkDownUserAccountInfo(list);

            }
            return ResponseVO.success();
        } catch (IOException e) {
            throw new BaowangDefaultException("解析excel失败");
        }
    }

    @GetMapping("getDownBox")
    @Operation(summary = "获取发起申请下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getActivityTemplateDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.ACTIVITY_TEMPLATE);
        param.add(CommonConstant.MANUAL_ADJUST_TYPE);
        return systemParamApi.getSystemParamsByList(param);
    }


    @PostMapping("getUserBalanceByExcelByLabels")
    @Operation(summary = "会员账号查询-excel上传查询")
    public ResponseVO<GetUserBalanceVO> getUserBalanceByExcelByLabels(MultipartFile file) {
        try {
            if (!file.getOriginalFilename().contains("xlsx")){
                throw new BaowangDefaultException("上传失败");
            }
            List<String> userList = new ArrayList<>();
            ExcelUtil.read(file.getInputStream(), UserAccountReadExcelDTO.class, new UserAccountExcelConsumerListener(userList::add)).sheet().doRead();
            if (CollectionUtil.isNotEmpty(userList)) {
                GetUserBalanceVO queryVO = new GetUserBalanceVO();
                String userAccounts = String.join(CommonConstant.COMMA, userList);
                queryVO.setUserAccounts(userAccounts);
                return ResponseVO.success(queryVO);
            }
            return ResponseVO.success();
        } catch (Exception e) {
           throw new BaowangDefaultException("上传失败");
        }
    }

}

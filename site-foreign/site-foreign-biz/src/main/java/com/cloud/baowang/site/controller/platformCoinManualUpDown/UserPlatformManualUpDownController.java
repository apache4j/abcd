package com.cloud.baowang.site.controller.platformCoinManualUpDown;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualAccountReadExcelDTO;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualDownAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserManualDownAccountReadExcelDTO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinManualUpDownApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualDownSubmitVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpSubmitVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: qiqi
 */
@Tag(name = "资金-资金调整-会员平台币调整")
@AllArgsConstructor
@RestController
@RequestMapping("/user-platform-coin-manual/api")
public class UserPlatformManualUpDownController {
    private final SystemParamApi systemParamApi;

    private final UserPlatformCoinManualUpDownApi userPlatformCoinManualUpDownApi;


    @Operation(summary = "平台币上分提交")
    @PostMapping(value = "/savePlatformCoinManualUp")
    public ResponseVO<Boolean> submit(@Valid @RequestBody UserPlatformCoinManualUpSubmitVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return userPlatformCoinManualUpDownApi.savePlatformCoinManualUp(vo);
    }


    @Operation(summary = "平台币下分提交")
    @PostMapping(value = "/savePlatformCoinManualDown")
    public ResponseVO<Boolean> saveManualDown(@Valid @RequestBody UserPlatformCoinManualDownSubmitVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userPlatformCoinManualUpDownApi.savePlatformCoinManualDown(vo);
    }

    @Operation(summary = "会员账号查询-输入框查询")
    @PostMapping(value = "/getUserBalance")
    public ResponseVO<GetUserBalanceVO> getUserBalance(@Valid @RequestBody GetUserBalanceQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setUserAccount(vo.getUserAccount().replaceAll(CommonConstant.SEMICOLON, CommonConstant.COMMA));
        return userPlatformCoinManualUpDownApi.getUserBalance(vo);
    }

    @Operation(summary = "平台币上分-excel模版导出")
    @GetMapping("excelImport")
    public void excelImport(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员平台币上分") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @Operation(summary = "平台币下分-excel模版导出")
    @GetMapping("excelImportDown")
    public void excelImportDown(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员平台币下分") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @PostMapping("getUserBalanceByExcel")
    @Operation(summary = "平台币上分-excel上传查询")
    public ResponseVO<UserManualAccountResponseVO> getUserBalanceByExcel(MultipartFile file) {
        try {
            List<UserManualAccountReadExcelDTO> userList = new ArrayList<>();
            ExcelUtil.read(file.getInputStream(), UserManualAccountReadExcelDTO.class, new UserManualAccountExcelConsumerListener(userList)).sheet().doRead();
            if (CollectionUtil.isNotEmpty(userList)) {
                List<UserManualAccountResultVO> list =  ConvertUtil.entityListToModelList(userList, UserManualAccountResultVO.class);
                return userPlatformCoinManualUpDownApi.checkUpUserAccountInfo(list);


            }
            return ResponseVO.success();
        } catch (IOException e) {
            throw new BaowangDefaultException("解析excel失败");
        }
    }

    @PostMapping("getUserBalanceByExcelForDown")
    @Operation(summary = "平台币下分-excel上传查询 减额")
    public ResponseVO<UserManualDownAccountResponseVO> getUserBalanceByExcelForDown(MultipartFile file) {
        try {
            List<UserManualDownAccountReadExcelDTO> userList = new ArrayList<>();
            ExcelUtil.read(file.getInputStream(), UserManualDownAccountReadExcelDTO.class, new UserManualDownAccountExcelConsumerListener(userList)).sheet().doRead();
            if (CollectionUtil.isNotEmpty(userList)) {

                List<UserManualDownAccountResultVO> list =  ConvertUtil.entityListToModelList(userList, UserManualDownAccountResultVO.class);
                return userPlatformCoinManualUpDownApi.checkDownUserAccountInfo(list);

            }
            return ResponseVO.success();
        } catch (IOException e) {
            throw new BaowangDefaultException("解析excel失败");
        }
    }

    @GetMapping("getDownBox")
    @Operation(summary = "获取平台币上分下分发起申请下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getActivityTemplateDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.ACTIVITY_TEMPLATE);
        param.add(CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_UP_TYPE);
        param.add(CommonConstant.PLATFORM_COIN_MANUAL_ADJUST_DOWN_TYPE);
        return systemParamApi.getSystemParamsByList(param);
    }



}

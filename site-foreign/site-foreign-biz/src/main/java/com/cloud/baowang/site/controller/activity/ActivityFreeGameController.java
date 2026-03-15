package com.cloud.baowang.site.controller.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityFreeGameApi;
import com.cloud.baowang.activity.api.api.ActivitySpinWheelApi;
import com.cloud.baowang.activity.api.vo.SiteActivityLotteryRecordReqVO;
import com.cloud.baowang.activity.api.vo.SiteActivityLotteryRecordRespVO;
import com.cloud.baowang.activity.api.vo.excel.SiteActivityLotteryRecordRespExcelVO;
import com.cloud.baowang.activity.api.vo.free.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.excel.ImportExcelTemplateUtils;
import com.cloud.baowang.site.vo.export.UserInfoExportVO;
import com.cloud.baowang.common.excel.freeGame.AddFreeGameConsumerListener;
import com.cloud.baowang.common.excel.freeGame.AddFreeGameReadExcelDTO;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountExcelConsumerListener;
import com.cloud.baowang.common.excel.userNotifyExcel.UserAccountReadExcelDTO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.play.api.api.third.PPGameApi;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.vo.pp.req.PPGameLimitReqVO;
import com.cloud.baowang.play.api.vo.pp.res.PPGameLimitResVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoRequestVO;
import com.cloud.baowang.play.api.vo.venue.GameInfoVO;
import com.cloud.baowang.play.api.vo.venue.SiteGameInfoVO;
import com.cloud.baowang.site.service.CommonService;
import com.cloud.baowang.site.vo.export.UserInFreeGameExportVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.freegame.GetUserInfoCurrencyReqVO;
import com.cloud.baowang.user.api.vo.freegame.GetUserInfoCurrencyRespVO;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Tag(name = "添加免费旋转活动")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/freeGame/api")
public class ActivityFreeGameController {

    private final ActivityFreeGameApi freeGameApi;

    private final MinioUploadApi minioUploadApi;

    private final PPGameApi ppGameApi;

    private final GameInfoApi gameInfoApi;

    private final UserInfoApi userInfoApi;

    private final CommonService commonService;

    @PostMapping("/freeGamePageList")
    @Operation(summary = "旋转次数获取/使用记录")
    public ResponseVO<Page<ActivityFreeGameRespVO>> freeGamePageList(@Valid @RequestBody FreeGameReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return freeGameApi.freeGamePageList(vo);
    }

    @PostMapping("/addFreeGameConfig")
    @Operation(summary = "添加旋转次数")
    public ResponseVO<?> addFreeGameConfig(FreeGameSubmitVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        FreeGameSubmitModifyVO modifyVO = new FreeGameSubmitModifyVO();
        BeanUtils.copyProperties(vo, modifyVO);
        modifyVO.setOperator(CurrReqUtils.getAccount());
        List<AddFreeGameConfigDTO> combinedAccounts = new ArrayList<>();
        if (vo.getFile() != null && !vo.getFile().isEmpty()) {
            combinedAccounts.addAll(userListOfParseExcel(vo.getFile()));
        }
        if (StringUtils.isNotBlank(vo.getUserAccounts())) {
            List<AddFreeGameConfigDTO> accounts = convertStringToList(vo.getUserAccounts(), vo.getAcquirerNum(), vo.getBetLimitAmount(), vo.getCurrency());
            combinedAccounts.addAll(accounts);
        }
        // 校验
        if (CollectionUtil.isEmpty(combinedAccounts)) {
            throw new BaowangDefaultException(ResultCode.ACCOUNT_NOT_NULL);
        }
        PPGameLimitReqVO req = new PPGameLimitReqVO();
        req.setGameIds(vo.getGameId());
        req.setCurrencies(vo.getCurrency());
        req.setVenueCode(vo.getVenueCode());
        req.setSiteCode(vo.getSiteCode());

        // 替换 combinedAccounts 为去重后的
        combinedAccounts = deduplicateAndValidate(combinedAccounts);
        modifyVO.setFileAccount(combinedAccounts.stream().distinct().collect(Collectors.toList()));
        // 校验是否是同一个货币
        GetUserInfoCurrencyReqVO checkUserCurrencyReqVO = new GetUserInfoCurrencyReqVO();
        String userAccounts = combinedAccounts.stream().map(AddFreeGameConfigDTO::getUserAccount).collect(Collectors.joining(";"));
        checkUserCurrencyReqVO.setUserAccounts(userAccounts);
        ResponseVO<GetUserInfoCurrencyRespVO> getUserInfoCurrencyRespVOResponseVO = checkUserCurrency(checkUserCurrencyReqVO);
        if (!getUserInfoCurrencyRespVOResponseVO.isOk()) {
            return getUserInfoCurrencyRespVOResponseVO;
        }
        if (!getUserInfoCurrencyRespVOResponseVO.getData().getIsSingleCurrency()) {
            return getUserInfoCurrencyRespVOResponseVO;
        }
        if (ObjectUtil.isEmpty(req.getCurrencies())) {
            req.setCurrencies(getUserInfoCurrencyRespVOResponseVO.getData().getCurrency());
        }
        ResponseVO<List<PPGameLimitResVO>> limitGameLine = ppGameApi.getLimitGameLine(req);
        if (!limitGameLine.isOk()) {
            throw new BaowangDefaultException(ResultCode.SERVER_DISABLE);
        }
        List<BigDecimal> betLimitAmountList = limitGameLine.getData().get(0).getCurrencyGameLimits().get(0).getBetPerLineScales().toJavaList(BigDecimal.class);
        for (AddFreeGameConfigDTO dto : combinedAccounts) {
            if (StringUtils.isBlank(dto.getUserAccount())) {
                throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
            }
            if (dto.getAcquireNum() == null || dto.getAcquireNum() <= 0) {
                throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_NO_EXCEPT);
            }
            if (dto.getBetLimitAmount() == null || dto.getBetLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_NO_EXCEPT);
            }
            boolean matched = betLimitAmountList.stream()
                    .anyMatch(limit -> limit.compareTo(dto.getBetLimitAmount()) == 0);
            if (!matched) {
                throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_LIMIT_AMOUNT);
            }


        }

        return freeGameApi.addFreeGameConfig(modifyVO);
    }

    public List<AddFreeGameConfigDTO> convertStringToList(String input, Integer num, BigDecimal betLimitAmount, String currency) {
        if (input == null || input.trim().isEmpty()) {
            return List.of(); // 返回空列表
        }

        // 使用逗号进行分割，并去除空格和空字符串
        return Arrays.stream(input.split("[,;]"))
                .map(String::trim)              // 去除每个元素的前后空格
                .filter(item -> !item.isEmpty()) // 过滤空字符串
                .map(e -> {
                    AddFreeGameConfigDTO dto = new AddFreeGameConfigDTO();
                    dto.setUserAccount(e);
                    dto.setAcquireNum(num);
                    dto.setBetLimitAmount(betLimitAmount);
                    dto.setCurrency(currency);
                    return dto;
                }).toList();  // 转换为列表

    }

    private List<AddFreeGameConfigDTO> userListOfParseExcel(MultipartFile file) {
        List<AddFreeGameReadExcelDTO> userList = new ArrayList<>();
        try {
            ExcelUtil.read(file.getInputStream(), AddFreeGameReadExcelDTO.class, new AddFreeGameConsumerListener(userList)).sheet().doRead();
        } catch (Exception e) {
            log.error("新增用户通知解析会员账号excel异常,error:", e);
            throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_NO);
        }
        if (userList.size() == 0) {
            throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_NO);
        }
        if (userList.size() > 2000) {
            throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_LIMIT);
        }
        return ConvertUtil.entityListToModelList(userList, AddFreeGameConfigDTO.class);
    }


    @Operation(summary = "免费旋转配置：会员添加旋转配置-excel模版导出")
    @GetMapping("/excelImport")
    public void excelImport(HttpServletResponse response, @RequestParam(value = "fileName", required = false, defaultValue = "会员旋转配置") String fileName) {
        ImportExcelTemplateUtils.importExcelTemplateByCode(response, fileName);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody FreeGameReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::freeGamePageList::userInfo::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> responseVO = freeGameApi.getTotalCount(vo);
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
                UserInFreeGameExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(freeGamePageList(param).getData().getRecords(), UserInFreeGameExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_FREE_GAME)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    @PostMapping("/siteGameInfoList")
    @Operation(summary = "获取游戏名称")
    public ResponseVO<List<SiteGameInfoVO>> siteGameInfoList(@Valid @RequestBody GameInfoRequestVO vo) {
        return gameInfoApi.getConfigSiteGameInfoList(vo);
    }

    @PostMapping("/getLimitGameLine")
    @Operation(summary = "限注金额")
    public ResponseVO<List<PPGameLimitResVO>> getLimitGameLine(@Valid @RequestBody PPGameLimitReqVO req) {
        req.setSiteCode(CurrReqUtils.getSiteCode());

        return ppGameApi.getLimitGameLine(req);
    }

    @PostMapping("/checkFileUserCurrency")
    @Operation(summary = "校验指定用户File是否为同一种货币")
    public ResponseVO<GetUserInfoCurrencyRespVO> checkFileUserCurrency(FreeGameSubmitVO req) {
        req.setSiteCode(CurrReqUtils.getSiteCode());
        List<AddFreeGameConfigDTO> combinedAccounts = new ArrayList<>();
        if (req.getFile() != null && !req.getFile().isEmpty()) {
            combinedAccounts.addAll(userListOfParseExcel(req.getFile()));
        }
        if (CollectionUtil.isEmpty(combinedAccounts)) {
            throw new BaowangDefaultException(ResultCode.ACCOUNT_NOT_NULL);
        }
        for (AddFreeGameConfigDTO dto : combinedAccounts) {
            if (StringUtils.isBlank(dto.getUserAccount())) {
                throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
            }
            if (dto.getAcquireNum() == null || dto.getAcquireNum() <= 0) {
                throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_NO_EXCEPT);
            }
            if (dto.getBetLimitAmount() == null || dto.getBetLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.FILE_ACCOUNT_WRONG_NO_EXCEPT);
            }

        }
        // 校验如果有重复账号，如果账号一致，且要判断配置是否一致，如果不一致，提示，如果一致，则使用一个
        // 加入校验和去重逻辑

        // 替换 combinedAccounts 为去重后的
        combinedAccounts = deduplicateAndValidate(combinedAccounts);
        // 校验是否是同一个货币
        GetUserInfoCurrencyReqVO checkUserCurrencyReqVO = new GetUserInfoCurrencyReqVO();
        String userAccounts = combinedAccounts.stream().map(AddFreeGameConfigDTO::getUserAccount).collect(Collectors.joining(";"));
        checkUserCurrencyReqVO.setUserAccounts(userAccounts);
        return checkUserCurrency(checkUserCurrencyReqVO);

    }

    /**
     * 校验并去重免费旋转配置
     *
     * @param originalList 原始账号配置列表
     * @return 去重后的账号配置列表
     */
    public List<AddFreeGameConfigDTO> deduplicateAndValidate(List<AddFreeGameConfigDTO> originalList) {
        if (CollectionUtil.isEmpty(originalList)) {
            throw new BaowangDefaultException(ResultCode.ACCOUNT_NOT_NULL);
        }

        Map<String, AddFreeGameConfigDTO> accountConfigMap = new HashMap<>();

        for (AddFreeGameConfigDTO dto : originalList) {
            String account = dto.getUserAccount();
            if (StrUtil.isBlank(account)) {
                throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
            }

            if (accountConfigMap.containsKey(account)) {
                AddFreeGameConfigDTO existing = accountConfigMap.get(account);
                boolean configEqual = Objects.equals(existing.getCurrency(), dto.getCurrency()) &&
                        Objects.equals(existing.getBetLimitAmount(), dto.getBetLimitAmount()) &&
                        Objects.equals(existing.getAcquireNum(), dto.getAcquireNum());

                if (!configEqual) {
                    throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
                }
            } else {
                accountConfigMap.put(account, dto);
            }
        }

        return new ArrayList<>(accountConfigMap.values());
    }

    @PostMapping("/checkUserCurrency")
    @Operation(summary = "校验指定用户是否为同一种货币")
    public ResponseVO<GetUserInfoCurrencyRespVO> checkUserCurrency(@Valid @RequestBody GetUserInfoCurrencyReqVO req) {
        req.setSiteCode(CurrReqUtils.getSiteCode());
        if (StringUtils.isBlank(req.getUserAccounts())) {
            throw new BaowangDefaultException(ResultCode.ACCOUNT_NOT_NULL);
        }
        return userInfoApi.checkUserCurrency(req);
    }


    @Operation(summary = "下拉框-通用")
    @PostMapping(value = "/getDownBoxForPP")
    public ResponseVO<List<CodeValueVO>> getDownBoxForPP() {
        String type = "venue_code";
        Map<String, List<CodeValueVO>> venueCode = commonService.getSystemParamsByList(List.of(type));
        List<CodeValueVO> list = venueCode.get(type);

        List<CodeValueVO> result = list.stream().filter(e -> e.getCode().equals("PP")).collect(Collectors.toList());
        return ResponseVO.success(result);
    }

}

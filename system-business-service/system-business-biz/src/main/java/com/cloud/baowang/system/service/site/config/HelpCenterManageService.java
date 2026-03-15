package com.cloud.baowang.system.service.site.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.vo.PwaVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.HelpCenterOptionEnum;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.ContactInfoVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.agreement.BusinessBasicVO;
import com.cloud.baowang.system.api.vo.site.agreement.i18nMessagesVO;
import com.cloud.baowang.system.api.vo.site.tutorial.OptionTypeConfigVO;
import com.cloud.baowang.system.api.vo.site.tutorial.SiteBasicVo;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.po.site.config.SiteDownloadConfigPO;
import com.cloud.baowang.system.po.site.config.UserAgreementPO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.site.agreement.UserAgreementRepository;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.service.tutorial.TutorialCategoryService;
import com.cloud.baowang.system.service.tutorial.TutorialClassService;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HelpCenterManageService extends ServiceImpl<UserAgreementRepository, UserAgreementPO> {

    private final UserAgreementRepository repository;
    private final I18nApi i18nApi;
    private final SystemParamApi systemParamApi;

    private final MinioFileService fileService;

    private final TutorialCategoryService tutorialCategoryService;
    private final TutorialClassService tutorialClassService;


    private final LanguageManagerService languageService;

    private final MinioUploadApi minioUploadApi;
    private final SiteRepository siteRepository;

    private final SiteDownloadConfigService siteDownloadConfigService;

    private final SiteApi siteApi;


    public void updateBasicInfo(String siteCode,Integer code) {
        LambdaUpdateWrapper<UserAgreementPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAgreementPO::getSiteCode,siteCode);
        updateWrapper.eq(UserAgreementPO::getCode,code);
        updateWrapper.set(UserAgreementPO::getUpdater,CurrReqUtils.getAccount());
        updateWrapper.set(UserAgreementPO::getUpdatedTime,System.currentTimeMillis());
        update(updateWrapper);
    }

    public UserAgreementPO getOne(String id) {
        return UserAgreementPO.builder().siteCode(CurrReqUtils.getSiteCode())
                .updatedTime(System.currentTimeMillis())
                .id(id)
                .updater(CurrReqUtils.getAccount())
                .build();
    }

    public void saveOrUpdateBySiteCode(UserAgreementPO po) {
        // 根据 site_code 查询是否存在
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, po.getSiteCode());
        UserAgreementPO existingAgreement = this.getOne(queryWrapper);

        if (existingAgreement != null) {
            po.setId(existingAgreement.getId());
            repository.updateById(po);
        } else {
            repository.insert(po);
        }
    }

    public void checkParamLengthLimit(String param){
        if (param.length() > 100){
            throw new BaowangDefaultException(ResultCode.MAX_LENGTH);
        }
    }

    public boolean isHandicap(String siteCode){
        ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(siteCode);
        if (!responseVO.isOk()){
            return false;
        }
        SiteVO siteVO = responseVO.getData();
        Integer handicapMode = siteVO.getHandicapMode();
        if (handicapMode == null || handicapMode == 0){
            return false;
        }
        return true;
    }


    public ResponseVO<Boolean> addConfig(i18nMessagesVO vo) {
        Integer code = vo.getCode();
        switch (code) {
            case 12 -> {//经营地址
                UserAgreementPO po = getOne(vo.getId());
                po.setOptionValue(vo.getBusinessAddress());
                this.updateById(po);
            }
            case 1 -> {//关于我们
                UserAgreementPO po = getOne(vo.getId());
                String aboutUs = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ABOUT_US.getCode());
                po.setOptionValue(aboutUs);
                i18nApi.insert(Map.of(aboutUs, vo.getI18nMessages()));
                this.updateById(po);
            }
            case 3 -> {//隐私政策
                UserAgreementPO po = getOne(vo.getId());
                String privacyPolicy = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.PRIVACY_POLICY.getCode());
                po.setOptionValue(privacyPolicy);
                i18nApi.insert(Map.of(privacyPolicy, vo.getI18nMessages()));
                this.updateById(po);
            }
            case 4 -> {//规则与条款
                UserAgreementPO po = getOne(vo.getId());
                String termsCondition = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TERMS_CONDITION.getCode());
                po.setOptionValue(termsCondition);
                i18nApi.insert(Map.of(termsCondition, vo.getI18nMessages()));
                this.updateById(po);
            }
            case 5 -> {//联系我们
                UserAgreementPO po = getOne(vo.getId());
                List<I18nMsgFrontVO> helpCenterI18List = vo.getI18nMessages();
                for (I18nMsgFrontVO i18nMsgFrontVO : helpCenterI18List) {
                    checkParamLengthLimit(i18nMsgFrontVO.getMessage());
                    if (i18nMsgFrontVO.getMessageKey().equals(CommonConstant.COMPLAINT_EMAIL)) {
                        po.setOptionValue(i18nMsgFrontVO.getMessage());
                    }
                    if (i18nMsgFrontVO.getMessageKey().equals(CommonConstant.CUSTOMER_SERVICE_EMAIL)) {
                        po.setOptionValueExtend(i18nMsgFrontVO.getMessage());
                    }
                }
                po.setTelegram(vo.getTelegram());
                po.setSkype(vo.getSkype());
                this.updateById(po);
            }
            case 6 -> {//ios下载
                UserAgreementPO po = getOne(vo.getId());
                String iosUrl = vo.getI18nMessages().get(0).getMessage();
                po.setOptionValue(iosUrl);
                this.updateById(po);
            }
            case 7 -> {//安卓下载
                UserAgreementPO po = getOne(vo.getId());
                String androidUrl = vo.getI18nMessages().get(0).getMessage();
                po.setOptionValue(androidUrl);
                this.updateById(po);
            }
            case 8 -> {//底栏合规监管
                UserAgreementPO po = getOne(vo.getId());
                String compliance = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.COMPLIANCE_REGULATION.getCode());
                po.setOptionValue(compliance);
                i18nApi.insert(Map.of(compliance, vo.getI18nMessages()));
                this.updateById(po);
            }
            case 9 -> {//用户协议
                UserAgreementPO po = getOne(vo.getId());
                String agreement = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.USER_AGREEMENT.getCode());
                po.setOptionValue(agreement);
                i18nApi.insert(Map.of(agreement, vo.getI18nMessages()));
                this.updateById(po);
            }

            case 10 -> {//首页未登陆背景图
                UserAgreementPO po = getOne(vo.getId());
                List<List<I18nMsgFrontVO>> i18nFileList = vo.getI18nFileUrl();
                if (!i18nFileList.isEmpty()) {
                    for (List<I18nMsgFrontVO> i18nMsgFrontVOList : i18nFileList) {
                       if (i18nMsgFrontVOList.get(0).getMessageKey().equals(CommonConstant.UN_LOGIN_H5_URL)) {
                           String h5I18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.WITHOUT_LOGIN_PICTURE.getCode());
                           po.setOptionValue(h5I18nCode);
                           i18nApi.insert(Map.of(h5I18nCode, i18nMsgFrontVOList));
                       }else if (i18nMsgFrontVOList.get(0).getMessageKey().equals(CommonConstant.UN_LOGIN_PC_URL)){
                           String pcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.WITHOUT_LOGIN_PICTURE.getCode());
                           po.setOptionValueExtend(pcI18nCode);
                           i18nApi.insert(Map.of(pcI18nCode, i18nMsgFrontVOList));
                       }
                    }
                }
                this.updateById(po);
                RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_UN_BANNER));
            }

        }
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_HELP_CENTER_OPTION));
        return ResponseVO.success(Boolean.TRUE);
    }



    public ResponseVO<i18nMessagesVO> showUnLoginPic(){
        i18nMessagesVO result = new i18nMessagesVO();
        List<I18nMsgFrontVO> i18nMessages = new ArrayList<>();
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());
        queryWrapper.eq(UserAgreementPO::getCode, HelpCenterOptionEnum.WITHOUT_LOGIN_PICTURE.getCode());
        UserAgreementPO existPO = this.getOne(queryWrapper);
        String pcUrl = existPO.getOptionValueExtend();
        String h5Url = existPO.getOptionValue();
        i18nMessages.add(I18nMsgFrontVO.builder().messageKey(CommonConstant.UN_LOGIN_H5_URL).message(h5Url).build());
        i18nMessages.add(I18nMsgFrontVO.builder().messageKey(CommonConstant.UN_LOGIN_PC_URL).message(pcUrl).build());
        result.setI18nMessages(i18nMessages);
        return ResponseVO.success(result);
    }

    public ResponseVO<i18nMessagesVO> getConfig(Integer code)  {
        i18nMessagesVO result = new i18nMessagesVO();
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());

        ResponseVO<List<I18NMessageDTO>> messageByKey = ResponseVO.success(new ArrayList<>());
        switch (code) {
            case 12 ->{
                queryWrapper.eq(UserAgreementPO::getCode, code);
                UserAgreementPO existingOption = this.getOne(queryWrapper);
                result.setBusinessAddress(existingOption.getOptionValue());
            }
            case 1, 3, 4, 8, 9 -> {
                queryWrapper.eq(UserAgreementPO::getCode, code);
                UserAgreementPO existingOption = this.getOne(queryWrapper);
                if (existingOption.getOptionValue() != null) {
                    messageByKey = i18nApi.getMessageByKey(existingOption.getOptionValue());
                }
            }
            case 5 -> {
                queryWrapper.eq(UserAgreementPO::getCode, code);
                UserAgreementPO existingOption = this.getOne(queryWrapper);
                if (existingOption.getOptionValue() != null && existingOption.getOptionValueExtend() != null) {
                    List<I18NMessageDTO> tempList = new ArrayList<>();
                    tempList.add(I18NMessageDTO.builder().messageKey(CommonConstant.COMPLAINT_EMAIL).message(existingOption.getOptionValue()).build());
                    tempList.add(I18NMessageDTO.builder().messageKey(CommonConstant.CUSTOMER_SERVICE_EMAIL).message(existingOption.getOptionValueExtend()).build());
                    messageByKey.setData(tempList);
                }
                result.setTelegram(existingOption.getTelegram());
                result.setSkype(existingOption.getSkype());
            }
            case 6 -> {
                queryWrapper.eq(UserAgreementPO::getCode, code);
                UserAgreementPO existingOption = this.getOne(queryWrapper);
                if (existingOption.getOptionValue() != null) {
                    List<I18NMessageDTO> tempList = new ArrayList<>();
                    tempList.add(I18NMessageDTO.builder().messageKey(CommonConstant.IOS_DOWNLOAD_URL).message(existingOption.getOptionValue()).build());
                    messageByKey.setData(tempList);
                }
            }
            case 7 -> {
                queryWrapper.eq(UserAgreementPO::getCode, code);
                UserAgreementPO existingOption = this.getOne(queryWrapper);
                if (existingOption.getOptionValue() != null) {
                    List<I18NMessageDTO> tempList = new ArrayList<>();
                    tempList.add(I18NMessageDTO.builder().messageKey(CommonConstant.ANDROID_DOWNLOAD_URL).message(existingOption.getOptionValue()).build());
                    messageByKey.setData(tempList);
                }
            }
            case 10 -> {
                queryWrapper.eq(UserAgreementPO::getCode, code);
                UserAgreementPO existingOption = this.getOne(queryWrapper);
                if (existingOption.getOptionValue() != null && existingOption.getOptionValueExtend() != null) {
                    String minioDomain = fileService.getMinioDomain();
                    String pcUrl = existingOption.getOptionValueExtend();
                    String h5Url = existingOption.getOptionValue();
                    List<I18NMessageDTO> h5Data = i18nApi.getMessageByKey(h5Url).getData();
                    List<I18NMessageDTO> pcData = i18nApi.getMessageByKey(pcUrl).getData();
                    List<I18nMsgFrontVO> h5TempData = new ArrayList<>();
                    List<I18nMsgFrontVO> pcTempData = new ArrayList<>();
                    if (h5Data.isEmpty() && pcData.isEmpty()) {
                        return ResponseVO.success(result);
                    }
                    try {
                        h5TempData = ConvertUtil.convertListToList(h5Data,new I18nMsgFrontVO());
                        pcTempData =  ConvertUtil.convertListToList(pcData,new I18nMsgFrontVO());
                    } catch (Exception ignored) {}
                        h5TempData.forEach(item -> {
                        item.setMessageKey(CommonConstant.UN_LOGIN_H5_URL);
                        item.setMessageFileUrl(minioDomain+"/"+item.getMessage());
                    });
                        pcTempData.forEach(item -> {
                        item.setMessageKey(CommonConstant.UN_LOGIN_PC_URL);
                        item.setMessageFileUrl(minioDomain+"/"+item.getMessage());
                    });
                    result.setI18nFileUrl(List.of(h5TempData,pcTempData));
                    result.setCode(code);

                    return ResponseVO.success(result);
                }
            }
            default -> throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        try {
            List<I18nMsgFrontVO> rspI18nList = ConvertUtil.convertListToList(messageByKey.getData(), new I18nMsgFrontVO());
            result.setI18nMessages(rspI18nList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        result.setCode(code);
        return ResponseVO.success(result);
    }

    public SiteBasicVo buildSiteBasicVO(UserAgreementPO po) {
        SiteBasicVo result = new SiteBasicVo();
        Integer code = po.getCode();
        SiteDownloadConfigPO siteDownloadConfig = siteDownloadConfigService.getSiteDownloadConfig(po.getSiteCode());
        switch (code) {
            case 1, 3, 4, 8, 9 -> {
                if (po.getOptionValue() != null && po.getOptionValue().startsWith("BIZ")) {
                    List<I18NMessageDTO> data = i18nApi.getMessageByKey(po.getOptionValue()).getData();
                    if (!data.isEmpty()) {
                        for (I18NMessageDTO i18nMessageDTO : data) {
                            String language = CurrReqUtils.getLanguage();
                            if (i18nMessageDTO.getLanguage().equals(language)) {
                                result.setValueDetail(i18nMessageDTO.getMessage());
                            }
                        }
                    }
                }

            }
            case 12 ->{
                result.setValueDetail(po.getOptionValue());
            }
            case 5 -> {
                //联系我们
                result.setValueDetail(po.getOptionValue());
                result.setValueDetailExtend(po.getOptionValueExtend());
            }
            case 6, 7 -> {
                if (siteDownloadConfig != null ) {
                    String[] urlList = siteDownloadConfig.getBanner().split(CommonConstant.COMMA);
                    StringBuilder picFullUrlBuilder = new StringBuilder();
                    for (int i = 0; i < urlList.length; i++) {
                        String url = urlList[i];
                        List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
                        if (!data.isEmpty()) {
                            for (I18NMessageDTO i18nMessageDTO : data) {
                                String language = CurrReqUtils.getLanguage();
                                if (i18nMessageDTO.getLanguage().equals(language)) {
                                    String message = i18nMessageDTO.getMessage();
                                    String minioDomain = fileService.getMinioDomain();
                                    if (i < urlList.length - 1) {
                                        picFullUrlBuilder.append(minioDomain).append("/").append(message).append(CommonConstant.COMMA);
                                    } else {
                                        picFullUrlBuilder.append(minioDomain).append("/").append(message);
                                    }
                                }
                            }
                        }
                    }
                    if (siteDownloadConfig .getJumpType().equals(CommonConstant.business_one_str)) {
                        //安装包
                        if (code == 6){
                            result.setValueDetail(siteDownloadConfig.getIosDownloadUrl());
                        }else {
                            result.setValueDetail(siteDownloadConfig.getAndroidDownloadUrl());
                        }
                    } else if (siteDownloadConfig .getJumpType().equals(CommonConstant.business_two_str)) {
                        //跳转链接
                        result.setValueDetail(siteDownloadConfig.getDomainUrl());
                    }
                    result.setValueDetailExtend(picFullUrlBuilder.toString());
                }
            }

        }
        result.setValue(po.getOptionType());
        result.setCode(String.valueOf(code));
        return result;
    }

    public ResponseVO<List<SiteBasicVo>> getSiteBasicInfo() {
        initOptionTypeInfo();
        String optionKey = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_HELP_CENTER_OPTION);
        List<SiteBasicVo> list = RedisUtil.getList(optionKey);
        if (CollectionUtil.isNotEmpty(list)) {
            return ResponseVO.success(list);
        }
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());
        queryWrapper.ne(UserAgreementPO::getCode, CommonConstant.business_ten);
        queryWrapper.ne(UserAgreementPO::getCode, CommonConstant.business_eleven);
        List<UserAgreementPO> poList = repository.selectList(queryWrapper);
        List<SiteBasicVo> result = new ArrayList<>();
        if (!poList.isEmpty()) {
            poList.stream().forEach(po -> {
                result.add(buildSiteBasicVO(po));
            });
        }
        //RedisUtil.setList(optionKey, result);
        return ResponseVO.success(result);
    }

    /**
     * 分页
     *
     * @param pageVO
     * @return
     */
    public Page<OptionTypeConfigVO> getOptionPage(PageVO pageVO) {
        initOptionTypeInfo();
        Page<UserAgreementPO> result = new Page<>(pageVO.getPageNumber(), pageVO.getPageSize());
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());
        queryWrapper.ne(UserAgreementPO::getCode, CommonConstant.business_two);
        queryWrapper.ne(UserAgreementPO::getCode, CommonConstant.business_six);
        queryWrapper.ne(UserAgreementPO::getCode, CommonConstant.business_seven);
        Page<UserAgreementPO> poPage = repository.selectPage(result, queryWrapper);
        return ConvertUtil.toConverPage(poPage.convert(item -> {
            return BeanUtil.copyProperties(item, OptionTypeConfigVO.class);
        }));
    }

    public void insertOptionTypeInfo(int code){
        List<String> param = List.of(CommonConstant.HELP_CENTER_OPTION);
        Map<String, List<CodeValueVO>> data = systemParamApi.getSystemParamsByList(param).getData();
        List<CodeValueVO> optionI18nList = data.get(CommonConstant.HELP_CENTER_OPTION);
        Optional<CodeValueVO> optional = optionI18nList.stream()
                .filter(option -> option.getCode().equals(String.valueOf(code)))
                .findFirst();
        UserAgreementPO.UserAgreementPOBuilder builder = UserAgreementPO.builder();
        optional.ifPresent(option -> {
            builder.siteCode(CurrReqUtils.getSiteCode())
                    .optionType(option.getValue())
                    .code(Integer.valueOf(option.getCode()))
                    .build();
        });
        this.save(builder.build());
    }

    public void initOptionTypeInfo() {
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());
        Long count = repository.selectCount(queryWrapper);
        if (count > 0) {
            queryWrapper.eq(UserAgreementPO::getCode, CommonConstant.business_twelve);
            UserAgreementPO po = repository.selectOne(queryWrapper);
            if (po == null) {
                RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_HELP_CENTER_OPTION));
                insertOptionTypeInfo(CommonConstant.business_twelve);
                return;
            }
            return;
        }
        List<String> param = List.of(CommonConstant.HELP_CENTER_OPTION);
        Map<String, List<CodeValueVO>> data = systemParamApi.getSystemParamsByList(param).getData();
        List<CodeValueVO> optionI18nList = data.get(CommonConstant.HELP_CENTER_OPTION);
        List<UserAgreementPO> poList = new ArrayList<>();
        optionI18nList.forEach(item -> {
            poList.add(UserAgreementPO.builder().siteCode(CurrReqUtils.getSiteCode())
                    .optionType(item.getValue())
                    .code(Integer.valueOf(item.getCode()))
                    .build());
        });
        this.saveBatch(poList);
    }

    public ResponseVO<List<TutorialClientShowRspVO>> getHelpCenterInfo() {
        //教程
        List<TutorialClientShowVO> categoryList = tutorialCategoryService.getCategoryList();
        List<TutorialClientShowRspVO> rspList = new ArrayList<>();
        for (TutorialClientShowVO categoryVO : categoryList) {
            List<TutorialClientShowVO> classList = tutorialClassService.getClassList(categoryVO.getId());
            if (CollectionUtil.isNotEmpty(classList)) {
                TutorialClientShowRspVO resultVO = new TutorialClientShowRspVO();
                resultVO.setId(categoryVO.getId());
                resultVO.setName(categoryVO.getName());
                resultVO.setIcon(categoryVO.getIcon());
                resultVO.setSubset(classList);
                rspList.add(resultVO);
            }
        }
        //信息配置
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());
        queryWrapper.in(UserAgreementPO::getCode, CommonConstant.business_one, CommonConstant.business_three, CommonConstant.business_four, CommonConstant.business_five);
        List<UserAgreementPO> poList = repository.selectList(queryWrapper);
        if (!poList.isEmpty()) {
            poList.forEach(po -> {
                TutorialClientShowRspVO vo = new TutorialClientShowRspVO();
                vo.setName(po.getOptionType());
                vo.setValue(po.getOptionValue());
                vo.setCode(po.getCode());
                if (po.getCode().equals(CommonConstant.business_five)) {
                    vo.setValueExtends(po.getOptionValueExtend() == null ? "" : po.getOptionValueExtend());
                }
                rspList.add(vo);
            });
        }
        return ResponseVO.success(rspList);
    }

    //放到siteDownloadConfigService
    public ResponseVO<Boolean> addDownloadInfo(i18nMessagesVO vo) {
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        String siteCode=CurrReqUtils.getSiteCode();
        queryWrapper.eq(UserAgreementPO::getSiteCode, siteCode);
        queryWrapper.in(UserAgreementPO::getCode, CommonConstant.business_six, CommonConstant.business_seven, CommonConstant.business_eleven);
        List<UserAgreementPO> poList = repository.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(poList)) {
            for (UserAgreementPO po : poList) {
                if (po.getCode().equals(CommonConstant.business_six)) {
                    //ios
                    String iosDownloadUrl = vo.getIosDownloadUrl();
                    po.setOptionValue(iosDownloadUrl);
                    po.setUpdater(CurrReqUtils.getAccount());
                    po.setOptionValueExtend(vo.getIcon());
                    this.saveOrUpdate(po);
                    //initPwaFile(siteCode);
                } else if (po.getCode().equals(CommonConstant.business_seven)) {
                    //android
                    String androidDownloadUrl = vo.getAndroidDownloadUrl();
                    po.setOptionValue(androidDownloadUrl);
                    po.setUpdater(CurrReqUtils.getAccount());
                    po.setOptionValueExtend(vo.getIcon());
                    this.saveOrUpdate(po);
                } else if (po.getCode().equals(CommonConstant.business_eleven)) {
                    //图片地址国际化
                    List<List<I18nMsgFrontVO>> i18nFileUrl = vo.getI18nFileUrl();
                    ArrayList<String> urlList = new ArrayList<>();
                    for (List<I18nMsgFrontVO> fileUrl : i18nFileUrl) {
                        String downloadUrl = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.IOS_ANDROID_DOWNLOAD_URL.getCode());
                        i18nApi.insert(Map.of(downloadUrl, fileUrl));
                        urlList.add(downloadUrl);
                    }
                    po.setOptionValue(String.join(CommonConstant.COMMA, urlList));
                    po.setUpdatedTime(System.currentTimeMillis());
                    po.setUpdater(CurrReqUtils.getAccount());
                    this.saveOrUpdate(po);
                }
            }
        }

        return ResponseVO.success(true);
    }

    //放到siteDownloadConfigService
    public ResponseVO<i18nMessagesVO> getDownloadInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, siteCode);
        queryWrapper.in(UserAgreementPO::getCode, CommonConstant.business_six, CommonConstant.business_seven, CommonConstant.business_eleven);
        List<UserAgreementPO> poList = repository.selectList(queryWrapper);
        i18nMessagesVO result = new i18nMessagesVO();
        for (UserAgreementPO po : poList) {
            if (po.getCode().equals(CommonConstant.business_six)) {
                result.setIosDownloadUrl(po.getOptionValue());
                result.setIcon(po.getOptionValueExtend());
                result.setIconFileUrl(fileService.getMinioDomain()+"/"+po.getOptionValueExtend());
            } else if (po.getCode().equals(CommonConstant.business_seven)) {
                result.setAndroidDownloadUrl(po.getOptionValue());
            } else if (po.getCode().equals(CommonConstant.business_eleven)) {
                List<List<I18nMsgFrontVO>> urlResultList = new ArrayList<>();

                String i18nOption = po.getOptionValue();
                if (StringUtils.isNotBlank(i18nOption)) {

                    String[] urlList = i18nOption.split(CommonConstant.COMMA);
                    String minioDomain = fileService.getMinioDomain();
                    List<LanguageManagerListVO> languages = languageService.languageByList(siteCode).getData();
                    List<String> languageCodes = languages.stream().map(LanguageManagerListVO::getCode).toList();
                    for (String url : urlList) {
                        List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
                        Set<String> i18LanguageCode = data.stream().map(I18NMessageDTO::getLanguage).collect(Collectors.toSet());
                        if (CollectionUtil.isNotEmpty(data)) {
                            List<I18nMsgFrontVO> i18nUrlList = new ArrayList<>();
                            data.forEach(item -> {
                                if (languageCodes.contains(item.getLanguage())) {
                                    I18nMsgFrontVO vo = new I18nMsgFrontVO();
                                    vo.setLanguage(item.getLanguage());
                                    vo.setMessage(item.getMessage());
                                    String fullUrl = minioDomain + "/" + item.getMessage();
                                    vo.setMessageFileUrl(fullUrl);
                                    i18nUrlList.add(vo);
                                }
                            });
                            List<String> extraLanguage = languages.stream().map(LanguageManagerListVO::getCode)
                                    .filter(language -> !i18LanguageCode.contains(language))
                                    .toList();
                            if (!extraLanguage.isEmpty()) {
                                extraLanguage.forEach(language-> {
                                    I18nMsgFrontVO vo = new I18nMsgFrontVO();
                                    vo.setLanguage(language);
                                    i18nUrlList.add(vo);
                                });
                            }
                            urlResultList.add(i18nUrlList);
                        }
                        result.setI18nFileUrl(urlResultList);
                    }
                }
            }
        }
        return ResponseVO.success(result);
    }

    public ResponseVO<i18nMessagesVO> getDownloadInfoBySiteCode(String siteCode) {
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, siteCode);
        queryWrapper.in(UserAgreementPO::getCode, CommonConstant.business_six, CommonConstant.business_seven, CommonConstant.business_eleven);
        List<UserAgreementPO> poList = repository.selectList(queryWrapper);
        i18nMessagesVO result = new i18nMessagesVO();
        for (UserAgreementPO po : poList) {
            if (po.getCode().equals(CommonConstant.business_six)) {
                result.setIosDownloadUrl(po.getOptionValue());
                result.setIcon(po.getOptionValueExtend());
                result.setIconFileUrl(fileService.getMinioDomain()+"/"+po.getOptionValueExtend());
            } else if (po.getCode().equals(CommonConstant.business_seven)) {
                result.setAndroidDownloadUrl(po.getOptionValue());
            } else if (po.getCode().equals(CommonConstant.business_eleven)) {
                List<List<I18nMsgFrontVO>> urlResultList = new ArrayList<>();

                String i18nOption = po.getOptionValue();
                if (StringUtils.isNotBlank(i18nOption)) {

                    String[] urlList = i18nOption.split(CommonConstant.COMMA);
                    String minioDomain = fileService.getMinioDomain();
                    for (String url : urlList) {
                        List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
                        if (CollectionUtil.isNotEmpty(data)) {
                            List<I18nMsgFrontVO> i18nUrlList = new ArrayList<>();
                            data.forEach(item -> {
                                I18nMsgFrontVO vo = new I18nMsgFrontVO();
                                vo.setLanguage(item.getLanguage());
                                vo.setMessage(item.getMessage());
                                String fullUrl = minioDomain + "/" + item.getMessage();
                                vo.setMessageFileUrl(fullUrl);
                                i18nUrlList.add(vo);
                            });
                            urlResultList.add(i18nUrlList);
                        }
                        result.setI18nFileUrl(urlResultList);
                    }
                }
            }
        }
        return ResponseVO.success(result);
    }

    //获取加入我们信息
    public ResponseVO<List<ContactInfoVO>> getContactInfo() {
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, CurrReqUtils.getSiteCode());
        queryWrapper.eq(UserAgreementPO::getCode, CommonConstant.business_five);
        UserAgreementPO po = repository.selectOne(queryWrapper);
        List<ContactInfoVO> result = new ArrayList<>();
        if (po != null) {
            if ( StringUtils.isNotBlank(po.getTelegram())) {
                result.add(ContactInfoVO.builder().IMPlatformName("telegram").account(po.getTelegram()).build());
            }
            if (StringUtils.isNotBlank(po.getSkype())) {
                result.add(ContactInfoVO.builder().IMPlatformName("skype").account(po.getSkype()).build());
            }
        }
        return ResponseVO.success(result);
    }


    public List<I18nMsgFrontVO> getDownloadBackImgList(String siteCode) {
        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAgreementPO::getSiteCode, siteCode);
        queryWrapper.eq(UserAgreementPO::getCode, CommonConstant.business_eleven);
        UserAgreementPO agreementPO = this.getOne(queryWrapper);
        List<I18nMsgFrontVO> resultList = new ArrayList<>();
        Map<String,I18nMsgFrontVO> tempMap = new ConcurrentHashMap<>();
        StringBuilder picFullUrlBuilder = new StringBuilder();
        if (agreementPO != null && StringUtils.isNotBlank(agreementPO.getOptionValue())) {
            String[] urlList = agreementPO.getOptionValue().split(CommonConstant.COMMA);
            for (String url : urlList) {
                List<I18NMessageDTO> data = i18nApi.getMessageByKey(url).getData();
                if (CollectionUtil.isNotEmpty(data)) {
                    String minioDomain = fileService.getMinioDomain();
                    Iterator<I18NMessageDTO> iterator = data.iterator();
                    if (!resultList.isEmpty()) {
                        while (iterator.hasNext()) {
                            I18NMessageDTO i18NMessageDTO = iterator.next();
                            //重新组装result
                            Iterator<I18nMsgFrontVO> resultIterator = resultList.iterator();
                            while (resultIterator.hasNext()) {
                                I18nMsgFrontVO next = resultIterator.next();
                                if (next.getLanguage().equals(i18NMessageDTO.getLanguage())) {
                                    I18nMsgFrontVO newNext = new I18nMsgFrontVO();
                                    picFullUrlBuilder.append(next.getMessageFileUrl()).append(CommonConstant.COMMA);
                                    picFullUrlBuilder.append(minioDomain).append("/").append(i18NMessageDTO.getMessage());
                                    newNext.setMessageFileUrl(picFullUrlBuilder.toString());
                                    newNext.setLanguage(i18NMessageDTO.getLanguage());
                                    picFullUrlBuilder.setLength(0);
                                    tempMap.put(next.getLanguage(), newNext);
                                    resultIterator.remove();

                                    break;
                                }
                            }

                        }
                        resultList.addAll(tempMap.values());
                    } else {
                        while (iterator.hasNext()) {
                            I18NMessageDTO i18nMessageDTO = iterator.next();
                            //第一次添加
                            I18nMsgFrontVO resultVO = new I18nMsgFrontVO();
                            String message = i18nMessageDTO.getMessage();
                            String language = i18nMessageDTO.getLanguage();
                            picFullUrlBuilder.append(minioDomain).append("/").append(message);
                            resultVO.setLanguage(language);
                            resultVO.setMessageFileUrl(picFullUrlBuilder.toString());
                            picFullUrlBuilder.setLength(0);
                            resultList.add(resultVO);
                        }

                    }
                }
            }
        }

        return resultList;
    }


    private void initPwaFile(String siteCode) {
        try {
            log.info("开始创建当前站点pwa配置文件至minio,siteCode:{}", siteCode);
            LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
            query.eq(SitePO::getSiteCode, siteCode);
            SitePO po =  siteRepository.selectOne(query);
            if (po != null) {
                PwaVO pwaVO = new PwaVO();
                pwaVO.setName(po.getSiteName());
                pwaVO.setShort_name(po.getSiteName());
                pwaVO.setDescription(po.getSiteName());
                pwaVO.setTheme_color("#ffffff");
                pwaVO.setBackground_color("#ffffff");
                pwaVO.setDisplay("standalone");
                pwaVO.setStart_url("/");
                pwaVO.setScope("/");
                List<PwaVO.Icon> arr = new ArrayList<>();
                PwaVO.Icon shortIcon = new PwaVO.Icon();
                shortIcon.setSizes("192x192");
                shortIcon.setType("image/png");
                PwaVO.Icon longIcon = new PwaVO.Icon();
                longIcon.setSizes("512x512");
                longIcon.setType("image/png");
                ResponseVO<i18nMessagesVO> resp = getDownloadInfoBySiteCode(siteCode);
                if (resp.isOk()) {
                    i18nMessagesVO data = resp.getData();
                    shortIcon.setSrc(data.getIconFileUrl());
                    longIcon.setSrc(data.getIconFileUrl());
                }
                arr.add(shortIcon);
                arr.add(longIcon);
                pwaVO.setIcons(arr);
                minioUploadApi.uploadPwa(pwaVO, siteCode + "_manifest.json");
                log.info("当前{}站点pwa配置创建完成", siteCode);
            }
        } catch (Exception e) {
            log.error("当前站点:{},初始化pwa文件失败,原因:{}", siteCode, e.getMessage());
        }
    }
}

package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.userlabel.GetAllUserLabelVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.po.UserInformationChangePO;
import com.cloud.baowang.user.po.UserReviewPO;
import com.cloud.baowang.user.repositories.UserInformationChangeRepository;
import com.cloud.baowang.user.repositories.UserReviewRepository;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeReqVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeResVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.DVALRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserInformationChangeService extends ServiceImpl<UserReviewRepository, UserReviewPO> {


    private final UserInformationChangeRepository userInformationChangeRepository;

    private final SystemParamApi systemParamApi;

    private final RiskApi riskApi;

    private final SiteUserLabelConfigService userLabelConfigService;

    private final VipGradeApi vipGradeApi;

    public LambdaQueryWrapper buildLqw(UserInformationChangeReqVO userDetailsReqVO) {
        LambdaQueryWrapper<UserInformationChangePO> queryWrapper = new LambdaQueryWrapper<>();
        //操作时间
        if (ObjectUtil.isNotEmpty(userDetailsReqVO.getStartOperatingTime()) && ObjectUtil.isNotEmpty(userDetailsReqVO.getEndReOperatingTime())) {
            queryWrapper.ge(UserInformationChangePO::getOperatingTime, userDetailsReqVO.getStartOperatingTime());
            queryWrapper.le(UserInformationChangePO::getOperatingTime, userDetailsReqVO.getEndReOperatingTime());
        }
        //会员账号
        if (StringUtils.isNotBlank(userDetailsReqVO.getMemberAccount())) {
            queryWrapper.eq(UserInformationChangePO::getMemberAccount, userDetailsReqVO.getMemberAccount());
        }
        //账号类型
        if (ObjectUtil.isNotEmpty(userDetailsReqVO.getAccountType())) {
            queryWrapper.in(UserInformationChangePO::getAccountType, userDetailsReqVO.getAccountType());
        }
        //变更类型
        if (ObjectUtil.isNotEmpty(userDetailsReqVO.getChangeType())) {
            queryWrapper.in(UserInformationChangePO::getChangeType, userDetailsReqVO.getChangeType());
        }
        //操作人
        if (StringUtils.isNotBlank(userDetailsReqVO.getOperator())) {
            queryWrapper.eq(UserInformationChangePO::getOperator, userDetailsReqVO.getOperator());
        }
        queryWrapper.eq(UserInformationChangePO::getSiteCode, userDetailsReqVO.getSiteCode());

        //升序
        if (CommonConstant.ORDER_BY_ASC.equals(userDetailsReqVO.getOrderType())) {
            queryWrapper.orderByAsc(UserInformationChangePO::getOperatingTime);
        } else {
            //降序
            queryWrapper.orderByDesc(UserInformationChangePO::getOperatingTime);
        }

        return queryWrapper;

    }

    public Long getUserInformationChangeCount(UserInformationChangeReqVO vo) {
        LambdaQueryWrapper<UserInformationChangePO> lqw = buildLqw(vo);
        return userInformationChangeRepository.selectCount(lqw);
    }

    public ResponseVO<Page<UserInformationChangeResVO>> getUserInformationChange(UserInformationChangeReqVO userDetailsReqVO) {
        try {
            Page<UserInformationChangePO> page = new Page<>(userDetailsReqVO.getPageNumber(), userDetailsReqVO.getPageSize());
            LambdaQueryWrapper<UserInformationChangePO> queryWrapper = buildLqw(userDetailsReqVO);

            Page<UserInformationChangePO> result = userInformationChangeRepository.selectPage(page, queryWrapper);
            List<UserInformationChangeResVO> list = result.getRecords().stream().map(po -> {
                UserInformationChangeResVO vo = new UserInformationChangeResVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).toList();

            Page<UserInformationChangeResVO> pageResult = new Page<>();
            BeanUtils.copyProperties(result, pageResult);
            pageResult.setRecords(list);
            //账号状态
            List<CodeValueVO> accountStatus = systemParamApi.getSystemParamByType(CommonConstant.USER_ACCOUNT_STATUS).getData();
            List<CodeValueVO> changeTypes = systemParamApi.getSystemParamByType(CommonConstant.USER_CHANGE_TYPE).getData();
            List<CodeValueVO> accountSex = systemParamApi.getSystemParamByType(CommonConstant.USER_GENDER).getData();

            // 风控层级
            RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
            riskLevelDownReqVO.setSiteCode(userDetailsReqVO.getSiteCode());
            riskLevelDownReqVO.setRiskControlType("1");
            ResponseVO<List<RiskLevelResVO>> riskLevelList = riskApi.getRiskLevelList(riskLevelDownReqVO);

            // 用户标签 所有，包括禁止与删除的
            Map<String, String> labelMap = new HashMap<>();
            List<GetAllUserLabelVO> labelByIds = userLabelConfigService.getAllEnableUserLabelBySiteCodeForHis(userDetailsReqVO.getSiteCode());
            if (CollectionUtil.isNotEmpty(labelByIds)) {
                labelMap = labelByIds.stream().collect(Collectors.toMap(GetAllUserLabelVO::getId, GetAllUserLabelVO::getLabelName, (k1, k2) -> k2));
            }
            // 查询vip等级
            List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(userDetailsReqVO.getSiteCode());
            Map<String, String> vipMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
                vipMap = siteVIPGradeVOS.stream().collect(Collectors.toMap(vo -> vo.getVipGradeCode().toString(), SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
            }

            String sexManName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_GENDER, "1");
            String sexFemaleName = I18nMessageUtil.getSystemParamAndTrans(CommonConstant.USER_GENDER, "2");

            for (UserInformationChangeResVO record : pageResult.getRecords()) {
                // 变更类型
                if (null != record.getChangeType()) {
                    changeTypes.forEach(changeType -> {
                        if (record.getChangeType().equals(changeType.getCode())) {
                            record.setChangeTypeName(I18nMessageUtil.getI18NMessageInAdvice(changeType.getValue()));
                        }
                    });
                }
                if (userDetailsReqVO.getDataDesensitization()) {
                    if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.EMAIL_STATUS.getCode()))) {
                        record.setInformationBeforeChange(SymbolUtil.showEmail(record.getInformationBeforeChange()));
                        record.setInformationAfterChange(SymbolUtil.showEmail(record.getInformationAfterChange()));
                    }
                    if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.PHONE_STATUS.getCode()))) {
                        record.setInformationBeforeChange(SymbolUtil.showPhone(record.getInformationBeforeChange()));
                        record.setInformationAfterChange(SymbolUtil.showPhone(record.getInformationAfterChange()));
                    }
                    if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.NAME_STATUS.getCode()))) {
                        record.setInformationBeforeChange(SymbolUtil.showUserName(record.getInformationBeforeChange()));
                        record.setInformationAfterChange(SymbolUtil.showUserName(record.getInformationAfterChange()));
                    }
                    if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.BANK_CARD_UN_BIND.getCode()))) {
                        String[] beforeChangeArr = record.getInformationBeforeChange().split("\\|");
                        String backCard =  SymbolUtil.showBankOrVirtualNo(beforeChangeArr[1]);
                        beforeChangeArr[1] = backCard;
                        record.setInformationBeforeChange(String.join("|",beforeChangeArr));
                    }
                    if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.ELECTRONIC_WALLET_UN_BIND.getCode()))) {
                        String[] beforeChangeArr = record.getInformationBeforeChange().split("\\|");
                        String userName = "";
                        String addressNo = "";
                        String electronicAccount = "";
                        if(beforeChangeArr.length == CommonConstant.business_three){
                            userName = SymbolUtil.showUserName(beforeChangeArr[0]);
                            beforeChangeArr[0] = userName;
                            electronicAccount = SymbolUtil.showWalletNo(beforeChangeArr[1]);
                            beforeChangeArr[1] = electronicAccount;
                            addressNo = SymbolUtil.showWalletNo(beforeChangeArr[2]);
                            beforeChangeArr[2] = addressNo;

                        }else{
                            userName = SymbolUtil.showUserName(beforeChangeArr[1]);
                            beforeChangeArr[1] = userName;
                            electronicAccount = SymbolUtil.showWalletNo(beforeChangeArr[2]);
                            beforeChangeArr[2] = electronicAccount;
                            addressNo = SymbolUtil.showWalletNo(beforeChangeArr[3]);
                            beforeChangeArr[3] = addressNo;
                        }
                        record.setInformationBeforeChange(String.join("|",beforeChangeArr));
                    }
                    if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.CRYPTO_CURRENCY_UN_BIND.getCode()))) {
                        String[] beforeChangeArr = record.getInformationBeforeChange().split("\\|");
                        String addressNo =  SymbolUtil.showBankOrVirtualNo(beforeChangeArr[1]);
                        beforeChangeArr[1] = addressNo;
                        record.setInformationBeforeChange(String.join("|",beforeChangeArr));
                    }
                }

                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.PHONE_STATUS.getCode()))) {

                    String beforeFixing = record.getInformationBeforeChange();
                    String afterModification = record.getInformationAfterChange();
                    if (StringUtils.isNotBlank(beforeFixing)) {
                        record.setInformationBeforeChange(beforeFixing.replaceAll(CommonConstant.COMMA, ""));
                    }
                    if (StringUtils.isNotBlank(afterModification)) {
                        record.setInformationAfterChange(afterModification.replaceAll(CommonConstant.COMMA, ""));
                    }
                }

                StringBuilder beforeName = new StringBuilder();
                StringBuilder afterName = new StringBuilder();
                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.VIP_RANK_STATUS.getCode()))) {
                    if (StringUtils.isNotBlank(record.getInformationBeforeChange())) {
                        record.setInformationBeforeChange(vipMap.get(record.getInformationBeforeChange()));
                    }
                    if (StringUtils.isNotBlank(record.getInformationAfterChange())) {
                        record.setInformationAfterChange(vipMap.get(record.getInformationAfterChange()));
                    }

                }
                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.ZHANG_HAO_STATUS.getCode()))) {
                    if (ObjectUtil.isNotEmpty(record.getInformationBeforeChange())) {
                        String[] before = record.getInformationBeforeChange().split(",");
                        for (String be : before) {
                            for (CodeValueVO res : accountStatus) {
                                if (be.equals(res.getCode())) {
                                    beforeName.append(I18nMessageUtil.getI18NMessageInAdvice(res.getValue())).append(",");

                                }
                            }
                        }
                    }
                    if (ObjectUtil.isNotEmpty(beforeName.toString())) {
                        record.setInformationBeforeChange(beforeName.substring(0, beforeName.length() - 1));
                    }
                    if (ObjectUtil.isNotEmpty(record.getInformationAfterChange())) {
                        String[] after = record.getInformationAfterChange().split(",");
                        for (String be : after) {
                            for (CodeValueVO res : accountStatus) {
                                if (be.equals(res.getCode())) {
                                    afterName.append(I18nMessageUtil.getI18NMessageInAdvice(res.getValue())).append(",");

                                }
                            }
                        }
                        record.setInformationAfterChange(afterName.substring(0, afterName.length() - 1));
                    }
                }

                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.FENG_KONG_STATUS.getCode()))) {
                    riskLevelList.getData().forEach(riskLevel -> {
                        if (ObjectUtil.isNotEmpty(record.getInformationBeforeChange())) {
                            if (record.getInformationBeforeChange().equals(String.valueOf(riskLevel.getId()))) {
                                record.setInformationBeforeChange(riskLevel.getRiskControlLevel());
                            }
                        }
                        if (record.getInformationAfterChange().equals(String.valueOf(riskLevel.getId()))) {
                            record.setInformationAfterChange(riskLevel.getRiskControlLevel());
                        }
                    });
                }


                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.HUI_YUAN_STATUS.getCode()))) {
                    if (StringUtils.isNotBlank(record.getInformationBeforeChange())) {
                        String[] split = record.getInformationBeforeChange().split(",");
                        StringJoiner before = new StringJoiner(",");
                        for (String id : split) {
                            before.add(labelMap.get(id));
                        }
                        record.setInformationBeforeChange(before.toString());
                    }
                    if (StringUtils.isNotBlank(record.getInformationAfterChange())) {
                        String[] split = record.getInformationAfterChange().split(",");
                        StringJoiner after = new StringJoiner(",");
                        for (String id : split) {
                            after.add(labelMap.get(id));
                        }
                        record.setInformationAfterChange(after.toString());
                    }
                }
                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.ADD_TYPING.getCode()))) {
                    BigDecimal before = BigDecimal.ZERO;
                    String informationBeforeChange = record.getInformationBeforeChange();
                    if (StringUtils.isNotBlank(informationBeforeChange)) {
                        before = new BigDecimal(informationBeforeChange);
                    }
                    String informationAfterChange = record.getInformationAfterChange();
                    if (StringUtils.isNotBlank(informationAfterChange)) {
                        record.setInformationAfterChange(String.valueOf(new BigDecimal(informationAfterChange).add(before)));
                    }
                }

                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.CHU_SHENG_STATUS.getCode()))) {
                    /*if (ObjectUtil.isNotEmpty(record.getInformationBeforeChange())) {
                        long time = DateUtil.parse(record.getInformationBeforeChange(), DatePattern.NORM_DATE_PATTERN).getTime();
                        record.setInformationBeforeChange(String.valueOf(time));
                    }
                    if (ObjectUtil.isNotEmpty(record.getInformationAfterChange())) {
                        long time = DateUtil.parse(record.getInformationAfterChange(), DatePattern.NORM_DATE_PATTERN).getTime();
                        record.setInformationAfterChange(String.valueOf(time));
                    }*/
                }
                if (record.getChangeType().equals(String.valueOf(UserChangeTypeEnum.SEX_STATUS.getCode()))) {
                    if (ObjectUtil.isNotEmpty(record.getInformationBeforeChange())) {
                        accountSex.forEach(sex -> {
                            if (record.getInformationBeforeChange().equals(sex.getCode())) {
                                //record.setInformationBeforeChange(I18nMessageUtil.getI18NMessageInAdvice(sex.getValue()));
                                record.setInformationBeforeChange(getSexName(record.getInformationBeforeChange(), sexManName, sexFemaleName));//修改前
                            }
                        });
                    }
                    accountSex.forEach(sex -> {
                        if (ObjectUtil.isNotEmpty(record.getInformationAfterChange()) && record.getInformationAfterChange().equals(sex.getCode())) {
                            //record.setInformationAfterChange(I18nMessageUtil.getI18NMessageInAdvice(sex.getValue()));
                            record.setInformationAfterChange(getSexName(record.getInformationAfterChange(), sexManName, sexFemaleName));//修改后
                        }
                    });
                }
            }
            return ResponseVO.success(pageResult);
        } catch (Exception e) {
            log.error("会员变更记录查询失败：", e);
            throw new BaowangDefaultException(ResultCode.USER_CHANGE);
        }
    }

    public String getSexName(String sex, String sexManName, String sexFemaleName) {
        if (StringUtils.isBlank(sex)) {
            return "";
        }
        return switch (sex) {
            case "1" -> sexManName;
            case "2" -> sexFemaleName;
            default -> "";
        };
    }
}

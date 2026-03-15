package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.RegexEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.user.api.enums.UserChangeTypeEnum;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.user.api.api.auth.AliAuthApi;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateVO;
import com.cloud.baowang.wallet.api.enums.wallet.WithDrawCollectEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AddressUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.user.api.api.UserDetailsApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserDetails.UserDetailsReqVO;
import com.cloud.baowang.wallet.api.vo.bank.BankManageVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.userwallet.ClientUserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountQueryVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountUnBindVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserReceiveAccountVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserAccountBindBaseInfoVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import com.cloud.baowang.wallet.po.UserReceiveAccountPO;
import com.cloud.baowang.wallet.repositories.UserReceiveAccountRepository;
import com.cloud.baowang.wallet.service.bank.BankCardManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员收款信息
 *
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserReceiveAccountService extends ServiceImpl<UserReceiveAccountRepository, UserReceiveAccountPO> {

    private final SystemDictConfigApi systemDictConfigApi;

     private final AreaSiteManageApi areaSiteManageApi;

     private final SystemWithdrawWayService systemWithdrawWayService;

     private final BankCardManagerService bankCardManagerService;

     private final UserInfoApi userInfoApi;

     private final UserDetailsApi userDetailsApi;

     private final AliAuthApi aliAuthApi;

     private final RiskApi riskApi;



    public List<UserReceiveAccountResponseVO> userReceiveAccount(WalletUserBasicRequestVO requestVO) {
        String siteCode = requestVO.getSiteCode();
        LambdaQueryWrapper<UserReceiveAccountPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserReceiveAccountPO::getSiteCode, requestVO.getSiteCode());
        lqw.eq(UserReceiveAccountPO::getUserAccount,requestVO.getUserAccount());
        lqw.eq(UserReceiveAccountPO::getBindingStatus, CommonConstant.business_one);

        List<UserReceiveAccountPO> userReceiveAccountPOS = this.baseMapper.selectList(lqw);


        List<Integer> list = Arrays.asList(DictCodeConfigEnums.BANK_CARD_BINDING_NUMS.getCode(),
                DictCodeConfigEnums.ELECTRONIC_WALLET_BINDING_NUMS.getCode(),
                DictCodeConfigEnums.CRYPTO_CURRENCY_BINDING_NUMS.getCode());
        List<SystemDictConfigRespVO> systemDictConfigRespVOS =  systemDictConfigApi.getByCodes(list,siteCode).getData();
        Map<Integer, String> dictConfigMap = systemDictConfigRespVOS.stream()
                .collect(Collectors.toMap(SystemDictConfigRespVO::getDictCode, SystemDictConfigRespVO::getConfigParam));

        Map<String,List<UserReceiveAccountPO>> userReceiveAccountMap = userReceiveAccountPOS.stream()
                .collect(Collectors.groupingBy(UserReceiveAccountPO::getReceiveType));
        List<UserReceiveAccountResponseVO> userReceiveAccountResponseVOS = new ArrayList<>();
        UserReceiveAccountResponseVO bankReceiveAccountResponseVO = new UserReceiveAccountResponseVO();
        UserReceiveAccountResponseVO electronicReceiveAccountResponseVO = new UserReceiveAccountResponseVO();
        UserReceiveAccountResponseVO cryptoReceiveAccountResponseVO = new UserReceiveAccountResponseVO();
        String bankParam = dictConfigMap.get(DictCodeConfigEnums.BANK_CARD_BINDING_NUMS.getCode());
        String electronicParam = dictConfigMap.get(DictCodeConfigEnums.ELECTRONIC_WALLET_BINDING_NUMS.getCode());
        String cryptoParam = dictConfigMap.get(DictCodeConfigEnums.CRYPTO_CURRENCY_BINDING_NUMS.getCode());
        bankReceiveAccountResponseVO.setReceiveType(WithdrawTypeEnum.BANK_CARD.getCode());
        electronicReceiveAccountResponseVO.setReceiveType(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode());
        cryptoReceiveAccountResponseVO.setReceiveType(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode());
        bankReceiveAccountResponseVO.setBindableNums(StringUtils.isNotBlank(bankParam)?Integer.parseInt(bankParam):CommonConstant.business_zero);
        electronicReceiveAccountResponseVO.setBindableNums(StringUtils.isNotBlank(electronicParam)?Integer.parseInt(electronicParam):CommonConstant.business_zero);
        cryptoReceiveAccountResponseVO.setBindableNums(StringUtils.isNotBlank(cryptoParam)?Integer.parseInt(cryptoParam):CommonConstant.business_zero);

        bankReceiveAccountResponseVO.setBoundNums(CommonConstant.business_zero);
        electronicReceiveAccountResponseVO.setBoundNums(CommonConstant.business_zero);
        cryptoReceiveAccountResponseVO.setBoundNums(CommonConstant.business_zero);

        for (Map.Entry<String, List<UserReceiveAccountPO>> entry : userReceiveAccountMap.entrySet()) {
            String key = entry.getKey();
            List<UserReceiveAccountPO> userReceiveAccountPOList = entry.getValue();
            List<String> accounts = new ArrayList<>();
            String riskTypeCode = "";
            if(WithdrawTypeEnum.BANK_CARD.getCode().equals(key)){
                accounts = userReceiveAccountPOList.stream().map(UserReceiveAccountPO::getBankCard).toList();
                riskTypeCode = RiskTypeEnum.RISK_BANK.getCode();
            }else if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(key)){
                accounts =  new ArrayList<>(userReceiveAccountPOList.stream().map(UserReceiveAccountPO::getElectronicWalletAccount).toList());
                List<String> accounts1 = new ArrayList<>(userReceiveAccountPOList.stream().map(UserReceiveAccountPO::getAddressNo).toList());
                accounts.addAll(accounts1);
                riskTypeCode = RiskTypeEnum.RISK_WALLET.getCode();
            }else if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(key)){
                accounts = userReceiveAccountPOList.stream().map(UserReceiveAccountPO::getAddressNo).toList();
                riskTypeCode = RiskTypeEnum.RISK_VIRTUAL.getCode();
            }

            List<UserReceiveAccountVO> userReceiveAccountVOS = ConvertUtil.entityListToModelList(userReceiveAccountPOList, UserReceiveAccountVO.class);

            //获取风控等级
            if(!accounts.isEmpty()){
                RiskListAccountQueryVO queryVO = new RiskListAccountQueryVO();
                queryVO.setSiteCode(siteCode);
                queryVO.setRiskControlAccounts(accounts);
                queryVO.setRiskControlTypeCode(riskTypeCode);
                List<RiskAccountVO> riskAccountVOS = riskApi.getRiskListAccount(queryVO);
                Map<String,RiskAccountVO> riskMap = riskAccountVOS.stream().collect(Collectors.toMap(RiskAccountVO::getRiskControlAccount, p -> p, (k1, k2) -> k2));
                for (UserReceiveAccountVO vo:userReceiveAccountVOS) {
                    if(WithdrawTypeEnum.BANK_CARD.getCode().equals(key)){
                        RiskAccountVO riskAccountVO = riskMap.get(vo.getBankCard());
                        if(null != riskAccountVO){
                            vo.setRiskControlLevelId(riskAccountVO.getRiskControlLevelId());
                            vo.setRiskControlLevel(riskAccountVO.getRiskControlLevel());
                        }

                    }else if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(key)){
                        RiskAccountVO riskAccountVO = riskMap.get(vo.getElectronicWalletAccount());
                        RiskAccountVO riskAccount1VO = riskMap.get(vo.getAddressNo());
                        if(null!=riskAccountVO && null!= riskAccount1VO){
                            vo.setRiskControlLevel(riskAccountVO.getRiskControlLevel()+"|"+riskAccount1VO.getRiskControlLevel());
                        }else if(null!=riskAccountVO && null== riskAccount1VO){
                            vo.setRiskControlLevel(riskAccountVO.getRiskControlLevel());
                        }else if (null==riskAccountVO && null!= riskAccount1VO){
                            vo.setRiskControlLevel(riskAccount1VO.getRiskControlLevel());
                        }

                    }else if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(key)){
                        RiskAccountVO riskAccountVO = riskMap.get(vo.getAddressNo());
                        if(null != riskAccountVO){
                            vo.setRiskControlLevelId(riskAccountVO.getRiskControlLevelId());
                            vo.setRiskControlLevel(riskAccountVO.getRiskControlLevel());
                        }
                    }
                    if (requestVO.getDataDesensitization()) {
                        // 脱敏
                        if (WithdrawTypeEnum.BANK_CARD.getCode().equals(key)) {
                            //SymbolUtil
                            vo.setBankCard(SymbolUtil.showBankOrVirtualNo(vo.getBankCard()));
                        } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(key)) {
                            if(StringUtils.isNotBlank(vo.getSurname())){
                                vo.setSurname(SymbolUtil.showUserName(vo.getSurname()));
                            }
                            if(StringUtils.isNotBlank(vo.getElectronicWalletAccount())){
                                vo.setElectronicWalletAccount(SymbolUtil.showWalletNo(vo.getElectronicWalletAccount()));

                            }
                            if(StringUtils.isNotBlank(vo.getAddressNo())){
                                vo.setAddressNo(SymbolUtil.showWalletNo(vo.getAddressNo()));
                            }

                        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(key)) {
                            vo.setAddressNo(SymbolUtil.showBankOrVirtualNo(vo.getAddressNo()));
                        }
                    }
                }
            }
            //是否有审核中解绑申请
            List<String> ids = userReceiveAccountPOList.stream().map(UserReceiveAccountPO::getId).toList();
            if(!ids.isEmpty()){
                List<UserAccountUpdateReviewResVO> userAccountUpdateVOList = userDetailsApi.getReviewingList(ids);
                Map<String,UserAccountUpdateReviewResVO>  accountUpdateReviewResVOMap = userAccountUpdateVOList.stream().collect(Collectors.toMap(UserAccountUpdateReviewResVO::getExtParam, p -> p, (k1, k2) -> k2));
                for (UserReceiveAccountVO vo:userReceiveAccountVOS) {
                    UserAccountUpdateReviewResVO userAccountUpdateReviewResVO = accountUpdateReviewResVOMap.get(vo.getId());
                    if(null != userAccountUpdateReviewResVO){
                        vo.setUnBindFlag(YesOrNoEnum.YES.getCode());
                    }else{
                        vo.setUnBindFlag(YesOrNoEnum.NO.getCode());
                    }
                }
            }

            if(WithdrawTypeEnum.BANK_CARD.getCode().equals(key)){
                bankReceiveAccountResponseVO.setBoundNums(userReceiveAccountPOList.size());
                bankReceiveAccountResponseVO.setUserReceiveAccountVOS(userReceiveAccountVOS);
            }else if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(key)){
                electronicReceiveAccountResponseVO.setBoundNums(userReceiveAccountPOList.size());
                electronicReceiveAccountResponseVO.setUserReceiveAccountVOS(userReceiveAccountVOS);
            }else if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(key)){
                cryptoReceiveAccountResponseVO.setBoundNums(userReceiveAccountPOList.size());
                cryptoReceiveAccountResponseVO.setUserReceiveAccountVOS(userReceiveAccountVOS);
            }


        }
        userReceiveAccountResponseVOS.add(bankReceiveAccountResponseVO);
        userReceiveAccountResponseVOS.add(electronicReceiveAccountResponseVO);
        userReceiveAccountResponseVOS.add(cryptoReceiveAccountResponseVO);

        return userReceiveAccountResponseVOS;
    }

    public List<UserReceiveAccountResponseVO> clientUserReceiveAccount(WalletUserBasicRequestVO requestVO) {
        String siteCode = requestVO.getSiteCode();
        LambdaQueryWrapper<UserReceiveAccountPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserReceiveAccountPO::getSiteCode, requestVO.getSiteCode());
        lqw.eq(UserReceiveAccountPO::getUserAccount,requestVO.getUserAccount());
        lqw.eq(UserReceiveAccountPO::getBindingStatus, CommonConstant.business_one);

        List<UserReceiveAccountPO> userReceiveAccountPOS = this.baseMapper.selectList(lqw);

        List<Integer> list = Arrays.asList(DictCodeConfigEnums.BANK_CARD_BINDING_NUMS.getCode(),
                DictCodeConfigEnums.ELECTRONIC_WALLET_BINDING_NUMS.getCode(),
                DictCodeConfigEnums.CRYPTO_CURRENCY_BINDING_NUMS.getCode());
        List<SystemDictConfigRespVO> systemDictConfigRespVOS =  systemDictConfigApi.getByCodes(list,siteCode).getData();
        Map<Integer, String> dictConfigMap = systemDictConfigRespVOS.stream()
                .collect(Collectors.toMap(SystemDictConfigRespVO::getDictCode, SystemDictConfigRespVO::getConfigParam));

        Map<String,List<UserReceiveAccountPO>> userReceiveAccountMap = userReceiveAccountPOS.stream()
                .collect(Collectors.groupingBy(UserReceiveAccountPO::getReceiveType));
        List<UserReceiveAccountResponseVO> userReceiveAccountResponseVOS = new ArrayList<>();
        UserReceiveAccountResponseVO bankReceiveAccountResponseVO = new UserReceiveAccountResponseVO();
        UserReceiveAccountResponseVO electronicReceiveAccountResponseVO = new UserReceiveAccountResponseVO();
        UserReceiveAccountResponseVO cryptoReceiveAccountResponseVO = new UserReceiveAccountResponseVO();
        String bankParam = dictConfigMap.get(DictCodeConfigEnums.BANK_CARD_BINDING_NUMS.getCode());
        String electronicParam = dictConfigMap.get(DictCodeConfigEnums.ELECTRONIC_WALLET_BINDING_NUMS.getCode());
        String cryptoParam = dictConfigMap.get(DictCodeConfigEnums.CRYPTO_CURRENCY_BINDING_NUMS.getCode());
        bankReceiveAccountResponseVO.setReceiveType(WithdrawTypeEnum.BANK_CARD.getCode());
        electronicReceiveAccountResponseVO.setReceiveType(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode());
        cryptoReceiveAccountResponseVO.setReceiveType(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode());
        bankReceiveAccountResponseVO.setBindableNums(StringUtils.isNotBlank(bankParam)?Integer.parseInt(bankParam):CommonConstant.business_zero);
        electronicReceiveAccountResponseVO.setBindableNums(StringUtils.isNotBlank(electronicParam)?Integer.parseInt(electronicParam):CommonConstant.business_zero);
        cryptoReceiveAccountResponseVO.setBindableNums(StringUtils.isNotBlank(cryptoParam)?Integer.parseInt(cryptoParam):CommonConstant.business_zero);

        bankReceiveAccountResponseVO.setBoundNums(CommonConstant.business_zero);
        electronicReceiveAccountResponseVO.setBoundNums(CommonConstant.business_zero);
        cryptoReceiveAccountResponseVO.setBoundNums(CommonConstant.business_zero);

        for (Map.Entry<String, List<UserReceiveAccountPO>> entry : userReceiveAccountMap.entrySet()) {
            String key = entry.getKey();
            List<UserReceiveAccountPO> userReceiveAccountPOList = entry.getValue();

            List<UserReceiveAccountVO> userReceiveAccountVOS = ConvertUtil.entityListToModelList(userReceiveAccountPOList, UserReceiveAccountVO.class);
            if(WithdrawTypeEnum.BANK_CARD.getCode().equals(key)){
                bankReceiveAccountResponseVO.setBoundNums(userReceiveAccountPOList.size());
                bankReceiveAccountResponseVO.setUserReceiveAccountVOS(userReceiveAccountVOS);
            }else if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(key)){
                electronicReceiveAccountResponseVO.setBoundNums(userReceiveAccountPOList.size());
                electronicReceiveAccountResponseVO.setUserReceiveAccountVOS(userReceiveAccountVOS);
            }else if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(key)){
                cryptoReceiveAccountResponseVO.setBoundNums(userReceiveAccountPOList.size());
                cryptoReceiveAccountResponseVO.setUserReceiveAccountVOS(userReceiveAccountVOS);
            }


        }
        userReceiveAccountResponseVOS.add(bankReceiveAccountResponseVO);
        userReceiveAccountResponseVOS.add(electronicReceiveAccountResponseVO);
        userReceiveAccountResponseVOS.add(cryptoReceiveAccountResponseVO);

        return userReceiveAccountResponseVOS;
    }

    public ResponseVO<Boolean> userReceiveAccountUnBind(UserReceiveAccountUnBindVO vo) {
        UserReceiveAccountPO userReceiveAccountPO = this.baseMapper.selectById(vo.getId());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        UserDetailsReqVO userDetailsReqVO = new UserDetailsReqVO();
        userDetailsReqVO.setUserAccount(vo.getUserAccount());
        userDetailsReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userDetailsReqVO.setExtParam(userReceiveAccountPO.getId());
        if(WithdrawTypeEnum.BANK_CARD.getCode().equals(userReceiveAccountPO.getReceiveType())){
            userDetailsReqVO.setUserReceiveAccount(userReceiveAccountPO.getBankName()+"|"+userReceiveAccountPO.getBankCard());
            userDetailsReqVO.setChangeType(String.valueOf(UserChangeTypeEnum.BANK_CARD_UN_BIND.getCode()));
        } else if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(userReceiveAccountPO.getReceiveType())){
            userDetailsReqVO.setUserReceiveAccount((StringUtils.isNotBlank(userReceiveAccountPO.getElectronicWalletName())?userReceiveAccountPO.getElectronicWalletName()+"|":"")
                    +userReceiveAccountPO.getSurname()
                    +(StringUtils.isNotBlank(userReceiveAccountPO.getElectronicWalletAccount())?"|"+userReceiveAccountPO.getElectronicWalletAccount():"")
                    +(StringUtils.isNotBlank(userReceiveAccountPO.getAddressNo())?"|"+userReceiveAccountPO.getAddressNo():""));
            userDetailsReqVO.setChangeType(String.valueOf(UserChangeTypeEnum.ELECTRONIC_WALLET_UN_BIND.getCode()));

        } else if(WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(userReceiveAccountPO.getReceiveType())){
            userDetailsReqVO.setUserReceiveAccount(userReceiveAccountPO.getNetworkType()+"|"+userReceiveAccountPO.getAddressNo());
            userDetailsReqVO.setChangeType(String.valueOf(UserChangeTypeEnum.CRYPTO_CURRENCY_UN_BIND.getCode()));
        }

        return  userDetailsApi.addUserReceiveAccountReview(userDetailsReqVO);

    }

    public ResponseVO<Boolean> userReceiveAccountBind(UserReceiveAccountBindVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        String userAccount = CurrReqUtils.getAccount();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        String currencyCode = userInfoVO.getMainCurrency();
        String withdrawTypeCode = vo.getReceiveType();


        Boolean isNeedRealName =  getAuthStatus(currencyCode,siteCode,userInfoVO.getAuthStatus());
        if(isNeedRealName && CommonConstant.business_one.equals(userInfoVO.getAuthStatus())){
            vo.setSurname(userInfoVO.getUserName());
        }

        if(WithdrawTypeEnum.BANK_CARD.getCode().equals(withdrawTypeCode) && isNeedRealName){
            if(!CommonConstant.business_one.equals(userInfoVO.getAuthStatus())){
                throw new BaowangDefaultException(ResultCode.NEED_AUTH_INFO);
            }
            Boolean flag =  aliAuthApi.bankVerification(vo.getSurname(),vo.getBankCard());
            if(!flag){
                throw new BaowangDefaultException(ResultCode.USER_NAME_NOT_MATCH_BANK_CARD);
            }
        }
        //校验取款密码
        checkWithdrawPassword(vo.getWithdrawPassWord(), userInfoVO);
        List<Integer> list = Arrays.asList(DictCodeConfigEnums.BANK_CARD_BINDING_NUMS.getCode(),
                DictCodeConfigEnums.ELECTRONIC_WALLET_BINDING_NUMS.getCode(),
                DictCodeConfigEnums.CRYPTO_CURRENCY_BINDING_NUMS.getCode());
        String paramNums = "0";
        if(WithdrawTypeEnum.BANK_CARD.getCode().equals(withdrawTypeCode)){
            paramNums = systemDictConfigApi.getByCode(DictCodeConfigEnums.BANK_CARD_BINDING_NUMS.getCode(),siteCode).getData().getConfigParam();
        } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)) {
            paramNums = systemDictConfigApi.getByCode(DictCodeConfigEnums.ELECTRONIC_WALLET_BINDING_NUMS.getCode(),siteCode).getData().getConfigParam();
        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)) {
            paramNums = systemDictConfigApi.getByCode(DictCodeConfigEnums.CRYPTO_CURRENCY_BINDING_NUMS.getCode(),siteCode).getData().getConfigParam();
        }
        LambdaQueryWrapper<UserReceiveAccountPO> userReceiveAccountLqw = new LambdaQueryWrapper<>();
        userReceiveAccountLqw.eq(UserReceiveAccountPO::getReceiveType,vo.getReceiveType());
        userReceiveAccountLqw.eq(UserReceiveAccountPO::getBindingStatus,YesOrNoEnum.YES.getCode());
        userReceiveAccountLqw.eq(UserReceiveAccountPO::getUserId,userId);

        List<UserReceiveAccountPO> userReceiveAccountPOList = this.baseMapper.selectList(userReceiveAccountLqw);
        if(userReceiveAccountPOList.size()>Integer.parseInt(paramNums)){
            throw new BaowangDefaultException(ResultCode.ACCOUNT_BIND_NUMS_GT);
        }

        List<BankManageVO> bankManageVOList = bankCardManagerService.bankList(currencyCode);
        //获取站点 提款类型下提款方式 所有收集信息集合
        List<WithdrawCollectInfoVO> collectInfoVOS = systemWithdrawWayService.getWithdrawCollectInfoList(siteCode,withdrawTypeCode,currencyCode);

        //校验参数
        checkParam(vo,collectInfoVOS,bankManageVOList);
        LambdaQueryWrapper<UserReceiveAccountPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserReceiveAccountPO::getSiteCode,siteCode);
        if(WithdrawTypeEnum.BANK_CARD.getCode().equals(withdrawTypeCode)){
            lqw.eq(UserReceiveAccountPO::getBankCard,vo.getBankCard());
        } else if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)) {
            lqw.eq(StringUtils.isNotBlank(vo.getElectronicWalletName()),UserReceiveAccountPO::getElectronicWalletName,vo.getElectronicWalletName());
            if(StringUtils.isBlank(vo.getUserAccount())){
                lqw.eq(UserReceiveAccountPO::getElectronicWalletAccount,vo.getUserPhone());
            }else{
                lqw.eq(UserReceiveAccountPO::getElectronicWalletAccount,vo.getUserAccount());
            }
            if(StringUtils.isBlank(vo.getAddressNo())){
                lqw.eq(UserReceiveAccountPO::getAddressNo,vo.getUserPhone());
            }else{
                lqw.eq(UserReceiveAccountPO::getAddressNo,vo.getAddressNo());
            }
        } else if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)) {
            lqw.eq(UserReceiveAccountPO::getAddressNo,vo.getAddressNo());
        }
        UserReceiveAccountPO userReceiveAccountPO = this.baseMapper.selectOne(lqw);
        if(null != userReceiveAccountPO){
            if(YesOrNoEnum.YES.getCode().equals(userReceiveAccountPO.getBindingStatus())){
                throw new BaowangDefaultException(ResultCode.ACCOUNT_HAS_BEEN_BOUND);
            }else{
                throw new BaowangDefaultException(ResultCode.ACCOUNT_HAS_BEEN_BOUND_CANNOT_BE_ADDED);
            }
        }
        UserReceiveAccountPO receiveAccountPO = ConvertUtil.entityToModel(vo,UserReceiveAccountPO.class);

        receiveAccountPO.setSiteCode(siteCode);
        receiveAccountPO.setUserAccount(userAccount);
        receiveAccountPO.setUserId(userId);
        if (WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)) {
            //因电子钱包搜集信息 电子钱包账户与 会员账号冲突，所以这里接收参数与搜集信息不一致，需要特殊处理
            if(StringUtils.isBlank(vo.getUserAccount())){
                receiveAccountPO.setElectronicWalletAccount(vo.getUserPhone());
            }else{
                receiveAccountPO.setElectronicWalletAccount(vo.getUserAccount());
            }
            if(StringUtils.isBlank(vo.getAddressNo())){
                receiveAccountPO.setAddressNo(vo.getUserPhone());
            }
        }
        //如果插入的是默认，去掉其他账号的默认值
        if(YesOrNoEnum.YES.getCode().equals(vo.getDefaultFlag())){
            cleanDefaultFlag(vo.getReceiveType(),CurrReqUtils.getOneId());
        }
        receiveAccountPO.setBindingStatus(YesOrNoEnum.YES.getCode());
        receiveAccountPO.setCreatedTime(System.currentTimeMillis());
        receiveAccountPO.setCreator(userAccount);
        this.baseMapper.insert(receiveAccountPO);
        return ResponseVO.success();


    }
    private void checkWithdrawPassword(String withdrawPassword, UserInfoVO userInfoVO) {
        if (StringUtils.isBlank(withdrawPassword)) {
            throw new BaowangDefaultException(ResultCode.USER_WITHDRAW_PASSWORD_ERROR);
        }
        String regex = RegexEnum.WITHDRAW_PWD.getRegex();
        boolean withdrawPwdRegex = withdrawPassword.matches(regex);
        if (!withdrawPwdRegex) {
            throw new BaowangDefaultException(ResultCode.USER_WITHDRAW_PASSWORD_ERROR);
        }
        String originPassword = getEncryptPassword(withdrawPassword, userInfoVO.getSalt());
        if (!originPassword.equals(userInfoVO.getWithdrawPwd())) {
            throw new BaowangDefaultException(ResultCode.USER_WITHDRAW_PASSWORD_ERROR);
        }
    }
    public String getEncryptPassword(String password, String salt) {
        String origin = password + salt;
        return MD5Util.MD5Encode(MD5Util.MD5Encode(origin));
    }

    private void checkParam(UserReceiveAccountBindVO vo,List<WithdrawCollectInfoVO> collectInfoVOS, List<BankManageVO> bankManageVOList) {

        String withdrawTypeCode = vo.getReceiveType();
        for (WithdrawCollectInfoVO collectInfoVO : collectInfoVOS) {
            WithDrawCollectEnum withDrawCollectEnum = WithDrawCollectEnum.of(collectInfoVO.getFiledCode());
            if(null == withDrawCollectEnum){
                continue;
            }
            switch (withDrawCollectEnum) {
                case BANK_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getBankName())) {
                        throw new BaowangDefaultException(ResultCode.BANK_NAME_IS_EMPTY);
                    }
                }
                case BANK_CARD -> {
                    if (ObjectUtil.isEmpty(vo.getBankCard())) {
                        throw new BaowangDefaultException(ResultCode.BANK_CARD_IS_EMPTY);
                    } else {
                        if (!vo.getBankCard().matches("^\\d{1,19}$")) {
                            throw new BaowangDefaultException(ResultCode.BANK_CARD_IS_ERROR);
                        }
                    }
                }
                case BANK_CODE -> {
                    if (ObjectUtil.isEmpty(vo.getBankCode())) {
                        throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EMPTY);
                    } else {
                        if (CollectionUtil.isNotEmpty(bankManageVOList)) {
                            List<String> bankCodeList = bankManageVOList.stream().map(BankManageVO::getBankCode).collect(Collectors.toList());
                            if (!bankCodeList.contains(vo.getBankCode())) {
                                throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EXIST);
                            }
                        } else {
                            throw new BaowangDefaultException(ResultCode.BANK_CODE_IS_EXIST);
                        }
                    }
                }
                case SURNAME -> {
                    if (ObjectUtil.isEmpty(vo.getSurname())) {
                        throw new BaowangDefaultException(ResultCode.SURNAME_IS_EMPTY);
                    }
                }
                /*case USER_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getUserName())) {
                        throw new BaowangDefaultException(ResultCode.USER_NAME_IS_EMPTY);
                    }
                }*/
                case USER_EMAIL -> {
                    if (ObjectUtil.isEmpty(vo.getUserEmail())) {
                        throw new BaowangDefaultException(ResultCode.USER_EMAIL_IS_EMPTY);
                    }
                }
                case USER_PHONE -> {
                    if (ObjectUtil.isEmpty(vo.getUserPhone())) {
                        throw new BaowangDefaultException(ResultCode.USER_PHONE_IS_EMPTY);
                    } else {
                        if (!vo.getUserPhone().matches("^\\d{3,15}$")) {
                            throw new BaowangDefaultException(ResultCode.USER_PHONE_IS_ERROR);
                        }
                    }
                    if (ObjectUtil.isEmpty(vo.getAreaCode())) {
                        throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EMPTY);
                    } else {
                        String language = CurrReqUtils.getLanguage();
                        List<AreaSiteLangVO> areaList = areaSiteManageApi.getAreaList(CurrReqUtils.getSiteCode(), language).getData();
                        if (CollectionUtil.isNotEmpty(areaList)) {
                            List<String> areaCodeList = areaList.stream().map(AreaSiteLangVO::getAreaCode).collect(Collectors.toList());
                            if (!areaCodeList.contains(vo.getAreaCode())) {
                                throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
                            }
                        } else {
                            throw new BaowangDefaultException(ResultCode.AREA_CODE_IS_EXIST);
                        }
                    }
                }
                case PROVINCE_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getProvinceName())) {
                        throw new BaowangDefaultException(ResultCode.PROVINCE_NAME_IS_EMPTY);
                    }
                }
                case CITY_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getCityName())) {
                        throw new BaowangDefaultException(ResultCode.CITY_NAME_IS_EMPTY);
                    }
                }
                case DETAIL_ADDRESS -> {
                    if (ObjectUtil.isEmpty(vo.getDetailAddress())) {
                        throw new BaowangDefaultException(ResultCode.DETAIL_ADDRESS_IS_EMPTY);
                    }
                }
                /*case ELECTRONIC_WALLET_NAME -> {
                    if (ObjectUtil.isEmpty(vo.getElectronicWalletName())) {
                        throw new BaowangDefaultException(ResultCode.ELECTRONIC_WALLET_NAME_IS_EMPTY);
                    }
                }*/
                case NETWORK_TYPE -> {
                    if (ObjectUtil.isEmpty(vo.getNetworkType())) {
                        throw new BaowangDefaultException(ResultCode.NETWORK_TYPE_IS_EMPTY);
                    }
                }
                case ADDRESS_NO -> {
                    if (!WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)) {
                        if (ObjectUtil.isEmpty(vo.getAddressNo())) {
                            throw new BaowangDefaultException(ResultCode.ADDRESS_NO_IS_EMPTY);
                        }
                    }
                }
                case IFSC_CODE -> {
                    if (ObjectUtil.isEmpty(vo.getIfscCode())) {
                        throw new BaowangDefaultException(ResultCode.IFSC_CODE_IS_EMPTY);
                    }
                }
            }
        }

        if (WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(withdrawTypeCode)) {
            //校验取款地址
            if (!AddressUtils.isValidAddress(vo.getAddressNo(), NetWorkTypeEnum.nameOfCode(vo.getNetworkType()).getType())) {
                throw new BaowangDefaultException(ResultCode.WITHDRAW_ADDRESS_ERROR);
            }
        }

    }

    public ResponseVO<Boolean> siteUerReceiveAccountUnBind(IdVO vo) {
        UserReceiveAccountPO userReceiveAccountPO = this.baseMapper.selectById(vo.getId());
        userReceiveAccountPO.setBindingStatus(YesOrNoEnum.NO.getCode());
        userReceiveAccountPO.setUserId("");
        userReceiveAccountPO.setUserAccount("");
        userReceiveAccountPO.setUpdatedTime(System.currentTimeMillis());
        this.baseMapper.updateById(userReceiveAccountPO);
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> clientUserReceiveAccountUnBind(ClientUserReceiveAccountUnBindVO vo) {
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
        checkWithdrawPassword(vo.getWithdrawPassWord(),userInfoVO);
        UserReceiveAccountPO userReceiveAccountPO = this.baseMapper.selectById(vo.getId());
        userReceiveAccountPO.setBindingStatus(YesOrNoEnum.NO.getCode());
        userReceiveAccountPO.setUserId("");
        userReceiveAccountPO.setUserAccount("");
        userReceiveAccountPO.setUpdatedTime(System.currentTimeMillis());
        this.baseMapper.updateById(userReceiveAccountPO);
        return ResponseVO.success();
    }

    public ResponseVO<UserReceiveAccountVO> getUserReceiveAccountByAddressNo(String receiveAccount) {
        LambdaQueryWrapper<UserReceiveAccountPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserReceiveAccountPO::getAddressNo,receiveAccount);
        UserReceiveAccountPO userReceiveAccountPO = this.baseMapper.selectOne(lqw);

        return ResponseVO.success(ConvertUtil.entityToModel(userReceiveAccountPO,UserReceiveAccountVO.class));

    }

    public ResponseVO<List<WithdrawCollectInfoVO>> getCollectInfo(UserReceiveAccountQueryVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String userId = CurrReqUtils.getOneId();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        String currencyCode = userInfoVO.getMainCurrency();
        String withdrawTypeCode = vo.getReceiveType();
        List<WithdrawCollectInfoVO> collectInfoVOS = systemWithdrawWayService.getWithdrawCollectInfoList(siteCode,withdrawTypeCode,currencyCode);
        if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)){
            for (WithdrawCollectInfoVO collectInfoVO:collectInfoVOS){
                if(!WithDrawCollectEnum.SURNAME.getType().equals(collectInfoVO.getFiledCode()) &&
                        !WithDrawCollectEnum.USER_PHONE.getType().equals(collectInfoVO.getFiledCode())  ){
                    collectInfoVO.setCheckFlag(false);
                }
            }

        }
        return ResponseVO.success(collectInfoVOS);
    }

    public ResponseVO<UserAccountBindBaseInfoVO> getBindBaseInfo() {
        String userId = CurrReqUtils.getOneId();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);


        List<BankManageVO> bankManageVOList = bankCardManagerService.bankList(userInfoVO.getMainCurrency());

        List<CodeValueVO> codeValueVOS = NetWorkTypeEnum.getList().stream().map(s -> CodeValueVO.builder()
                .code(s.getCode())
                .value(s.getName()).build()).toList();
        UserAccountBindBaseInfoVO userAccountBindBaseInfoVO = new UserAccountBindBaseInfoVO();
        userAccountBindBaseInfoVO.setBankList(bankManageVOList);
        userAccountBindBaseInfoVO.setNetWorkTypeList(codeValueVOS);

        return ResponseVO.success(userAccountBindBaseInfoVO);
    }

    public Boolean getAuthStatus(String currencyCode,String siteCode,Integer authStatus){

        Boolean isNeedAuth = false;
//        if(CurrencyEnum.CNY.getCode().equals(currencyCode)){
            String param = systemDictConfigApi.getByCode(DictCodeConfigEnums.WITHDRAW_REAL_NAME.getCode(),siteCode).getData().getConfigParam();
            if(YesOrNoEnum.YES.getCode().equals(param)  ){
                isNeedAuth = true;
            }
//        }

        return isNeedAuth;
    }

    public ResponseVO<Boolean> userReceiveAccountDefault(IdVO vo) {
        UserReceiveAccountPO userReceiveAccountPO = this.baseMapper.selectById(vo.getId());
        if(null == userReceiveAccountPO){
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        userReceiveAccountPO.setDefaultFlag(YesOrNoEnum.YES.getCode());
        //去掉其他账号默认值
        cleanDefaultFlag(userReceiveAccountPO.getReceiveType(),CurrReqUtils.getOneId());
        this.baseMapper.updateById(userReceiveAccountPO);
        return ResponseVO.success();
    }
    private void cleanDefaultFlag (String receiveType,String userId){
        LambdaUpdateWrapper<UserReceiveAccountPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserReceiveAccountPO::getDefaultFlag, YesOrNoEnum.NO.getCode());
        updateWrapper.eq(UserReceiveAccountPO::getUserId, userId)
                .eq(UserReceiveAccountPO::getReceiveType,receiveType);
        this.baseMapper.update(null,updateWrapper);
    }
}

//package com.cloud.baowang.system.service.site.agreement;
//
//import cn.hutool.core.util.ObjectUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.cloud.baowang.common.core.utils.ConvertUtil;
//import com.cloud.baowang.common.core.vo.base.ResponseVO;
//import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementEditVO;
//import com.cloud.baowang.system.api.vo.site.agreement.UserAgreementVO;
//import com.cloud.baowang.system.po.site.agreement.UserAgreementPO;
//import com.cloud.baowang.system.repositories.site.agreement.UserAgreementRepository;
//import com.cloud.baowang.system.service.language.LanguageManagerService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class UserAgreementService extends ServiceImpl<UserAgreementRepository, UserAgreementPO> {
//
//    private final UserAgreementRepository userAgreementRepository;
//    private final LanguageManagerService languageManagerService;
//
//    public List<UserAgreementVO> getList(String siteCode) {
//
//        ResponseVO<List<LanguageManagerListVO>> responseVO = languageManagerService.languageByList(siteCode);
//        List<LanguageManagerListVO> languageList = responseVO.getData();
//        Map<String, LanguageManagerListVO> cacheVOMap = languageList.stream().collect(Collectors.toMap(LanguageManagerListVO::getCode, p -> p, (k1, k2) -> k2));
//
//        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UserAgreementPO::getSiteCode, siteCode);
//        List<UserAgreementPO> poList = userAgreementRepository.selectList(queryWrapper);
//        List<UserAgreementVO> voList = new ArrayList<>();
//        Map<String, UserAgreementVO> agreementMap = new HashMap<>();
//        if (ObjectUtil.isNotEmpty(poList)) {
//            voList = ConvertUtil.entityListToModelList(poList, UserAgreementVO.class);
//            agreementMap = voList.stream().collect(Collectors.toMap(UserAgreementVO::getLanguage, p -> p, (k1, k2) -> k2));
//        }
//
//        List<UserAgreementVO> resultList = new ArrayList<>();
//        try {
//            for (LanguageManagerListVO languageVO : languageList) {
//                UserAgreementVO agreementVO = agreementMap.get(languageVO.getCode());
//                if (agreementVO == null) {
//                    agreementVO = new UserAgreementVO();
//                    agreementVO.setSiteCode(siteCode);
//                    agreementVO.setLanguage(languageVO.getCode());
//                    agreementVO.setLanguageName(languageVO.getName());
//                }
//                agreementVO.setLanguageName(languageVO.getName());
//                //agreementVO.setShowCode(languageVO.getShowCode());
//                resultList.add(agreementVO);
//            }
//            return resultList;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return new ArrayList<>();
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean saveAgreement(UserAgreementEditVO vo) {
//        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UserAgreementPO::getSiteCode, vo.getSiteCode());
//        queryWrapper.eq(UserAgreementPO::getLanguage, vo.getLanguage());
//        UserAgreementPO agreementPO = userAgreementRepository.selectOne(queryWrapper);
//
//        if (agreementPO == null) {
//            UserAgreementPO po = new UserAgreementPO();
//            BeanUtils.copyProperties(vo, po);
//            po.setCreator(vo.getUpdater());
//            po.setCreatedTime(System.currentTimeMillis());
//            userAgreementRepository.insert(po);
//            return true;
//        } else {
//            UserAgreementPO updatePO = new UserAgreementPO();
//            updatePO.setId(agreementPO.getId());
//            updatePO.setAgreement(vo.getAgreement());
//            updatePO.setUpdater(vo.getUpdater());
//            updatePO.setUpdatedTime(System.currentTimeMillis());
//            return this.updateById(updatePO);
//        }
//
//    }
//
//    public UserAgreementVO getAgreement(String siteCode, String language) {
//        LambdaQueryWrapper<UserAgreementPO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(UserAgreementPO::getSiteCode, siteCode);
//        queryWrapper.eq(UserAgreementPO::getLanguage, language);
//        UserAgreementPO agreementPO = userAgreementRepository.selectOne(queryWrapper);
//
//        return ConvertUtil.entityToModel(agreementPO, UserAgreementVO.class);
//    }
//}

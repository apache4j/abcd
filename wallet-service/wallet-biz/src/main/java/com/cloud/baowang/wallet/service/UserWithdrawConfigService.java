package com.cloud.baowang.wallet.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserVipWithdrawConfigAPPVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigVO;
import com.cloud.baowang.wallet.po.UserWithdrawConfigPO;
import com.cloud.baowang.wallet.repositories.UserWithdrawConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserWithdrawConfigService extends ServiceImpl<UserWithdrawConfigRepository, UserWithdrawConfigPO> {

    private final UserWithdrawConfigRepository userWithdrawConfigRepository;

    private final VipRankApi vipRankApi;


    private final VipGradeApi vipGradeApi;


    public Integer addUserWithdrawConfig(UserWithdrawConfigAddOrUpdateVO userWithdrawConfigAddVO) {
        UserWithdrawConfigPO userWithdrawConfigPO = new UserWithdrawConfigPO();
        BeanUtils.copyProperties(userWithdrawConfigAddVO, userWithdrawConfigPO);
        userWithdrawConfigPO.setCreatedTime(System.currentTimeMillis());
        return userWithdrawConfigRepository.insert(userWithdrawConfigPO);
    }

    public Integer updateUserWithdrawConfig(UserWithdrawConfigAddOrUpdateVO userWithdrawConfigAUpdateVO) {
        UserWithdrawConfigPO userWithdrawConfigPO = new UserWithdrawConfigPO();
        BeanUtils.copyProperties(userWithdrawConfigAUpdateVO, userWithdrawConfigPO);
        userWithdrawConfigPO.setUpdatedTime(System.currentTimeMillis());
        return userWithdrawConfigRepository.updateById(userWithdrawConfigPO);
    }

    public List<UserWithdrawConfigVO> listUserWithdrawConfig(UserWithdrawConfigRequestVO vo) {
        List<UserWithdrawConfigVO> userWithdrawConfigVOS = new ArrayList<>();
        try {
            LambdaQueryWrapper<UserWithdrawConfigPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserWithdrawConfigPO::getSiteCode, vo.getSiteCode());
            lqw.eq(UserWithdrawConfigPO::getCurrencyCode,vo.getCurrency());
            lqw.orderByAsc(UserWithdrawConfigPO::getVipRankCode);
            List<UserWithdrawConfigPO> userWithdrawConfigPOS = userWithdrawConfigRepository.selectList(lqw);
            userWithdrawConfigVOS = ConvertUtil.convertListToList(userWithdrawConfigPOS, new UserWithdrawConfigVO());
            if( null != userWithdrawConfigVOS && !userWithdrawConfigVOS.isEmpty()){

                if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
                    List<Integer> vipGradeCodes = userWithdrawConfigVOS.stream().map(UserWithdrawConfigVO::getVipGradeCode).toList();
                    if(!vipGradeCodes.isEmpty()){
                        Map<Integer,String> vipGradeMap  =  vipGradeApi.queryAllVIPGradeNameMap(vo.getSiteCode());
                        for (UserWithdrawConfigVO withdrawConfigVO:userWithdrawConfigVOS) {
                            withdrawConfigVO.setVipGradeCodeName(vipGradeMap.get(withdrawConfigVO.getVipGradeCode()));
                        }
                    }
                }else {
                    List<Integer> vipRankCodes = userWithdrawConfigVOS.stream().map(UserWithdrawConfigVO::getVipRankCode).toList();
                    List<SiteVIPRankVO> siteVIPRankVOS =  vipRankApi.getVipRankListBySiteCodeAndCodes(vo.getSiteCode(),vipRankCodes).getData();
                    Map<Integer,String> rankMap = siteVIPRankVOS.stream().collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
                    for (UserWithdrawConfigVO withdrawConfigVO:userWithdrawConfigVOS) {
                        withdrawConfigVO.setVipRankNameI18nCode(rankMap.get(withdrawConfigVO.getVipRankCode()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取用户提款信息列表失败,原因:{}",e.getMessage());
        }
        return userWithdrawConfigVOS;
    }


    public boolean checkVipRankUnique(String currencyCode, String vipRank, Long id) {
        LambdaQueryWrapper<UserWithdrawConfigPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserWithdrawConfigPO::getVipRankCode, vipRank);
        lqw.eq(UserWithdrawConfigPO::getCurrencyCode, currencyCode);
        lqw.ne(null != id, UserWithdrawConfigPO::getId, id);
        List<UserWithdrawConfigPO> businessAdminPOList = userWithdrawConfigRepository.selectList(lqw);
        return businessAdminPOList.isEmpty();
    }

    /**
     * 更新会员提款信息
     *
     * @param siteCode    站点code
     * @param vipRankCode 会员段位code
     * @param vos         需要修改的信息
     * @return true
     */
    @Transactional
    public Boolean updateBySiteCodeAndRankCode(String siteCode, Integer vipRankCode, List<UserWithdrawConfigAddOrUpdateVO> vos) {
        LambdaQueryWrapper<UserWithdrawConfigPO> query = Wrappers.lambdaQuery();
        query.eq(UserWithdrawConfigPO::getSiteCode, siteCode).eq(UserWithdrawConfigPO::getVipRankCode, vipRankCode);
        List<UserWithdrawConfigPO> list = this.list(query);
        //获取到当前站点当前段位对应的所有配置信息
        if (CollectionUtil.isNotEmpty(list)) {
            for (UserWithdrawConfigPO po : list) {
                for (UserWithdrawConfigAddOrUpdateVO vo : vos) {
                    if (po.getCurrencyCode().equals(vo.getCurrencyCode())) {
                        po.setSingleDayWithdrawCount(vo.getSingleDayWithdrawCount());
                        po.setSingleMaxWithdrawAmount(vo.getSingleMaxWithdrawAmount());
                        po.setDailyWithdrawalNumsLimit(vo.getDailyWithdrawalNumsLimit());
                        po.setDailyWithdrawAmountLimit(vo.getDailyWithdrawAmountLimit());
                        break;
                    }
                }
            }
            this.updateBatchById(list);
            return true;
        }
        return false;
    }

    /**
     * b
     * @param siteCode
     * @param vipGradeCode
     * @param vo
     * @return
     */
    @Transactional
    public Boolean updateBySiteCodeAndVipGradeCode( UserWithdrawConfigAddOrUpdateVO vo) {
        LambdaQueryWrapper<UserWithdrawConfigPO> query = Wrappers.lambdaQuery();
        query.eq(UserWithdrawConfigPO::getSiteCode, vo.getSiteCode())
            .eq(UserWithdrawConfigPO::getCurrencyCode,vo.getCurrencyCode())
                .eq(UserWithdrawConfigPO::getVipGradeCode, vo.getVipGradeCode());
        UserWithdrawConfigPO userWithdrawConfigPO = this.getOne(query);
        //获取到当前站点当前段位对应的所有配置信息
        if (null != userWithdrawConfigPO) {
            userWithdrawConfigPO.setSingleDayWithdrawCount(vo.getSingleDayWithdrawCount());
            userWithdrawConfigPO.setSingleMaxWithdrawAmount(vo.getSingleMaxWithdrawAmount());
            userWithdrawConfigPO.setDailyWithdrawalNumsLimit(vo.getDailyWithdrawalNumsLimit());
            userWithdrawConfigPO.setDailyWithdrawAmountLimit(vo.getDailyWithdrawAmountLimit());
            this.updateById(userWithdrawConfigPO);
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean initUserWithdrawConfigData(List<UserWithdrawConfigAddOrUpdateVO> vos) {
        if (CollectionUtil.isNotEmpty(vos)){
            String siteCode= vos.get(0).getSiteCode();
            List<UserWithdrawConfigPO> list= userWithdrawConfigRepository.selectList(Wrappers.lambdaQuery(UserWithdrawConfigPO.class)
                    .eq(UserWithdrawConfigPO::getSiteCode, siteCode));
            Map<String, List<UserWithdrawConfigPO>> currencyData = list.stream()
                    .collect(Collectors.groupingBy(UserWithdrawConfigPO::getCurrencyCode));
            vos = vos.stream()
                    .filter(item -> !currencyData.containsKey(item.getCurrencyCode()))  // 等同于 .filter(item -> map.containsKey(item))
                    .collect(Collectors.toList());
        }
        List<UserWithdrawConfigPO> userWithdrawConfigPOS = BeanUtil.copyToList(vos, UserWithdrawConfigPO.class);
        this.saveBatch(userWithdrawConfigPOS);
        return true;
    }

    public UserWithdrawConfigVO detailUserWithdrawConfig(IdVO idVO) {
        UserWithdrawConfigPO userWithdrawConfigPO =  userWithdrawConfigRepository.selectById(idVO.getId());
        UserWithdrawConfigVO userWithdrawConfigVO = ConvertUtil.entityToModel(userWithdrawConfigPO,UserWithdrawConfigVO.class);

        if(null != userWithdrawConfigVO.getVipGradeCode()){
            Map<Integer,String> vipGradeMap  =  vipGradeApi.queryAllVIPGradeNameMap(userWithdrawConfigPO.getSiteCode());
            userWithdrawConfigVO.setVipGradeCodeName(vipGradeMap.get(userWithdrawConfigVO.getVipGradeCode()));
        }
        SiteVIPRankVO vipRankVO =  vipRankApi.getVipRankBySiteCodeAndCode(userWithdrawConfigPO.getSiteCode(),userWithdrawConfigVO.getVipRankCode());
        userWithdrawConfigVO.setVipRankNameI18nCode(vipRankVO.getVipRankNameI18nCode());
        return  userWithdrawConfigVO;
    }


}

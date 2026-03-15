package com.cloud.baowang.wallet.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.VIPRankVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailQueryVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigDetailResponseVO;
import com.cloud.baowang.wallet.po.UserWithdrawConfigDetailPO;
import com.cloud.baowang.wallet.po.UserWithdrawConfigPO;
import com.cloud.baowang.wallet.repositories.UserWithdrawConfigDetailRepository;
import com.cloud.baowang.wallet.repositories.UserWithdrawConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class UserWithdrawConfigDetailService extends ServiceImpl<UserWithdrawConfigDetailRepository, UserWithdrawConfigDetailPO> {

    private final UserWithdrawConfigRepository userWithdrawConfigRepository;

    private final VipRankApi vipRankApi;



    public Integer setUserWithdrawConfigDetail(UserWithdrawConfigDetailAddOrUpdateVO userWithdrawConfigAddVO) {
        LambdaQueryWrapper<UserWithdrawConfigDetailPO> detailLqw = new LambdaQueryWrapper<>();
        detailLqw.eq(UserWithdrawConfigDetailPO::getUserId,userWithdrawConfigAddVO.getUserId());
        UserWithdrawConfigDetailPO userWithdrawConfigDetailPO = this.baseMapper.selectOne(detailLqw);
        int num = 0;
        if(null == userWithdrawConfigDetailPO){
            userWithdrawConfigDetailPO = new UserWithdrawConfigDetailPO();
            BeanUtils.copyProperties(userWithdrawConfigAddVO, userWithdrawConfigDetailPO);
            num =  this.baseMapper.insert(userWithdrawConfigDetailPO);
        }else{
            userWithdrawConfigDetailPO.setDayWithdrawCount(userWithdrawConfigAddVO.getDayWithdrawCount());
            userWithdrawConfigDetailPO.setMaxWithdrawAmount(userWithdrawConfigAddVO.getMaxWithdrawAmount());
            userWithdrawConfigDetailPO.setSingleDayWithdrawCount(userWithdrawConfigAddVO.getSingleDayWithdrawCount());
            userWithdrawConfigDetailPO.setSingleMaxWithdrawAmount(userWithdrawConfigAddVO.getSingleMaxWithdrawAmount());
            userWithdrawConfigDetailPO.setUpdatedTime(System.currentTimeMillis());
            num = this.baseMapper.updateById(userWithdrawConfigDetailPO);
        }
        return num;
    }


    /**
     * 获取会员提款配置
     * @param queryVO
     * @return
     */
    public UserWithdrawConfigDetailResponseVO getUserWithdrawConfigDetail(UserWithdrawConfigDetailQueryVO queryVO) {
        LambdaQueryWrapper<UserWithdrawConfigDetailPO> detailLqw = new LambdaQueryWrapper<>();
        detailLqw.eq(UserWithdrawConfigDetailPO::getUserId,queryVO.getUserId());
        UserWithdrawConfigDetailPO userWithdrawConfigDetailPO = this.baseMapper.selectOne(detailLqw);
        UserWithdrawConfigDetailResponseVO userWithdrawConfigDetailResponseVO = new UserWithdrawConfigDetailResponseVO();
        if(null != userWithdrawConfigDetailPO){
            userWithdrawConfigDetailResponseVO = ConvertUtil.entityToModel(userWithdrawConfigDetailPO,UserWithdrawConfigDetailResponseVO.class);
        }else{
            LambdaQueryWrapper<UserWithdrawConfigPO> withdrawConfigLqw = new LambdaQueryWrapper<>();
            withdrawConfigLqw.eq(UserWithdrawConfigPO::getSiteCode, queryVO.getSiteCode());
            withdrawConfigLqw.eq(UserWithdrawConfigPO::getCurrencyCode, queryVO.getCurrencyCode());
            withdrawConfigLqw.eq(UserWithdrawConfigPO::getVipRankCode, queryVO.getVipRankCode());
            UserWithdrawConfigPO userWithdrawConfigPO = userWithdrawConfigRepository.selectOne(withdrawConfigLqw);
            if(null != userWithdrawConfigPO ){
                userWithdrawConfigDetailResponseVO = ConvertUtil.entityToModel(userWithdrawConfigPO,UserWithdrawConfigDetailResponseVO.class);
                userWithdrawConfigDetailResponseVO.setDayWithdrawCount(userWithdrawConfigPO.getDailyWithdrawalNumsLimit());
                userWithdrawConfigDetailResponseVO.setMaxWithdrawAmount(userWithdrawConfigPO.getDailyWithdrawAmountLimit());
            }
        }
        if(StringUtils.isBlank(userWithdrawConfigDetailResponseVO.getUserAccount())){
            userWithdrawConfigDetailResponseVO.setUserAccount(queryVO.getUserAccount());
            userWithdrawConfigDetailResponseVO.setUserId(queryVO.getUserId());
        }
        SiteVIPRankVO vipRankVO =  vipRankApi.getVipRankBySiteCodeAndCode(queryVO.getSiteCode(),queryVO.getVipRankCode());
        userWithdrawConfigDetailResponseVO.setVipRankCodeName(vipRankVO.getVipRankNameI18nCode());
        return userWithdrawConfigDetailResponseVO;
    }

    /**
     * 恢复通用设置
     * @param userId
     * @return
     */
    public Integer resetUserWithdrawConfigDetail(String userId) {
        LambdaQueryWrapper<UserWithdrawConfigDetailPO> detailLqw = new LambdaQueryWrapper<>();
        detailLqw.eq(UserWithdrawConfigDetailPO::getUserId,userId);
        return  this.baseMapper.delete(detailLqw);
    }
}

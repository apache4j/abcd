package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.activity.UserActivityTypingAmountResp;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserActivityTypingChangeVO;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountPO;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountRecordPO;
import com.cloud.baowang.wallet.repositories.UserActivityTypingAmountRecordRepository;
import com.cloud.baowang.wallet.repositories.UserActivityTypingAmountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class UserActivityTypingAmountService extends ServiceImpl<UserActivityTypingAmountRepository, UserActivityTypingAmountPO> {

    private final UserActivityTypingAmountRecordRepository recordRepository;
    private final UserInfoApi userInfoApi;
    /**
     * 初始化加入存款活动信息
     */
    @DistributedLock(name = RedisConstants.TO_ACTIVITY_GAME_LOCK, unique = "#vo.userId + ':' + #vo.siteCode", waitTime = 3, leaseTime = 180)
    public boolean initUserActivityTypingAmountLimit(UserActivityTypingAmountVO vo) {
        log.info("存款活动:初始化添加游戏限制流水:{}", JSONObject.toJSONString(vo));
        LambdaQueryWrapper<UserActivityTypingAmountPO> wrapper = new LambdaQueryWrapper<UserActivityTypingAmountPO>()
                .eq(UserActivityTypingAmountPO::getUserId, vo.getUserId())
                .eq(UserActivityTypingAmountPO::getSiteCode, vo.getSiteCode());
        UserActivityTypingAmountPO existPO = this.baseMapper.selectOne(wrapper);
        UserActivityTypingAmountPO po = BeanUtil.copyProperties(vo, UserActivityTypingAmountPO.class);
        if (existPO == null) {
            return this.save(po);
        } else {
            LambdaUpdateWrapper<UserActivityTypingAmountPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserActivityTypingAmountPO::getLimitGameType,vo.getLimitGameType())
                    .eq(UserActivityTypingAmountPO::getUserId,vo.getUserId())
                    .eq(UserActivityTypingAmountPO::getSiteCode,vo.getSiteCode());
            return this.update(null,updateWrapper);
        }


    }


    /**
     * 更新打码量
     */

    public boolean updateUserActivityInfo(UserActivityTypingChangeVO vo) {
        LambdaQueryWrapper<UserActivityTypingAmountPO> wrapper = new LambdaQueryWrapper<UserActivityTypingAmountPO>()
                .eq(UserActivityTypingAmountPO::getUserId, vo.getUserId()).eq(UserActivityTypingAmountPO::getSiteCode, vo.getSiteCode())
                .eq(UserActivityTypingAmountPO::getLimitGameType, vo.getLimitGameType());
        UserActivityTypingAmountPO po = this.baseMapper.selectOne(wrapper);
        if (po == null) {
            return false;
        }
        LambdaUpdateWrapper<UserActivityTypingAmountPO> updateWrapper = new LambdaUpdateWrapper<UserActivityTypingAmountPO>()
                .eq(UserActivityTypingAmountPO::getUserId, vo.getUserId())
                .eq(UserActivityTypingAmountPO::getSiteCode, vo.getSiteCode())
                .eq(UserActivityTypingAmountPO::getLimitGameType, vo.getLimitGameType())
                .set(UserActivityTypingAmountPO::getTypingAmount, vo.getTypingAmount())
                .set(UserActivityTypingAmountPO::getStartTime, vo.getStartTime());
        this.baseMapper.update(po, updateWrapper);
        return true;
    }

    /**
     * 添加打码量
     */
    @DistributedLock(name = RedisConstants.TO_ACTIVITY_GAME_LOCK, unique = "#vo.userId + ':' + #vo.siteCode", waitTime = 3, leaseTime = 180)
    public boolean addUserActivityInfo(UserActivityTypingChangeVO vo) {
        log.info("添加打码量:添加游戏限制流水:{}", JSONObject.toJSONString(vo));
        LambdaQueryWrapper<UserActivityTypingAmountPO> wrapper = new LambdaQueryWrapper<UserActivityTypingAmountPO>()
                .eq(UserActivityTypingAmountPO::getUserId, vo.getUserId())
                .eq(UserActivityTypingAmountPO::getSiteCode, vo.getSiteCode());
        UserActivityTypingAmountPO po = this.baseMapper.selectOne(wrapper);
        if (po == null) {
            log.info("添加打码量:添加游戏限制流水,未查询到数据:{}", JSONObject.toJSONString(vo));
            return false;
        }
        LambdaUpdateWrapper<UserActivityTypingAmountPO> updateWrapper = new LambdaUpdateWrapper<UserActivityTypingAmountPO>()
                .eq(UserActivityTypingAmountPO::getUserId, vo.getUserId())
                .set(UserActivityTypingAmountPO::getTypingAmount,
                        Objects.requireNonNullElse(vo.getTypingAmount(), BigDecimal.ZERO)
                                .add(Objects.requireNonNullElse(po.getTypingAmount(), BigDecimal.ZERO)))
                .set(UserActivityTypingAmountPO::getStartTime, vo.getStartTime());
        this.baseMapper.update(null, updateWrapper);
        initActivityTypingRecord(vo,po);
        return true;
    }

    /**
     * 初始化流水记录
     */
    private void initActivityTypingRecord(UserActivityTypingChangeVO vo,UserActivityTypingAmountPO oldPO){
        UserInfoVO userInfoVO = userInfoApi.getByUserId(vo.getUserId());
        UserActivityTypingAmountRecordPO po = new UserActivityTypingAmountRecordPO();
        po.setUserAccount(userInfoVO.getUserAccount());
        po.setSiteCode(userInfoVO.getSiteCode());
        po.setUserId(userInfoVO.getUserId());
        po.setAccountType(userInfoVO.getAccountType());
        po.setCoinValue(vo.getTypingAmount());
        po.setCoinFrom(oldPO.getTypingAmount().compareTo(BigDecimal.ZERO) >0 ? oldPO.getTypingAmount() : BigDecimal.ZERO);
        po.setCoinTo(po.getCoinFrom().add(vo.getTypingAmount()));
        po.setAdjustWay(CommonConstant.business_one_str);
        po.setAdjustType(TypingAmountAdjustTypeEnum.ACTIVITY.getCode());
        po.setCurrency(userInfoVO.getMainCurrency());
        po.setCreatedTime(System.currentTimeMillis());
        po.setOrderNo(vo.getOrderNo());
        recordRepository.insert(po);
    }


    public BigDecimal getUserActivityTypingAmount(WalletUserInfoVO vo) {
        String siteCode = vo.getSiteCode();
        String userId = vo.getUserId();
        if (!StringUtils.isBlank(siteCode) && !StringUtils.isBlank(userId)) {
            LambdaQueryWrapper<UserActivityTypingAmountPO> wrapper = new LambdaQueryWrapper<UserActivityTypingAmountPO>().
                    eq(UserActivityTypingAmountPO::getUserId, userId).eq(UserActivityTypingAmountPO::getSiteCode, siteCode);
            UserActivityTypingAmountPO po = this.baseMapper.selectOne(wrapper);
            return po == null ? BigDecimal.ZERO : po.getTypingAmount();
        }
        return BigDecimal.ZERO;
    }


    public boolean checkUserActivityTypingLimit(String userId, String siteCode) {
        if (!StringUtils.isBlank(userId)) {
            LambdaQueryWrapper<UserActivityTypingAmountPO> wrapper = new LambdaQueryWrapper<UserActivityTypingAmountPO>().
                    eq(UserActivityTypingAmountPO::getUserId, userId).
                    eq(StringUtils.isNotBlank(siteCode),UserActivityTypingAmountPO::getSiteCode, siteCode);
            UserActivityTypingAmountPO po = this.baseMapper.selectOne(wrapper);
            if (Objects.isNull(po)) {
                return false;
            }
            return po.getTypingAmount().compareTo(BigDecimal.ZERO) > 0;
        }
        return false;
    }

    public UserActivityTypingAmountResp getUserActivityTypingLimit(String userId, String siteCode) {
        UserActivityTypingAmountResp resp = new UserActivityTypingAmountResp();
        if (!StringUtils.isBlank(siteCode) && !StringUtils.isBlank(userId)) {
            LambdaQueryWrapper<UserActivityTypingAmountPO> wrapper = new LambdaQueryWrapper<UserActivityTypingAmountPO>().
                    eq(UserActivityTypingAmountPO::getUserId, userId).eq(UserActivityTypingAmountPO::getSiteCode, siteCode);
            UserActivityTypingAmountPO po = this.baseMapper.selectOne(wrapper);
            if (Objects.isNull(po)) {
                return null;
            }
            BeanUtil.copyProperties(po, resp);
            return resp;
        }
        return null;
    }
}

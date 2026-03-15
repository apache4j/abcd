package com.cloud.baowang.play.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.po.CasinoMemberLoginPO;
import com.cloud.baowang.play.repositories.CasinoMemberLoginRepository;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberLoginVO;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CasinoMemberLoginService extends ServiceImpl<CasinoMemberLoginRepository, CasinoMemberLoginPO> {

    private final CasinoMemberLoginRepository casinoMemberLoginRepository;

    public CasinoMemberLoginVO getCasinoMemberLogin(CasinoMemberReq casinoMemberReq) {
        LambdaQueryWrapper<CasinoMemberLoginPO> wrapper = new LambdaQueryWrapper<CasinoMemberLoginPO>();
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueUserAccount()), CasinoMemberLoginPO::getVenueUserAccount, casinoMemberReq.getVenueUserAccount())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getUserAccount()), CasinoMemberLoginPO::getUserAccount, casinoMemberReq.getUserAccount())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getSiteCode()), CasinoMemberLoginPO::getSiteCode, casinoMemberReq.getSiteCode())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getUserId()), CasinoMemberLoginPO::getUserId, casinoMemberReq.getUserId())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueCode()), CasinoMemberLoginPO::getVenueCode, casinoMemberReq.getVenueCode());
        CasinoMemberLoginPO casinoMemberLoginPO = casinoMemberLoginRepository.selectOne(wrapper);
        if (casinoMemberLoginPO != null) {
            CasinoMemberLoginVO casinoMemberLoginVO = new CasinoMemberLoginVO();
            BeanUtils.copyProperties(casinoMemberLoginPO, casinoMemberLoginVO);
            return casinoMemberLoginVO;
        }
        return null;
    }

    public int updateLastLoginTimeById(CasinoMemberReq casinoMemberReq) {
        CasinoMemberLoginPO casinoMemberLoginPO = new CasinoMemberLoginPO();
        casinoMemberLoginPO.setId(casinoMemberReq.getId());
        casinoMemberLoginPO.setLastLoginTime(casinoMemberReq.getLoginTime());
        casinoMemberLoginPO.setVenuePlatform(casinoMemberReq.getVenuePlatform());
        casinoMemberLoginPO.setVenueCode(casinoMemberReq.getVenueCode());
        casinoMemberLoginPO.setVenueUserAccount(casinoMemberReq.getVenueUserAccount());
        return casinoMemberLoginRepository.updateById(casinoMemberLoginPO);
    }

    public int saveLastLoginTime(CasinoMemberLoginVO casinoMemberLoginVO) {
        CasinoMemberLoginPO casinoMemberLoginPO = new CasinoMemberLoginPO();
        BeanUtils.copyProperties(casinoMemberLoginVO, casinoMemberLoginPO);
        return casinoMemberLoginRepository.insert(casinoMemberLoginPO);
    }
}

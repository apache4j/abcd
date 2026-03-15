package com.cloud.baowang.play.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.po.CasinoMemberPO;
import com.cloud.baowang.play.repositories.CasinoMemberRepository;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CasinoMemberService extends ServiceImpl<CasinoMemberRepository, CasinoMemberPO> {

    private final CasinoMemberRepository casinoMemberRepository;

    private final VenueUserAccountConfig venueUserAccountConfig;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CasinoMemberPO existCasinoMember(CasinoMemberVO casinoMemberVO) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(CasinoMemberPO::getVenueCode, casinoMemberVO.getVenueCode())
                .eq(CasinoMemberPO::getVenuePlatform, casinoMemberVO.getVenuePlatform())
                .eq(CasinoMemberPO::getUserAccount, casinoMemberVO.getUserAccount())
                .eq(CasinoMemberPO::getSiteCode, casinoMemberVO.getSiteCode())
                .one();
    }

    public boolean addCasinoMember(CasinoMemberVO casinoMemberVO) {
        CasinoMemberPO casinoMember = new CasinoMemberPO();
        casinoMember.setVenueUserAccount(casinoMemberVO.getVenueUserAccount());
        casinoMember.setUserId(CurrReqUtils.getOneId());
        casinoMember.setUserAccount(casinoMemberVO.getUserAccount());
        casinoMember.setCasinoPassword(casinoMemberVO.getCasinoPassword());
        casinoMember.setVenueCode(casinoMemberVO.getVenueCode());
        casinoMember.setVenuePlatform(casinoMemberVO.getVenuePlatform());
        casinoMember.setVenueUserId(casinoMemberVO.getVenueUserId());
        casinoMember.setStatus(casinoMemberVO.getStatus());
        casinoMember.setCreatedTime(System.currentTimeMillis());
        casinoMember.setSiteCode(casinoMemberVO.getSiteCode());
        log.info("插入记录:{}",casinoMemberVO);
        boolean save = save(casinoMember);
        if (save){
            casinoMemberVO.setId(casinoMember.getId());
        }
        return save;
    }

    public void updateCasinoMember(CasinoMemberVO casinoMemberVO) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(CasinoMemberPO::getVenueCode, casinoMemberVO.getVenueCode())
                .eq(CasinoMemberPO::getSiteCode, casinoMemberVO.getSiteCode())
                .eq(CasinoMemberPO::getVenuePlatform, casinoMemberVO.getVenuePlatform())
                .eq(CasinoMemberPO::getUserAccount, casinoMemberVO.getUserAccount())
                .set(Objects.nonNull(casinoMemberVO.getUserId()), CasinoMemberPO::getUserId, casinoMemberVO.getUserId())
                .set(Strings.isNotBlank(casinoMemberVO.getVenueUserId()), CasinoMemberPO::getVenueUserId, casinoMemberVO.getVenueUserId())
                .set(CasinoMemberPO::getStatus, casinoMemberVO.getStatus())
                .set(CasinoMemberPO::getUpdatedTime, System.currentTimeMillis())
                .set(Strings.isNotEmpty(casinoMemberVO.getCasinoPassword()),CasinoMemberPO::getCasinoPassword, casinoMemberVO.getCasinoPassword())
                .update();
    }

    public CasinoMemberVO getCasinoMember(CasinoMemberReq casinoMemberReq) {
        LambdaQueryWrapper<CasinoMemberPO> wrapper = new LambdaQueryWrapper<CasinoMemberPO>();
        wrapper.eq(StringUtils.isNotBlank(casinoMemberReq.getCasinoPassword()), CasinoMemberPO::getCasinoPassword, casinoMemberReq.getCasinoPassword())
                .eq(StringUtils.isNotBlank(casinoMemberReq.getUserAccount()), CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccount())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenuePlatform()),CasinoMemberPO::getVenuePlatform, casinoMemberReq.getVenuePlatform())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueCode()),CasinoMemberPO::getVenueCode, casinoMemberReq.getVenueCode())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getStatus()),CasinoMemberPO::getStatus, casinoMemberReq.getStatus())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getSiteCode()),CasinoMemberPO::getSiteCode, casinoMemberReq.getSiteCode())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getUserId()),CasinoMemberPO::getUserId, casinoMemberReq.getUserId())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueUserId()),CasinoMemberPO::getVenueUserId, casinoMemberReq.getVenueUserId());
//        if ((VenueEnum.NEXTSPIN.getVenueCode().equals(casinoMemberReq.getVenuePlatform()) ||
//                VenueEnum.NEXTSPIN.getVenueCode().equals(casinoMemberReq.getVenueCode())) &&
//                StringUtils.isNotBlank(casinoMemberReq.getVenueUserAccount())) {
//            wrapper.apply("UPPER(venue_user_account) = '"+casinoMemberReq.getVenueUserAccount().toUpperCase()+"'");
//        }else{
//            wrapper.eq(StringUtils.isNotBlank(casinoMemberReq.getVenueUserAccount()),CasinoMemberPO::getVenueUserAccount, casinoMemberReq.getVenueUserAccount());
//        }
        if(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueUserAccount())){
            String userId = venueUserAccountConfig.getVenueUserAccount(casinoMemberReq.getVenueUserAccount());
            wrapper.eq(CasinoMemberPO::getUserId,userId);
        }

        CasinoMemberPO casinoMemberPO = casinoMemberRepository.selectOne(wrapper);
        if (casinoMemberPO != null) {
            CasinoMemberVO casinoMemberVO = new CasinoMemberVO();
            BeanUtils.copyProperties(casinoMemberPO, casinoMemberVO);
            return casinoMemberVO;
        }
        return null;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CasinoMemberVO getLatestCasinoMember(CasinoMemberReq casinoMemberReq) {
        LambdaQueryWrapper<CasinoMemberPO> wrapper = new LambdaQueryWrapper<CasinoMemberPO>();
        wrapper.eq(StringUtils.isNotBlank(casinoMemberReq.getCasinoPassword()), CasinoMemberPO::getCasinoPassword, casinoMemberReq.getCasinoPassword())
                .eq(StringUtils.isNotBlank(casinoMemberReq.getUserAccount()), CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccount())
                .eq(StringUtils.isNotBlank(casinoMemberReq.getVenueUserAccount()),CasinoMemberPO::getVenueUserAccount, casinoMemberReq.getVenueUserAccount())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenuePlatform()),CasinoMemberPO::getVenuePlatform, casinoMemberReq.getVenuePlatform())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueCode()),CasinoMemberPO::getVenueCode, casinoMemberReq.getVenueCode())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getStatus()),CasinoMemberPO::getStatus, casinoMemberReq.getStatus())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getSiteCode()),CasinoMemberPO::getSiteCode, casinoMemberReq.getSiteCode())
                .eq(ObjectUtil.isNotEmpty(casinoMemberReq.getUserId()),CasinoMemberPO::getUserId, casinoMemberReq.getUserId());

        CasinoMemberPO casinoMemberPO = casinoMemberRepository.selectOne(wrapper);
        if (casinoMemberPO != null) {
            CasinoMemberVO casinoMemberVO = new CasinoMemberVO();
            BeanUtils.copyProperties(casinoMemberPO, casinoMemberVO);
            return casinoMemberVO;
        }
        return null;
    }

    public List<CasinoMemberVO> getCasinoMembers(CasinoMemberReq casinoMemberReq) {
        LambdaQueryWrapper<CasinoMemberPO> wrapper = new LambdaQueryWrapper<CasinoMemberPO>();
        wrapper.eq(StringUtils.isNotBlank(casinoMemberReq.getUserAccount()), CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccount());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenuePlatform()),CasinoMemberPO::getVenuePlatform, casinoMemberReq.getVenuePlatform());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueCode()),CasinoMemberPO::getVenueCode, casinoMemberReq.getVenueCode());
        wrapper.in(CollUtil.isNotEmpty(casinoMemberReq.getVenueUserAccountList()),CasinoMemberPO::getVenueUserAccount, casinoMemberReq.getVenueUserAccountList());
        wrapper.in(CollUtil.isNotEmpty(casinoMemberReq.getUserAccountList()),CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccountList());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getSiteCode()),CasinoMemberPO::getSiteCode, casinoMemberReq.getSiteCode());

        List<CasinoMemberPO> casinoMemberPOS = casinoMemberRepository.selectList(wrapper);
        if (CollUtil.isNotEmpty(casinoMemberPOS)){

            return BeanUtil.copyToList(casinoMemberPOS, CasinoMemberVO.class);
        }
        return null;
    }

    public Map<String, CasinoMemberPO> getNextSpinCasinoMemberMap(CasinoMemberReq casinoMemberReq) {
        LambdaQueryWrapper<CasinoMemberPO> wrapper = new LambdaQueryWrapper<CasinoMemberPO>();
        wrapper.eq(StringUtils.isNotBlank(casinoMemberReq.getUserAccount()), CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccount());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenuePlatform()),CasinoMemberPO::getVenuePlatform, casinoMemberReq.getVenuePlatform());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueCode()),CasinoMemberPO::getVenueCode, casinoMemberReq.getVenueCode());
        wrapper.in(CollUtil.isNotEmpty(casinoMemberReq.getUserAccountList()),CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccountList());
        if ((VenueEnum.NEXTSPIN.getVenueCode().equals(casinoMemberReq.getVenuePlatform()) ||
                VenueEnum.NEXTSPIN.getVenueCode().equals(casinoMemberReq.getVenueCode())) &&
                CollUtil.isNotEmpty(casinoMemberReq.getVenueUserAccountList())) {
                List<String> resultList = casinoMemberReq.getVenueUserAccountList().stream()
                    .map(s -> s.split("_")).filter(parts -> parts.length > 1).map(parts -> parts[1]).collect(Collectors.toList());
                wrapper.in(CollUtil.isNotEmpty(resultList),CasinoMemberPO::getUserId, resultList);
        }
        List<CasinoMemberPO> casinoMemberPOS = casinoMemberRepository.selectList(wrapper);
        if (CollUtil.isNotEmpty(casinoMemberPOS)) {
            return casinoMemberPOS.stream()
                    .collect(Collectors.toMap(
                            p -> p.getVenueUserAccount().toUpperCase(),  // 将键转换为大写
                            p -> p,
                            (k1, k2) -> k2
                    ));
        }
        return null;
    }



    public Map<String, CasinoMemberPO> getCasinoMemberMap(CasinoMemberReq casinoMemberReq) {
        LambdaQueryWrapper<CasinoMemberPO> wrapper = new LambdaQueryWrapper<CasinoMemberPO>();
        wrapper.eq(StringUtils.isNotBlank(casinoMemberReq.getUserAccount()), CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccount());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenuePlatform()),CasinoMemberPO::getVenuePlatform, casinoMemberReq.getVenuePlatform());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberReq.getVenueCode()),CasinoMemberPO::getVenueCode, casinoMemberReq.getVenueCode());
        wrapper.in(CollUtil.isNotEmpty(casinoMemberReq.getUserAccountList()),CasinoMemberPO::getUserAccount, casinoMemberReq.getUserAccountList());
        wrapper.in(CollUtil.isNotEmpty(casinoMemberReq.getVenueUserAccountList()),CasinoMemberPO::getVenueUserAccount, casinoMemberReq.getVenueUserAccountList());
        List<CasinoMemberPO> casinoMemberPOS = casinoMemberRepository.selectList(wrapper);
        if (CollUtil.isNotEmpty(casinoMemberPOS)){
            return casinoMemberPOS.stream().collect(Collectors.toMap(CasinoMemberPO::getVenueUserAccount, p -> p ,(k1, k2)-> k2));
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePasswordById(CasinoMemberVO casinoMemberVO) {
        LambdaQueryWrapper<CasinoMemberPO> wrapper = new LambdaQueryWrapper<CasinoMemberPO>();
        wrapper.eq(StringUtils.isNotBlank(casinoMemberVO.getUserAccount()), CasinoMemberPO::getUserAccount, casinoMemberVO.getUserAccount());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberVO.getVenuePlatform()),CasinoMemberPO::getVenuePlatform, casinoMemberVO.getVenuePlatform());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberVO.getSiteCode()),CasinoMemberPO::getSiteCode, casinoMemberVO.getSiteCode());
        wrapper.eq(ObjectUtil.isNotEmpty(casinoMemberVO.getUserId()),CasinoMemberPO::getUserId, casinoMemberVO.getUserId());
        CasinoMemberPO po = this.getOne(wrapper);
        if(Objects.nonNull(po)){
            po.setCasinoPassword(casinoMemberVO.getCasinoPassword());
            casinoMemberRepository.updateById(po);
        }
    }
}

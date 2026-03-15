package com.cloud.baowang.system.service.areaLimit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.enums.areaLimit.AreaLimitTypeEnum;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerAddReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerEditReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerIdReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerStatusChangeReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.system.po.areaLimit.AreaLimitManagerPO;
import com.cloud.baowang.system.repositories.areaLimit.AreaLimitManagerRepository;
import com.cloud.baowang.system.service.member.BusinessAdminService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AreaLimitService extends ServiceImpl<AreaLimitManagerRepository, AreaLimitManagerPO> {

    private final BusinessAdminService businessAdminService;

    public ResponseVO<Page<AreaLimitManagerVO>> pageList(AreaLimitManagerReqVO vo) {
        String operator = null;
        if (Strings.isNotBlank(vo.getName())) {
            BusinessAdminVO adminVO = businessAdminService.getAdminByUserName(vo.getOperator());
            if (Optional.ofNullable(adminVO).map(BusinessAdminVO::getId).isPresent()) {
                operator = adminVO.getId();
            }
        }
        Page<AreaLimitManagerPO> page = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(StrUtil.isNotBlank(vo.getName()), AreaLimitManagerPO::getName, vo.getName())
                .eq(StrUtil.isNotBlank(vo.getAreaCode()), AreaLimitManagerPO::getAreaCode, vo.getAreaCode())
                .eq(ObjUtil.isNotEmpty(vo.getStatus()), AreaLimitManagerPO::getStatus, vo.getStatus())
                .eq(StrUtil.isNotBlank(operator), AreaLimitManagerPO::getOperator, operator)
                .orderByDesc(AreaLimitManagerPO::getOperatorTime)
                .page(new Page<>(vo.getPageNumber(), vo.getPageSize()));
        List<AreaLimitManagerPO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            Page<AreaLimitManagerVO> pageResult = new Page<>();
            BeanUtil.copyProperties(page, pageResult);
            return ResponseVO.success(pageResult);
        }
        List<String> list = records.stream().map(AreaLimitManagerPO::getOperator).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        list.addAll(records.stream().map(AreaLimitManagerPO::getCreator).filter(Strings::isNotBlank).toList());
        Map<String, String> adminMap = Optional.ofNullable(businessAdminService.getUserByIds(list))
                .filter(CollUtil::isNotEmpty).map(s -> s.stream().collect(Collectors.toMap(BusinessAdminVO::getId, BusinessAdminVO::getUserName))).orElse(Maps.newHashMap());
        List<AreaLimitManagerVO> result = Lists.newArrayList();
        records.forEach(areaLimitManagerPO -> {
            AreaLimitManagerVO areaLimitManagerVO = new AreaLimitManagerVO();
            BeanUtil.copyProperties(areaLimitManagerPO, areaLimitManagerVO);
            areaLimitManagerVO.setCreatorName(adminMap.get(areaLimitManagerPO.getCreator()));
            areaLimitManagerVO.setOperatorName(adminMap.get(areaLimitManagerPO.getOperator()));
            result.add(areaLimitManagerVO);
        });

        Page<AreaLimitManagerVO> pageResult = new Page<>();
        BeanUtil.copyProperties(page, pageResult);
        pageResult.setRecords(result);
        return ResponseVO.success(pageResult);
    }

    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(name = RedisKeyTransUtil.AREA_LIMIT_CHANGE, waitTime = 20, leaseTime = 30)
    public ResponseVO<Void> edit(AreaLimitManagerEditReqVO vo) {
        AreaLimitManagerPO po = getById(vo.getId());
        if (ObjUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 开启状态下变更
        if (po.getStatus().equals(CommonConstant.business_one)) {
            Integer type = po.getType();
            RSet<Object> countrySet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY);
            if (Objects.equals(type, AreaLimitTypeEnum.COUNTRY.getCode())) {
                countrySet.remove(vo.getAreaCode());

            }
            RSet<Object> ipSet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY);
            if (Objects.equals(type, AreaLimitTypeEnum.IP.getCode())) {
                ipSet.remove(vo.getName());
                RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY, ipSet);
            }

            if (Objects.equals(vo.getType(), AreaLimitTypeEnum.COUNTRY.getCode())) {
                countrySet.add(vo.getAreaCode());
            }
            if (Objects.equals(vo.getType(), AreaLimitTypeEnum.IP.getCode())) {
                ipSet.add(vo.getName());
            }

            RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY, countrySet);
            RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY, ipSet);
        }
        if (Objects.equals(vo.getType(), AreaLimitTypeEnum.IP.getCode())) {
//            vo.setAreaCode(Optional.ofNullable(IpAddressUtils.queryIpRegion(vo.getName())).map(IPResponse::getCountryCode).orElse(Strings.EMPTY));
            vo.setAreaCode(Optional.ofNullable(IpAPICoUtils.getIp(vo.getName())).map(IPRespVO::getCountryCode).orElse(Strings.EMPTY));
        }
        long now = System.currentTimeMillis();
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(AreaLimitManagerPO::getId, vo.getId())
                .set(StrUtil.isNotBlank(vo.getName()), AreaLimitManagerPO::getName, vo.getName())
                .set(ObjUtil.isNotEmpty(vo.getType()), AreaLimitManagerPO::getType, vo.getType())
                .set(StrUtil.isNotBlank(vo.getAreaCode()), AreaLimitManagerPO::getAreaCode, vo.getAreaCode())
                .set(AreaLimitManagerPO::getOperator, vo.getOperator())
                .set(AreaLimitManagerPO::getUpdater, now)
                .set(AreaLimitManagerPO::getOperatorTime, now)
                .set(AreaLimitManagerPO::getUpdatedTime, now)
                .update();
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> statusChange(AreaLimitManagerStatusChangeReqVO vo) {
        AreaLimitManagerPO po = getById(vo.getId());
        if (ObjUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        RLock lock = RedisUtil.getLock(RedisKeyTransUtil.AREA_LIMIT_CHANGE);
        try {
            boolean b = lock.tryLock(20000, 30000L, TimeUnit.MILLISECONDS);
            if (b) {
                Integer type = po.getType();
                // 开启
                if (vo.getStatus().equals(CommonConstant.business_one)) {
                    if (type.equals(AreaLimitTypeEnum.COUNTRY.getCode())) {
                        RSet<Object> countrySet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY);
                        countrySet.add(po.getAreaCode());
                        RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY, countrySet);
                    }

                    if (type.equals(AreaLimitTypeEnum.IP.getCode())) {
                        RSet<Object> ipSet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY);
                        ipSet.add(po.getName());
                        RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY, ipSet);
                    }
                }
                //关闭
                if (vo.getStatus().equals(CommonConstant.business_zero)) {
                    if (type.equals(AreaLimitTypeEnum.COUNTRY.getCode())) {
                        RSet<Object> countrySet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY);
                        countrySet.remove(po.getAreaCode());
                        RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY, countrySet);
                    }
                    if (type.equals(AreaLimitTypeEnum.IP.getCode())) {
                        RSet<Object> ipSet = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY);
                        ipSet.remove(po.getName());
                        RedisUtil.setSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY, ipSet);
                    }
                }
                long now = System.currentTimeMillis();
                // 开启关闭
                new LambdaUpdateChainWrapper<>(baseMapper)
                        .eq(AreaLimitManagerPO::getId, vo.getId())
                        .set(AreaLimitManagerPO::getStatus, vo.getStatus())
                        .set(AreaLimitManagerPO::getOperator, vo.getOperator())
                        .set(AreaLimitManagerPO::getUpdater, now)
                        .set(AreaLimitManagerPO::getOperatorTime, now)
                        .set(AreaLimitManagerPO::getUpdatedTime, now)
                        .update();
                return ResponseVO.success();
            }
        } catch (Exception e) {
            log.error("区域限制变更异常,error: ", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return ResponseVO.fail(ResultCode.SYSTEM_LOCK_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> del(AreaLimitManagerIdReqVO vo) {
        AreaLimitManagerPO po = getById(vo.getId());
        Integer type = po.getType();
        if (po.getStatus().equals(CommonConstant.business_one)) {
            if (type.equals(AreaLimitTypeEnum.COUNTRY.getCode())) {
                RSet<Object> set = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_COUNTRY_KEY);
                set.remove(po.getAreaCode());
            }
            if (type.equals(AreaLimitTypeEnum.IP.getCode())) {
                RSet<Object> set = RedisUtil.getSet(RedisConstants.KEY_AREA_LIMIT_IP_KEY);
                set.remove(po.getName());
            }
        }
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(AreaLimitManagerPO::getId, vo.getId())
                .remove();
        return ResponseVO.success();
    }

    public ResponseVO<AreaLimitManagerVO> info(AreaLimitManagerIdReqVO vo) {
        AreaLimitManagerPO po = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(AreaLimitManagerPO::getId, vo.getId())
                .one();
        AreaLimitManagerVO result = new AreaLimitManagerVO();
        BeanUtil.copyProperties(po, result);
        return ResponseVO.success(result);
    }

    public ResponseVO<Void> add(AreaLimitManagerAddReqVO vo) {
        AreaLimitManagerPO po = new AreaLimitManagerPO();
        BeanUtil.copyProperties(vo, po);
        long now = System.currentTimeMillis();
        po.setStatus(CommonConstant.business_zero);
        po.setOperator(vo.getOperator());
        po.setCreator(vo.getOperator());
        po.setUpdater(vo.getOperator());
        po.setCreatedTime(now);
        po.setUpdatedTime(now);
        po.setOperatorTime(now);
        save(po);
        return ResponseVO.success();
    }
}

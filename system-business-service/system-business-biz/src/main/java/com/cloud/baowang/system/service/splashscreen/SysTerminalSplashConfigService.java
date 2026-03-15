package com.cloud.baowang.system.service.splashscreen;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.StringUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.enums.TerminalSplashDeviceType;
import com.cloud.baowang.system.api.enums.ValidityPeriod;
import com.cloud.baowang.system.api.vo.splashscreen.*;
import com.cloud.baowang.system.po.banner.SiteBannerConfigPO;
import com.cloud.baowang.system.po.site.SysTerminalSplashConfigPO;
import com.cloud.baowang.system.repositories.splashscreen.SysTerminalSplashConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysTerminalSplashConfigService extends ServiceImpl<SysTerminalSplashConfigRepository, SysTerminalSplashConfigPO> {

    private final SysTerminalSplashConfigRepository sysRepository;
    private final I18nApi i18nApi;

    public ResponseVO<Page<SysTerminalSplashConfigRespVO>> pageList(SysTerminalSplashConfigRequestVO requestVO) {
        Page<SysTerminalSplashConfigPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        LambdaQueryWrapper<SysTerminalSplashConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ObjectUtil.isNotEmpty(requestVO.getTerminal()), SysTerminalSplashConfigPO::getTerminal, requestVO.getTerminal());
        wrapper.like(ObjectUtil.isNotEmpty(requestVO.getName()), SysTerminalSplashConfigPO::getName, requestVO.getName());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getStatus()), SysTerminalSplashConfigPO::getStatus, requestVO.getStatus());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getSiteCode()), SysTerminalSplashConfigPO::getSiteCode, requestVO.getSiteCode());
        wrapper.eq(ObjectUtil.isNotEmpty(requestVO.getCreator()), SysTerminalSplashConfigPO::getCreator, requestVO.getCreator());
        wrapper.orderByDesc(SysTerminalSplashConfigPO::getCreatedTime);

        IPage<SysTerminalSplashConfigPO> list = this.page(page, wrapper);
        // 当面时间不在，则修改状态为禁止用
        handleRecords(list.getRecords());
        Page<SysTerminalSplashConfigRespVO> voPage = new Page<>();
        BeanUtil.copyProperties(list, voPage);
        List<SysTerminalSplashConfigRespVO> volist = new ArrayList<>();
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        long nowTime = System.currentTimeMillis();
        if (ObjectUtil.isNotEmpty(list)) {
            list.getRecords().forEach(item -> {
                SysTerminalSplashConfigRespVO vo = new SysTerminalSplashConfigRespVO();
                BeanUtils.copyProperties(item, vo);
                String validityPeriod = vo.getValidityPeriod();
                if (ValidityPeriod.PERMANENT.getCode().toString().equals(validityPeriod)) {
                    vo.setIsEnable(yesCode);
                } else {
                    //看看有没有过有效期,如果有效期比当前时间小,说明已过期,则不允许启用了,同时设置启用状态为禁用
                    Long displayEndTime = vo.getEndTime();
                    if (displayEndTime == null) {
                        vo.setStatus(EnableStatusEnum.DISABLE.getCode());
                        vo.setIsEnable(noCode);
                    } else {
                        if (nowTime > displayEndTime) {
                            vo.setStatus(EnableStatusEnum.DISABLE.getCode());
                            vo.setIsEnable(noCode);
                        } else {
                            vo.setIsEnable(yesCode);
                        }
                    }
                }
                String terminal = item.getTerminal();
                if (StringUtils.isNotBlank(terminal)) {
                    String[] arrs = StringUtils.split(terminal, ",");
                    StringBuffer text = new StringBuffer();
                    for (String str : arrs) {
                        text.append(TerminalSplashDeviceType.nameByCode(Integer.valueOf(str))).append(",");
                    }
                    text.setLength(text.length() - 1);
                    vo.setTerminalText(text.toString());
                }
                volist.add(vo);
            });
        }
        voPage.setRecords(volist);
        return ResponseVO.success(voPage);
    }

    private void handleRecords(List<SysTerminalSplashConfigPO> records) {
        if (CollectionUtil.isNotEmpty(records)) {
            for (SysTerminalSplashConfigPO record : records) {
                if (record.getValidityPeriod().equals(CommonConstant.business_zero_str) && record.getStatus() == CommonConstant.business_one) {
                    long curr = System.currentTimeMillis();
                    // 当前时间不在开始与结束之间
                    if (curr < record.getStartTime() || curr > record.getEndTime()) {
                        // 修改status
                        record.setStatus(CommonConstant.business_zero);
                        SysTerminalSplashConfigPO update = new SysTerminalSplashConfigPO();
                        update.setStatus(CommonConstant.business_zero);
                        update.setId(record.getId());
                        this.baseMapper.updateById(update);
                        cleanCache(update.getTerminal());
                    }
                }
            }
        }
        // 更新redis

    }


    public List<SysTerminalSplashConfigAppRespVO> queryList(SysTerminalSplashConfigRequestVO requestVO) {
        String key = getCacheKey(requestVO.getTerminal());
        List<SysTerminalSplashConfigAppRespVO> list = RedisUtil.getList(key);
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }
        long time = System.currentTimeMillis();
        LambdaQueryWrapper<SysTerminalSplashConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SysTerminalSplashConfigPO::getTerminal, requestVO.getTerminal());
        queryWrapper.eq(SysTerminalSplashConfigPO::getSiteCode, requestVO.getSiteCode());
        queryWrapper.eq(SysTerminalSplashConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        queryWrapper.and(wrapper -> wrapper.eq(SysTerminalSplashConfigPO::getValidityPeriod, ValidityPeriod.LIMIT_TIME.getCode()).le(SysTerminalSplashConfigPO::getStartTime, time).ge(SysTerminalSplashConfigPO::getEndTime, time));
        List<SysTerminalSplashConfigPO> poList = super.list(queryWrapper);

        LambdaQueryWrapper<SysTerminalSplashConfigPO> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.like(SysTerminalSplashConfigPO::getTerminal, requestVO.getTerminal());
        queryWrapper2.eq(SysTerminalSplashConfigPO::getSiteCode, requestVO.getSiteCode());
        queryWrapper2.eq(SysTerminalSplashConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        queryWrapper2.eq(SysTerminalSplashConfigPO::getValidityPeriod, ValidityPeriod.PERMANENT.getCode());

        List<SysTerminalSplashConfigPO> poList2 = super.list(queryWrapper2);
        if (CollectionUtil.isNotEmpty(poList2)) {
            poList.addAll(poList2);
        }


        List<SysTerminalSplashConfigAppRespVO> voList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(poList)) {
            for (SysTerminalSplashConfigPO po : poList) {
                SysTerminalSplashConfigAppRespVO vo = new SysTerminalSplashConfigAppRespVO();
                BeanUtil.copyProperties(po, vo);
                voList.add(vo);
            }
        }
        //缓存值
        if (CollectionUtil.isNotEmpty(voList)) {
            RedisUtil.setList(key, voList, 10L, TimeUnit.MINUTES);
        }
        return voList;
    }

    public SysTerminalSplashConfigDetailVO queryDetail(String id) {
        SysTerminalSplashConfigPO po = this.getById(id);
        return BeanUtil.copyProperties(po, SysTerminalSplashConfigDetailVO.class);
    }

    public ResponseVO<Boolean> add(SysTerminalSplashConfigReqVO vo) {
        SysTerminalSplashConfigPO po = new SysTerminalSplashConfigPO();
        if (ValidityPeriod.LIMIT_TIME.getCode().toString().equals(vo.getValidityPeriod())) {
            if (vo.getEndTime() == null || vo.getStartTime() == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        BeanUtil.copyProperties(vo, po);
        String[] arrs = StringUtils.split(vo.getTerminal(), ",");
        Arrays.sort(arrs);
        String temp = StringUtils.join(arrs, ",");
        po.setTerminal(temp);
        po.setStatus(EnableStatusEnum.DISABLE.getCode());
        long time = System.currentTimeMillis();
        po.setCreatedTime(time);
        String bannerI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SITE_SPLASH_IMAGE.getCode());
        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(bannerI18, vo.getBannerUrlList());
        i18nApi.update(i18nData);
        po.setBannerUrl(bannerI18);
        sysRepository.insert(po);
        cleanCache(po.getTerminal());
        return ResponseVO.success();
    }


    public ResponseVO<Boolean> update(SysTerminalSplashConfigReqVO vo) {
        SysTerminalSplashConfigPO po = this.getById(vo.getId());
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (ValidityPeriod.LIMIT_TIME.getCode().toString().equals(vo.getValidityPeriod())) {
            if (vo.getEndTime() == null || vo.getStartTime() == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
        BeanUtil.copyProperties(vo, po);
        String[] arrs = StringUtils.split(vo.getTerminal(), ",");
        Arrays.sort(arrs);
        String temp = StringUtils.join(arrs, ",");
        po.setTerminal(temp);
        long time = System.currentTimeMillis();
        po.setUpdatedTime(time);
        if (ValidityPeriod.PERMANENT.getCode().toString().equals(vo.getValidityPeriod())) {
            po.setEndTime(null);
        }
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(po.getBannerUrl(), vo.getBannerUrlList());
        i18nApi.update(i18nData);
        sysRepository.updateById(po);
        cleanCache(po.getTerminal());
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> statusChange(SysTerminalSplashConfigRespVO vo) {
        String id = vo.getId();
        if (ObjectUtil.isEmpty(id)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SysTerminalSplashConfigPO po = this.getById(id);
        long nowTime = System.currentTimeMillis();
        Long displayEndTime = po.getEndTime();
        if (ValidityPeriod.LIMIT_TIME.getCode().toString().equals(po.getValidityPeriod()) && EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus())) {
            if (nowTime > displayEndTime) {
                throw new BaowangDefaultException(ResultCode.SYS_TERMINAL_SPLASH_EXPIRED);
            }
        }
        // 1.查询启用状态是否有，如果有，只能启用一个 2.终端类型只能启用一个，3.时间有效范围内，也只能有一个
        if (vo.getStatus() != null && vo.getStatus() == 1) {
            LambdaQueryWrapper<SysTerminalSplashConfigPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysTerminalSplashConfigPO::getStatus, vo.getStatus());
            queryWrapper.eq(StringUtils.isNotBlank(vo.getSiteCode()), SysTerminalSplashConfigPO::getSiteCode, vo.getSiteCode());
            List<SysTerminalSplashConfigPO> records = this.baseMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(records)) {
                String[] terminalArr = vo.getTerminal().split(CommonConstant.COMMA);
                for (String terminal : terminalArr) {
                    for (SysTerminalSplashConfigPO record : records) {
                        if (record.getTerminal().contains(terminal)) {
                            if (record.getValidityPeriod().equals(CommonConstant.business_one_str)) {
                                // 同一个类型，有开启的，则不能开启
                                throw new BaowangDefaultException(ResultCode.TERMINAL_ONE_ENABLE);
                            } else {
                                long curr = System.currentTimeMillis();
                                if (curr > record.getStartTime() && curr < record.getEndTime()) {
                                    throw new BaowangDefaultException(ResultCode.TERMINAL_ONE_ENABLE);
                                }
                            }
                        }
                    }
                }
            }

        }
        po.setStatus(vo.getStatus());
        long time = System.currentTimeMillis();
        po.setUpdatedTime(time);
        po.setUpdater(vo.getUpdater());
        LambdaUpdateWrapper<SysTerminalSplashConfigPO> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(SysTerminalSplashConfigPO::getId,po.getId());
        updateWrapper.set(SysTerminalSplashConfigPO::getUpdater,vo.getUpdater());
        updateWrapper.set(SysTerminalSplashConfigPO::getStatus,vo.getStatus());
        updateWrapper.set(SysTerminalSplashConfigPO::getUpdatedTime,time);
        sysRepository.update(null,updateWrapper);
        //sysRepository.updateById(po);
        cleanCache(po.getTerminal());
        return ResponseVO.success();
    }

    public ResponseVO<Boolean> delete(String id) {

        SysTerminalSplashConfigPO po = this.getById(id);
        if (ObjectUtil.isEmpty(po)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (po.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.SYS_TERMINAL_SPLASH_USED);
        }
        i18nApi.deleteByMsgKey(po.getBannerUrl());
        sysRepository.deleteById(id);
        //
        cleanCache(po.getTerminal());

        return ResponseVO.success();
    }

    private void cleanCache(String terminals) {
        if (StringUtils.isNotBlank(terminals)) {
            String[] terminalArr = terminals.split(CommonConstant.COMMA);
            for (String term : terminalArr) {
                RedisUtil.deleteKey(getCacheKey(term));
            }
        }
    }

    private String getCacheKey(String terminal) {
        return RedisConstants.getToSetSiteCodeKeyConstant(String.format("%s:%s", CurrReqUtils.getSiteCode(), terminal), RedisConstants.KEY_SPLASH_SCREEN);
    }
}

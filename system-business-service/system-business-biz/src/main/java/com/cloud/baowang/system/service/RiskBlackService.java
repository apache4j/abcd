package com.cloud.baowang.system.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.RiskBlackTypeEnum;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.po.risk.RiskCtrlBlackAccountPO;
import com.cloud.baowang.system.repositories.RiskCtrlBlackAccountRepository;
import com.cloud.baowang.system.service.member.BusinessAdminService;
import com.cloud.baowang.system.util.IpRangeUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountReqVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author rudger
 * @Date 2023.05.03
 */
@Slf4j
@Service
@AllArgsConstructor
public class RiskBlackService extends ServiceImpl<RiskCtrlBlackAccountRepository, RiskCtrlBlackAccountPO> {

    private final RiskCtrlBlackAccountRepository riskCtrlBlackAccountRepository;
    private final UserInfoApi userInfoApi;
    private final BusinessAdminService businessAdminService;


    public RiskBlackAccountVO getRiskByAccount(RiskInfoReqVO riskInfoReqVO) {
        LambdaQueryWrapper<RiskCtrlBlackAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getRiskControlTypeCode()), RiskCtrlBlackAccountPO::getRiskControlTypeCode, riskInfoReqVO.getRiskControlTypeCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getRiskControlAccount()), RiskCtrlBlackAccountPO::getRiskControlAccount, riskInfoReqVO.getRiskControlAccount());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getRiskControlAccountName()), RiskCtrlBlackAccountPO::getRiskControlAccountName, riskInfoReqVO.getRiskControlAccountName());
        RiskCtrlBlackAccountPO po = riskCtrlBlackAccountRepository.selectOne(queryWrapper);
        if (po == null) return null;

        RiskBlackAccountVO vo = new RiskBlackAccountVO();
        BeanUtils.copyProperties(po, vo);

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> save(RiskBlackAccountVO vo) {
        RiskCtrlBlackAccountPO po = new RiskCtrlBlackAccountPO();
        if (vo == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(vo, po);

        if (vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_REG_IP.getCode())
                || vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_LOGIN_IP.getCode())) {

            if (vo.getIpSegmentFlag()) {
                if (vo.getIpStart() == null || vo.getIpEnd() == null) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (!IPUtil.validateIP(vo.getIpEnd())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                if (!IPUtil.validateIP(vo.getIpStart())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setRiskControlAccount(vo.getIpStart() + "~" + vo.getIpEnd());
            } else {
                if (!IPUtil.validateIP(vo.getRiskControlAccount())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setIpWhitelist(null);
            }
            //NOTE 判断有没有跟原来的IP重叠
            LambdaQueryWrapper<RiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RiskCtrlBlackAccountPO::getRiskControlTypeCode, vo.getRiskControlTypeCode());
            List<RiskCtrlBlackAccountPO> pos = riskCtrlBlackAccountRepository.selectList(wrapper);
            AtomicBoolean isHasFlag = new AtomicBoolean(false);
            boolean belongToBlackFlag = true;
            if (vo.getRiskControlAccount().contains("~")) {
                for (RiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isRangeOverlap(vo.getIpStart(), vo.getIpEnd(), split[0], split[1]));
                    } else {
                        isHasFlag.set(IpRangeUtil.isIpInRange(PO.getRiskControlAccount(), vo.getIpStart(), vo.getIpEnd()));
                    }
                    if (isHasFlag.get()) {
                        return ResponseVO.failAppend(ResultCode.IP_LIST_EXIST, PO.getRiskControlAccount());
                    }
                }
            } else {
                for (RiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isIpInRange(vo.getRiskControlAccount(), split[0], split[1]));
                    } else {
                        isHasFlag.set(PO.getRiskControlAccount().equals(vo.getRiskControlAccount()));
                    }
                    if (isHasFlag.get()) {
                        return ResponseVO.failAppend(ResultCode.IP_LIST_EXIST, PO.getRiskControlAccount());
                    }
                }
            }

            if (CollUtil.isNotEmpty(vo.getIpWhitelist())) {
                //NOTE 判断白名单IP有没有属不属于本黑名单
                for (String ipStr : vo.getIpWhitelist()) {
                    if (ipStr.contains("~")) {
                        String[] split = ipStr.split("~");
                        belongToBlackFlag = IpRangeUtil.isRangeFullyContains(vo.getIpStart(), vo.getIpEnd(), split[0], split[1]);
                    } else {
                        belongToBlackFlag = IpRangeUtil.isIpInRange(ipStr, vo.getIpStart(), vo.getIpEnd());
                    }
                    if (!belongToBlackFlag) {
                        return ResponseVO.fail(ResultCode.IP_WHITE_NOT_EXIST);
                    }
                }
                po.setIpWhitelist(String.join(",", vo.getIpWhitelist()));
            } else {
                po.setIpWhitelist("");
            }

            //统计账户数量
            if (StringUtils.equalsAny(vo.getRiskControlAccount(),
                    RiskBlackTypeEnum.RISK_REG_IP.getCode(),
                    RiskBlackTypeEnum.RISK_LOGIN_IP.getCode(),
                    RiskBlackTypeEnum.RISK_REG_DEVICE.getCode(),
                    RiskBlackTypeEnum.RISK_LOGIN_DEVICE.getCode())) {
                ResponseVO<Long> rCount = getAccountNum(vo);
                if (!rCount.isOk()) {
                    log.error("用户服务调用失败,{}", rCount.getMessage());
                    return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
                }
                Long data = rCount.getData();
                po.setRiskCount(data.intValue());
            }
        }

        riskCtrlBlackAccountRepository.insert(po);
        return ResponseVO.success(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> updateBlackAccount(RiskBlackAccountVO vo) {

        String id = vo.getId();
        RiskCtrlBlackAccountPO po = riskCtrlBlackAccountRepository.selectById(id);
        if (po == null) {
            ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        if (vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_REG_IP.getCode())
                || vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_LOGIN_IP.getCode())) {

            if (vo.getIpSegmentFlag()) {
                if (vo.getIpStart() == null || vo.getIpEnd() == null) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                if (!IPUtil.validateIP(vo.getIpEnd())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                if (!IPUtil.validateIP(vo.getIpStart())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setRiskControlAccount(vo.getIpStart() + "~" + vo.getIpEnd());
            } else {
                if (!IPUtil.validateIP(vo.getRiskControlAccount())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setIpWhitelist(null);
            }

            //NOTE 判断有没有跟原来的IP重叠
            LambdaQueryWrapper<RiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ObjectUtil.isNotEmpty(vo.getRiskControlTypeCode()), RiskCtrlBlackAccountPO::getRiskControlTypeCode, vo.getRiskControlTypeCode());
            wrapper.ne(ObjectUtil.isNotEmpty(vo.getId()), RiskCtrlBlackAccountPO::getId, vo.getId());
            List<RiskCtrlBlackAccountPO> pos = riskCtrlBlackAccountRepository.selectList(wrapper);
            AtomicBoolean isHasFlag = new AtomicBoolean(false);
            boolean belongToBlackFlag = true;
            if (vo.getRiskControlAccount().contains("~")) {
                for (RiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isRangeOverlap(vo.getIpStart(), vo.getIpEnd(), split[0], split[1]));
                    } else {
                        isHasFlag.set(IpRangeUtil.isIpInRange(PO.getRiskControlAccount(), vo.getIpStart(), vo.getIpEnd()));
                    }
                    if (isHasFlag.get()) {
                        return ResponseVO.failAppend(ResultCode.IP_LIST_EXIST, PO.getRiskControlAccount());
                    }
                }
            } else {
                for (RiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isIpInRange(vo.getRiskControlAccount(), split[0], split[1]));
                    } else {
                        isHasFlag.set(PO.getRiskControlAccount().equals(vo.getRiskControlAccount()));
                    }
                    if (isHasFlag.get()) {
                        return ResponseVO.failAppend(ResultCode.IP_LIST_EXIST, PO.getRiskControlAccount());
                    }
                }
            }

            if (CollUtil.isNotEmpty(vo.getIpWhitelist())) {
                //NOTE 判断白名单IP有没有属不属于本黑名单
                for (String ipStr : vo.getIpWhitelist()) {
                    if (ipStr.contains("~")) {
                        String[] split = ipStr.split("~");
                        if (!IPUtil.validateIP(split[0]) || !IPUtil.validateIP(split[1])) {
                            throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                        }
                        belongToBlackFlag = IpRangeUtil.isRangeFullyContains(vo.getIpStart(), vo.getIpEnd(), split[0], split[1]);
                    } else {
                        if (!IPUtil.validateIP(ipStr)) {
                            throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                        }
                        belongToBlackFlag = IpRangeUtil.isIpInRange(ipStr, vo.getIpStart(), vo.getIpEnd());
                    }
                    if (!belongToBlackFlag) {
                        return ResponseVO.fail(ResultCode.IP_WHITE_NOT_EXIST);
                    }
                }
                po.setIpWhitelist(String.join(",", vo.getIpWhitelist()));
            } else {
                po.setIpWhitelist("");
            }
        }

        po.setUpdater(vo.getUpdater());
        po.setUpdatedTime(vo.getUpdatedTime());
        po.setRemark(vo.getRemark());
        po.setRiskControlAccount(vo.getRiskControlAccount());
        po.setRiskControlAccountName(vo.getRiskControlAccountName());
        if (StringUtils.equalsAny(vo.getRiskControlAccount(),
                RiskBlackTypeEnum.RISK_REG_IP.getCode(),
                RiskBlackTypeEnum.RISK_LOGIN_IP.getCode(),
                RiskBlackTypeEnum.RISK_REG_DEVICE.getCode(),
                RiskBlackTypeEnum.RISK_LOGIN_DEVICE.getCode())) {
            ResponseVO<Long> rCount = getAccountNum(vo);
            if (!rCount.isOk()) {
                log.error("用户服务调用失败,{}", rCount.getMessage());
                return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
            }
            Long data = rCount.getData();
            po.setRiskCount(data.intValue());
        }

        updateById(po);
        return ResponseVO.success(true);
    }

    private ResponseVO<Long> getAccountNum(RiskBlackAccountVO vo) {
        RiskUserBlackAccountReqVO requestVO = new RiskUserBlackAccountReqVO();
        requestVO.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        requestVO.setRiskControlAccount(vo.getRiskControlAccount());
        ResponseVO<Long> rCount = userInfoApi.getCountByBlackAccount(requestVO);
        return rCount;
    }

    public ResponseVO<Boolean> removeBlackAccount(IdVO vo) {
        removeById(vo.getId());
        return ResponseVO.success(true);
    }

    /**
     * 构建风控账号查询字符串
     * 规则：
     * 1. 两个字段都有值 => account|name
     * 2. 只有 account 有值 => account
     * 3. 只有 name 有值 => name
     * 4. 两个都没有值 => 返回 null
     */
   /* public String buildRiskControlAccount(String account, String name) {
        if (ObjectUtil.isNotEmpty(account) && ObjectUtil.isNotEmpty(name)) {
            return account + "|" + name;
        } else if (ObjectUtil.isNotEmpty(account)) {
            return account;
        } else if (ObjectUtil.isNotEmpty(name)) {
            return name; // 不再加前导
        }
        return null;
    }*/
    public ResponseVO<Page<RiskBlackAccountVO>> getRiskBlackListPage(RiskBlackAccountReqVO reqVO) {
        //String riskControlAccount = buildRiskControlAccount(reqVO.getRiskControlAccount(), reqVO.getRiskControlAccountName());

        //reqVO.setRiskControlAccount(riskControlAccount);
        LambdaQueryWrapper<RiskCtrlBlackAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getRiskControlTypeCode()), RiskCtrlBlackAccountPO::getRiskControlTypeCode, reqVO.getRiskControlTypeCode());
        queryWrapper.like(ObjectUtil.isNotEmpty(reqVO.getRiskControlAccount()), RiskCtrlBlackAccountPO::getRiskControlAccount, reqVO.getRiskControlAccount());
        queryWrapper.like(ObjectUtil.isNotEmpty(reqVO.getRiskControlAccountName()), RiskCtrlBlackAccountPO::getRiskControlAccountName, reqVO.getRiskControlAccountName());
        queryWrapper.ge(ObjectUtil.isNotEmpty(reqVO.getCreateBeginTime()), RiskCtrlBlackAccountPO::getCreatedTime, reqVO.getCreateBeginTime());
        queryWrapper.le(ObjectUtil.isNotEmpty(reqVO.getCreateEndTime()), RiskCtrlBlackAccountPO::getCreatedTime, reqVO.getCreateEndTime());
        queryWrapper.orderByDesc(RiskCtrlBlackAccountPO::getCreatedTime);

        Page<RiskCtrlBlackAccountPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<RiskCtrlBlackAccountPO> riskBlackAccountPOPage = riskCtrlBlackAccountRepository.selectPage(page, queryWrapper);
        Page<RiskBlackAccountVO> riskBlackAccountVOPage = new Page<>();
        BeanUtil.copyProperties(riskBlackAccountPOPage, riskBlackAccountVOPage);
        if (riskBlackAccountVOPage.getTotal() > 0) {
            List<RiskBlackAccountVO> riskBlackAccountVOS = new ArrayList<>();
            riskBlackAccountPOPage.getRecords().forEach(e -> {
                RiskBlackAccountVO riskBlackAccountVO = BeanUtil.copyProperties(e, RiskBlackAccountVO.class);
                riskBlackAccountVO.setCreatorName(e.getCreator());
                riskBlackAccountVO.setUpdaterName(e.getUpdater());
                if (RiskBlackTypeEnum.RISK_REG_IP.getCode().equals(e.getRiskControlTypeCode()) || RiskBlackTypeEnum.RISK_LOGIN_IP.getCode().equals(e.getRiskControlTypeCode())) {
                    if (StrUtil.isNotEmpty(e.getIpWhitelist())) {
                        riskBlackAccountVO.setIpWhitelist(Arrays.stream(e.getIpWhitelist().split(",")).toList());
                    }

                    if (e.getRiskControlAccount().contains("~")) {
                        String[] split = e.getRiskControlAccount().split("~");
                        riskBlackAccountVO.setIpSegmentFlag(true);
                        riskBlackAccountVO.setIpStart(split[0]);
                        riskBlackAccountVO.setIpEnd(split[1]);
                    }
                }
                riskBlackAccountVOS.add(riskBlackAccountVO);
            });
            riskBlackAccountVOPage.setRecords(riskBlackAccountVOS);

        }
        return ResponseVO.success(riskBlackAccountVOPage);
    }

    public ResponseVO<List<RiskBlackAccountVO>> getRiskBlack(RiskBlackAccountVO queryVO) {
        LambdaQueryWrapper<RiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(queryVO.getRiskControlAccount())) {
            wrapper.eq(RiskCtrlBlackAccountPO::getRiskControlAccount, queryVO.getRiskControlAccount());
        }

        if (StringUtils.isNotEmpty(queryVO.getRiskControlTypeCode())) {
            wrapper.eq(RiskCtrlBlackAccountPO::getRiskControlTypeCode, queryVO.getRiskControlTypeCode());
        }
        List<RiskCtrlBlackAccountPO> pos = riskCtrlBlackAccountRepository.selectList(wrapper);
        if (CollectionUtil.isEmpty(pos)) {
            return ResponseVO.success(Lists.newArrayList());
        }
        List<RiskBlackAccountVO> vos = BeanUtil.copyToList(pos, RiskBlackAccountVO.class);
        return ResponseVO.success(vos);
    }

    public ResponseVO<Boolean> getRiskIpBlack(RiskBlackAccountVO queryVO) {
        LambdaQueryWrapper<RiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
        //wrapper.eq(StringUtils.isNotEmpty(queryVO.getRiskControlAccount()), SiteRiskCtrlBlackAccountPO::getRiskControlAccount, queryVO.getRiskControlAccount());
        wrapper.eq(StringUtils.isNotEmpty(queryVO.getRiskControlTypeCode()), RiskCtrlBlackAccountPO::getRiskControlTypeCode, queryVO.getRiskControlTypeCode());
        List<RiskCtrlBlackAccountPO> pos = riskCtrlBlackAccountRepository.selectList(wrapper);
        boolean ipInRange = false;
        if (!CollectionUtil.isEmpty(pos)) {
            for (RiskCtrlBlackAccountPO po : pos) {
                if (po.getRiskControlAccount().contains("~")) {
                    String[] split = po.getRiskControlAccount().split("~");
                    ipInRange = IpRangeUtil.isIpInRange(queryVO.getRiskControlAccount(), split[0], split[1]);
                    if (ipInRange) {
                        if (StrUtil.isNotEmpty(po.getIpWhitelist())) {
                            String[] whiteList = po.getIpWhitelist().split(",");
                            for (String ipStr : whiteList) {
                                if (ipStr.contains("~")) {
                                    String[] split1 = ipStr.split("~");
                                    ipInRange = IpRangeUtil.isIpInRange(queryVO.getRiskControlAccount(), split[0], split[1]);
                                } else {
                                    ipInRange = queryVO.getRiskControlAccount().equals(ipStr);
                                }
                                if (ipInRange) {
                                    //NOTE 如是在白名单中， 取反，通过
                                    return ResponseVO.success(false);
                                }
                            }
                        }
                    }
                } else {
                    ipInRange = queryVO.getRiskControlAccount().equals(po.getRiskControlAccount());
                }
                if (ipInRange) {
                    return ResponseVO.success(true);
                }
            }
        }
        return ResponseVO.success(false);
    }
}

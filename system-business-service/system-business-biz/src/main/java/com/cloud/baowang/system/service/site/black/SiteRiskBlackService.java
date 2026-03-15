package com.cloud.baowang.system.service.site.black;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
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
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountIsBlackReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.po.risk.RiskCtrlBlackAccountPO;
import com.cloud.baowang.system.po.site.black.SiteRiskCtrlBlackAccountPO;
import com.cloud.baowang.system.repositories.RiskCtrlBlackAccountRepository;
import com.cloud.baowang.system.repositories.site.black.SiteRiskCtrlBlackAccountRepository;
import com.cloud.baowang.system.service.member.BusinessAdminService;
import com.cloud.baowang.system.util.IpRangeUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountReqVO;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
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
public class SiteRiskBlackService extends ServiceImpl<SiteRiskCtrlBlackAccountRepository, SiteRiskCtrlBlackAccountPO> {

    private final SiteRiskCtrlBlackAccountRepository siteRiskCtrlBlackAccountRepository;
    private final UserInfoApi userInfoApi;
    private final BusinessAdminService businessAdminService;

    private final RiskCtrlBlackAccountRepository riskCtrlBlackAccountRepository;


    public RiskBlackAccountVO getRiskByAccount(RiskInfoReqVO riskInfoReqVO) {
        LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getRiskControlTypeCode()), SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, riskInfoReqVO.getRiskControlTypeCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getRiskControlAccount()), SiteRiskCtrlBlackAccountPO::getRiskControlAccount, riskInfoReqVO.getRiskControlAccount());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getRiskControlAccountName()), SiteRiskCtrlBlackAccountPO::getRiskControlAccountName, riskInfoReqVO.getRiskControlAccountName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskInfoReqVO.getSiteCode()), SiteRiskCtrlBlackAccountPO::getSiteCode, riskInfoReqVO.getSiteCode());
        SiteRiskCtrlBlackAccountPO po = siteRiskCtrlBlackAccountRepository.selectOne(queryWrapper);
        if (po == null) return null;

        RiskBlackAccountVO vo = new RiskBlackAccountVO();
        BeanUtils.copyProperties(po, vo);

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> save(RiskBlackAccountVO vo) {


        SiteRiskCtrlBlackAccountPO po = new SiteRiskCtrlBlackAccountPO();
        if (vo == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        BeanUtils.copyProperties(vo, po);

        if (vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_REG_IP.getCode())
                || vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_LOGIN_IP.getCode())) {

            if (vo.getIpSegmentFlag()){
                if (vo.getIpStart()==null || vo.getIpEnd()==null){
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (!IPUtil.validateIP(vo.getIpEnd())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                if (!IPUtil.validateIP(vo.getIpStart())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setRiskControlAccount(vo.getIpStart() + "~" + vo.getIpEnd());
            }else {
                if (!IPUtil.validateIP(vo.getRiskControlAccount())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setIpWhitelist(null);
            }

            //NOTE 判断有没有跟原来的IP重叠
            LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ObjectUtil.isNotEmpty(vo.getSiteCode()), SiteRiskCtrlBlackAccountPO::getSiteCode, vo.getSiteCode());
            wrapper.eq(ObjectUtil.isNotEmpty(vo.getRiskControlTypeCode()), SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, vo.getRiskControlTypeCode());
            List<SiteRiskCtrlBlackAccountPO> pos = siteRiskCtrlBlackAccountRepository.selectList(wrapper);
            AtomicBoolean isHasFlag = new AtomicBoolean(false);
            boolean belongToBlackFlag = true;
            if (vo.getRiskControlAccount().contains("~")) {
                for (SiteRiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isRangeOverlap(vo.getIpStart(), vo.getIpEnd(), split[0], split[1]));
                    } else {
                        isHasFlag.set(IpRangeUtil.isIpInRange(PO.getRiskControlAccount(), vo.getIpStart(), vo.getIpEnd()));
                    }
                    if (isHasFlag.get()){
                        return ResponseVO.failAppend(ResultCode.IP_LIST_EXIST, PO.getRiskControlAccount());
                    }
                }
            } else {
                for (SiteRiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isIpInRange(vo.getRiskControlAccount(), split[0], split[1]));
                    } else {
                        isHasFlag.set(PO.getRiskControlAccount().equals(vo.getRiskControlAccount()));
                    }
                    if (isHasFlag.get()){
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
            }else {
                po.setIpWhitelist("");
            }
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
        po.setRiskControlAccount(vo.getRiskControlAccount());

        if (vo.getRiskControlTypeCode().equals("1") || vo.getRiskControlTypeCode().equals("2")) {
            if (CollUtil.isNotEmpty(vo.getIpWhitelist())) {
                po.setIpWhitelist(String.join(",", vo.getIpWhitelist()));
            }
            if (StrUtil.isNotEmpty(vo.getIpStart()) || StrUtil.isNotEmpty(vo.getIpEnd())) {
                po.setRiskControlAccount(vo.getIpStart() + "~" + vo.getIpEnd());
            }
        }
        siteRiskCtrlBlackAccountRepository.insert(po);
        return ResponseVO.success(true);
    }

    public ResponseVO<Boolean> checkBlackAccount(RiskBlackAccountVO vo) {
        // 校验入参数
        if (ObjectUtil.isEmpty(vo.getRiskControlAccount())) {
            log.info("站点 riskControlAccount 是空，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (vo.getRiskControlAccount().contains("|")) {
            log.info("站点 riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (ObjectUtil.isNotEmpty(vo.getRiskControlAccountName())) {
            if (vo.getRiskControlAccountName().contains("|")) {
                log.info("站点 riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }
        if (vo.getRiskControlAccount().contains("|")) {
            log.info("站点 riskControlAccount 参数不合法，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (ObjectUtil.isEmpty(vo.getRiskControlTypeCode())) {
            log.info("站点 riskControlTypeCode 是空，参数：{}", JSON.toJSONString(vo));
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> updateBlackAccount(RiskBlackAccountVO vo) {
        ResponseVO<Boolean> booleanResponseVO = checkBlackAccount(vo);
        if (ObjectUtil.isNotNull(booleanResponseVO)) {
            return booleanResponseVO;
        }

        String id = vo.getId();
        SiteRiskCtrlBlackAccountPO po = siteRiskCtrlBlackAccountRepository.selectById(id);
        if (po == null) {
            ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }else {
            po.setRiskControlAccountName(vo.getRiskControlAccountName());
        }

        if (vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_REG_IP.getCode()) || vo.getRiskControlTypeCode().equals(RiskBlackTypeEnum.RISK_LOGIN_IP.getCode())) {

            if (vo.getIpSegmentFlag()){
                if (vo.getIpStart()==null || vo.getIpEnd()==null){
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }

                if (!IPUtil.validateIP(vo.getIpEnd())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                if (!IPUtil.validateIP(vo.getIpStart())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setRiskControlAccount(vo.getIpStart() + "~" + vo.getIpEnd());
            }else {
                if (!IPUtil.validateIP(vo.getRiskControlAccount())) {
                    throw new BaowangDefaultException(ResultCode.IP_FORMAT_ERROR);
                }
                vo.setIpWhitelist(null);
            }

            //NOTE 判断有没有跟原来的IP重叠
            LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ObjectUtil.isNotEmpty(vo.getSiteCode()), SiteRiskCtrlBlackAccountPO::getSiteCode, vo.getSiteCode());
            wrapper.eq(ObjectUtil.isNotEmpty(vo.getRiskControlTypeCode()), SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, vo.getRiskControlTypeCode());
            wrapper.ne(ObjectUtil.isNotEmpty(vo.getId()), SiteRiskCtrlBlackAccountPO::getId, vo.getId());
            List<SiteRiskCtrlBlackAccountPO> pos = siteRiskCtrlBlackAccountRepository.selectList(wrapper);
            AtomicBoolean isHasFlag = new AtomicBoolean(false);
            boolean belongToBlackFlag = true;



            if (vo.getRiskControlAccount().contains("~")) {
                for (SiteRiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isRangeOverlap(vo.getIpStart(), vo.getIpEnd(), split[0], split[1]));
                    } else {
                        isHasFlag.set(IpRangeUtil.isIpInRange(PO.getRiskControlAccount(), vo.getIpStart(), vo.getIpEnd()));
                    }
                    if (isHasFlag.get()){
                        return ResponseVO.failAppend(ResultCode.IP_LIST_EXIST, PO.getRiskControlAccount());
                    }
                }
            } else {
                for (SiteRiskCtrlBlackAccountPO PO : pos) {
                    if (PO.getRiskControlAccount().contains("~")) {
                        String[] split = PO.getRiskControlAccount().split("~");
                        isHasFlag.set(IpRangeUtil.isIpInRange(vo.getRiskControlAccount(), split[0], split[1]));
                    } else {
                        isHasFlag.set(PO.getRiskControlAccount().equals(vo.getRiskControlAccount()));
                    }
                    if (isHasFlag.get()){
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
            }else {
                po.setIpWhitelist("");
            }
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
        po.setUpdater(vo.getUpdater());
        po.setUpdatedTime(vo.getUpdatedTime());
        po.setRemark(vo.getRemark());
        po.setRiskControlAccount(vo.getRiskControlAccount());
        updateById(po);
        return ResponseVO.success(true);
    }

    private ResponseVO<Long> getAccountNum(RiskBlackAccountVO vo) {
        RiskUserBlackAccountReqVO requestVO = new RiskUserBlackAccountReqVO();
        requestVO.setRiskControlTypeCode(vo.getRiskControlTypeCode());
        requestVO.setRiskControlAccount(vo.getRiskControlAccount());
        requestVO.setRiskControlAccountName(vo.getRiskControlAccountName());
        requestVO.setSiteCode(vo.getSiteCode());
        ResponseVO<Long> rCount = userInfoApi.getCountByBlackAccount(requestVO);
        return rCount;
    }

    public ResponseVO<Boolean> removeBlackAccount(IdVO vo) {
        removeById(vo.getId());
        return ResponseVO.success(true);
    }

    /**
     * 构建风控账号查询字符串 总站
     * 规则：
     * 1. 两个字段都有值 => account|name
     * 2. 只有 account 有值 => account
     * 3. 只有 name 有值 => name
     * 4. 两个都没有值 => 返回 null
     */
    public String buildRiskControlAccount(String account, String name) {
        if (ObjectUtil.isNotEmpty(account) && ObjectUtil.isNotEmpty(name)) {
            return account + "|" + name;
        } else if (ObjectUtil.isNotEmpty(account)) {
            return account;
        } else if (ObjectUtil.isNotEmpty(name)) {
            return name; // 不再加前导
        }
        return null;
    }

    public ResponseVO<Page<RiskBlackAccountVO>> getRiskBlackListPage(RiskBlackAccountReqVO reqVO) {
        //String riskControlAccount = buildRiskControlAccount(reqVO.getRiskControlAccount(), reqVO.getRiskControlAccountName());
        //reqVO.setRiskControlAccount(riskControlAccount);
        LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getRiskControlTypeCode()), SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, reqVO.getRiskControlTypeCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getRiskControlAccount()), SiteRiskCtrlBlackAccountPO::getRiskControlAccount, reqVO.getRiskControlAccount());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getRiskControlAccountName()), SiteRiskCtrlBlackAccountPO::getRiskControlAccountName, reqVO.getRiskControlAccountName());

        queryWrapper.ge(ObjectUtil.isNotEmpty(reqVO.getCreateBeginTime()), SiteRiskCtrlBlackAccountPO::getCreatedTime, reqVO.getCreateBeginTime());
        queryWrapper.le(ObjectUtil.isNotEmpty(reqVO.getCreateEndTime()), SiteRiskCtrlBlackAccountPO::getCreatedTime, reqVO.getCreateEndTime());
        queryWrapper.eq(SiteRiskCtrlBlackAccountPO::getSiteCode, reqVO.getSiteCode());
        queryWrapper.orderByDesc(SiteRiskCtrlBlackAccountPO::getCreatedTime);

        Page<SiteRiskCtrlBlackAccountPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SiteRiskCtrlBlackAccountPO> riskBlackAccountPOPage = siteRiskCtrlBlackAccountRepository.selectPage(page, queryWrapper);
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
        LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(queryVO.getRiskControlAccount())) {
            wrapper.eq(SiteRiskCtrlBlackAccountPO::getRiskControlAccount, queryVO.getRiskControlAccount());
        }

        if (StringUtils.isNotEmpty(queryVO.getRiskControlTypeCode())) {
            wrapper.eq(SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, queryVO.getRiskControlTypeCode());
        }
        wrapper.eq(ObjectUtil.isNotEmpty(queryVO.getSiteCode()), SiteRiskCtrlBlackAccountPO::getSiteCode, queryVO.getSiteCode());
        List<SiteRiskCtrlBlackAccountPO> pos = siteRiskCtrlBlackAccountRepository.selectList(wrapper);
        if (CollectionUtil.isEmpty(pos)) {
            return ResponseVO.success(Lists.newArrayList());
        }
        List<RiskBlackAccountVO> vos = BeanUtil.copyToList(pos, RiskBlackAccountVO.class);
        return ResponseVO.success(vos);
    }

    public ResponseVO<Boolean> getRiskIpBlack(RiskBlackAccountVO queryVO) {
        LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ObjectUtil.isNotEmpty(queryVO.getSiteCode()), SiteRiskCtrlBlackAccountPO::getSiteCode, queryVO.getSiteCode());
        //wrapper.eq(StringUtils.isNotEmpty(queryVO.getRiskControlAccount()), SiteRiskCtrlBlackAccountPO::getRiskControlAccount, queryVO.getRiskControlAccount());
        wrapper.eq(StringUtils.isNotEmpty(queryVO.getRiskControlTypeCode()), SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, queryVO.getRiskControlTypeCode());
        List<SiteRiskCtrlBlackAccountPO> pos = siteRiskCtrlBlackAccountRepository.selectList(wrapper);
        Boolean ipInRange = false;
        if (CollectionUtil.isNotEmpty(pos)) {
            for (SiteRiskCtrlBlackAccountPO po : pos) {
                if (po.getRiskControlAccount().contains("~")) {
                    String[] split = po.getRiskControlAccount().split("~");
                    ipInRange = IpRangeUtil.isIpInRange(queryVO.getRiskControlAccount(), split[0], split[1]);
                    if (ipInRange){
                        if (StrUtil.isNotEmpty(po.getIpWhitelist())) {
                            String[] whiteList = po.getIpWhitelist().split(",");
                            for (String ipStr : whiteList) {
                                if (ipStr.contains("~")){
                                    String[] split1 = ipStr.split("~");
                                    ipInRange = IpRangeUtil.isIpInRange(queryVO.getRiskControlAccount(), split[0], split[1]);
                                }else {
                                    ipInRange = queryVO.getRiskControlAccount().equals(ipStr);
                                }
                                if (ipInRange){
                                    //NOTE 如是在白名单中， 取反，通过
                                    return ResponseVO.success(false);
                                }
                            }
                        }
                    }
                }else {
                    ipInRange = queryVO.getRiskControlAccount().equals(po.getRiskControlAccount());
                }
                if (ipInRange){
                    return ResponseVO.success(ipInRange);
                }
            }
        }
        return ResponseVO.success(ipInRange);
    }

    /**
     * 判断指定账号是否在风险黑名单中
     * <p>
     * 逻辑：
     * 1. 根据入参的风险控制类型代码（riskControlTypeCode），映射为对应的风控黑名单类型代码
     * - 例如：提现方式是银行卡 -> 风控类型改为 RISK_BANK_ACCOUNT
     * 2. 先查询总站黑名单表，如果存在记录，返回 TRUE
     * 3. 如果总站未匹配，再查询站点黑名单表，如果存在记录，返回 TRUE
     * 4. 都未匹配，返回 FALSE
     * <p>
     * 特殊逻辑：
     * - 如果提现方式为电子钱包，需要额外匹配账号名（account + accountName）
     */
    public ResponseVO<Boolean> isRiskBlack(RiskBlackAccountIsBlackReqVO queryVO) {
        // 取出提现类型代码
        String riskControlTypeCode = queryVO.getRiskControlTypeCode();

        // 将提现类型代码转换为枚举，方便后续 switch 判断
        WithdrawTypeEnum withdrawTypeEnum = WithdrawTypeEnum.nameOfCode(riskControlTypeCode);

        // 如果提现类型不为空，则进行风控类型代码映射
        if (StringUtils.isNotEmpty(riskControlTypeCode) && withdrawTypeEnum != null) {
            switch (withdrawTypeEnum) {
                case BANK_CARD:
                    // 银行卡提现，映射到风险类型：银行账户
                    riskControlTypeCode = RiskBlackTypeEnum.RISK_BANK_ACCOUNT.getCode();
                    break;
                case ELECTRONIC_WALLET:
                    // 电子钱包提现，映射到风险类型：电子钱包
                    riskControlTypeCode = RiskBlackTypeEnum.RISK_ELECTRONIC_WALLET.getCode();
                    break;
                case CRYPTO_CURRENCY:
                    // 虚拟币提现，映射到风险类型：虚拟账户
                    riskControlTypeCode = RiskBlackTypeEnum.RISK_VIRTUAL_ACCOUNT.getCode();
                    break;
                default:
                    // 其他类型不做映射
                    break;
            }
        }

        // 查询时仍然用 queryVO.getRiskControlTypeCode()，而不是映射后的 riskControlTypeCode
        // 会导致映射不生效，所以这里应该把 queryVO 里的字段更新，或者直接用本地变量 riskControlTypeCode
        // 这里选择用 riskControlTypeCode 参与查询

        // 1. 查询总站黑名单
        LambdaQueryWrapper<RiskCtrlBlackAccountPO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RiskCtrlBlackAccountPO::getRiskControlAccount, queryVO.getRiskControlAccount());
        wrapper.eq(RiskCtrlBlackAccountPO::getRiskControlTypeCode, riskControlTypeCode);

        // 如果是电子钱包，需要额外匹配账户名
        if (ObjectUtil.equals(withdrawTypeEnum, WithdrawTypeEnum.ELECTRONIC_WALLET)) {
            wrapper.eq(RiskCtrlBlackAccountPO::getRiskControlAccountName, queryVO.getRiskControlAccountName());
        }

        wrapper.last("limit 1");

        RiskCtrlBlackAccountPO po = riskCtrlBlackAccountRepository.selectOne(wrapper);
        if (ObjectUtil.isNotEmpty(po)) {
            // 匹配到总站黑名单，返回 TRUE
            return ResponseVO.success(Boolean.TRUE);
        }

        // 2. 查询站点黑名单
        LambdaQueryWrapper<SiteRiskCtrlBlackAccountPO> siteWrapper = Wrappers.lambdaQuery();
        siteWrapper.eq(SiteRiskCtrlBlackAccountPO::getRiskControlAccount, queryVO.getRiskControlAccount());
        siteWrapper.eq(SiteRiskCtrlBlackAccountPO::getRiskControlTypeCode, riskControlTypeCode);

        if (ObjectUtil.equals(withdrawTypeEnum, WithdrawTypeEnum.ELECTRONIC_WALLET)) {
            siteWrapper.eq(SiteRiskCtrlBlackAccountPO::getRiskControlAccountName, queryVO.getRiskControlAccountName());
        }

        siteWrapper.eq(SiteRiskCtrlBlackAccountPO::getSiteCode, queryVO.getSiteCode());
        siteWrapper.last("limit 1");

        SiteRiskCtrlBlackAccountPO siteRiskCtrlBlackAccountPO = siteRiskCtrlBlackAccountRepository.selectOne(siteWrapper);
        if (ObjectUtil.isNotEmpty(siteRiskCtrlBlackAccountPO)) {
            // 匹配到站点黑名单，返回 TRUE
            return ResponseVO.success(Boolean.TRUE);
        }

        // 都未匹配，返回 FALSE
        return ResponseVO.success(Boolean.FALSE);
    }

}

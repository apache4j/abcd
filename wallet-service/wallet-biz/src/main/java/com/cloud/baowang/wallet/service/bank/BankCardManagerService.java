package com.cloud.baowang.wallet.service.bank;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.wallet.api.enums.FrontShowStatusEnum;
import com.cloud.baowang.wallet.api.vo.bank.*;
import com.cloud.baowang.wallet.po.bank.BankCardManagerPO;
import com.cloud.baowang.wallet.repositories.bank.BankCardManagerRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BankCardManagerService extends ServiceImpl<BankCardManagerRepository, BankCardManagerPO> {
    private final I18nApi i18nApi;
    private final BankCardManagerRepository repository;
    public ResponseVO<List<BankCardManagerVO>> pageList(BankCardManagerListReqVO vo) {
        List<String> arrayList = Lists.newArrayList();
        if (StrUtil.isNotBlank(vo.getBankName())) {
            ResponseVO<List<String>> search = i18nApi.search(I18nSearchVO.builder().bizKeyPrefix(I18MsgKeyEnum.BANK_CARD_SHOW_NAME.getCode()).searchContent(vo.getBankName()).build());
            if (search.isOk()) {
                arrayList = search.getData();
            }
            if (CollUtil.isEmpty(arrayList)) {
                return ResponseVO.success();
            }
        }
        List<BankCardManagerPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(ObjUtil.isNotEmpty(vo.getStatus()), BankCardManagerPO::getStatus, vo.getStatus())
                .in(StrUtil.isNotBlank(vo.getBankName()), BankCardManagerPO::getBankName, arrayList)
                .eq(StrUtil.isNotEmpty(vo.getCurrency()), BankCardManagerPO::getCurrency, vo.getCurrency())
                .orderByDesc(BankCardManagerPO::getCreatedTime)
                .list();
        if (CollUtil.isNotEmpty(list)) {
            return ResponseVO.success(list.stream().map(s -> {
                BankCardManagerVO bankCardManagerVO = new BankCardManagerVO();
                BeanUtils.copyProperties(s, bankCardManagerVO);
                return bankCardManagerVO;
            }).toList());
        }

        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> edit(BankCardManagerEditVO vo) {
        List<BankCardManagerPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getBankCode, vo.getBankCode())
                .ne(BankCardManagerPO::getId, vo.getId())
                .list();
        if (CollUtil.isNotEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.BANK_CODE_REPEAT);
        }
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getId, vo.getId())
                .set(StrUtil.isNotBlank(vo.getBankCode()), BankCardManagerPO::getBankCode, vo.getBankCode())
                .set(StrUtil.isNotBlank(vo.getIcon()), BankCardManagerPO::getIcon, vo.getIcon())
                .set(StrUtil.isNotBlank(vo.getCurrency()), BankCardManagerPO::getCurrency, vo.getCurrency())
                .set(BankCardManagerPO::getOperator, CurrReqUtils.getAccount())
                .set(BankCardManagerPO::getOperateTime, System.currentTimeMillis())
                .update();
        BankCardManagerPO po = getById(vo.getId());
        i18nApi.update(I18nMsgBindUtil.bind(po.getBankName(), vo.getBankNameList()));
        return ResponseVO.success();
    }

    public ResponseVO<BankCardManagerInfoResVO> info(BankCardManagerInfoReqVO vo) {
        BankCardManagerPO one = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getId, vo.getId()).one();
        BankCardManagerInfoResVO resVO = new BankCardManagerInfoResVO();
        BeanUtils.copyProperties(one, resVO);
        return ResponseVO.success(resVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> changeStatus(BankCardManagerChangStatusReqVO vo) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getId, vo.getId())
                .set(BankCardManagerPO::getStatus, vo.getStatus())
                .set(BankCardManagerPO::getOperator, CurrReqUtils.getAccount())
                .set(BankCardManagerPO::getOperateTime, System.currentTimeMillis())
                .update();
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> add(BankCardManagerAddReqVO vo) {
        List<BankCardManagerPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getBankCode, vo.getBankCode())
                .list();
        if (CollUtil.isNotEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.BANK_CODE_REPEAT);
        }
        BankCardManagerPO po = new BankCardManagerPO();
        BeanUtils.copyProperties(vo, po);
        po.setOperator(CurrReqUtils.getAccount());
        po.setOperateTime(System.currentTimeMillis());
        String bankNameI18nKey = I18MsgKeyEnum.BANK_CARD_SHOW_NAME.getCode() + CommonConstant.UNDERLINE + vo.getBankCode();
        po.setBankName(bankNameI18nKey);
        save(po);
        repository.updateChannelBankRelationStatus(vo.getCurrency());
        i18nApi.insert(I18nMsgBindUtil.bind(bankNameI18nKey, vo.getBankNameList()));
        return ResponseVO.success();
    }

    public List<BankManageVO> bankList(String currency) {
        List<BankCardManagerPO> bankCardManagerPOS = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getCurrency,currency)
                .eq(BankCardManagerPO::getStatus, FrontShowStatusEnum.SHOW.getCode())
                .orderByDesc(BankCardManagerPO::getCreatedTime)
                .list();
        List<BankManageVO> bankManageVOS = new ArrayList<>(bankCardManagerPOS.size());
        try {
            bankManageVOS =  ConvertUtil.convertListToList(bankCardManagerPOS,new BankManageVO());
        }catch (Exception e){
            log.error("银行列表查询错误！");
        }
        return bankManageVOS;
    }

    public ResponseVO<List<BankInfoRspVO>> queryBankInfoByCurrency(String currency) {
        List<BankCardManagerPO> bankCardManagerPOS = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(BankCardManagerPO::getCurrency,currency)
                .orderByDesc(BankCardManagerPO::getCreatedTime)
                .list();
        List<BankInfoRspVO> bankInfo = new ArrayList<>();
        try {
            bankCardManagerPOS.forEach(po -> {
                BankInfoRspVO rspVO = BankInfoRspVO.builder().id(po.getId()).bankCode(po.getBankCode()).bankName(po.getBankName()).build();
                bankInfo.add(rspVO);
            });
        }catch (Exception e){
            log.error("银行列表查询错误！");
        }
        return ResponseVO.success(bankInfo);
    }
}

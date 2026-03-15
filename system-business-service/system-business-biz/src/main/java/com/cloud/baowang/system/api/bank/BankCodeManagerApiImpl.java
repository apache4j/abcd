package com.cloud.baowang.system.api.bank;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.bank.BankChannelManagerApi;
import com.cloud.baowang.system.api.vo.bank.*;
import com.cloud.baowang.system.service.bank.SystemWithdrawBankChannelManageService;
import com.cloud.baowang.system.service.bank.SystemWithdrawChannelInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class BankCodeManagerApiImpl implements BankChannelManagerApi {
    private SystemWithdrawBankChannelManageService bankChannelManageService;
    private SystemWithdrawChannelInfoService channelManageService;
    @Override
    public ResponseVO<Page<BankChannelInfoRspVO>> pageList(BankCodeListReqVO vo) {
        return channelManageService.pageList(vo);
    }

    @Override
    public ResponseVO<Boolean> add(BankChannelManageAddVO vo) {
        return channelManageService.add(vo);
    }

    @Override
    public ResponseVO<Void> deleteBankChannelConfig(String id) {
        return bankChannelManageService.deleteBankChannelConfig(id);
    }

    @Override
    public ResponseVO<Void> deleteChannelInfo(String id) {
        return channelManageService.deleteChannelManage(id);
    }

    @Override
    public ResponseVO<List<BankChannelManageRspVO>> queryChannelBankRelation(String id) {
        return bankChannelManageService.queryChannelBankRelation(id);
    }

    @Override
    public ResponseVO<Void> edit(BankChannelManageAddVO editVO) {
        return channelManageService.edit(editVO);
    }

    @Override
    public ResponseVO<BankChannelManageRspVO> getSystemChannelBankRelation(ChannelBankRelationReqVO reqVO) {
        return bankChannelManageService.getSystemChannelBankRelation(reqVO);
    }

    @Override
    public ResponseVO<Set<BankInfoAdminRspVO>> queryAllChannelBankRelation(BankChannelRelationQueryVO req) {
        return bankChannelManageService.queryAllChannelBankRelation(req);
    }


//    @Override
//    public ResponseVO<Void> edit(BankChannelManageAddVO vo) {
//        return null;
//    }
//
//    @Override
//    public ResponseVO<BankCardManagerInfoResVO> info(BankCardManagerInfoReqVO vo) {
//        return null;
//    }
//
//    @Override
//    public ResponseVO<Void> changeStatus(BankCardManagerChangStatusReqVO vo) {
//        return null;
//    }
}

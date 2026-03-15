package com.cloud.baowang.wallet.api.bank;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.bank.ChannelBankRelationReqVO;
import com.cloud.baowang.wallet.api.api.bank.BankCardManagerApi;
import com.cloud.baowang.wallet.api.vo.bank.*;
import com.cloud.baowang.wallet.service.bank.BankCardManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class BankCardManagerApiImpl implements BankCardManagerApi {

    private final BankCardManagerService bankCardManagerService;

    @Override
    public ResponseVO<List<BankCardManagerVO>> pageList(BankCardManagerListReqVO vo) {
        return bankCardManagerService.pageList(vo);
    }

    @Override
    public ResponseVO<Void> add(BankCardManagerAddReqVO vo) {
        return bankCardManagerService.add(vo);
    }

    @Override
    public ResponseVO<Void> edit(BankCardManagerEditVO vo) {
        return bankCardManagerService.edit(vo);
    }

    @Override
    public ResponseVO<BankCardManagerInfoResVO> info(BankCardManagerInfoReqVO vo) {
        return bankCardManagerService.info(vo);
    }

    @Override
    public ResponseVO<Void> changeStatus(BankCardManagerChangStatusReqVO vo) {
        return bankCardManagerService.changeStatus(vo);
    }

    @Override
    public List<BankManageVO> bankList(String currency){
        return bankCardManagerService.bankList(currency);
    }

    @Override
    public ResponseVO<List<BankInfoRspVO>> queryBankInfoByCurrency(String currencyCode) {
        return bankCardManagerService.queryBankInfoByCurrency(currencyCode);
    }

}

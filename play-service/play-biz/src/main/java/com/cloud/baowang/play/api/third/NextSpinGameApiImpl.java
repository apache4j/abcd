package com.cloud.baowang.play.api.third;


import com.cloud.baowang.play.api.api.third.NextSpinGameApi;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinReq;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinTransactionRecordVO;
import com.cloud.baowang.play.service.NextSpinTransactionRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class NextSpinGameApiImpl implements NextSpinGameApi {


    private final NextSpinTransactionRecordService nextSpinTransactionRecordService;


    @Override
    public void save(NextSpinTransactionRecordVO vo) {
        nextSpinTransactionRecordService.insert(vo);
    }

    @Override
    public NextSpinTransactionRecordVO getByTransferId(String transferId) {
        return nextSpinTransactionRecordService.getByTransferId(transferId);
    }

    @Override
    public Object oauth(NextSpinReq request) {
        return null;
    }

    @Override
    public Object checkBalance(NextSpinReq request) {
        return null;
    }

    @Override
    public Object bet(NextSpinReq request) {
        return null;
    }
}

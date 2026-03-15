package com.cloud.baowang.wallet.api;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.wallet.api.api.UserBankCardManageApi;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.UserBankQueryApi;
import com.cloud.baowang.wallet.po.UserBankCardManagePO;
import com.cloud.baowang.wallet.service.UserBankCardManageService;
import com.cloud.baowang.wallet.api.vo.userbankcard.RiskEditBankCardInfoVO;
import com.cloud.baowang.wallet.api.vo.userbankcard.EditBankCardInfoVO;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
public class UserBankCardManageApiImpl implements UserBankCardManageApi {

    private final UserBankCardManageService userBankCardManageService;


    @Override
    public ResponseVO<RiskEditBankCardInfoVO> getRiskEditBankCardInfoGetByCardNo(String bankCardNo) {
        UserBankCardManagePO one = userBankCardManageService.getOne(Wrappers.<UserBankCardManagePO>lambdaQuery()
                .eq(UserBankCardManagePO::getBankCardNo, bankCardNo)
                .last("limit 1")
        );

        if (null == one) {
            return ResponseVO.success(null);
        }
        return ResponseVO.success(ConvertUtil.entityToModel(one, RiskEditBankCardInfoVO.class));
    }

    @Override
    public ResponseVO<RiskEditBankCardInfoVO> getRiskEditBankCardInfoGetByCardNoAndSiteCode(String bankCardNo, String siteCode) {
        UserBankCardManagePO one = userBankCardManageService.getOne(Wrappers.<UserBankCardManagePO>lambdaQuery()
                .eq(UserBankCardManagePO::getBankCardNo, bankCardNo).eq(UserBankCardManagePO::getSiteCode,siteCode)
                .last("limit 1")
        );

        if (null == one) {
            return ResponseVO.success(null);
        }
        return ResponseVO.success(ConvertUtil.entityToModel(one, RiskEditBankCardInfoVO.class));
    }

    @Override
    public ResponseVO<Boolean> updateBankInfoById(EditBankCardInfoVO editBankCardInfoVO) {
        if (editBankCardInfoVO.getId() == null){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return userBankCardManageService.updateBankInfoById(editBankCardInfoVO);
    }
}

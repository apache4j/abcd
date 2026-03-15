package com.cloud.baowang.play.api.sba;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.sba.action.base.SBABase;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.play.api.vo.sba.*;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import jakarta.annotation.PostConstruct;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SBASportContext extends SBABase {

    @Autowired
    private List<SBASportInterface> interfaceList;

    private final Map<SBActionEnum, SBASportInterface> actionMap = new HashMap<>();

    @Autowired
    private VenueInfoService venueInfoService;

    public SBASportContext(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    @PostConstruct
    public void init() {
        for (SBASportInterface baseInterface : interfaceList) {
            actionMap.put(baseInterface.getAction(), baseInterface);
        }
    }


    /**
     * 密钥验证
     */
    private void validateKey(SBBaseReq baseReq) {
        if (ObjectUtil.isEmpty(baseReq) || ObjectUtil.isEmpty(baseReq.getKey())) {
            log.info("{}, 场馆密钥确认,密钥验证失败。密钥参数为空", VenuePlatformConstants.SBA);
            throw new SBDefaultException(SBResultCode.INVALID_VERIFICATION_KEY.getCode());
        }

        //因为沙巴体育的场馆信息就只有一条，所以就固定用null币种
        VenueInfoVO venueInfoVO = venueInfoService.getAdminVenueInfoByVenueCode(VenueEnum.SBA.getVenueCode(), null);

        if (ObjectUtil.isEmpty(venueInfoVO)) {
            log.info("{},场馆密钥确认,查询场馆信息失败 ", VenuePlatformConstants.SBA);
            throw new SBDefaultException(SBResultCode.THE_SYSTEM_IS_BUSY.getCode());
        }


        if (ObjectUtil.isEmpty(venueInfoVO)) {
            log.info("{}, 场馆密钥确认,查询场馆信息失败 ", VenuePlatformConstants.SBA);
            throw new SBDefaultException(SBResultCode.THE_SYSTEM_IS_BUSY.getCode());
        }

        if (!baseReq.getKey().equals(venueInfoVO.getMerchantNo())) {
            log.info("{}, 场馆密钥确认,密钥验证失败。传入密钥:{},我方密钥:{} ", VenuePlatformConstants.SBA, baseReq.getKey(), venueInfoVO.getMerchantNo());
            throw new SBDefaultException(SBResultCode.INVALID_VERIFICATION_KEY.getCode());
        }
    }


    /**
     * 沙巴体育统一入口
     */
    public String toAction(SBActionEnum sbActionEnum, SBBaseReq sbBaseReq) {
        validateKey(sbBaseReq);
        SBASportInterface sbaSportInterface = actionMap.get(sbActionEnum);
        if (ObjectUtils.isEmpty(sbaSportInterface)) {
            log.info("{},路由异常,未找到对应接口,参数异常,:{}", VenueEnum.SBA.getVenueName(), sbBaseReq);
            throw new SBDefaultException(SBResultCode.PARAMETER_ERROR.getCode());
        }
        try {
            SBResBaseVO sbResBaseVO = sbaSportInterface.toAction(sbBaseReq);
            return responseToSetBalance(sbResBaseVO);
        } catch (SBDefaultException sb){
            throw new SBDefaultException(sb.getResultCode());
        }catch (Exception e) {
            log.info("服务异常:", e);
        }
        throw new SBDefaultException(SBResultCode.THE_SYSTEM_IS_BUSY);
    }


    /**
     * 返回前组装数据
     */
    private String responseToSetBalance(SBResBaseVO sbResBaseVO) {


        //这两个接口是需要返回余额的
        if (sbResBaseVO instanceof SBConfirmBetRes) {
            String status = sbResBaseVO.getStatus();
            if (!StringUtil.isBlank(status) && status.equals(SBResultCode.SUCCESS.getCode())) {
                SBConfirmBetRes sbConfirmBetRes = (SBConfirmBetRes) sbResBaseVO;
                if (!StringUtil.isBlank(sbConfirmBetRes.getAccount())) {
                    UserInfoVO userAccount = getUserInfo(sbConfirmBetRes.getAccount());
                    UserCoinWalletVO userCoinWalletVO = getUserCenterCoin(userAccount.getUserId());
                    sbConfirmBetRes.setBalance(userCoinWalletVO.getTotalAmount());
                    sbConfirmBetRes.setMsg(SBResultCode.of(sbResBaseVO.getStatus()));
//                    return sbConfirmBetRes;
                    return JSON.toJSONString(sbConfirmBetRes);
                }
            }
        }
        sbResBaseVO.setMsg(SBResultCode.of(sbResBaseVO.getStatus()));

        return JSON.toJSONString(sbResBaseVO);
    }


    private String convertToMessage(SBActionEnum actionEnum, CheckTicketStatusVO checkTicketStatusVO, TransHistoryVO historyVO) {
        return switch (actionEnum) {
            case CONFIRM_BET ->
                    JSON.toJSONString(SBConfirmBetReq.transHistoryConversion(checkTicketStatusVO, historyVO));
            case CANCEL_BET -> JSON.toJSONString(SBCancelBetReq.transHistoryConversion(checkTicketStatusVO, historyVO));
            case SETTLE -> JSON.toJSONString(SBSettleReq.transHistoryConversion(checkTicketStatusVO, historyVO));
            default -> null;
        };
    }


}

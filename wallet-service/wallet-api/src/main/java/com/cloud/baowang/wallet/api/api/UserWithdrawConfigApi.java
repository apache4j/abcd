package com.cloud.baowang.wallet.api.api;


import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigAddOrUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author qiqi
 */


@FeignClient(contextId = "remoteUserWithdrawConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 会员提款配置 服务")
public interface UserWithdrawConfigApi {


    String PREFIX = ApiConstants.PREFIX + "/userWithdrawConfig/api/";

    @Operation(summary = "会员提款配置列表")
    @PostMapping("listUserWithdrawConfig")
    ResponseVO<List<UserWithdrawConfigVO>> listUserWithdrawConfig(@RequestBody UserWithdrawConfigRequestVO vo);

    @Operation(summary = "会员提款配置添加")
    @PostMapping("addUserWithdrawConfig")
    public ResponseVO<Integer> addUserWithdrawConfig(@RequestBody UserWithdrawConfigAddOrUpdateVO vo);

    @Operation(summary = "会员提款配置初始化数据-站点初始化时调用")
    @PostMapping("initUserWithdrawConfigData")
    ResponseVO<Boolean> initUserWithdrawConfigData(@RequestBody List<UserWithdrawConfigAddOrUpdateVO> vos);


    @Operation(summary = "会员提款配置修改")
    @PostMapping("updateUserWithdrawConfig")
    public ResponseVO<Integer> updateUserWithdrawConfig(@RequestBody UserWithdrawConfigAddOrUpdateVO vo);

    @PostMapping("updateBySiteCodeAndRankCode")
    @Operation(summary = "根据站点code+vipRankCode,更新提款配置中对应单日提款总次数，单日最高提款总额")
    ResponseVO<Boolean> updateBySiteCodeAndRankCode(@RequestParam("siteCode")String siteCode,
                                                    @RequestParam("vipRankCode")Integer vipRankCode,
                                                    @RequestBody List<UserWithdrawConfigAddOrUpdateVO> vos);

    @Operation(summary = "会员提款配置详情")
    @PostMapping("detailUserWithdrawConfig")
    ResponseVO<UserWithdrawConfigVO> detailUserWithdrawConfig(@RequestBody IdVO idVO);


    @PostMapping("updateBySiteCodeAndVipGradeCode")
    @Operation(summary = "根据站点code+vipGrade+currencyCode,更新提款配置中对应单日提款总次数，单日最高提款总额")
    ResponseVO<Boolean> updateBySiteCodeAndVipGradeCode(@RequestBody UserWithdrawConfigAddOrUpdateVO vo) ;
}

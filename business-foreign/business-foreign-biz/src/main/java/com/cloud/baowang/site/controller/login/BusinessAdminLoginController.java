package com.cloud.baowang.site.controller.login;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.baowang.agent.api.api.AgentMerchantApi;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantParamVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantResultVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.MerchantAgentInfoVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantAccountCheckResVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantAccountCheckVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantGoogleVerifyCodeVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginBindEmailVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginBindGoogleVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginBindNotLoginEmailVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginChangePasswordVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginEmailBindGoogleVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginFindPasswordVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantLoginGetMailCodeVO;
import com.cloud.baowang.common.auth.util.BusinessAuthUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.TokenConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.LoginTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.core.utils.UserChecker;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.site.service.BusinessCaptchaService;
import com.cloud.baowang.site.service.BusinessLoginInfoService;
import com.cloud.baowang.site.service.BussinessTokenService;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;
import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.LOGIN_ERROR_LOCK_TIME;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/business_admin_login/api")
@Tag(name = "商务后台登录")
public class BusinessAdminLoginController {

    private final AgentMerchantApi agentMerchantApi;

    private final BussinessTokenService tokenService;

    private final DomainInfoApi domainInfoApi;

    private final BusinessLoginInfoService businessLoginInfoService;

    private final CustomerChannelApi customerChannelApi;

    private final BusinessCaptchaService businessCaptchaService;


    private final SystemDictConfigApi systemDictConfigApi;



    @Operation(summary = "获取验证码")
    @GetMapping("/v1/captcha")
    public void captcha(@RequestParam("codeKey") String codeKey, HttpServletResponse response) throws Exception {
        businessCaptchaService.create(codeKey, response);
    }



    @PostMapping("/v1/login")
    @Operation(summary = "商务后台登录")
    public ResponseVO<AgentMerchantResultVO> login(@Valid @RequestBody AgentMerchantParamVO agentMerchantParamVO, HttpServletRequest request) {
        String siteCode=CurrReqUtils.getSiteCode();
        //校验验证码
        boolean isTrue = businessCaptchaService.check(agentMerchantParamVO.getCodeKey(), agentMerchantParamVO.getVerifyCode());

        AgentMerchantVO vo = new AgentMerchantVO();
        BeanUtil.copyProperties(agentMerchantParamVO,vo);
        vo.setMerchantAccount(agentMerchantParamVO.getUserName());
        vo.setMerchantPassword(agentMerchantParamVO.getPassword());

        String ip = CurrReqUtils.getReqIp();
        String domain = request.getHeader(X_CUSTOM);
        log.info("站点后台登录获取到的域名：{}, IP：{}", domain, ip);

        vo.setLoginIp(ip);
        vo.setIpAddress(getIpAddress(ip));
        vo.setLoginAddress(domain);
        // 登录ip风控层级
        // 登录终端
        vo.setLoginTerminal(Objects.nonNull(CurrReqUtils.getReqDeviceType()) ? CurrReqUtils.getReqDeviceType()+"":"");
        // 登录设备号
        //vo.setLoginDeviceNo(agentMerchantParamVO.getLoginDeviceNo());
        vo.setLoginDeviceNo(CurrReqUtils.getReqDeviceId());


        AgentMerchantResultVO resultVO = merchantLogin(vo,isTrue);


        vo.setLoginType(LoginTypeEnum.SUCCESS.getCode());
        vo.setMerchantName(resultVO.getMerchantName());
        businessLoginInfoService.recordLoginInfoRecord(vo);


        String token =  tokenService.createToken(resultVO);
        log.info("token ={}", token);
        resultVO.setToken(token);
        agentMerchantApi.updateAgentMerchantLoginInfo(resultVO);

        AgentMerchantResultVO merchantResultVO =  AgentMerchantResultVO.builder().build();
        merchantResultVO.setMerchantName(resultVO.getMerchantName());
        merchantResultVO.setMerchantAccount(resultVO.getMerchantAccount());
        merchantResultVO.setToken(resultVO.getToken());
        return ResponseVO.success(merchantResultVO);
    }



    /**
     * ip转化地址,去重
     *
     * @param ip
     * @return
     */
    private String getIpAddress(String ip) {
        return IpAPICoUtils.getIp(ip).getAddress();
    }


    @Operation(summary = "商务找回密码--身份验证（无需登录）")
    @PostMapping(value = "/v1/loginCheckMerchatAccount")
    public ResponseVO<MerchantAccountCheckResVO> loginCheckMerchatAccount(@Valid @RequestBody MerchantAccountCheckVO checkVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        //校验验证码
        boolean isTrue = businessCaptchaService.check(checkVO.getCodeKey(), checkVO.getCode());
        if(!isTrue) {
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }
        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(checkVO.getMerchantAccount(),siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        /*if(StringUtils.isEmpty(agentMerchantVO.getEmail())) {
            return ResponseVO.fail(ResultCode.ERROR_EMAIL);
        }*/

        Boolean isSetGoogle = true;
        if (StringUtils.isEmpty(agentMerchantVO.getGoogleAuthKey())) {
            isSetGoogle = false;
        }

        MerchantAccountCheckResVO resVO = new MerchantAccountCheckResVO();
        resVO.setMerchantAccount(agentMerchantVO.getMerchantAccount());
        resVO.setIsSetGoogle(isSetGoogle);

        return ResponseVO.success(resVO);
    }




    public AgentMerchantResultVO merchantLogin(AgentMerchantVO agentMerchantVO,boolean isTrue) {
        String siteCode = agentMerchantVO.getSiteCode();
        if (StringUtils.isEmpty(siteCode)) {
            DomainVO domainVO = domainInfoApi.getDomainByAddress(agentMerchantVO.getLoginAddress());
            if (domainVO == null || domainVO.getSiteCode() == null) {
                throw new BaowangDefaultException(ResultCode.ADMIN_NAME_NOT_EXIST);
            }
            siteCode = domainVO.getSiteCode();
        }
        log.info("商务后台获取到的siteCode：{}", siteCode);
        // 商务后台账号不存在
        AgentMerchantVO merchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(agentMerchantVO.getMerchantAccount(), siteCode);

        if (null == merchantVO || null == merchantVO.getId()) {
            log.info("商务后台账号不存在, merchantAccount={}", merchantVO);
            agentMerchantVO.setLoginType(LoginTypeEnum.FAIL.getCode());
            agentMerchantVO.setRemark(ResultCode.ICCORRECT_PASSWORD_VERIFY.getDesc());
            agentMerchantVO.setSiteCode(siteCode);
            agentMerchantVO.setMerchantName(Objects.isNull(merchantVO)?"":merchantVO.getMerchantName());
            businessLoginInfoService.recordLoginInfoRecord(agentMerchantVO);
            throw new BaowangDefaultException(ResultCode.ICCORRECT_PASSWORD_VERIFY);
        }

        checkLoginErrorTimeLimit(merchantVO.getMerchantAccount(), siteCode);

        if(!isTrue) {
            log.info("商务后台登录验证码错误");
            agentMerchantVO.setLoginType(LoginTypeEnum.FAIL.getCode());
            agentMerchantVO.setRemark(ResultCode.ICCORRECT_PASSWORD_VERIFY.getDesc());
            agentMerchantVO.setSiteCode(siteCode);
            agentMerchantVO.setMerchantName(merchantVO.getMerchantName());
            businessLoginInfoService.recordLoginInfoRecord(agentMerchantVO);
            throw new BaowangDefaultException(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }

        // 商务后台状态为2，禁用
        if (AgentStatusEnum.LOGIN_LOCK.getCode().equals(merchantVO.getStatus())) {
            log.info("商务后台状态为0，禁用, merchantAccount={}", merchantVO.getMerchantAccount());
            agentMerchantVO.setLoginType(LoginTypeEnum.FAIL.getCode());
            agentMerchantVO.setRemark(ResultCode.ACCOUNT_LOCK.getDesc());
            agentMerchantVO.setSiteCode(siteCode);
            agentMerchantVO.setMerchantName(merchantVO.getMerchantName());
            businessLoginInfoService.recordLoginInfoRecord(agentMerchantVO);
            throw new BaowangDefaultException(ResultCode.ACCOUNT_LOCK);
        }

        // 密码不正确
        boolean flag = agentMerchantApi.validate(merchantVO,agentMerchantVO.getMerchantPassword());
        if(!flag){
            incrGoogleAuthTimeLimit(merchantVO.getMerchantAccount(), siteCode);
            log.info("商务后台密码不正确, merchantAccount={}", merchantVO.getMerchantAccount());
            agentMerchantVO.setLoginType(LoginTypeEnum.FAIL.getCode());
            agentMerchantVO.setRemark(ResultCode.ICCORRECT_PASSWORD_VERIFY.getDesc());
            agentMerchantVO.setSiteCode(siteCode);
            businessLoginInfoService.recordLoginInfoRecord(agentMerchantVO);
            throw new BaowangDefaultException(ResultCode.ICCORRECT_PASSWORD_VERIFY);
        }

        //判断之前是否有登录，有登录的话要清空之前的token信息
        String oldToken = RedisUtil.getValue(BusinessAuthUtil.getJwtKey(siteCode,merchantVO.getMerchantId()));
        if (!ObjectUtils.isEmpty(oldToken)) {
            tokenService.delLoginUser(siteCode,oldToken);
        }

        AgentMerchantResultVO merchantResultVO = AgentMerchantResultVO.builder().build();
        merchantResultVO.setMerchantName(merchantVO.getMerchantName());
        agentMerchantVO.setSiteCode(merchantVO.getSiteCode());
        agentMerchantVO.setRiskId(merchantVO.getRiskId());
        BeanUtil.copyProperties(merchantVO, merchantResultVO);
        // 登录成功重置失败次数
        resetLoginErrorTimeLimit(merchantVO.getMerchantAccount(),false,siteCode);
        return merchantResultVO;
    }


    @Operation(summary = "商务登出")
    @PostMapping(value = "/loginOut")
    public ResponseVO loginOut() {
        AgentMerchantResultVO agentMerchantResultVO=getCurrentAgent();
        if (agentMerchantResultVO == null) {
            return ResponseVO.fail(ResultCode.SIGN_EMPTY);
        }
        String token=agentMerchantResultVO.getToken();
        if (org.springframework.util.StringUtils.hasText(token)) {
            String userKey = JwtUtil.getUserKey(token);
            String merchantId = JwtUtil.getUserId(token);
            String siteCode = JwtUtil.getSiteCode(token);
            RedisUtil.deleteKey(BusinessAuthUtil.getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(BusinessAuthUtil.getJwtKey(siteCode,merchantId));
        }
        return ResponseVO.success();
    }

    public static AgentMerchantResultVO getCurrentAgent() {
        HttpServletRequest request = ServletUtil.getRequest();
        if (request == null) return null;
        String token = getToken(request);
        if (ObjectUtils.isEmpty(token)) return null;
        String merchantId = JwtUtil.getUserId(token);
        String merchantAccount = JwtUtil.getUserAccount(token);
        String siteCode = JwtUtil.getSiteCode(token);
        String oldToken = RedisUtil.getValue(BusinessAuthUtil.getJwtKey(siteCode,merchantId));
        if (oldToken == null) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        String id = JwtUtil.getId(token);
        return AgentMerchantResultVO.builder().merchantAccount(merchantAccount).merchantId(merchantId).token(token).id(id).siteCode(siteCode).build();
    }

    /**
     * 获取请求token
     */
    private static String getToken(HttpServletRequest request) {
        String token = request.getHeader(TokenConstants.SIGN);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        } else if (!ObjectUtils.isEmpty(token) && token.startsWith(ServletUtil.urlEncode(TokenConstants.PREFIX))) {
            token = ServletUtil.urlDecode(token);
            token = token.replaceFirst(TokenConstants.PREFIX, ServletUtil.EMPTY_STRING);
        }
        return token;
    }

    @PostMapping("/v1/findPassword")
    @Operation(summary = "找回密码（无需登录）")
    public ResponseVO<AgentMerchantResultVO> findPassword(@Valid @RequestBody MerchantLoginFindPasswordVO merchantLoginFindPasswordVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String account = merchantLoginFindPasswordVO.getMerchantAccount();
        if(!StringUtils.equals(merchantLoginFindPasswordVO.getConfirmPassword(), merchantLoginFindPasswordVO.getNewPassword())){
            return ResponseVO.fail(ResultCode.NOT_MATCH_PASSWORD);
        }
        log.info("修改密码 获取siteCode：{}", siteCode);
        merchantLoginFindPasswordVO.setSiteCode(siteCode);


        if (UserChecker.checkPassword(merchantLoginFindPasswordVO.getNewPassword())) {
            log.info("修改密码 新密码格式错误：{}", merchantLoginFindPasswordVO.getNewPassword());
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (UserChecker.checkPassword(merchantLoginFindPasswordVO.getConfirmPassword())) {
            log.info("修改密码 确认密码格式错误：{}", merchantLoginFindPasswordVO.getConfirmPassword());
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        //再次校验验证码
        Integer result = RedisUtil.getValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, siteCode, account));
        if (result == null || result != 1) {
            return ResponseVO.fail(ResultCode.CODE_ERROR);
        }

        if (RedisUtil.isKeyExist(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, siteCode, account))) {
            RedisUtil.deleteKey(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, siteCode, account));
        }

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(account, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        agentMerchantApi.updatePassword(agentMerchantVO, merchantLoginFindPasswordVO.getNewPassword());
        return ResponseVO.success();
    }


    @Operation(summary = "Google验证码校验（无需登录）")
    @PostMapping("/v1/googleCheckVerifyCode")
    public ResponseVO googleCheckVerifyCode(@Valid @RequestBody MerchantGoogleVerifyCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(vo.getMerchantAccount(),siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        if (!verifyCode(vo.getVerifyCode(), agentMerchantVO.getGoogleAuthKey())) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }

        RedisUtil.setValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getMerchantAccount()), 1, 5 * 60L);
        return ResponseVO.success();
    }



    @PostMapping("changePassword")
    @Operation(summary = "修改密码")
    public ResponseVO<AgentMerchantResultVO> changePassword(@Valid @RequestBody MerchantLoginChangePasswordVO merchantLoginChangePasswordVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        String account = CurrReqUtils.getAccount();
        if(!StringUtils.equals(merchantLoginChangePasswordVO.getConfirmPassword(), merchantLoginChangePasswordVO.getNewPassword())){
            log.info("两次输入的新密码不一致");
            return ResponseVO.fail(ResultCode.NOT_MATCH_PASSWORD);
        }
        log.info("修改密码 获取siteCode：{}", siteCode);
        merchantLoginChangePasswordVO.setSiteCode(siteCode);

        if (UserChecker.checkPassword(merchantLoginChangePasswordVO.getNewPassword())) {
            log.info("新密码格式不正确");
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }

        if (UserChecker.checkPassword(merchantLoginChangePasswordVO.getConfirmPassword())) {
            log.info("确认密码格式不正确");
            return ResponseVO.fail(ResultCode.PASSWORD_ERROR);
        }
        if(StringUtils.equals(merchantLoginChangePasswordVO.getOldPassword(), merchantLoginChangePasswordVO.getNewPassword())){
            log.info("新密码不能与旧密码相同");
            return ResponseVO.fail(ResultCode.NEW_PASSWORD_SAME_OLD_PASSWORD);
        }

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(account, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        // 旧密码不正确
        boolean flag = agentMerchantApi.validate(agentMerchantVO, merchantLoginChangePasswordVO.getOldPassword());
        if (!flag) {
            log.info("旧密码不正确");
            return ResponseVO.fail(ResultCode.ERR_OLD_PASSWORD);
        }
        agentMerchantApi.updatePassword(agentMerchantVO, merchantLoginChangePasswordVO.getNewPassword());
        return ResponseVO.success();
    }


    public void incrGoogleAuthTimeLimit(String userId,String siteCode) {
        String authKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT, siteCode, userId);

        Long authValue = RedisUtil.getValue(authKey);
        if (authValue != null && authValue >= CommonConstant.business_four) {
            //锁住
            resetLoginErrorTimeLimit(userId,true,siteCode);
        }else{
            RedisUtil.setValue(authKey, authValue == null ? 1L : authValue + 1);
        }


    }

    public void resetGoogleAuthTimeLimit(String userId,String siteCode) {
        String authKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT, siteCode, userId);
        RedisUtil.deleteKey(authKey);
    }


    public void resetLoginErrorTimeLimit(String userId,boolean lock,String siteCode) {

        String limitKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT, siteCode, userId);
        if (lock){
            //锁定
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOGIN_ERROR_LOCK_TIME.getCode(), siteCode).getData();
            long lockSeconds = Long.parseLong(configValue.getConfigParam())*60L;
            RedisUtil.setValue(limitKey,limitKey,lockSeconds);


            String authKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT, siteCode, userId);
            RedisUtil.deleteKey(authKey);
            RedisUtil.setValue(authKey,authKey,lockSeconds);
        }else {
            //解锁
            RedisUtil.deleteKey(limitKey);
        }
    }



    public void checkLoginErrorTimeLimit(String userAccount,String siteCode) {
        String lockKey = String.format(RedisConstants.KEY_LOGIN_ERROR_FIVE_TIMES_LIMIT,siteCode, userAccount);
        if (RedisUtil.isKeyExist(lockKey)) {
            Long limitSeconds = RedisUtil.getRemainExpireTime(lockKey);
            SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(LOGIN_ERROR_LOCK_TIME.getCode(),siteCode).getData();
            if (limitSeconds > 0) {
                throw new BaowangDefaultException(configValue.getHintInfo());
            }
        }
    }


    @Operation(summary = "发送邮箱验证码（无需登录）")
    @PostMapping("/v1/sendMail")
    public ResponseVO<?> sendMail(@Valid  @RequestBody MerchantLoginGetMailCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(vo.getMerchantAccount(), siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        if(StringUtils.isEmpty(agentMerchantVO.getEmail())){
            return ResponseVO.fail(ResultCode.ERROR_EMAIL);
        }
        if(UserChecker.checkEmail(agentMerchantVO.getEmail())){
            return ResponseVO.fail(ResultCode.EMAIL_TYPE_ERROR);
        }
        return agentMerchantApi.sendMail(vo);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/sendMailLogin")
    public ResponseVO<?> sendMailLogin(@Valid  @RequestBody MerchantLoginGetMailCodeVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(vo.getMerchantAccount(), siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        if(StringUtils.isNotEmpty(vo.getType()) && vo.getType().equals("rebind")){
            if(StringUtils.isEmpty(agentMerchantVO.getEmail())) {
                return ResponseVO.fail(ResultCode.MAIL_CODE_ERROR);
            }
        }
        // 邮箱不相同的情况下，
        if(StringUtils.isNotEmpty(agentMerchantVO.getEmail()) && !StringUtils.equals(vo.getEmail(), agentMerchantVO.getEmail())){
            return ResponseVO.fail(ResultCode.BIND_OTHER_BIND);
        }
        return agentMerchantApi.sendMail(vo);
    }


    @Operation(summary = "绑定邮箱")
    @PostMapping("/bindEmail")
    public ResponseVO<?> bindEmail(@Valid  @RequestBody MerchantLoginBindEmailVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);
        String merchatAccount = CurrReqUtils.getAccount();

        if(UserChecker.checkEmail(vo.getEmail())){
            return ResponseVO.fail(ResultCode.EMAIL_TYPE_ERROR);
        }
        // 校验验证码是否发送正确
        String verfiyCode = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, siteCode, merchatAccount));
        if(!StringUtils.equals(verfiyCode,vo.getCode())){
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }
        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(merchatAccount, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        long count = agentMerchantApi.countByAccountAndSite(vo.getEmail(), siteCode, agentMerchantVO.getId());
        if(count > 0){
            return ResponseVO.fail(ResultCode.OTHER_BIND);
        }
        return agentMerchantApi.bindEmail(merchatAccount, vo.getEmail(), siteCode);
    }


    @Operation(summary = "绑定google验证码")
    @PostMapping("/bindGoogle")
    public ResponseVO<?> bindGoogle(@Valid  @RequestBody MerchantLoginBindGoogleVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("绑定google验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);
        String merchatAccount = CurrReqUtils.getAccount();

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(merchatAccount, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        if (!verifyCode(vo.getCode(), vo.getGoogleAuthKey())) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        // 旧密码不正确
        boolean flag = agentMerchantApi.validate(agentMerchantVO, vo.getPassword());
        if (!flag) {
            return ResponseVO.fail(ResultCode.BIND_FAILED_ERROR_PASSWORD);
        }

        return agentMerchantApi.bindGoogle(agentMerchantVO, vo.getGoogleAuthKey());
    }


    @Operation(summary = "重新绑定google验证码")
    @PostMapping("/rebindGoogle")
    public ResponseVO<?> rebindGoogle(@Valid  @RequestBody MerchantLoginEmailBindGoogleVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("绑定google验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);
        String merchatAccount = CurrReqUtils.getAccount();

        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(merchatAccount, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        boolean flag = agentMerchantApi.validate(agentMerchantVO, vo.getPassword());  

        if (!flag) {
             return ResponseVO.fail(ResultCode.BIND_FAILED_ERROR_PASSWORD);
        }
        if (!verifyCode(vo.getCode(), vo.getGoogleAuthKey())) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        return agentMerchantApi.bindGoogle(agentMerchantVO, vo.getGoogleAuthKey());
    }


    private boolean verifyCode(String verifyCode, String googleAuthKey) {
        try {
            return GoogleAuthUtil.checkCode(googleAuthKey, Integer.parseInt(verifyCode));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    @Operation(summary = "校验邮箱")
    @PostMapping("/verfiyCode")
    public ResponseVO<?> verfiyCode(@Valid  @RequestBody MerchantLoginBindEmailVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);
        String merchatAccount = CurrReqUtils.getAccount();

        if(UserChecker.checkEmail(vo.getEmail())){
            return ResponseVO.fail(ResultCode.EMAIL_TYPE_ERROR);
        }
        // 校验验证码是否发送正确
        String verfiyCode = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, siteCode, merchatAccount));
        if(!StringUtils.equals(verfiyCode,vo.getCode())){
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }
        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(merchatAccount, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        return ResponseVO.success();
    }


    @Operation(summary = "生成谷歌验证码")
    @PostMapping("/createCode")
    public ResponseVO<?> createCode() {
        return ResponseVO.success(GoogleAuthUtil.generateSecretKey());
    }


    @Operation(summary = "获取客服通道")
    @PostMapping("/getCustomerChannel")
    public ResponseVO<?> getCustomerChannel() {
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(customerChannelApi.getCustomerChannel(siteCode));
    }


    @Operation(summary = "个人资料信息及总代")
    @PostMapping("/getMerchatInfo")
    public ResponseVO<?> getMerchatInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        String merchatAccount = CurrReqUtils.getAccount();
        MerchantAgentInfoVO merchantAgentInfo = agentMerchantApi.getMerchantAgentInfo(siteCode, merchatAccount);
        if (merchantAgentInfo == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        return ResponseVO.success(merchantAgentInfo);
    }


    @Operation(summary = "无登录校验邮箱（无需登录）")
    @PostMapping("/v1/notLoginCheckCode")
    public ResponseVO<?> notLoginCheckCode(@Valid  @RequestBody MerchantLoginBindNotLoginEmailVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        log.info("发送邮箱验证码 获取siteCode：{}", siteCode);
        vo.setSiteCode(siteCode);
        String merchantAccount = vo.getMerchantAccount();

        if(UserChecker.checkEmail(vo.getEmail())){
            return ResponseVO.fail(ResultCode.EMAIL_TYPE_ERROR);
        }
        // 校验验证码是否发送正确
        String verfiyCode = RedisUtil.getValue(String.format(RedisConstants.VERIFY_CODE_CACHE, siteCode, merchantAccount));
        if(!StringUtils.equals(verfiyCode,vo.getCode())){
            return ResponseVO.fail(ResultCode.AGENT_LOGIN_CODE_ERROR);
        }
        AgentMerchantVO agentMerchantVO = agentMerchantApi.getAdminByMerchantAccountAndSite(merchantAccount, siteCode);
        if (agentMerchantVO == null) {
            return ResponseVO.fail(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }
        if(StringUtils.isNotEmpty(agentMerchantVO.getEmail()) && !vo.getEmail().equals(agentMerchantVO.getEmail())){
            return ResponseVO.fail(ResultCode.EMAIL_TYPE_ERROR);
        }
        RedisUtil.setValue(String.format(RedisConstants.AGENT_VERIFY_CODE_RESULT, vo.getSiteCode(), vo.getMerchantAccount()), 1, 5 * 60L);
        return ResponseVO.success();
    }

}

package com.cloud.baowang.system.service.dict;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigReqVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.po.dict.SystemDictConfigChangeLogPO;
import com.cloud.baowang.system.po.dict.SystemDictConfigPO;
import com.cloud.baowang.system.repositories.dict.SystemDictConfigChangeLogMapper;
import com.cloud.baowang.system.repositories.dict.SystemDictConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemDictConfigService extends ServiceImpl<SystemDictConfigMapper, SystemDictConfigPO> {

    @Value("${common.config.jvPayDomain}")
    private String jvPayDomainUrl;


    @Value("${common.config.jvPayPrivateKey}")
    private String jvPayPrivateKey;

    private final SystemDictConfigChangeLogMapper logMapper;

    public ResponseVO<Page<SystemDictConfigRespVO>> pageQuery(SystemDictConfigPageQueryVO queryVO) {
        Page<SystemDictConfigPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
        String siteCode = queryVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(SystemDictConfigPO::getSiteCode, siteCode);
        }
        Integer isSyncSite = queryVO.getIsSyncSite();
        if (isSyncSite != null) {
            query.eq(SystemDictConfigPO::getIsSyncSite, isSyncSite);
        }

        page = this.page(page, query);
        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, SystemDictConfigRespVO.class))));
    }

    /**
     * 报文加密
     *
     * @param paramJson 请求参数
     * @return
     */
    private Map<String, String> buildHeadMap(String paramJson) {
        String timestamp = System.currentTimeMillis() + "";
        String random = RandomStringUtils.randomAlphabetic(6);
        String signVal = "";
        if (JSON.isValidArray(paramJson)) {
            JSONArray bodyJsonArray = JSONArray.parseArray(paramJson);
            signVal = ECDSAUtil.signParam(timestamp, random, bodyJsonArray, jvPayPrivateKey);
        } else {
            signVal = ECDSAUtil.signParam(timestamp, random, JSON.parseObject(paramJson), jvPayPrivateKey);
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(WalletConstants.HEAD_TIMESTAMP, timestamp);
        headerMap.put(WalletConstants.HEAD_RANDOM, random);
        headerMap.put(WalletConstants.HEAD_SIGN_NAME, signVal);
        return headerMap;
    }

    public void updJVPay(String siteCode,String param) {
        final String apiUrl = jvPayDomainUrl + "api/dict/updateAutoCollect";
        JSONObject paramObj = new JSONObject();
        paramObj.put("dictVal", param);
        paramObj.put("platNo",siteCode);
        Map<String, String> headerMap = buildHeadMap(paramObj.toJSONString());
        try {
            String result = HttpClientUtil.doPostJson(apiUrl, paramObj.toJSONString(), headerMap);
            log.info("同步fordJVPay结果:{}", result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object success = jsonObject.get("success");
            if (success != null && success.equals("true")) {
                log.error("http request success:{}", jsonObject.get("code"));
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> upd(SystemDictConfigReqVO reqVO) {
        String id = reqVO.getId();
        SystemDictConfigPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //增加长度校验最大9位除了特殊的除EZpay资金密码外code写死26
        if ( po.getDictCode() != 26){
            if (reqVO.getConfigParam().length() > 9) {
                throw new BaowangDefaultException(ConstantsCode.MAX_LENGTH);
            }
        }
        //针对返水执行脚本时间做特殊校验
        if (DictCodeConfigEnums.REBATE_SCRIPT_TIME.getCode().equals(po.getDictCode())){
            boolean isValid = reqVO.getConfigParam().matches("^([0-9]|1[0-9]|2[0-3])$");
            if (!isValid) {
                throw new BaowangDefaultException("请输入0-23之间数值");
            }
        }
        String before = po.getConfigParam();
        po.setConfigParam(reqVO.getConfigParam());
        po.setUpdater(reqVO.getOperator());
        po.setUpdatedTime(System.currentTimeMillis());
        Integer dictCode = po.getDictCode();
        if (DictCodeConfigEnums.USDT_MIN_THRESHOLD.getCode().equals(dictCode)) {
            String configParam = reqVO.getConfigParam();
            // 正则表达式：匹配最多两位小数的数字格式
            String regex = "^-?\\d+(\\.\\d{1,2})?$";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(configParam);
            if (!matcher.matches()) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            //调用一下ford的jvPay接口
            updJVPay(reqVO.getSiteCode(),reqVO.getConfigParam());
        }
        this.updateById(po);
        if (!before.equals(reqVO.getConfigParam())) {
            //变更前后不一样,才生成操作记录
            long currentTime = System.currentTimeMillis();
            SystemDictConfigChangeLogPO logPO = new SystemDictConfigChangeLogPO();
            logPO.setSiteCode(reqVO.getSiteCode());
            logPO.setConfigCategory(po.getConfigCategory());
            logPO.setConfigDescription(po.getConfigDescription());
            logPO.setConfigName(po.getConfigName());
            logPO.setBeforeChange(before);
            logPO.setAfterChange(reqVO.getConfigParam());
            logPO.setCreator(reqVO.getOperator());
            logPO.setCreatedTime(currentTime);
            logPO.setUpdater(reqVO.getOperator());
            logPO.setUpdatedTime(currentTime);
            logMapper.insert(logPO);
        }

        DictCodeConfigEnums configEnums = DictCodeConfigEnums.getByCode(po.getDictCode());
        //总台的字典编辑
        if (DictCodeConfigEnums.DictType.ADMIN_CENTER.getType().equals(configEnums.getType().getType())) {
            RedisUtil.deleteKey(String.format(RedisConstants.SYSTEM_DICT_CONFIG, CommonConstant.ADMIN_CENTER_SITE_CODE) + po.getDictCode());
        } else if (DictCodeConfigEnums.DictType.SITE_CENTER.getType().equals(configEnums.getType().getType())) {
            RedisUtil.deleteKey(String.format(RedisConstants.SYSTEM_DICT_CONFIG, po.getSiteCode()) + po.getDictCode());
        }
        return ResponseVO.success();
    }

    public ResponseVO<SystemDictConfigRespVO> getByCode(Integer dictCode, String siteCode) {
        DictCodeConfigEnums configEnums = DictCodeConfigEnums.getByCode(dictCode);
        if (configEnums == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        SystemDictConfigRespVO respVO = new SystemDictConfigRespVO();
        if (DictCodeConfigEnums.DictType.ADMIN_CENTER.getType().equals(configEnums.getType().getType())) {
            //总台的字典
            respVO = RedisUtil.getValue(String.format(RedisConstants.SYSTEM_DICT_CONFIG, CommonConstant.ADMIN_CENTER_SITE_CODE) + dictCode);
            if (respVO == null) {
                LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
                query.eq(SystemDictConfigPO::getDictCode, dictCode);
                query.eq(SystemDictConfigPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
                SystemDictConfigPO one = this.getOne(query);
                respVO = BeanUtil.copyProperties(one, SystemDictConfigRespVO.class);
                RedisUtil.setValue(String.format(RedisConstants.SYSTEM_DICT_CONFIG, CommonConstant.ADMIN_CENTER_SITE_CODE) + dictCode, respVO);
            }
        } else {
            //站点的字典
            respVO = RedisUtil.getValue(String.format(RedisConstants.SYSTEM_DICT_CONFIG, siteCode) + dictCode);
            if (respVO == null) {
                LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
                query.eq(SystemDictConfigPO::getDictCode, dictCode);
                query.eq(SystemDictConfigPO::getSiteCode, siteCode);
                SystemDictConfigPO one = this.getOne(query);
                if (one != null) {
                    respVO = BeanUtil.copyProperties(one, SystemDictConfigRespVO.class);
                    RedisUtil.setValue(String.format(RedisConstants.SYSTEM_DICT_CONFIG, siteCode) + dictCode, respVO);
                }
            }
        }
        return ResponseVO.success(respVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void initSiteDictConfig(String siteCode, String operator) {
        LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SystemDictConfigPO::getSiteCode, siteCode);
        if (this.count(query) <= 0) {
            //初始化一下站点的基础配置信息
            int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
            int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
            LambdaQueryWrapper<SystemDictConfigPO> systemQuery = Wrappers.lambdaQuery();
            systemQuery.eq(SystemDictConfigPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE)
                    .eq(SystemDictConfigPO::getIsSyncSite, yesCode);
            List<SystemDictConfigPO> systemDictList = this.list(systemQuery);
            long l = System.currentTimeMillis();
            systemDictList.forEach(item -> {
                item.setId(null);
                item.setIsSyncSite(noCode);
                item.setSiteCode(siteCode);

                item.setCreator(operator);
                item.setCreatedTime(l);
                item.setUpdater(operator);
                item.setUpdatedTime(l);
            });
            this.saveBatch(systemDictList);
        }
    }

    public ResponseVO<List<SystemDictConfigRespVO>> getListByCode(Integer dictCode) {
        LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SystemDictConfigPO::getDictCode, dictCode);
        List<SystemDictConfigPO> list = this.list(query);
        return ResponseVO.success(BeanUtil.copyToList(list, SystemDictConfigRespVO.class));
    }

    public ResponseVO<SystemDictConfigRespVO> queryWithdrawSwitch(String siteCode, Integer code) {
        LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
        query
                .eq(SystemDictConfigPO::getSiteCode, siteCode)
                .eq(SystemDictConfigPO::getDictCode, code);
        SystemDictConfigPO one = this.getOne(query);
        return ResponseVO.success(BeanUtil.copyProperties(one, SystemDictConfigRespVO.class));
    }

    public ResponseVO<List<SystemDictConfigRespVO>> getByCodes(List<Integer> dictCodes, String siteCode) {
        LambdaQueryWrapper<SystemDictConfigPO> query = Wrappers.lambdaQuery();
        query
                .eq(SystemDictConfigPO::getSiteCode, siteCode)
                .in(SystemDictConfigPO::getDictCode, dictCodes);
        List<SystemDictConfigPO> list = this.list(query);
        return ResponseVO.success(ConvertUtil.entityListToModelList(list,SystemDictConfigRespVO.class));
    }
}

package com.cloud.baowang.system.service.operations;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.po.operations.IpAddressAreaCurrencyPO;
import com.cloud.baowang.system.repositories.operations.IpAddressAreaCurrencyRepository;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.utils.TimeZoneUtils.patten_yyyMMddHHmmss;

@Slf4j
@Service
@AllArgsConstructor
public class IpAddressAreaCurrencyService extends ServiceImpl<IpAddressAreaCurrencyRepository, IpAddressAreaCurrencyPO> {

    private final IpAddressAreaCurrencyRepository ipAddressAreaCurrencyRepository;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;


    public Page<IpAddressAreaCurrencyResVO> findPage(IpAddressAreaCurrencyQueryReqVO reqVO) {
        Page<IpAddressAreaCurrencyPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());

        //条件
        LambdaQueryWrapper<IpAddressAreaCurrencyPO> query = new LambdaQueryWrapper<>();
        query.eq(StrUtil.isNotEmpty(reqVO.getCategoryName()),IpAddressAreaCurrencyPO::getCategoryName, reqVO.getCategoryName());
        query.eq(StrUtil.isNotEmpty(reqVO.getCurrencyCode()),IpAddressAreaCurrencyPO::getCurrencyCode, reqVO.getCurrencyCode());
        query.eq(StrUtil.isNotEmpty(reqVO.getCurrencyName()),IpAddressAreaCurrencyPO::getCurrencyName, reqVO.getCurrencyName());
        query.eq(reqVO.getStatus()!=null,IpAddressAreaCurrencyPO::getStatus, reqVO.getStatus());
        query.eq(reqVO.getId()!=null,IpAddressAreaCurrencyPO::getId, reqVO.getId());
        query.eq(StrUtil.isNotEmpty(reqVO.getCreator()),IpAddressAreaCurrencyPO::getCreator, reqVO.getCreator());
        query.eq(StrUtil.isNotEmpty(reqVO.getUpdater()),IpAddressAreaCurrencyPO::getUpdater, reqVO.getUpdater());

        query.orderByDesc(IpAddressAreaCurrencyPO::getDefaultType);
        query.orderByDesc(IpAddressAreaCurrencyPO::getUpdatedTime);
        Page<IpAddressAreaCurrencyPO> ipAddressAreaCurrencyPOPage = ipAddressAreaCurrencyRepository.selectPage(page, query);

        IPage<IpAddressAreaCurrencyResVO> convert = ipAddressAreaCurrencyPOPage.convert(item -> {
            IpAddressAreaCurrencyResVO vo = BeanUtil.copyProperties(item, IpAddressAreaCurrencyResVO.class);
            if (StrUtil.isNotEmpty(item.getAreaName())){
                List<AreaVO> list = JSONUtil.toList(item.getAreaName(), AreaVO.class);
                vo.setAreaNameList(list);
                vo.setAreaName(list.stream()
                        .map(AreaVO::getName)       // 提取每个 Map 中的 "name"
                        .filter(Objects::nonNull)          // 过滤掉 null 值（避免空指针）
                        .collect(Collectors.joining(",")));
            }
            return vo;
        });

        return ConvertUtil.toConverPage(convert);
    }


    public List<IpAddressAreaCurrencyResVO> findList(IpAddressAreaCurrencyReqVO reqVO){
        LambdaQueryWrapper<IpAddressAreaCurrencyPO> query = Wrappers.lambdaQuery(IpAddressAreaCurrencyPO.class);
        List<IpAddressAreaCurrencyPO> pos = ipAddressAreaCurrencyRepository.selectList(query);

        return pos.stream().map(po -> BeanUtil.copyProperties(po, IpAddressAreaCurrencyResVO.class)).toList();
    }


    public IpAddressAreaCurrencyResVO findById(IpAddressAreaCurrencyIdReqVO reqVO) {
        IpAddressAreaCurrencyPO po = ipAddressAreaCurrencyRepository.selectById(reqVO.getId());
        IpAddressAreaCurrencyResVO vo = BeanUtil.copyProperties(po, IpAddressAreaCurrencyResVO.class);
        if (StrUtil.isNotEmpty(po.getAreaName())){
            List<AreaVO> list = JSONUtil.toList(po.getAreaName(), AreaVO.class);
            vo.setAreaNameList(list);
            vo.setAreaName(list.stream()
                    .map(AreaVO::getName)       // 提取每个 Map 中的 "name"
                    .filter(Objects::nonNull)          // 过滤掉 null 值（避免空指针）
                    .collect(Collectors.joining(",")));
        }
        return  vo;
    }


    public ResponseVO<Boolean> insert(IpAddressAreaCurrencyAddReqVO reqVO) {
        IpAddressAreaCurrencyPO ipAddressAreaCurrencyPO = BeanUtil.copyProperties(reqVO, IpAddressAreaCurrencyPO.class);
        ipAddressAreaCurrencyPO.setCreatedTime(System.currentTimeMillis());
        ipAddressAreaCurrencyPO.setUpdatedTime(System.currentTimeMillis());
        ipAddressAreaCurrencyPO.setCategoryId(encodeToBase36(null));
        List<AreaVO> areaNameList = reqVO.getAreaNameList();
        if (CollUtil.isNotEmpty(areaNameList)){
            String joinedNames = areaNameList.stream()
                    .map(AreaVO::getCode)       // 提取每个 Map 中的 "name"
                    .filter(Objects::nonNull)          // 过滤掉 null 值（避免空指针）
                    .collect(Collectors.joining(","));
            ipAddressAreaCurrencyPO.setAreaCode(joinedNames);
            ipAddressAreaCurrencyPO.setAreaName(JSON.toJSONString(areaNameList));

        }
        deleteWebRedis();
        return ResponseVO.success(save(ipAddressAreaCurrencyPO));
    }


    public ResponseVO<Boolean>  update(IpAddressAreaCurrencyUpdateReqVO reqVO) {

        IpAddressAreaCurrencyPO po = ipAddressAreaCurrencyRepository.selectById(reqVO.getId());

        if (po==null){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        IpAddressAreaCurrencyPO ipAddressAreaCurrencyPO = BeanUtil.copyProperties(reqVO, IpAddressAreaCurrencyPO.class);
        ipAddressAreaCurrencyPO.setUpdatedTime(System.currentTimeMillis());
        List<AreaVO> areaNameList = reqVO.getAreaNameList();
        if (CollUtil.isNotEmpty(areaNameList)){
            String joinedNames = areaNameList.stream()
                    .map(AreaVO::getCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(","));
            ipAddressAreaCurrencyPO.setAreaCode(joinedNames);
            ipAddressAreaCurrencyPO.setAreaName(JSON.toJSONString(areaNameList));
        }
        //NOTE 默认项不能修改
        if (po.getDefaultType()==1){
            ipAddressAreaCurrencyPO.setAreaCode(null);
            ipAddressAreaCurrencyPO.setAreaName(null);
            ipAddressAreaCurrencyPO.setStatus(null);
            ipAddressAreaCurrencyPO.setOrderSort(null);
        }
        deleteWebRedis();
        return ResponseVO.success(updateById(ipAddressAreaCurrencyPO));
    }


    public ResponseVO<Boolean> delete(IpAddressAreaCurrencyIdReqVO reqVO) {
        IpAddressAreaCurrencyPO po = ipAddressAreaCurrencyRepository.selectById(reqVO.getId());

        if (po==null||po.getDefaultType()==1){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        deleteWebRedis();
        return ResponseVO.success(ipAddressAreaCurrencyRepository.deleteById(reqVO.getId())>0);
    }

    public ResponseVO<Boolean> enableOrDisable(IpAddressAreaCurrencyStatusReqVO reqVO) {

        EnableStatusEnum enableStatusEnum = EnableStatusEnum.nameOfCode(reqVO.getStatus());
        if (enableStatusEnum == null || StrUtil.isEmpty(reqVO.getId())) {
            return ResponseVO.success(false) ;
        }
        IpAddressAreaCurrencyPO po = IpAddressAreaCurrencyPO.builder().status(enableStatusEnum.getCode()).build();
        po.setId(reqVO.getId());
        ipAddressAreaCurrencyRepository.updateById(po);
        deleteWebRedis();
        return ResponseVO.success(true);
    }

    public ResponseVO<IpAdsWebResVO> queryWebCurrey(IpAdsWebReqVO reqVO){
        IpAdsWebResVO returnData=new IpAdsWebResVO();
        List<SiteCurrencyInfoRespVO> data= siteCurrencyInfoApi.getValidBySiteCode(reqVO.getSiteCode());
        IpAddressAreaCurrencyPO po= getIpAddressAreaCurrencyData(reqVO);
        String currencyCode=po.getCurrencyCode();
        var ref = new Object() {
            Boolean ischeck = false;
        };
        List<SiteCurrencyRespVO> currencys;
        currencys=data.stream().map(e ->{
            SiteCurrencyRespVO vo=new SiteCurrencyRespVO();
            BeanUtils.copyProperties(e, vo);
            if (vo.getCurrencyCode().equals(currencyCode)){
                ref.ischeck = true;
                vo.setIsCheck(true);
            }else{
                vo.setIsCheck(false);
            }
            return vo;
        }).collect(Collectors.toList());
        if (!ref.ischeck){
            IpAddressAreaCurrencyPO defluatCur= getDefaultCurrcny();
            currencys = data.stream().map(e ->{
                SiteCurrencyRespVO vo=new SiteCurrencyRespVO();
                BeanUtils.copyProperties(e, vo);
                if (vo.getCurrencyCode().equals(defluatCur.getCurrencyCode())){
                    vo.setIsCheck(true);
                }else{
                    vo.setIsCheck(false);
                }
                return vo;
            }).collect(Collectors.toList());
        }
        returnData.setCurrencys(currencys);
        returnData.setAreaCode(reqVO.getAreaCode());
        returnData.setIp(reqVO.getIp());
        returnData.setCurrencyCode(currencyCode);
        returnData.setCity(reqVO.getCity());
        return ResponseVO.success(returnData);
    }

    private  IpAddressAreaCurrencyPO getIpAddressAreaCurrencyData(IpAdsWebReqVO reqVO){
        String key=CacheConstants.IP_ADDRESS_CURRENCY+":"+reqVO.getAreaCode();
        IpAddressAreaCurrencyPO returnData = RedisUtil.getValue(key);
        if (ObjectUtils.isNotEmpty(returnData)){
            return returnData;
        }
        LambdaQueryWrapper<IpAddressAreaCurrencyPO> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(IpAddressAreaCurrencyPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        if (ObjectUtils.isNotEmpty(reqVO.getAreaCode())){
            lambdaQuery.like(IpAddressAreaCurrencyPO::getAreaCode,reqVO.getAreaCode())
                    .or().eq(IpAddressAreaCurrencyPO::getDefaultType, EnableStatusEnum.ENABLE.getCode());
        }else{
            lambdaQuery.eq(IpAddressAreaCurrencyPO::getDefaultType,EnableStatusEnum.ENABLE.getCode());
        }
        lambdaQuery.last("ORDER BY default_type asc,order_sort DESC, updated_time DESC LIMIT 1");
        IpAddressAreaCurrencyPO po = this.getBaseMapper().selectOne(lambdaQuery);
        RedisUtil.setValue(key, po);
        return po;
    }

    private IpAddressAreaCurrencyPO getDefaultCurrcny(){
        String key=CacheConstants.DEFALUT_IP_ADDRESS_CURRENCY;
        IpAddressAreaCurrencyPO returnData = RedisUtil.getValue(key);
        if (ObjectUtils.isNotEmpty(returnData)){
            return returnData;
        }
        LambdaQueryWrapper<IpAddressAreaCurrencyPO> defluatData = Wrappers.lambdaQuery();
        defluatData.eq(IpAddressAreaCurrencyPO::getDefaultType,EnableStatusEnum.ENABLE.getCode());
        IpAddressAreaCurrencyPO defaultData=this.getBaseMapper().selectOne(defluatData);
        RedisUtil.setValue(key, defaultData);
        return defaultData;
    }

    private void deleteWebRedis(){
        RedisUtil.deleteKeysByPattern(CacheConstants.IP_ADDRESS_CURRENCY+":*");
        RedisUtil.deleteKeysByPattern(CacheConstants.DEFALUT_IP_ADDRESS_CURRENCY);
    }

    public static String encodeToBase36(String timestampStr) {
        try {

            if (StrUtil.isEmpty(timestampStr)){
                timestampStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            }

            long timestamp = Long.parseLong(timestampStr);
            String base36 = Long.toString(timestamp, 36).toUpperCase();

            // 保证结果为 6 位，取最后6位（可调）
            if (base36.length() >= 6) {
                return base36.substring(base36.length() - 6);
            } else {
                return String.format("%6s", base36).replace(' ', '0');  // 不足6位左补0
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp input: " + timestampStr);
        }
    }


}

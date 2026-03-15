package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ActivityRedemptionCodeReqVO;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeBaseRespVO;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeDetailVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeBasePO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeDetailPO;
import com.cloud.baowang.activity.repositories.ActivityRedemptionCodeBaseRepository;
import com.cloud.baowang.activity.repositories.ActivityRedemptionCodeDetailRepository;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 兑换码基础信息处理
 */
@Service
@Slf4j
public class ActivityRedemptionCodeBaseService extends ServiceImpl<ActivityRedemptionCodeBaseRepository, SiteActivityRedemptionCodeBasePO> {



}

package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.v2.ActivityContestPayoutV2VO;
import com.cloud.baowang.activity.api.vo.v2.ActivityContestPayoutVenueV2VO;
import com.cloud.baowang.activity.po.v2.SiteActivityContestPayoutV2PO;
import com.cloud.baowang.activity.repositories.v2.ActivityContestPayoutV2Repository;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动赛事包赔服务类
 *
 * @author brence
 * @date 2025-10-20
 */
@Service
@AllArgsConstructor
@Slf4j
public class ActivityContestPayoutV2Service extends ServiceImpl<ActivityContestPayoutV2Repository, SiteActivityContestPayoutV2PO> {


    private final I18nApi i18nApi;

    /**
     * 保存活动包赔信息
     *
     * @param contestPayoutV2VO
     * @return
     */
    public boolean insert(ActivityContestPayoutV2VO contestPayoutV2VO, String activityId) {

        boolean flag = false;
        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        SiteActivityContestPayoutV2PO contestPayoutV2PO = converseContestPayoutPO(contestPayoutV2VO);

        //设置活动id
        contestPayoutV2PO.setActivityId(activityId);
        //设置站点code
        contestPayoutV2PO.setSiteCode(contestPayoutV2VO.getSiteCode());
        //设置活动适用范围,0:全体会员,1:新注册会员
        contestPayoutV2PO.setActivityScope(contestPayoutV2VO.getActivityScope());
        //设置活动访问参数,默认为活动id
        contestPayoutV2PO.setAccessParameters(contestPayoutV2VO.getAccessParameters());
        //设置场馆名称
        contestPayoutV2PO.setVenueName(contestPayoutV2VO.getVenueName());
        //设置场馆编码
        contestPayoutV2PO.setVenueCode(contestPayoutV2VO.getVenueCode());
        //设置场馆类型
        contestPayoutV2PO.setVenueType(contestPayoutV2VO.getVenueType());
        //设置活动币种
        contestPayoutV2PO.setPlatformOrFiatCurrency(contestPayoutV2VO.getPlatformOrFiatCurrency());
        //保存活动包赔信息
        int rows = this.baseMapper.insert(contestPayoutV2PO);

        flag = (rows > 0);

        log.info("SiteActivityContestPayoutV2PO save result: {}", flag);

        return flag;

    }

    /**
     * 修改赛事包赔活动信息
     *
     * @param activityContestPayoutV2VO 赛事活动包赔对象
     * @return
     */
    public boolean updateInfo(ActivityContestPayoutV2VO activityContestPayoutV2VO) {

        LambdaQueryWrapper<SiteActivityContestPayoutV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据赛事包赔记录id,活动id，站点编码查询对应的赛事包赔记录
        lambdaQueryWrapper.eq(SiteActivityContestPayoutV2PO::getActivityId, activityContestPayoutV2VO.getActivityId());
        lambdaQueryWrapper.eq(SiteActivityContestPayoutV2PO::getSiteCode, activityContestPayoutV2VO.getSiteCode());

        SiteActivityContestPayoutV2PO siteActivityContestPayoutV2PO = this.baseMapper.selectOne(lambdaQueryWrapper);

        if (siteActivityContestPayoutV2PO == null) {
            log.error("update SiteActivityContestPayoutV2PO fail,SiteActivityContestPayoutV2PO is null");
            return false;
        }

        SiteActivityContestPayoutV2PO contestPayoutV2PO = converseContestPayoutPO(activityContestPayoutV2VO);

        List<ActivityContestPayoutVenueV2VO> venueList = activityContestPayoutV2VO.getActivityContestPayoutVenueVOS();

        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        for (ActivityContestPayoutVenueV2VO contestPayoutVenueV2VO : venueList) {
            // 设置互动规则
            String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
            contestPayoutVenueV2VO.setActivityRuleI18nCode(activityRuleI18);
            i18nData.put(activityRuleI18, contestPayoutVenueV2VO.getActivityRuleI18nCodeList());
        }

        String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());

        //NOTE A_APP
        List<I18nMsgFrontVO> a_app_msg_list = activityContestPayoutV2VO.getThirdADayAppI18nCodeList();
        List<I18nMsgFrontVO> a_app_nigth_msg_list = activityContestPayoutV2VO.getThirdANightAppI18nCodeList();
        List<I18nMsgFrontVO> a_pc_msg_list = activityContestPayoutV2VO.getThirdADayPcI18nCodeList();
        List<I18nMsgFrontVO> a_pc_nigth_msg_list = activityContestPayoutV2VO.getThirdANightPcI18nCodeList();

        List<I18nMsgFrontVO> b_app_msg_list = activityContestPayoutV2VO.getThirdBDayAppI18nCodeList();
        List<I18nMsgFrontVO> b_app_nigth_msg_list = activityContestPayoutV2VO.getThirdBNightAppI18nCodeList();
        List<I18nMsgFrontVO> b_pc_msg_list = activityContestPayoutV2VO.getThirdBDayPcI18nCodeList();
        List<I18nMsgFrontVO> b_pc_nigth_msg_list = activityContestPayoutV2VO.getThirdBNightPcI18nCodeList();

        i18nData.put(activityRuleI18, a_app_msg_list);
        i18nData.put(activityRuleI18, a_app_msg_list);
        i18nData.put(activityRuleI18, a_app_msg_list);
        i18nData.put(activityRuleI18, a_app_msg_list);
        i18nData.put(activityRuleI18, a_app_msg_list);

        LambdaUpdateWrapper<SiteActivityContestPayoutV2PO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getVenueName, contestPayoutV2PO.getVenueName());
        lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getVenueCode, contestPayoutV2PO.getVenueCode());
        lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getAccessParameters, contestPayoutV2PO.getAccessParameters());
        lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getVenueType, contestPayoutV2PO.getVenueType());

        lambdaUpdateWrapper.eq(SiteActivityContestPayoutV2PO::getActivityId, contestPayoutV2PO.getActivityId());

        if (StrUtil.isNotBlank(contestPayoutV2PO.getSiteCode())) {
            lambdaUpdateWrapper.eq(SiteActivityContestPayoutV2PO::getSiteCode, contestPayoutV2PO.getSiteCode());
        }
        //设置赛事三方A白天PC端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdADayPcI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdADayPcI18nCode, contestPayoutV2PO.getThirdADayPcI18nCode());
        }
        //设置赛事三方A夜间PC端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdANightPcI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdANightPcI18nCode, contestPayoutV2PO.getThirdANightPcI18nCode());
        }

        //设置赛事三方A白天app端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdADayAppI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdADayAppI18nCode, contestPayoutV2PO.getThirdADayAppI18nCode());
        }
        //设置赛事三方A夜间app端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdANightAppI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdANightAppI18nCode, contestPayoutV2PO.getThirdANightAppI18nCode());
        }

        //设置赛事三方B白天PC端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdBDayPcI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdBDayPcI18nCode, contestPayoutV2PO.getThirdBDayPcI18nCode());
        }
        //设置赛事三方B夜间PC端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdBNightPcI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdBNightPcI18nCode, contestPayoutV2PO.getThirdBNightPcI18nCode());
        }

        //设置赛事三方B白天app端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdADayAppI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdBDayAppI18nCode, contestPayoutV2PO.getThirdBDayAppI18nCode());
        }
        //设置赛事三方B夜间app端图片
        if (StrUtil.isNotBlank(activityContestPayoutV2VO.getThirdANightAppI18nCode())) {
            lambdaUpdateWrapper.set(SiteActivityContestPayoutV2PO::getThirdBNightAppI18nCode, contestPayoutV2PO.getThirdBNightAppI18nCode());
        }
        this.update(lambdaUpdateWrapper);

        return true;
    }

    /**
     * 根据活动id
     * 获取一个赛事包赔活动记录
     *
     * @param activityId
     * @return
     */
    public SiteActivityContestPayoutV2PO info(String activityId) {
        LambdaQueryWrapper<SiteActivityContestPayoutV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityContestPayoutV2PO::getActivityId, activityId);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 根据siteCode
     * 删除其对应的所有赛事包赔活动记录
     *
     * @param siteCode
     */
    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityContestPayoutV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityContestPayoutV2PO::getSiteCode, siteCode);
        this.baseMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据指定的活动id列表
     * 获取其记录列表
     *
     * @param activityIds
     * @return
     */
    public List<SiteActivityContestPayoutV2PO> selectByActivityIds(String... activityIds) {
        LambdaQueryWrapper<SiteActivityContestPayoutV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityContestPayoutV2PO>();
        lambdaQueryWrapper.in(SiteActivityContestPayoutV2PO::getActivityId, activityIds);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据活动id删除赛事包赔记录
     *
     * @param activityId
     */
    public boolean deleteByActivityId(String activityId) {
        /**
         *
         * 主表只是将状态更改为禁用，字表没有状态字段，故不做任何操作
         LambdaQueryWrapper<SiteActivityContestPayoutV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
         lambdaQueryWrapper.eq(SiteActivityContestPayoutV2PO::getActivityId, activityId);
         int rows = this.baseMapper.delete(lambdaQueryWrapper);
         boolean flag = (rows >= 1 ? true: false);

         log.info("ActivityContestPayoutV2Service deleteByActivityId result:{},activityId={}",flag,activityId);
         */
        return true;
    }


    private void putIfNotNull(Map<String, List<I18nMsgFrontVO>> map, String key, List<I18nMsgFrontVO> value) {
        if (key != null && value != null) {
            map.put(key, value);
        }
    }

    /**
     * 将PO中的I18nList转成对应的code
     *
     * @param vo
     * @return
     */
    private SiteActivityContestPayoutV2PO converseContestPayoutPO(ActivityContestPayoutV2VO vo) {

        SiteActivityContestPayoutV2PO po = new SiteActivityContestPayoutV2PO();
        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();

        po.setId(vo.getId());
        po.setActivityId(vo.getId());
        po.setSiteCode(vo.getSiteCode());
        po.setVenueType(vo.getVenueType());
        po.setVenueCode(vo.getVenueCode());
        //设置活动范围
        po.setActivityScope(vo.getActivityScope());

        if (ObjectUtil.isNull(po)) {
            po = ConvertUtil.entityToModel(vo, SiteActivityContestPayoutV2PO.class);
        }
        //赛事方A白天app图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdADayAppI18nCodeList())) {
            String thirdADayAppI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_A_DAY_APP.getCode());
            po.setThirdADayAppI18nCode(thirdADayAppI18nCode);
            i18nData.put(thirdADayAppI18nCode, vo.getThirdADayAppI18nCodeList());

        }
        //赛事方A夜间app图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdANightAppI18nCodeList())) {
            String thirdANightAppI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_A_NIGHT_APP.getCode());
            po.setThirdANightAppI18nCode(thirdANightAppI18nCode);
            i18nData.put(thirdANightAppI18nCode, vo.getThirdANightAppI18nCodeList());

        }
        //赛事方A白天pc图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdADayPcI18nCodeList())) {
            String thirdADayPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_A_DAY_PC.getCode());
            po.setThirdADayPcI18nCode(thirdADayPcI18nCode);
            i18nData.put(thirdADayPcI18nCode, vo.getThirdADayPcI18nCodeList());

        }
        //赛事方A夜间pc图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdANightPcI18nCodeList())) {
            String thirdANightPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_A_NIGHT_PC.getCode());
            po.setThirdANightPcI18nCode(thirdANightPcI18nCode);
            i18nData.put(thirdANightPcI18nCode, vo.getThirdANightPcI18nCodeList());

        }
        //赛事方B夜间pc图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdBNightPcI18nCodeList())) {
            String thirdBNightPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_B_NIGHT_PC.getCode());
            po.setThirdBNightPcI18nCode(thirdBNightPcI18nCode);
            i18nData.put(thirdBNightPcI18nCode, vo.getThirdBNightPcI18nCodeList());

        }
        //赛事方B白天app图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdBDayAppI18nCodeList())) {
            String thirdBDayAppI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_B_DAY_APP.getCode());
            po.setThirdBDayAppI18nCode(thirdBDayAppI18nCode);
            i18nData.put(thirdBDayAppI18nCode, vo.getThirdBDayAppI18nCodeList());

        }
        //赛事方B夜间app图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdBNightAppI18nCodeList())) {
            String thirdBNightAppI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_B_NIGHT_APP.getCode());
            po.setThirdBNightAppI18nCode(thirdBNightAppI18nCode);
            i18nData.put(thirdBNightAppI18nCode, vo.getThirdBNightAppI18nCodeList());

        }
        //赛事方B白天pc图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdBDayPcI18nCodeList())) {
            String thirdBDayPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_B_DAY_PC.getCode());
            po.setThirdBDayPcI18nCode(thirdBDayPcI18nCode);
            i18nData.put(thirdBDayPcI18nCode, vo.getThirdBDayPcI18nCodeList());

        }
        //赛事方B夜间pc图片编码转换
        if (CollectionUtil.isNotEmpty(vo.getThirdBNightPcI18nCodeList())) {
            String thirdBNightPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ThIRD_B_NIGHT_PC.getCode());
            po.setThirdBNightPcI18nCode(thirdBNightPcI18nCode);
            i18nData.put(thirdBNightPcI18nCode, vo.getThirdBNightPcI18nCodeList());

        }
        //设置活动大类列表--目前仅体育类
        List<ActivityContestPayoutVenueV2VO> venueList = vo.getActivityContestPayoutVenueVOS();
        if (CollUtil.isNotEmpty(venueList)) {
            for (ActivityContestPayoutVenueV2VO venueV2VO : venueList) {
                // 活动规则
                String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
                venueV2VO.setActivityRuleI18nCode(activityRuleI18);
                i18nData.put(activityRuleI18, venueV2VO.getActivityRuleI18nCodeList());
            }
        }
        // 保存i18n
        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return po;
    }
}

package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.user.api.enums.MedalLockStatusEnum;
import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordNewReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.po.MedalAcquireRecordPO;
import com.cloud.baowang.user.repositories.MedalAcquireRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class MedalAcquireRecordService extends ServiceImpl<MedalAcquireRecordRepository, MedalAcquireRecordPO> {

    private final UserInfoService userInfoService;

    private final VipGradeApi vipGradeApi;

    private final I18nApi i18nApi;

    private final AgentInfoApi agentInfoApi;

    /**
     * 勋章获取记录查询
     *
     * @param medalAcquireRecordReqVO 请求参数
     * @return
     */
    public ResponseVO<Page<MedalAcquireRecordRespVO>> listPage(MedalAcquireRecordReqVO medalAcquireRecordReqVO) {
        Page<MedalAcquireRecordPO> page = new Page<MedalAcquireRecordPO>(medalAcquireRecordReqVO.getPageNumber(), medalAcquireRecordReqVO.getPageSize());
        LambdaQueryWrapper<MedalAcquireRecordPO> lqw = new LambdaQueryWrapper<MedalAcquireRecordPO>();
        if (medalAcquireRecordReqVO.getSiteCode() != null) {
            lqw.eq(MedalAcquireRecordPO::getSiteCode, medalAcquireRecordReqVO.getSiteCode());
        }
        if (StringUtils.hasText(medalAcquireRecordReqVO.getMedalName())) {
            List<String> messageKeyLists=i18nApi.getMessageKeyLikeKeyAndMessage(I18MsgKeyEnum.MEDAL_NAME.getCode(),medalAcquireRecordReqVO.getMedalName()).getData();
            if(!CollectionUtils.isEmpty(messageKeyLists)){
                lqw.in(MedalAcquireRecordPO::getMedalNameI18, messageKeyLists);
            }else {
                lqw.in(MedalAcquireRecordPO::getMedalNameI18, medalAcquireRecordReqVO.getMedalName());
            }
        }
        if (StringUtils.hasText(medalAcquireRecordReqVO.getUserAccount())) {
            lqw.like(MedalAcquireRecordPO::getUserAccount, medalAcquireRecordReqVO.getUserAccount());
        }
        if (medalAcquireRecordReqVO.getCompleteTimeStart() != null) {
            lqw.ge(MedalAcquireRecordPO::getCompleteTime, medalAcquireRecordReqVO.getCompleteTimeStart());
        }

        if (medalAcquireRecordReqVO.getCompleteTimeEnd() != null) {
            lqw.le(MedalAcquireRecordPO::getCompleteTime, medalAcquireRecordReqVO.getCompleteTimeEnd());
        }
        if (medalAcquireRecordReqVO.getUnlockTimeStart() != null) {
            lqw.ge(MedalAcquireRecordPO::getUnlockTime, medalAcquireRecordReqVO.getUnlockTimeStart());
        }

        if (medalAcquireRecordReqVO.getUnlockTimeEnd() != null) {
            lqw.le(MedalAcquireRecordPO::getUnlockTime, medalAcquireRecordReqVO.getUnlockTimeEnd());
        }
        if(StringUtils.hasText(medalAcquireRecordReqVO.getOrderField())){
            boolean ascFlag=false;
            if(medalAcquireRecordReqVO.getOrderType().equals("asc")){
                ascFlag=true;
            }
            if(medalAcquireRecordReqVO.getOrderField().equals("completeTime")){
                lqw.orderBy(true,ascFlag,MedalAcquireRecordPO::getCompleteTime);
            }
            if(medalAcquireRecordReqVO.getOrderField().equals("unlockTime")){
                lqw.orderBy(true,ascFlag,MedalAcquireRecordPO::getUnlockTime);
            }
        }else {
            lqw.orderByDesc(MedalAcquireRecordPO::getUnlockTime);
        }

        IPage<MedalAcquireRecordPO> medalInfoIPage = this.baseMapper.selectPage(page, lqw);
        Page<MedalAcquireRecordRespVO> medalInfoRespVOPage = new Page<MedalAcquireRecordRespVO>(medalAcquireRecordReqVO.getPageNumber(), medalAcquireRecordReqVO.getPageSize());
        medalInfoRespVOPage.setTotal(medalInfoIPage.getTotal());
        medalInfoRespVOPage.setPages(medalInfoIPage.getPages());
        List<MedalAcquireRecordRespVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(medalInfoIPage.getRecords())) {
            for (MedalAcquireRecordPO medalAcquireRecordPO : medalInfoIPage.getRecords()) {
                MedalAcquireRecordRespVO medalInfoRespVO = new MedalAcquireRecordRespVO();
                BeanUtils.copyProperties(medalAcquireRecordPO, medalInfoRespVO);
                medalInfoRespVO.setRewardAmount(BigDecimalUtils.formatFourVal(medalAcquireRecordPO.getRewardAmount()));
                resultLists.add(medalInfoRespVO);
            }
        }
        medalInfoRespVOPage.setRecords(resultLists);
        return ResponseVO.success(medalInfoRespVOPage);
    }


    public ResponseVO<List<MedalAcquireRecordRespVO>> listByCond(MedalAcquireRecordCondReqVO medalAcquireRecordCondReqVO) {
        LambdaQueryWrapper<MedalAcquireRecordPO> lqw = new LambdaQueryWrapper<MedalAcquireRecordPO>();
        if (medalAcquireRecordCondReqVO.getSiteCode() != null) {
            lqw.eq(MedalAcquireRecordPO::getSiteCode, medalAcquireRecordCondReqVO.getSiteCode());
        }
        if (medalAcquireRecordCondReqVO.getUserId() != null) {
            lqw.eq(MedalAcquireRecordPO::getUserId, medalAcquireRecordCondReqVO.getUserId());
        }
        if (medalAcquireRecordCondReqVO.getMedalCodeEnum() != null) {
            lqw.eq(MedalAcquireRecordPO::getMedalCode, medalAcquireRecordCondReqVO.getMedalCodeEnum().getCode());
        }
        List<MedalAcquireRecordPO> medalAcquireRecordPOS = this.baseMapper.selectList(lqw);
        List<MedalAcquireRecordRespVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(medalAcquireRecordPOS)) {
            for (MedalAcquireRecordPO medalAcquireRecordPO : medalAcquireRecordPOS) {
                MedalAcquireRecordRespVO medalInfoRespVO = new MedalAcquireRecordRespVO();
                BeanUtils.copyProperties(medalAcquireRecordPO, medalInfoRespVO);
                resultLists.add(medalInfoRespVO);
            }
        }
        return ResponseVO.success(resultLists);
    }


    public ResponseVO<Long> countByCond(MedalAcquireRecordCondReqVO medalAcquireRecordCondReqVO) {
        LambdaQueryWrapper<MedalAcquireRecordPO> lqw = new LambdaQueryWrapper<MedalAcquireRecordPO>();
        if (medalAcquireRecordCondReqVO.getSiteCode() != null) {
            lqw.eq(MedalAcquireRecordPO::getSiteCode, medalAcquireRecordCondReqVO.getSiteCode());
        }
        if (medalAcquireRecordCondReqVO.getUserId() != null) {
            lqw.eq(MedalAcquireRecordPO::getUserId, medalAcquireRecordCondReqVO.getUserId());
        }
        if (medalAcquireRecordCondReqVO.getMedalCodeEnum() != null) {
            lqw.eq(MedalAcquireRecordPO::getMedalCode, medalAcquireRecordCondReqVO.getMedalCodeEnum().getCode());
        }
        return ResponseVO.success(this.baseMapper.selectCount(lqw));
    }


    public List<MedalAcquireRecordPO> listByUserNoAndSiteCode(String currentUserNo, String siteCode) {
        LambdaQueryWrapper<MedalAcquireRecordPO> lqw = new LambdaQueryWrapper<MedalAcquireRecordPO>();
        lqw.eq(MedalAcquireRecordPO::getSiteCode, siteCode);
        lqw.eq(MedalAcquireRecordPO::getUserAccount, currentUserNo);
        lqw.orderByAsc(MedalAcquireRecordPO::getId);
        return this.baseMapper.selectList(lqw);
    }

    /**
     * 获取奖励配置
     * 增加余额
     * 增加打码量
     *
     * @param medalAcquireRecordNewReqVO
     * @param siteMedalInfoRespVO
     */

    public void insert(MedalAcquireRecordNewReqVO medalAcquireRecordNewReqVO, SiteMedalInfoRespVO siteMedalInfoRespVO) {
        LambdaQueryWrapper<MedalAcquireRecordPO> medalAcquireRecordPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        medalAcquireRecordPOLambdaQueryWrapper.eq(MedalAcquireRecordPO::getSiteCode, medalAcquireRecordNewReqVO.getSiteCode());
        medalAcquireRecordPOLambdaQueryWrapper.eq(MedalAcquireRecordPO::getMedalCode, medalAcquireRecordNewReqVO.getMedalCode());
        medalAcquireRecordPOLambdaQueryWrapper.eq(MedalAcquireRecordPO::getUserId, medalAcquireRecordNewReqVO.getUserId());
        Long countNum = this.baseMapper.selectCount(medalAcquireRecordPOLambdaQueryWrapper);
        //判断是否已经解锁勋章 已解锁则无需记录
        if (countNum > 0) {
            log.info("当前用户:{}已经获得勋章:{},不能再次获取", medalAcquireRecordNewReqVO.getUserId(), medalAcquireRecordNewReqVO.getMedalCode());
            return;
        }
        UserInfoVO userInfoVO=userInfoService.getByUserId(medalAcquireRecordNewReqVO.getUserId());

        String userId=userInfoVO.getUserId();
        String agentId=userInfoVO.getSuperAgentId();
        if(StringUtils.hasText(agentId)){
            AgentInfoVO agentInfoVO=agentInfoApi.getByAgentId(agentId);
            String userBenefit=agentInfoVO.getUserBenefit();
            if(!StringUtils.hasText(userBenefit)){
                log.info("当前用户:{},上级代理:{}设置会员福利为空,所以不能派发勋章:{}", userId, agentId,medalAcquireRecordNewReqVO.getMedalCode());
                return;
            }
            if(!userBenefit.contains(AgentUserBenefitEnum.MEDAL_REWARD.getCode().toString())){
                log.info("当前用户:{},上级代理:{}设置会员福利,没有勾选勋章奖励,不能派发勋章:{}", userId, agentId,medalAcquireRecordNewReqVO.getMedalCode());
                return ;
            }
        }

        String orderNo = OrderUtil.getOrderNo("ME");
        MedalAcquireRecordPO medalAcquireRecordPO = new MedalAcquireRecordPO();
        medalAcquireRecordPO.setOrderNo(orderNo);

        medalAcquireRecordPO.setMainCurrency(userInfoVO.getMainCurrency());
        medalAcquireRecordPO.setSuperAgentId(userInfoVO.getSuperAgentId());
        medalAcquireRecordPO.setSuperAgentAccount(userInfoVO.getSuperAgentAccount());
        medalAcquireRecordPO.setVipGradeCode(userInfoVO.getVipGradeCode());
        SiteVIPGradeVO siteVIPGradeVO=vipGradeApi.getSiteVipGradeByCodeAndSiteCode(medalAcquireRecordNewReqVO.getSiteCode(),userInfoVO.getVipGradeCode());
        if(siteVIPGradeVO!=null){
            medalAcquireRecordPO.setVipGradeName(siteVIPGradeVO.getVipGradeName());
        }
        medalAcquireRecordPO.setSiteCode(medalAcquireRecordNewReqVO.getSiteCode());
        medalAcquireRecordPO.setMedalId(siteMedalInfoRespVO.getId());
        medalAcquireRecordPO.setMedalCode(medalAcquireRecordNewReqVO.getMedalCode());
        medalAcquireRecordPO.setMedalName(siteMedalInfoRespVO.getMedalName());
        medalAcquireRecordPO.setMedalNameI18(siteMedalInfoRespVO.getMedalNameI18());
        medalAcquireRecordPO.setMedalDesc(siteMedalInfoRespVO.getMedalDesc());
        medalAcquireRecordPO.setRewardAmount(siteMedalInfoRespVO.getRewardAmount());
        medalAcquireRecordPO.setTypingMultiple(siteMedalInfoRespVO.getTypingMultiple());
        medalAcquireRecordPO.setUserId(userInfoVO.getUserId());
        medalAcquireRecordPO.setUserAccount(userInfoVO.getUserAccount());
        medalAcquireRecordPO.setCondNum1(siteMedalInfoRespVO.getCondNum1());
        medalAcquireRecordPO.setCondNum2(siteMedalInfoRespVO.getCondNum2());
        medalAcquireRecordPO.setLockStatus(MedalLockStatusEnum.CAN_UNLOCK.getCode());
        medalAcquireRecordPO.setCompleteTime(System.currentTimeMillis());
        medalAcquireRecordPO.setCreatedTime(System.currentTimeMillis());

        log.info("当前用户:{}记录勋章:{}成功", medalAcquireRecordNewReqVO.getUserId(), medalAcquireRecordNewReqVO.getMedalCode());
        this.baseMapper.insert(medalAcquireRecordPO);
    }


    public MedalAcquireRecordPO selectByUniq(String siteCode, String userAccount, String medalCode) {
        LambdaQueryWrapper<MedalAcquireRecordPO> medalAcquireRecordPOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        medalAcquireRecordPOLambdaQueryWrapper.eq(MedalAcquireRecordPO::getSiteCode, siteCode);
        medalAcquireRecordPOLambdaQueryWrapper.eq(MedalAcquireRecordPO::getMedalCode, medalCode);
        medalAcquireRecordPOLambdaQueryWrapper.eq(MedalAcquireRecordPO::getUserAccount, userAccount);
        return this.baseMapper.selectOne(medalAcquireRecordPOLambdaQueryWrapper);
    }


    public void lightUpMedal(String id, int lockStatus) {
        MedalAcquireRecordPO medalAcquireRecordPO = new MedalAcquireRecordPO();
        medalAcquireRecordPO.setId(id);
        medalAcquireRecordPO.setLockStatus(lockStatus);
        medalAcquireRecordPO.setUnlockTime(System.currentTimeMillis());
        medalAcquireRecordPO.setUpdatedTime(System.currentTimeMillis());
        this.baseMapper.updateById(medalAcquireRecordPO);
    }

    public ResponseVO<List<MedalAcquireRecordRespVO>> getRecordByUserAccountAndMedalType(List<String> userAccount, String siteCode, String medalCode) {
        LambdaQueryWrapper<MedalAcquireRecordPO> query = Wrappers.lambdaQuery();
        query.eq(MedalAcquireRecordPO::getSiteCode, siteCode).in(MedalAcquireRecordPO::getUserAccount, userAccount).eq(MedalAcquireRecordPO::getMedalCode, medalCode);
        List<MedalAcquireRecordPO> list = this.list(query);
        return ResponseVO.success(BeanUtil.copyToList(list,MedalAcquireRecordRespVO.class));
    }
}

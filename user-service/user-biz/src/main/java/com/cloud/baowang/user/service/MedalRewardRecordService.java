package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.user.api.enums.MedalOpenStatusEnum;
import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardRecordRespVO;
import com.cloud.baowang.user.po.MedalRewardRecordPO;
import com.cloud.baowang.user.repositories.MedalRewardRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Desciption: 宝箱奖励记录
 * @Author: Ford
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class MedalRewardRecordService extends ServiceImpl<MedalRewardRecordRepository, MedalRewardRecordPO> {

    private final I18nApi i18nApi;

    public List<MedalRewardRecordPO> listAll(String siteCode) {
        LambdaQueryWrapper<MedalRewardRecordPO> lqw = new LambdaQueryWrapper<MedalRewardRecordPO>();
        lqw.eq(MedalRewardRecordPO::getSiteCode,siteCode);
        return this.baseMapper.selectList(lqw);
    }


    public void insert(MedalRewardRecordPO medalRewardRecordPO) {
        LambdaQueryWrapper<MedalRewardRecordPO> lqw = new LambdaQueryWrapper<MedalRewardRecordPO>();
        lqw.eq(MedalRewardRecordPO::getSiteCode,medalRewardRecordPO.getSiteCode());
        lqw.eq(MedalRewardRecordPO::getRewardNo,medalRewardRecordPO.getRewardNo());
        lqw.eq(MedalRewardRecordPO::getUserAccount,medalRewardRecordPO.getUserAccount());
        long countNum=this.baseMapper.selectCount(lqw);
        if(countNum>=1){
            log.info("宝箱奖励:{}已经存在,无须再次奖励",medalRewardRecordPO);
            return;
        }
        medalRewardRecordPO.setOrderNo(OrderUtil.getOrderNo("RW"));
        medalRewardRecordPO.setOpenStatus(MedalOpenStatusEnum.CAN_UNLOCK.getCode());
        medalRewardRecordPO.setCompleteTime(System.currentTimeMillis());
        medalRewardRecordPO.setCreator(medalRewardRecordPO.getUserAccount());
        medalRewardRecordPO.setCreatedTime(System.currentTimeMillis());
        this.baseMapper.insert(medalRewardRecordPO);
    }

    public MedalRewardRecordPO selectByUniq(String siteCode, String userAccount, Integer rewardNo) {
        LambdaQueryWrapper<MedalRewardRecordPO> lqw = new LambdaQueryWrapper<MedalRewardRecordPO>();
        lqw.eq(MedalRewardRecordPO::getSiteCode,siteCode);
        lqw.eq(MedalRewardRecordPO::getUserAccount,userAccount);
        lqw.eq(MedalRewardRecordPO::getRewardNo,rewardNo);
        return this.baseMapper.selectOne(lqw);
    }

    public void openReward(String id, String userAccount) {
        UpdateWrapper<MedalRewardRecordPO> updateWrapper=new UpdateWrapper<MedalRewardRecordPO>();
        updateWrapper.set("open_status",MedalOpenStatusEnum.HAS_UNLOCK.getCode());
        updateWrapper.set("open_time",System.currentTimeMillis());
        updateWrapper.set("updater",userAccount);
        updateWrapper.set("updated_time",System.currentTimeMillis());
        updateWrapper.eq("id",id);
        this.update(updateWrapper);
    }

    public List<MedalRewardRecordPO> selectBySiteAndUser(String siteCode, String userAccount) {
        LambdaQueryWrapper<MedalRewardRecordPO> lqw = new LambdaQueryWrapper<MedalRewardRecordPO>();
        lqw.eq(MedalRewardRecordPO::getSiteCode,siteCode);
        lqw.eq(MedalRewardRecordPO::getUserAccount,userAccount);
        lqw.orderByAsc(MedalRewardRecordPO::getRewardNo);
        return this.baseMapper.selectList(lqw);
    }

    public ResponseVO<Page<MedalRewardRecordRespVO>> listPage(MedalRewardRecordReqVO medalRewardRecordReqVO) {
        Page<MedalRewardRecordPO> page = new Page<MedalRewardRecordPO>(medalRewardRecordReqVO.getPageNumber(), medalRewardRecordReqVO.getPageSize());
        LambdaQueryWrapper<MedalRewardRecordPO> lqw = new LambdaQueryWrapper<MedalRewardRecordPO>();
        if (medalRewardRecordReqVO.getSiteCode() != null) {
            lqw.eq(MedalRewardRecordPO::getSiteCode, medalRewardRecordReqVO.getSiteCode());
        }
      /*  if (StringUtils.hasText(medalRewardRecordReqVO.getMedalName())) {
            List<String> messageKeyLists=i18nApi.getMessageKeyLikeKeyAndMessage(I18MsgKeyEnum.MEDAL_NAME.getCode(),medalRewardRecordReqVO.getMedalName()).getData();
            if(!CollectionUtils.isEmpty(messageKeyLists)){
                lqw.in(MedalRewardRecordPO::getMedalNameI18, messageKeyLists);
            }
        }*/
        if (StringUtils.hasText(medalRewardRecordReqVO.getUserAccount())) {
            lqw.like(MedalRewardRecordPO::getUserAccount, medalRewardRecordReqVO.getUserAccount());
        }

        if (medalRewardRecordReqVO.getOpenStatus() != null) {
            lqw.eq(MedalRewardRecordPO::getOpenStatus, medalRewardRecordReqVO.getOpenStatus());
        }

        if (medalRewardRecordReqVO.getCompleteTimeStart() != null) {
            lqw.ge(MedalRewardRecordPO::getCompleteTime, medalRewardRecordReqVO.getCompleteTimeStart());
        }

        if (medalRewardRecordReqVO.getCompleteTimeEnd() != null) {
            lqw.le(MedalRewardRecordPO::getCompleteTime, medalRewardRecordReqVO.getCompleteTimeEnd());
        }
        if (medalRewardRecordReqVO.getOpenTimeStart() != null) {
            lqw.ge(MedalRewardRecordPO::getOpenTime, medalRewardRecordReqVO.getOpenTimeStart());
        }

        if (medalRewardRecordReqVO.getOpenTimeEnd() != null) {
            lqw.le(MedalRewardRecordPO::getOpenTime, medalRewardRecordReqVO.getOpenTimeEnd());
        }
        if(StringUtils.hasText(medalRewardRecordReqVO.getOrderField())){
            boolean ascFlag=false;
            if(medalRewardRecordReqVO.getOrderType().equals("asc")){
                ascFlag=true;
            }
            if(medalRewardRecordReqVO.getOrderField().equals("completeTime")){
                lqw.orderBy(true,ascFlag,MedalRewardRecordPO::getCompleteTime);
            }
            if(medalRewardRecordReqVO.getOrderField().equals("openTime")){
                lqw.orderBy(true,ascFlag,MedalRewardRecordPO::getOpenTime);
            }
        }else {
            lqw.orderByDesc(MedalRewardRecordPO::getCompleteTime);
        }

        IPage<MedalRewardRecordPO> medalInfoIPage = this.baseMapper.selectPage(page, lqw);
        Page<MedalRewardRecordRespVO> medalInfoRespVOPage = new Page<MedalRewardRecordRespVO>(medalRewardRecordReqVO.getPageNumber(), medalRewardRecordReqVO.getPageSize());
        medalInfoRespVOPage.setTotal(medalInfoIPage.getTotal());
        medalInfoRespVOPage.setPages(medalInfoIPage.getPages());
        List<MedalRewardRecordRespVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(medalInfoIPage.getRecords())) {
            for (MedalRewardRecordPO medalRewardRecordPO : medalInfoIPage.getRecords()) {
                MedalRewardRecordRespVO medalInfoRespVO = new MedalRewardRecordRespVO();
                BeanUtils.copyProperties(medalRewardRecordPO, medalInfoRespVO);
                medalInfoRespVO.setRewardAmount(BigDecimalUtils.formatFourVal(medalRewardRecordPO.getRewardAmount()));
                resultLists.add(medalInfoRespVO);
            }
        }
        medalInfoRespVOPage.setRecords(resultLists);
        return ResponseVO.success(medalInfoRespVOPage);
    }
}

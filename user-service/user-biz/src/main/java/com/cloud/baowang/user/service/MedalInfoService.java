package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoNewReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoStatusReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoUpdateReqVO;
import com.cloud.baowang.user.po.MedalInfoPO;
import com.cloud.baowang.user.repositories.MedalInfoRepository;
import com.cloud.baowang.user.util.MinioFileService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Desciption: 勋章信息
 * @Author: Ford
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class MedalInfoService extends ServiceImpl<MedalInfoRepository, MedalInfoPO> {

    @Resource
    private MinioFileService minioFileService;

    public ResponseVO<Page<MedalInfoRespVO>> selectPage(MedalInfoReqVO medalInfoReqVO) {
        Page<MedalInfoPO> page = new Page<MedalInfoPO>(medalInfoReqVO.getPageNumber(), medalInfoReqVO.getPageSize());
        LambdaQueryWrapper<MedalInfoPO> lqw = new LambdaQueryWrapper<MedalInfoPO>();
        if(StringUtils.hasText(medalInfoReqVO.getMedalName())){
            lqw.like(MedalInfoPO::getMedalName, medalInfoReqVO.getMedalName());
        }
        if(StringUtils.hasText(medalInfoReqVO.getMedalCode())){
            lqw.eq(MedalInfoPO::getMedalCode, medalInfoReqVO.getMedalCode());
        }
        if(medalInfoReqVO.getStatus()!=null){
            lqw.eq(MedalInfoPO::getStatus, medalInfoReqVO.getStatus());
        }
        IPage<MedalInfoPO> medalInfoIPage =  this.baseMapper.selectPage(page,lqw);
        Page<MedalInfoRespVO> medalInfoRespVOPage=new Page<MedalInfoRespVO>(medalInfoReqVO.getPageNumber(), medalInfoReqVO.getPageSize());
        medalInfoRespVOPage.setTotal(medalInfoIPage.getTotal());
        medalInfoRespVOPage.setPages(medalInfoIPage.getPages());
        List<MedalInfoRespVO> resultLists= Lists.newArrayList();
        for(MedalInfoPO medalInfo:medalInfoIPage.getRecords()){
            MedalInfoRespVO medalInfoRespVO=new MedalInfoRespVO();
            BeanUtils.copyProperties(medalInfo,medalInfoRespVO);
            medalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(medalInfoRespVO.getActivatedPic()));
            medalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(medalInfoRespVO.getInactivatedPic()));
            resultLists.add(medalInfoRespVO);
        }
        medalInfoRespVOPage.setRecords(resultLists);
        return ResponseVO.success(medalInfoRespVOPage);
    }

    public ResponseVO<Void> insert(MedalInfoNewReqVO medalInfoNewReqVO) {
        LambdaQueryWrapper<MedalInfoPO> lqw = new LambdaQueryWrapper<MedalInfoPO>();
        lqw.eq(MedalInfoPO::getMedalName, medalInfoNewReqVO.getMedalName());
        MedalInfoPO medalInfoOld= this.baseMapper.selectOne(lqw);
        if(medalInfoOld==null){
            MedalInfoPO medalInfo=new MedalInfoPO();
            BeanUtils.copyProperties(medalInfoNewReqVO,medalInfo);
            medalInfo.setStatus(EnableStatusEnum.ENABLE.getCode());
            medalInfo.setCreator(medalInfoNewReqVO.getOperatorUserNo());
            medalInfo.setUpdater(medalInfoNewReqVO.getOperatorUserNo());
            medalInfo.setCreatedTime(System.currentTimeMillis());
            medalInfo.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.insert(medalInfo);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
    }

    public ResponseVO<Void> updateByInfo(MedalInfoUpdateReqVO medalInfoUpdateReqVO) {
        LambdaQueryWrapper<MedalInfoPO> lqw = new LambdaQueryWrapper<MedalInfoPO>();
        lqw.eq(MedalInfoPO::getId, medalInfoUpdateReqVO.getId());
        MedalInfoPO medalInfoOld= this.baseMapper.selectOne(lqw);
        if(medalInfoOld!=null){
            BeanUtils.copyProperties(medalInfoUpdateReqVO,medalInfoOld);
            medalInfoOld.setUpdater(medalInfoUpdateReqVO.getOperatorUserNo());
            medalInfoOld.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.updateById(medalInfoOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<Void> enableOrDisable(MedalInfoStatusReqVO medalInfoStatusReqVO) {
        LambdaQueryWrapper<MedalInfoPO> lqw = new LambdaQueryWrapper<MedalInfoPO>();
        lqw.eq(MedalInfoPO::getId, medalInfoStatusReqVO.getId());
        MedalInfoPO medalInfoOld= this.baseMapper.selectOne(lqw);
        if(medalInfoOld!=null){
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), medalInfoOld.getStatus())){
                medalInfoOld.setStatus(EnableStatusEnum.DISABLE.getCode());
            }else {
                medalInfoOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            medalInfoOld.setUpdatedTime(System.currentTimeMillis());
            medalInfoOld.setUpdater(medalInfoStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(medalInfoOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }


    public ResponseVO<List<MedalInfoRespVO>> listAll() {
        LambdaQueryWrapper<MedalInfoPO> lqw = new LambdaQueryWrapper<MedalInfoPO>();
        lqw.eq(MedalInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<MedalInfoPO>  medalInfoPOS=this.baseMapper.selectList(lqw);
        List<MedalInfoRespVO> resultLists= Lists.newArrayList();
        for(MedalInfoPO medalInfo:medalInfoPOS){
            MedalInfoRespVO medalInfoRespVO=new MedalInfoRespVO();
            BeanUtils.copyProperties(medalInfo,medalInfoRespVO);
            medalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(medalInfoRespVO.getActivatedPic()));
            medalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(medalInfoRespVO.getInactivatedPic()));
            resultLists.add(medalInfoRespVO);
        }
        return ResponseVO.success(resultLists);
    }
}

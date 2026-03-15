package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeUnreadNumRspVO;
import com.cloud.baowang.user.api.vo.notice.user.request.NoticeUpdateVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetGetVO;
import com.cloud.baowang.user.api.vo.notice.user.usernoticetarget.UserNoticeTargetVO;
import com.cloud.baowang.user.po.UserNoticeTargetPO;
import com.cloud.baowang.user.repositories.UserNoticeConfigRepository;
import com.cloud.baowang.user.repositories.UserNoticeTargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserNoticeTargetService extends ServiceImpl<UserNoticeTargetRepository, UserNoticeTargetPO> {

    private final UserNoticeTargetRepository userNoticeTargetRepository;


    public UserNoticeTargetService(UserNoticeTargetRepository userNoticeTargetRepository) {
        this.userNoticeTargetRepository = userNoticeTargetRepository;
    }

    public ResponseVO<Page<UserNoticeTargetVO>> getList(UserNoticeTargetGetVO userNoticeTargetGetVO){
        if (userNoticeTargetGetVO.getId()==null){
            return null;
        }
        try {
            Page<UserNoticeTargetPO> page = new Page<>(userNoticeTargetGetVO.getPageNumber(), userNoticeTargetGetVO.getPageSize());
            LambdaQueryWrapper<UserNoticeTargetPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserNoticeTargetPO::getNoticeId,userNoticeTargetGetVO.getId());
            Page<UserNoticeTargetPO> result = userNoticeTargetRepository.selectPage(page, queryWrapper);
            List<UserNoticeTargetVO> list = result.getRecords().stream().map(po -> {
                UserNoticeTargetVO vo = new UserNoticeTargetVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).toList();
            Page<UserNoticeTargetVO> pageResult = new Page<>();
            BeanUtils.copyProperties(result, pageResult);
            pageResult.setRecords(list);
            return ResponseVO.success(pageResult);
        }catch (Exception e){
            log.error("特定会员展示失败：", e);
            throw new BaowangDefaultException(ResultCode.SPECIFLE_MEMBER_DISPLAY);
        }
    }

    public void deleteBatch(NoticeUpdateVO reqVO) {
        LambdaUpdateWrapper<UserNoticeTargetPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserNoticeTargetPO::getUserId, reqVO.getUserId());
        wrapper.in(UserNoticeTargetPO::getId, reqVO.getIds());
        userNoticeTargetRepository.delete(wrapper);
    }


}

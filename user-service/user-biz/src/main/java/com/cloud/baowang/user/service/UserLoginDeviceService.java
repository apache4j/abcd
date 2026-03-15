package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.user.api.vo.user.UserLoginDeviceVO;
import com.cloud.baowang.user.api.vo.user.request.UserDeviceReqVO;
import com.cloud.baowang.user.po.UserLoginDevicePO;
import com.cloud.baowang.user.repositories.UserLoginDeviceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class UserLoginDeviceService extends ServiceImpl<UserLoginDeviceRepository, UserLoginDevicePO> {

    private final UserLoginDeviceRepository userLoginDeviceRepository;

    public Page<UserLoginDeviceVO> getPage(UserDeviceReqVO requestVO) {
        Page<UserLoginDevicePO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        LambdaQueryWrapper<UserLoginDevicePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserLoginDevicePO::getUserAccount, requestVO.getUserAccount());

        Page<UserLoginDevicePO> pageList = userLoginDeviceRepository.selectPage(page, lambdaQueryWrapper);
        Page<UserLoginDeviceVO> pageResultList = new Page<>();
        BeanUtils.copyProperties(pageList, pageResultList);
        List<UserLoginDeviceVO> deviceVOList = pageList.getRecords().stream().map(po -> {
            UserLoginDeviceVO vo = new UserLoginDeviceVO();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }).collect(Collectors.toList());

        pageResultList.setRecords(deviceVOList);

        return pageResultList;
    }

    public void insertUserLoginDevice(final UserLoginDeviceVO userLoginDeviceVO) {
        UserLoginDevicePO po = new UserLoginDevicePO();
        BeanUtils.copyProperties(userLoginDeviceVO, po);
        userLoginDeviceRepository.insert(po);
    }

    public void deleteUserLoginDevice(final IdVO idVO) {
        userLoginDeviceRepository.deleteById(idVO.getId());
    }

}

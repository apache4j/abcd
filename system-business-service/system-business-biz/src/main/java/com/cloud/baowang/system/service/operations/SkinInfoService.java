package com.cloud.baowang.system.service.operations;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.po.operations.SkinInfoPO;
import com.cloud.baowang.system.repositories.operations.SkinInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class SkinInfoService extends ServiceImpl<SkinInfoRepository, SkinInfoPO> {
    private final SkinInfoRepository repository;

    public ResponseVO<Page<SkinResVO>> querySkinPage(SkinRequestVO skinRequestVO) {
        try {
            Page<SkinInfoPO> page = new Page<>(skinRequestVO.getPageNumber(), skinRequestVO.getPageSize());
            LambdaQueryWrapper<SkinInfoPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Objects.nonNull(skinRequestVO.getStatus()), SkinInfoPO::getStatus, skinRequestVO.getStatus());
            lqw.like(StringUtils.isNotBlank(skinRequestVO.getSkinCode()), SkinInfoPO::getSkinCode, skinRequestVO.getSkinCode());
            lqw.like(StringUtils.isNotBlank(skinRequestVO.getSkinName()), SkinInfoPO::getSkinName, skinRequestVO.getSkinName());
            lqw.orderByDesc(SkinInfoPO::getCreatedTime);
            Page<SkinInfoPO> skinPOPage = super.page(page, lqw);
            Page<SkinResVO> skinResVOPage = new Page<>();
            BeanUtils.copyProperties(skinPOPage, skinResVOPage);
            return ResponseVO.success(skinResVOPage);
        } catch (Exception e) {
            log.error("querySkinPage error", e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addSkin(SkinAddVO skinAddVO) {
        String skinName = skinAddVO.getSkinName();
        String skinCode = skinAddVO.getSkinCode();
        //校验皮肤名称是否重复
        if (this.count(Wrappers.lambdaQuery(SkinInfoPO.class).eq(SkinInfoPO::getSkinName, skinName)) > 0) {
            throw new BaowangDefaultException(ResultCode.SKIN_NAME_EXIST);
        }
        //皮肤包code
        if (this.count(Wrappers.lambdaQuery(SkinInfoPO.class).eq(SkinInfoPO::getSkinCode, skinCode)) > 0) {
            throw new BaowangDefaultException(ResultCode.SKIN_CODE_EXIST);
        }
        SkinInfoPO skinInfoPO = new SkinInfoPO();
        BeanUtils.copyProperties(skinAddVO, skinInfoPO);
        Long currentTimeMillis = System.currentTimeMillis();
        skinInfoPO.setCreatedTime(currentTimeMillis);
        skinInfoPO.setUpdatedTime(currentTimeMillis);
        skinInfoPO.setCreatorName(skinAddVO.getUpdaterName());
        skinInfoPO.setUpdaterName(skinAddVO.getUpdaterName());
        return ResponseVO.success(super.save(skinInfoPO));

    }

    public ResponseVO<?> editSkin(SkinAddVO skinAddVO) {
        String id = skinAddVO.getId();
        SkinInfoPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String skinName = skinAddVO.getSkinName();
        String skinCode = skinAddVO.getSkinCode();

        //校验皮肤名称是否重复
        SkinInfoPO one = this.getOne(Wrappers.lambdaQuery(SkinInfoPO.class).eq(SkinInfoPO::getSkinName, skinName));
        //不是同一条，不允许存在重复名称
        if (one != null && !one.getId().equals(po.getId())) {
            throw new BaowangDefaultException(ResultCode.SKIN_NAME_EXIST);
        }
        SkinInfoPO code = this.getOne(Wrappers.lambdaQuery(SkinInfoPO.class).eq(SkinInfoPO::getSkinCode, skinCode));
        //皮肤包code
        if (code != null && !code.getId().equals(po.getId())) {
            //不是同一条
            throw new BaowangDefaultException(ResultCode.SKIN_CODE_EXIST);
        }
        SkinInfoPO skinInfoPO = new SkinInfoPO();
        BeanUtils.copyProperties(skinAddVO, skinInfoPO);
        skinInfoPO.setUpdatedTime(System.currentTimeMillis());
        skinInfoPO.setUpdaterName(skinAddVO.getUpdaterName());
        return ResponseVO.success(super.updateById(skinInfoPO));
    }

    public ResponseVO<?> editSkinStatus(SkinEditVO skinEditVO) {
        SkinInfoPO skinInfoPO = new SkinInfoPO();
        skinInfoPO.setStatus(skinEditVO.getStatus());
        skinInfoPO.setId(skinEditVO.getId());
        skinInfoPO.setUpdatedTime(System.currentTimeMillis());
        skinInfoPO.setUpdaterName(skinEditVO.getUpdaterName());
        return ResponseVO.success(super.updateById(skinInfoPO));
    }

    public ResponseVO<List<SkinResVO>> querySkinList() {
        try {
            LambdaQueryWrapper<SkinInfoPO> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(SkinInfoPO::getHandicapMode,handicapMode);
            lqw.eq(SkinInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            lqw.orderByDesc(SkinInfoPO::getCreatedTime);
            List<SkinInfoPO> skinPO = super.list(lqw);
            List<SkinResVO> skinResVOS = ConvertUtil.entityListToModelList(skinPO, SkinResVO.class);
            return ResponseVO.success(skinResVOS);
        } catch (Exception e) {
            log.error("querySkinList error", e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }

    public SkinResVO querySkinOne(String skin) {
        LambdaQueryWrapper<SkinInfoPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SkinInfoPO::getSkinCode, skin);
        return BeanUtil.copyProperties(super.getOne(lqw), SkinResVO.class);
    }
}

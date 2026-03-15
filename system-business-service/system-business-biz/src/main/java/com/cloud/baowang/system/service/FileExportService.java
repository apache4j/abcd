package com.cloud.baowang.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.param.AddFileExportVO;
import com.cloud.baowang.system.api.vo.param.FileExportPageVO;
import com.cloud.baowang.system.api.vo.param.FileExportRespVO;
import com.cloud.baowang.system.po.FileExportPO;
import com.cloud.baowang.system.repositories.FileExportRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 文件导出表 服务类
 *
 * @author kimi
 * @since 2024-07-02 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class FileExportService extends ServiceImpl<FileExportRepository, FileExportPO> {

    private final FileExportRepository fileExportRepository;

    private final MinioFileService minioFileService;

    public void addFileExport(AddFileExportVO vo) {
        if (null == vo) {
            return;
        }
        FileExportPO po = ConvertUtil.entityToModel(vo, FileExportPO.class);
        po.setSiteCode(vo.getSiteCode());
        po.setCreator(vo.getAdminId());
        po.setUpdater(vo.getAdminId());
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        this.save(po);
    }

    public ResponseVO<Page<FileExportRespVO>> fileExportPage(FileExportPageVO fileExportPageVO) {
        try {
            Page<FileExportPO> page = new Page<FileExportPO>(fileExportPageVO.getPageNumber(), fileExportPageVO.getPageSize());
           LambdaQueryWrapper<FileExportPO> lambdaQueryWrapper=new LambdaQueryWrapper<FileExportPO>();
            if(StringUtils.hasText(fileExportPageVO.getSiteCode())) {
                lambdaQueryWrapper.eq(FileExportPO::getSiteCode, fileExportPageVO.getSiteCode());
            }
            if(StringUtils.hasText(fileExportPageVO.getAdminId())) {
                lambdaQueryWrapper.eq(FileExportPO::getCreator, fileExportPageVO.getAdminId());
            }
            if(StringUtils.hasText(fileExportPageVO.getFileName())){
               lambdaQueryWrapper.like(FileExportPO::getFileName,fileExportPageVO.getFileName());
            }
            lambdaQueryWrapper.orderByDesc(FileExportPO::getId);
            log.debug("file查询条件:{}",lambdaQueryWrapper.getCustomSqlSegment());
            log.debug("file查询参数:{}",lambdaQueryWrapper.getParamNameValuePairs());
            Page<FileExportPO> resultPage= this.baseMapper.selectPage(page,lambdaQueryWrapper);
            Page<FileExportRespVO> fileExportRespVOPage=new Page<FileExportRespVO>(fileExportPageVO.getPageNumber(), fileExportPageVO.getPageSize());
            fileExportRespVOPage.setTotal(resultPage.getTotal());
            fileExportRespVOPage.setPages(resultPage.getPages());
            if(resultPage!=null && !CollectionUtils.isEmpty(resultPage.getRecords())){
                List<FileExportRespVO> fileExportRespVOS=Lists.newArrayList();
                resultPage.getRecords().stream().forEach(o->{
                    FileExportRespVO fileExportRespVO=new FileExportRespVO();
                    BeanUtils.copyProperties(o,fileExportRespVO);
                    fileExportRespVO.setFileKeyUrl(minioFileService.getFileUrlByKey(fileExportRespVO.getFileKey()));
                    fileExportRespVOS.add(fileExportRespVO);
                });
                fileExportRespVOPage.setRecords(fileExportRespVOS);
            }
            return ResponseVO.success(fileExportRespVOPage);
        } catch (Exception e) {
            log.error("query file have problem", e);
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }
}

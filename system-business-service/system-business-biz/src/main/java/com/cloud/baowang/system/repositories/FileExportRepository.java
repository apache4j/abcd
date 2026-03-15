package com.cloud.baowang.system.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.FileExportPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件导出表 Mapper 接口
 *
 * @author kimi
 * @since 2024-07-02 10:00:00
 */
@Mapper
public interface FileExportRepository extends BaseMapper<FileExportPO> {

}

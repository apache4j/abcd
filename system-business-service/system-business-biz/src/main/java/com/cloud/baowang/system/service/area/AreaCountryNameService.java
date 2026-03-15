package com.cloud.baowang.system.service.area;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.system.po.area.AreaCountryNamePO;
import com.cloud.baowang.system.repositories.area.AreaCountryNameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/09/04 9:33
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AreaCountryNameService extends ServiceImpl<AreaCountryNameRepository, AreaCountryNamePO> {
}

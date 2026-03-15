package com.cloud.baowang.activity.vo.dto;

import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityCheckInPO;
import lombok.Data;

/**
 * @className: AcitvityConfigDTO
 * @author: wade
 * @description: dto
 * @date: 26/5/25 14:29
 */
@Data
public class ActivityConfigDTO {

    private SiteActivityBasePO basePO;

    private SiteActivityCheckInPO checkInPO;
}

package com.cloud.baowang.play.api.vo.third.SBA;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "沙巴体育赛事队伍信息")
public class SBATeamInfo {


    /**
     * 主队ID
     */
    private String homeId;

    /**
     * 主队名称
     */
    private String homeName;

    /**
     * 主队队徽URL, 如果图片不存在请使用预设主队队徽URL
     */
    private String homeIconUrl;

    /**
     * 客队ID
     */
    private String awayId;

    /**
     * 客队名称
     */
    private String awayName;

    /**
     * 客队队徽URL, 如果图片不存在请使用预设客队队徽URL
     * {domain}/TeamImg/team_flag_away.png
     * domain请用homeIconUrl返回的domain取代
     * No
     */
    private String awayIconUrl;


}

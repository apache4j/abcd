package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author sheldon
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "二级分类与游戏管理查询对象")
public class GameJoinClassVO extends PageVO {

    private List<String> gameTwoIds;

    private String gameTwoId;

    private String notGameOneId;

    private List<String> gameIds;

    private String gameId;

    private String gameOneId;

    private List<String> gameOneIds;

    private String siteCode;

    private String currencyCode;

    private List<String> ids;

}
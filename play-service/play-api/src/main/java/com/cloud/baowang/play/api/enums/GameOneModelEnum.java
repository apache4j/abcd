package com.cloud.baowang.play.api.enums;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum GameOneModelEnum {
    CA("CA", "赌场-常规一级分类", GameOneTypeEnum.MULTI_GAME.getCode()),
    SBA(VenuePlatformConstants.SBA, "沙巴体育", GameOneTypeEnum.SBA_ORIGINAL_SOUND.getCode()),
    ACELT(VenuePlatformConstants.ACELT, "彩票", GameOneTypeEnum.LOTTERY_ORIGINAL_SOUND.getCode()),
    SIGN_VENUE("SIGN_VENUE", "单游戏场馆,电竞,斗鸡", GameOneTypeEnum.VENUE.getCode());

    private static final long aLong = 0L;
    private final String code;
    private final String name;
    private final Integer type;

    public static GameOneModelEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        GameOneModelEnum[] types = GameOneModelEnum.values();
        for (GameOneModelEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


    public static GameOneModelEnum getByType(Integer gameOneType) {
        for (GameOneModelEnum type : GameOneModelEnum.values()) {
            if (gameOneType.equals(type.getType())) {
                return type;
            }
        }
        return null;
    }

    public static String nameByCode(String code) {
        GameOneModelEnum statusEnum = nameOfCode(code);
        if (statusEnum == null) {
            return null;
        }
        return statusEnum.getName();
    }


    public static List<GameOneModelEnum> getList() {
        return Arrays.asList(values());
    }

}

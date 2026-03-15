package com.cloud.baowang.system.util;

import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.enums.tutorial.ChangeDirectoryEnum;
import com.cloud.baowang.system.api.enums.tutorial.ChangeTypeEnum;
import com.cloud.baowang.system.po.tutorial.TutorialOperationRecordPO;

public class TutorialOperationRecordUtil {

    public static TutorialOperationRecordPO buildOperationRecord(String siteCode,String siteName,String beforeChange,String afterChange,int changePath,int changeType,long cTime,Integer curStatus,Integer typeMark){
        TutorialOperationRecordPO.TutorialOperationRecordPOBuilder builder = TutorialOperationRecordPO.builder();
        siteCode = CurrReqUtils.getSiteCode();
        builder.siteCode(siteCode)
                .siteName(siteName)
                .typeMark(String.valueOf(typeMark))
                .updateTime(cTime);
        if (changePath==0){
            builder.changeCatalog(ChangeDirectoryEnum.CATEGOPORY.getName());
        }else if (changePath==1){
            builder.changeCatalog(ChangeDirectoryEnum.TABS.getName());
        }else if (changePath==2){
            builder.changeCatalog(ChangeDirectoryEnum.CLASS.getName());
        }else if (changePath==3){
            builder.changeCatalog(ChangeDirectoryEnum.CONTENT.getName());
        }
        switch (changeType){
            case 0:
                builder.changeType(ChangeTypeEnum.TUTORIAL_NAME.getName())
                        .beforeChange(beforeChange)
                        .afterChange(afterChange);
                break;
            case 1:
                builder.changeType(ChangeTypeEnum.TABS_NAME.getName())
                        .beforeChange(beforeChange)
                        .afterChange(afterChange);
                break;
            case 2:
                builder.changeType(ChangeTypeEnum.TUTORIAL_ICON.getName())
                        .beforeChange(beforeChange)
                        .afterChange(afterChange);
                break;
            case 3:
                builder.changeType(ChangeTypeEnum.TABS_ICON.getName())
                        .beforeChange(beforeChange)
                        .afterChange(afterChange);
                break;
            case 4:
                builder.changeType(ChangeTypeEnum.TUTORIAL_STATUS.getName())
                        .beforeStatus(curStatus==0?1:0)
                        .afterStatus(curStatus)
                        .beforeChange(curStatus==0? EnableStatusEnum.ENABLE.getName():EnableStatusEnum.DISABLE.getName())
                        .afterChange(curStatus==0?EnableStatusEnum.DISABLE.getName():EnableStatusEnum.ENABLE.getName());
                break;
            case 5:
                builder.changeType(ChangeTypeEnum.TABS_STATUS.getName())
                        .beforeChange(curStatus==0?EnableStatusEnum.ENABLE.getName():EnableStatusEnum.DISABLE.getName())
                        .afterChange(curStatus==0?EnableStatusEnum.DISABLE.getName():EnableStatusEnum.ENABLE.getName());
                break;
            case 6:
                builder.changeType(ChangeTypeEnum.TUTORIAL_CONTENT.getName())
                        .beforeChange(beforeChange)
                        .afterChange(afterChange);
        }
        return builder.operator(CurrReqUtils.getAccount()).build();
    }
}

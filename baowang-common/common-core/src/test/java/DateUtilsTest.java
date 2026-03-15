import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/10 15:25
 * @Version: V1.0
 **/
public class DateUtilsTest {
    public static void main(String[] args) {
       // String orderDate = DateUtils.formatUTC5Date(System.currentTimeMillis(),"yyyyMMdd");
       // System.err.println(orderDate);
      /*  System.err.println(DateUtils.getStartDayBeforeMonthTimestamp("UTC-8"));
        System.err.println(DateUtils.getYesTodayStartTime());
        System.err.println(DateUtils.getYesTodayEndTime());
        System.err.println(System.currentTimeMillis()/1000);*/
      //  System.err.println(DateUtils.formatDefaultZoneDate(DateUtils.getYesTodayStartTime(),"yyyy-MM-dd HH:mm:ss SSS"));
      //  System.err.println(DateUtils.formatDefaultZoneDate(DateUtils.getYesTodayEndTime(),"yyyy-MM-dd HH:mm:ss SSS"));
       // System.err.println(DateUtils.formatUTC5Date(DateUtils.getYesTodayStartTime(),"yyyy-MM-dd HH:mm:ss"));
       // System.err.println(DateUtils.formatUTC5Date(DateUtils.getYesTodayEndTime(),"yyyy-MM-dd HH:mm:ss"));
        //System.err.println(Math.toIntExact((1752163200555l - 1752163200000l) / 1000) + 1);
        //System.err.println(16908/60/60);
       // long currentTimeMillis = 1752130799555l;//14:59:59:555
        // currentTimeMillis = 1752130799000l;//14:59:59:000
       // currentTimeMillis = 1752130800000l;//15:00:00:000
      /*  long startTime=1752130800000l;//15:00:00:000
        long advanceTime=600l;
        System.err.println((startTime - currentTimeMillis));
        if (startTime > currentTimeMillis) {
            int seconds = Math.toIntExact((startTime - currentTimeMillis) / 1000)+1;
            System.err.println("seconds:"+seconds);
            System.err.println("advanceTime:"+advanceTime);
            if (seconds < advanceTime) {
                advanceTime = seconds;
            }
        } else {
            // 开始时间小于等于当前时间 已开始
            advanceTime = CommonConstant.business_zero;
        }
        System.err.println(advanceTime);*/

        String timeZone="UTC+8";

        System.err.println("lastEndTime:"+TimeZoneUtils.getEndOfLastWeekInTimeZone(System.currentTimeMillis(), timeZone));
    }
}

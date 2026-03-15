import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/3/10 11:27
 * @Version: V1.0
 **/
public class SnowFlakeTest {
    public static void main(String[] args) throws UnknownHostException {
        // System.err.println(SnowFlakeUtils.MAX_SEQUENCE);
       // long sequence = (4094 + 1) & SnowFlakeUtils.MAX_SEQUENCE;
        //System.err.println(sequence);
        while(1==1){
            // System.err.println("Snow:"+SnowFlakeUtils.getSnowId());
           UserCoinRecordPO userCoinRecordPO=new UserCoinRecordPO();
            // userCoinRecordPO.setId(SnowFlakeUtils.getSnowId());
            System.out.println("coinId:"+userCoinRecordPO.getId());
            //  System.out.println("coinId:"+new SequenceLocal(InetAddress.getLocalHost()).nextId());
        }

    }
}

import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.snowFlake.SingletonUtils;
import com.cloud.baowang.common.core.utils.snowFlake.SnowflakeIdWorker;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SnowflakeTest {

   // @Test
    public void testSnow(){
      //  String id1= SnowFlakeUtils.getSnowIdBySelfCenterId("dddd");
       // String id2= SnowFlakeUtils.getSnowIdBySelfCenterId("aaa");
        //String id= SnowFlakeUtils.getSnowId();
        String id1="418626467800055808";
        String id2="418626467804250112";
        String id3="418626467808444416";
        SnowflakeIdWorker snowflakeIdWorker =  SingletonUtils.get(SnowflakeIdWorker.class);
        System.out.println("genId1="+id1);
        System.out.println("dataCenterId="+snowflakeIdWorker.getDataCenterId(Long.valueOf(id1)));
        System.out.println("workId="+snowflakeIdWorker.getWorkerId(Long.valueOf(id1)));
        System.out.println("genDate="+snowflakeIdWorker.getGenerateDateTime(Long.valueOf(id1)));
        System.out.println("seq1="+snowflakeIdWorker.getSequence(Long.valueOf(id1)));
        System.out.println("genId2="+id2);
        System.out.println("dataCenterId2="+snowflakeIdWorker.getDataCenterId(Long.valueOf(id2)));
        System.out.println("workId2="+snowflakeIdWorker.getWorkerId(Long.valueOf(id2)));
        System.out.println("genDate2="+snowflakeIdWorker.getGenerateDateTime(Long.valueOf(id2)));
        System.out.println("seq2="+snowflakeIdWorker.getSequence(Long.valueOf(id2)));
        System.out.println("genId3="+id3);
        System.out.println("dataCenterId3="+snowflakeIdWorker.getDataCenterId(Long.valueOf(id3)));
        System.out.println("workId3="+snowflakeIdWorker.getWorkerId(Long.valueOf(id3)));
        System.out.println("genDate3="+snowflakeIdWorker.getGenerateDateTime(Long.valueOf(id3)));
        System.out.println("seq3="+snowflakeIdWorker.getSequence(Long.valueOf(id3)));

    }

   // @Test
    public void mutliTest() throws InterruptedException, ExecutionException  {
        int numThreads = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        int iterations =5000;

        // Validate that the IDs are not same even if they are generated in the same ms in different threads
        Future<String>[] futures = new Future[iterations];
        for(int i = 0; i < iterations; i++) {
            futures[i] =  executorService.submit(() -> {
                String id = SnowFlakeUtils.getSnowIdBySelfCenterId("VD438R");
                // String id = SnowFlakeUtils.getSnowId();
                System.err.println("id="+id);
                latch.countDown();;
                return id;
            });
        }

        latch.await();
        for(int i = 0; i < futures.length; i++) {
            for(int j = i+1; j < futures.length; j++) {
                //System.err.println(j);
                assertFalse(futures[i].get() .equals(futures[j].get()));
            }
        }
    }


}
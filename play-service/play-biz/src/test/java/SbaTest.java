import com.cloud.baowang.common.core.utils.HttpClientHandler;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.google.common.collect.Maps;

import java.util.Map;

public class SbaTest {
    public static void main(String[] args) throws InterruptedException {
//        cg();
        dg();
    }

    private static void dg() throws InterruptedException {
        int index = 0;
//        while (true) {
//            for(int i=0;i<1000;i++){

//            }

        String orderId = OrderUtil.getGameNo();
        index++;
        String url = "https://apistaging.wx7777.com/betting/V1/PlaceBet?language=zhcn&vendorTransId=6qp8DsQDfJs4Tv1&sportType=1&marketId=770422493&price=1.73&point=3.25&key=a&stake=1&oddsOption=1\n";
        Map<String, String> head = Maps.newHashMap();
        head.put("authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVdGVzdF8zMDQ4OTEyOCIsInNuIjoib2tvayIsInAiOiJQd0dadlJwWlJGbklwbHhaTjVQOGk0ZFcyN1lrcDArYVpGYlhMeDNvRjkwOGlRNU9ja0YzUkJPa2UzL0dSVDFEL2t0Y1B4RDlVcGRJUUY4TFF4VlBpdz09IiwiU2l0ZUlkIjoiNDMzODkwMCIsIm5iZiI6MTczNDE0MjYyMSwiZXhwIjoxNzM0MTQzMjIxfQ.31S-024lQYfnU4KU5DE2spzTZ8QfBd9SzcgX54BIh8k");
        String body = "{\"language\":\"zhcn\",\"vendorTransId\":\"6qp8DsQDfJs4Tv1\",\"sportType\":1,\"marketId\":770422493,\"price\":1.73,\"point\":3.25,\"key\":\"a\",\"stake\":\"1\",\"oddsOption\":1}";
        System.err.println(body);
        String response = HttpClientHandler.post(url, head, body);
        System.err.println(index + "=" + response);
        Thread.sleep(1000);
//        }
    }

    /**
     * 串关
     */
    private static void cg() throws InterruptedException {
        int index = 0;
        Map<String, String> head = Maps.newHashMap();
        head.put("authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVdGVzdF8zMDQ4OTEyOCIsInNuIjoib2tvayIsInAiOiJRK2NRbWpnL3g4bm9DTytWTVAxckU0SXpRS2lUSmNNNE82NGVkeEpBUVhZREptL2VGd0NYSmt5OWpEN0lrdXBnRHUrRVB0aDMzOUUrL2w4Qnp5OEgzdz09IiwiU2l0ZUlkIjoiNDMzODkwMCIsIm5iZiI6MTczNDA5NjAwMiwiZXhwIjoxNzM0MDk2NjAyfQ.rawefzSYw0Cb8gaagtaNeVSfGirLnLYyO0jZhUj1LLo");

        for (int i = 0; i < 1000; i++) {

//        }
//        while (true){
            index++;
            String url = "https://apistaging.wx7777.com/betting/V1/PlaceParlayBet";
            String body = "{\"language\":\"zhcn\",\"betInfo\":{\"vendorTransId\":\"" +
                    OrderUtil.getGameNo() +
                    "\",\"tickets\":[{\"sportType\":1,\"marketId\":770556677,\"point\":0.25,\"key\":\"a\",\"price\":1.95},{\"sportType\":1,\"marketId\":767919658,\"point\":null,\"key\":\"1\",\"price\":\"19.0\"},{\"sportType\":1,\"marketId\":765862867,\"point\":\"0.0\",\"key\":\"h\",\"price\":1.92},{\"sportType\":1,\"marketId\":769377364,\"point\":null,\"key\":\"2\",\"price\":9.3}],\"combos\":[{\"combotype\":\"Trebles\",\"stake\":12},{\"combotype\":\"Fold4\",\"stake\":2},{\"combotype\":\"Yankee\",\"stake\":\"17\"}],\"priceOption\":1}}\n";
            String response = HttpClientHandler.post(url, head, body);
            System.err.println(index + "=" + response);
            Thread.sleep(1000);
        }

    }
}

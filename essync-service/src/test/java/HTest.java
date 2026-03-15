import com.cloud.baowang.es.sync.EsSyncServiceApplication;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EsSyncServiceApplication.class})
public class HTest {
    private static final String JDBC_URL = "jdbc:mysql:loadbalance://192.168.27.21:4000,192.168.27.22:4000/baowang_dev?characterEncoding=UTF-8&serverTimezone=UTC-4";
    private static final String USER = "bw_dev";
    private static final String PASSWORD = "wohnged8po2gieRu";

    @Test
    public void test1() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.26.91:9092,192.168.26.92:9092,192.168.26.93:9092");
        properties.put("group.id", "tidb-sync-es-consumer");
        properties.put("enable.auto.commit", false);
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest"); // 如果没有offset则从最后的offset开始读
        properties.put("request.timeout.ms", "40000"); // 必须大于session.timeout.ms的设置
        properties.put("session.timeout.ms", "30000"); // 默认为30秒
        properties.put("isolation.level", "read_committed");
        properties.put("max.poll.records", 20000);
        properties.put("max.partition.fetch.bytes", 209715200);
        properties.put("fetch.max.bytes", 209715200);
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);
        KafkaConsumer<Object, Object> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("baowang-dev"));

        ConsumerRecords<Object, Object> poll = kafkaConsumer.poll(TimeUnit.SECONDS.toMillis(100));
    }

    @Test
    public void test2() throws ParseException, SQLException {
        Connection connection = null;

        try {
            // 连接到数据库
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            ExecutorService executorService = Executors.newFixedThreadPool(16);
            Connection finalConnection = connection;
            for (int i = 0; i < 16; i++) {
                executorService.submit(() -> {
                    try {
                        finalConnection.setAutoCommit(false);
                        Statement statement = finalConnection.createStatement();

                        Long betTime = 1711534447986L;
                        for (int j = 0; j < 100000000; j++) {
                            StringBuilder s1 = new StringBuilder("INSERT INTO `baowang_dev`.`order_record` ( `agent_acct`, `user_account`, `user_name`, `account_type`, `casino_user_name`, `vip_rank_code`, `venue_code`, `game_type`, `game_name`, `game_code`, `room_type`, `room_type_name`, `play_type`, `bet_time`, `settle_time`, `first_settle_time`, `bet_amount`, `valid_amount`, `payout_amount`, `win_loss_amount`, `order_id`, `third_order_id`, `order_status`, `order_classify`, `odds`, `game_no`, `desk_no`, `boot_no`, `result_list`, `bet_content`, `rebate_rate`, `rebate_amount`, `change_status`, `change_time`, `change_count`, `bet_ip`, `currency`, `device_type`, `parlay_info`, `remark`, `created_time`, `updated_time`, `result_time`, `latest_time`) VALUES ");
                            String str = "('awei991', 'awei991', 'awei991', 1, 'awei991p', 1, 'PG', 1, '黄金矿工', '13', '1', '房间2', '3', %s, 1711534447986, 1711534447986, 1.3333, 2.44, 99999.223, 88.00415, '11', 'z123212313sfas2312', 1, 1, '1.33224', '123', '123', '123', '赢', '滚盘123123:vs1231', 0.6661, 1000.33333, 1, 1711534447986, 1, '127.0.0.1', 'CNY', 1, '{\\\"tabletype\\\":\\\"\\\",\\\"jackpottype\\\":\\\"\\\",\\\"bettime\\\":\\\"2024-01-12T20:41:49.029-04:00\\\",\\\"gamecode\\\":\\\"GB15\\\",\\\"bet\\\":2.5,\\\"balance\\\":1070.2,\\\"jackpot\\\":0,\\\"tableid\\\":\\\"\\\",\\\"currency\\\":\\\"CNY\\\",\\\"donate\\\":0,\\\"validbet\\\":0,\\\"win\\\":500,\\\"singlerowbet\\\":false,\\\"bettype\\\":[],\\\"jackpotcontribution\\\":[],\\\"createtime\\\":\\\"2024-01-12T20:41:58.531-04:00\\\",\\\"isdonate\\\":false,\\\"roundnumber\\\":\\\"\\\",\\\"endroundtime\\\":\\\"2024-01-12T20:41:58.584-04:00\\\",\\\"gamerole\\\":\\\"\\\",\\\"gameresult\\\":{\\\"cards\\\":[],\\\"points\\\":[]},\\\"round\\\":\\\"GB59476054\\\",\\\"roomfee\\\":0,\\\"gametype\\\":\\\"slot\\\",\\\"gameplat\\\":\\\"web\\\",\\\"detail\\\":[{\\\"freegame\\\":0},{\\\"luckydraw\\\":0},{\\\"bonus\\\":0}],\\\"rake\\\":0,\\\"gamehall\\\":\\\"cq9\\\",\\\"account\\\":\\\"xll36x\\\",\\\"bankertype\\\":\\\"\\\",\\\"status\\\":\\\"complete\\\"}', '哈时间的哈是大环境干净2', 1711534447986, 1711534447986, 1711534447986, 1711534447986)";
                            List<String> stringList = Lists.newArrayList();
                            for (int j1 = 0; j1 < 1000; j1++) {
                                betTime = betTime - 10000;
                                String format = String.format(str, betTime);
                                stringList.add(format);
                            }
                            String join = String.join(",", stringList);
                            s1.append(join);
                            statement.execute(s1.toString());
                            finalConnection.commit();
                        }
                    } catch (SQLException e) {
                        log.error("sql异常：", e);
                        throw new RuntimeException(e);
                    }
                });
            }
            while (true) {
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert connection != null;
            connection.rollback();
        } finally {
            // 关闭连接
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.play.PlayApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PlayApplication.class})
public class HTest {


    @Test
    public void test1() {
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,new UserTypingAmountMqVO());
        //SHLanguageConversionUtils.conversionBetResult("99006:8、99007:2");
    }

//    public static void main(String[] args) {{
//        try {
//
//            // 指定保存路径
//            String excelFilePath = "/Users/sheldon/Downloads/注单解析-explain.xlsx";
//
//            // 创建工作簿
//            Workbook workbook = new XSSFWorkbook();
//            Sheet sheet = workbook.createSheet("Sheet1");
//
//
//
//
//
//
//            // 指定文件路径
//            String path = "/Users/sheldon/Downloads/注单解析-explain.json";
//            // 读取文件内容为字符串
//            String content = new String(Files.readAllBytes(Paths.get(path)));
//
//            // 转成 JSONArray
//            JSONArray jsonArray = new JSONArray(content);
//
//            // 循环打印
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject obj = jsonArray.getJSONObject(i);
//                JSONArray betArray = obj.getJSONArray("bets");
//
//                for (int j = 0; j < betArray.length(); j++) {
//                    JSONObject bet = betArray.getJSONObject(j);
//
//                    String key = obj.get("gameType")+"_"+bet.get("bet");
//
//                    String name = bet.getString("betName");
//
//
//                    Row row = sheet.createRow(i);
//                    Cell cell1 = row.createCell(0);
//                    Cell cell2 = row.createCell(1);
//
//                    // 填充数据
//                    cell1.setCellValue(key); // 第一列
//                    cell2.setCellValue(name);           // 第二列
//
//                    System.err.println(key+"   |" +name);
//
//
//                }
//            }
//
//
//            // 自动调整列宽
//            sheet.autoSizeColumn(0);
//            sheet.autoSizeColumn(1);
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    }

    public static void main(String[] args) {
        try {
            String excelFilePath = "/Users/sheldon/Downloads/注单解析-explain.xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");

            String path = "/Users/sheldon/Downloads/注单解析-explain.json";
            String content = new String(Files.readAllBytes(Paths.get(path)));

            JSONArray jsonArray = new JSONArray(content);

            int rowIndex = 0; // 行索引

            Map<String,String> map = new HashMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONArray betArray = obj.getJSONArray("bets");

                for (int j = 0; j < betArray.length(); j++) {
                    JSONObject bet = betArray.getJSONObject(j);

                    String key = obj.get("gameType") + "_" + bet.get("bet");
                    String name = bet.getString("betName");
                    String code = "LOOKUP_EVO_bet_type_"+name.replace(" ","_");

                    if(map.containsKey(name)){
                        continue;
                    }
                    map.put(name,name);

                    Row row = sheet.createRow(rowIndex++);
                    Cell cell1 = row.createCell(0);
                    Cell cell2 = row.createCell(1);
                    Cell cell3 = row.createCell(2);

                    cell1.setCellValue(key);
                    cell2.setCellValue(name);
                    cell3.setCellValue(code);

                }
            }

            // 自动调整列宽
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // 保存文件
            try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
                workbook.write(fos);
            }

            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

public class i18ClassDoMain {


    //生成I18初始化的脚本

    public static void main(String[] args) {
        // Excel 文件路径
        String excelFilePath = "/Users/sheldon/Downloads/玩法投注点编号说明.xlsx";

        try {
            // 打开 Excel 文件
            FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));

            // 创建工作簿对象
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);



            long index = 1222013132884L;
            // 遍历每一行
            for (Row row : sheet) {
                index++;
                // 遍历每一列
                String key = "";
                String name = "";
                int i =1;
                for (Cell cell : row) {
                    // 打印每个单元格的值
                    switch (cell.getCellType()) {
                        case STRING:
                            if(i == 1){
                                key = cell.getStringCellValue();
                            }
                            if(i == 2){
                                name = cell.getStringCellValue();
                            }
//                            System.out.print("1 -"+cell.getStringCellValue() + "\t");

//                            System.out.print("("+index+", 'acelt_play_type', '"+cell.getStringCellValue() +"', 'LOOKUP_ACELT_PLAY_TYPE_"+cell.getStringCellValue()+"'," +
//                                    " '冠亚军和', '冠亚军和', 1, NULL, '1', NULL),");
                            break;
                    }
                    i++;

                }
//                System.out.println("key="+key +"    "+"name="+name);
                System.out.println("("+index+", 'acelt_bet_type', '"+key+"', 'LOOKUP_ACELT_BET_TYPE_"+key+"', '"+name+"', '"+name+"', 1, NULL, '1', NULL),");
            }

            // 关闭工作簿
            workbook.close();
            fileInputStream.close();

        } catch(Exception e){

        }
    }
}

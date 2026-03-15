package com.cloud.baowang.play.game.pp.utils;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.csv.CSVReader;

import java.io.StringReader;

public class CsvParserUtil {

    /**
    * 解析接口返回的 CSV 格式文本为 JSONArray，每行映射为一个 JSONObject。
    * @param csvRawText 接口返回的原始字符串（第一行为 timepoint=...）
    * @return JSONArray 形式的表格数据
    * @throws Exception
    */
    public static JSONArray parseCsv(String csvRawText) throws Exception {
        if (csvRawText == null || csvRawText.isEmpty()) {
            return new JSONArray();
        }

        String[] lines = csvRawText.split("\n", -1);
        if (lines.length < 2) {
            return new JSONArray();
        }

        // 跳过第一行 timepoint=...
        StringBuilder csvOnly = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {
            csvOnly.append(lines[i]).append("\n");
        }

        JSONArray result = new JSONArray();

        try (CSVReader<String[]> reader = CSVReader.of(new StringReader(csvOnly.toString()))) {
            String[] headers = reader.readLine(); // 表头

            String[] line;
            while ((line = reader.readLine()) != null) {
                JSONObject row = new JSONObject();
                for (int i = 0; i < headers.length && i < line.length; i++) {
                    row.put(headers[i], line[i]);
                }
                result.add(row);
            }
        }

        return result;
    }
}

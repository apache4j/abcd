package com.cloud.baowang.common.data.transfer.autocode;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutoGenI18Config {
    static List<String> arr = new ArrayList<>(Arrays.asList(
            "LOOKUP_SBA_BET_TYPE_9623",
            "LOOKUP_SBA_BET_TYPE_9622",
            "LOOKUP_SBA_BET_TYPE_9618",
            "LOOKUP_SBA_BET_TYPE_9619",
            "LOOKUP_SBA_BET_TYPE_9621",
            "LOOKUP_SBA_BET_TYPE_9620",
            "LOOKUP_SBA_BET_TYPE_9624",
            "LOOKUP_SBA_BET_TYPE_9628",
            "LOOKUP_SBA_BET_TYPE_9629",
            "LOOKUP_SBA_BET_TYPE_9632",
            "LOOKUP_SBA_BET_TYPE_9627",
            "LOOKUP_SBA_BET_TYPE_9630",
            "LOOKUP_SBA_BET_TYPE_9631",
            "LOOKUP_SBA_BET_TYPE_9633",
            "LOOKUP_SBA_BET_TYPE_9625",
            "LOOKUP_SBA_BET_TYPE_9626",
            "LOOKUP_SBA_BET_TYPE_9634"));

    public static void main(String[] args) {
        /**
         * 默认是增量更新，只插入，不更新，且插入不直接插入sql，是提供的sql脚本
         * 1.ResultCode
         * 在ResultCode添加好，直接运行，即可翻译，
         * 2.system-param
         * 在system-param插入，直接运行，然后复制sql脚本，到数据库执行即可-对比数据库是dev
         */
//        IncrementalUpdate();
        // todo 全量更新
//        allUpdate();
        // 只跑本地的，不跑数据库
        // LocalAllUpdate();
        // 读取excel
        readExcel();
        // 更新指定的key LOOKUP_SH_PLAY_TYPE%
        //IncrementalUpdateOnce();

    }

    //解析文件excel x修改为只添加印尼语
    public static void readExcel() {
        try {
            String strName = System.currentTimeMillis() + ".txt";
            // 存放文件
            Path filePathOutWrite = Paths.get("/Users/wade/Desktop/"+strName);
            PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePathOutWrite));
            String filePath = "./data-transfer/src/main/resources/OKSPORT-皮肤1.xlsx";
            try (FileInputStream fis = new FileInputStream(new File(filePath));
                 Workbook workbook = WorkbookFactory.create(fis)) {
                // 获取第二个工作表（索引从0开始）
                Sheet sheet = workbook.getSheetAt(4);
                //遍历每一行
                handleExcel2(sheet);
                Sheet sheet2 = workbook.getSheetAt(6);
                handleExcel2(sheet2);
                String sql = "SELECT * FROM i18n_message WHERE message_key LIKE 'LOOKUP_%'";
                System.out.println(dataMap);
                queryDataToTranslateMap(sql); // dataMap 包括所有LOOKup 开头的
                long curr = System.currentTimeMillis();

                try (Connection conn = getConnection()) {

                    // 预编译 SQL 语句
                    String updateSql = "UPDATE `i18n_message` " +
                            "SET message = {0}, `updated_time` = {1} " +
                            "WHERE message_key = {2} AND language = {3}";
                    String insertSql = "INSERT IGNORE INTO `i18n_message` " +
                            "(`message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                         PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                        for (Map.Entry<String, Map<String, String>> entry : dataMapFinal.entrySet()) {
                            String code = entry.getKey();
                            if (code.startsWith("LOOKUP_")) {
                                Map<String, String> map = entry.getValue();
                                for (Map.Entry<String, String> mapEntry : map.entrySet()) {
                                    String lang = mapEntry.getKey();
                                    String text = mapEntry.getValue();
                                    String messageKey = code + lang;
                                    if (dataMap.containsKey(messageKey)) {
                                        // 比较，如果不一样，打印sql
                                        String dataRecord = dataMap.get(messageKey);
                                        if (dataRecord.equals(text.trim())) {
                                            continue;
                                        }
                                        if (messageKey.startsWith("LOOKUP_SBA_BET_TYPE_9628en-US")) {
                                            String sss = messageKey;
                                        }
                                        // 打印更新 SQL
                                        String sqlPreview = updateSql
                                                .replace("{0}", "'" + text.trim() + "'")
                                                .replace("{1}", "'" + curr + "'")
                                                .replace("{2}", "'" + code + "'")
                                                .replace("{3}", "'" + lang + "'");
                                        if ("zh-CN".equals(lang) || "zh-TW".equals(lang) && arr.contains(code)) {
                                        } else {
                                            //不包含特殊字符串 X 的,生成sql
                                            System.out.println(sqlPreview + ";");
                                        }
                                        writer.println(sqlPreview + ";");




                                        // 添加更新语句到批处理
                                        /*updateStmt.setString(1, text);
                                        updateStmt.setLong(2, curr);
                                        updateStmt.setString(3, code);
                                        updateStmt.setString(4, lang);*/
                                        //updateStmt.executeUpdate();
                                    } else {
                                        if (code.equals("LOOKUP_SBA_BET_TYPE_224")) {

                                        }
                                        // 打印插入 SQL exce
                                        String insertSqlStr = "INSERT IGNORE INTO i18n_message (message_type, message_key, language, message, creator, created_time, updater, updated_time) " +
                                                "VALUES (:type, :key, :lang, :message, :creator, :createdTime, :updater, :updatedTime)";
                                        String sqlPreview = insertSqlStr
                                                .replace(":type", "'BACK_END'")
                                                .replace(":key", "'" + code.replace("'", "\\'") + "'")
                                                .replace(":lang", "'" + lang.replace("'", "\\'") + "'")
                                                .replace(":message", "'" + text.trim().replace("'", "\\'") + "'")
                                                .replace(":creator", "1")
                                                .replace(":createdTime", String.valueOf(curr))
                                                .replace(":updater", "1")
                                                .replace(":updatedTime", String.valueOf(curr));
                                        writer.println(" " + sqlPreview + ";");
                                        //System.out.println(" " + sqlPreview + ";");
                                        //System.out.println(" " + sqlPreview + ";");
                                        // 添加插入语句到批处理
                                        insertStmt.setString(1, "BACK_END");
                                        insertStmt.setString(2, code);
                                        insertStmt.setString(3, lang);
                                        insertStmt.setString(4, text);
                                        insertStmt.setInt(5, 1); // creator
                                        insertStmt.setLong(6, curr);
                                        insertStmt.setInt(7, 1); // updater
                                        insertStmt.setLong(8, curr);
                                        //insertStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                        // 执行所有批量操作
                        //updateStmt.executeBatch();
                        //insertStmt.executeBatch();
                        //
                        //conn.commit(); // 提交事务
                        // 最后一批方法也要调用

                        writer.close();
                    } catch (SQLException e) {
                        //conn.rollback(); // 回滚事务
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 更新配置文件
        addOrUpdateConfigurationFileFinal(dataMapFinal);
        System.out.println("更新完成！");
    }

    /**
     * 添加或更新配置文件，支持多条翻译结果批量操作
     *
     * @param dataMapFinal 包含多个 code 和其对应的翻译结果
     * @throws IOException
     */
    private static void addOrUpdateConfigurationFileFinal(Map<String, Map<String, String>> dataMapFinal) {
        // 按语言分组处理
        Map<String, Map<String, String>> langToTranslations = new LinkedHashMap<>();

        // 将所有翻译结果按语言整理
        dataMapFinal.forEach((code, translations) -> {
            // 过滤指定的 code
            if (!code.startsWith("PROMPT_")) {
                return; // 跳过本次循环
            }
            translations.forEach((lang, text) -> {
                lang = lang.replace(CommonConstant.CENTER_LINE, CommonConstant.UNDERLINE);
                langToTranslations
                        .computeIfAbsent(lang, k -> new LinkedHashMap<>())
                        .put(code, text);
            });
        });
        // 遍历每种语言并处理文件
        langToTranslations.forEach((lang, translations) -> {

            List<String> filePaths = new ArrayList<>();

            // 对于 en_US，需要同时更新 messages.properties 和 messages_en_US.properties
            if ("en_US".equals(lang)) {
                filePaths.add("./data-transfer/src/main/resources/messages.properties");
                filePaths.add("./data-transfer/src/main/resources/messages_en_US.properties");
            } else {
                filePaths.add("./data-transfer/src/main/resources/messages_" + lang + ".properties");
            }
            for (String filePath : filePaths) {
                File originalFile = new File(filePath);
                File tempFile = new File(filePath + ".tmp");

                // 用于存储更新后的内容
                Map<String, String> properties = new LinkedHashMap<>();

                // 读取文件内容到 Map 中
                if (originalFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(originalFile, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().startsWith("#") && line.contains("=")) {
                                String[] parts = line.split("=", 2);
                                properties.put(parts[0].trim(), parts[1].trim());
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("解析文件错误，错误原因: " + filePath, e);
                    }
                }

                // 更新或新增键值对
                properties.putAll(translations);

                // 写入更新后的内容到临时文件
                try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile, StandardCharsets.UTF_8))) {
                    properties.forEach((key, value) -> writer.println(key + "=" + value));
                } catch (IOException e) {
                    throw new RuntimeException("写入文件出错: " + tempFile.getAbsolutePath(), e);
                }

                // 替换原文件
                if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
                    throw new RuntimeException("替换原文件出错.");
                }
            }

        });
    }


    public static void handleExcel(Sheet sheet) {
        //lookup code 的excel集合
        List<String> excelList = new ArrayList<>();

        boolean flag = false;
        for (Row row : sheet) {
            // 跳过第一行标题
            if (row.getRowNum() == 0) continue;
            // 获取第一列和第二列的值 ,需要看excel的格式
            String code = getCellValueAsString(row.getCell(0)).trim();
            excelList.add(code);
            if (code.startsWith("LOOKUP_")) {
                flag = true;
            }
            /*if (!code.equals("LOOKUP_TRADE_TYPE_1")
                    && !code.equals("LOOKUP_TRADE_TYPE_3")
                    && !code.equals("LOOKUP_TRADE_TYPE_2")
                    && !code.equals("LOOKUP_TRADE_TYPE_8")
                    && !code.equals("LOOKUP_TRADE_TYPE_9")
                    && !code.equals("LOOKUP_TRADE_TYPE_10")
                    && !code.equals("LOOKUP_11104")
                    && !code.equals("PROMPT_98005")
                    && !code.equals("PROMPT_14011")
                    && !code.equals("PROMPT_20139")
                    && !code.equals("PROMPT_30133") &&
                    !code.equals("LOOKUP_11694")) {
                continue;
            }*/

            String zh_cn = getCellValueAsString(row.getCell(1));
            String zh_tw = getCellValueAsString(row.getCell(2));
            String en_us = getCellValueAsString(row.getCell(3));
            String vi_VN = getCellValueAsString(row.getCell(4));
            //String pt_BR = getCellValueAsString(row.getCell(5));
            String ko_KR = getCellValueAsString(row.getCell(5));
            String hi_IN = getCellValueAsString(row.getCell(6));
            Map<String, String> temp = new HashMap<>();

            if (StringUtils.isNotBlank(code)) {
                if (StringUtils.isNotBlank(zh_cn)) {
                    temp.put(LanguageEnum.ZH_CN.getLang(), zh_cn);
                }
                if (StringUtils.isNotBlank(zh_tw)) {
                    temp.put(LanguageEnum.ZH_TW.getLang(), zh_tw);
                } else {
                    //  使用翻译
                    /*String lang = LanguageEnum.ZH_TW.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*/
                }
                if (StringUtils.isNotBlank(en_us)) {
                    temp.put(LanguageEnum.EN_US.getLang(), en_us);
                } else {
                    /*String lang = LanguageEnum.EN_US.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*/
                }
                if (StringUtils.isNotBlank(vi_VN)) {
                    temp.put(LanguageEnum.VI_VN.getLang(), vi_VN);
                } else {
                    /*String lang = LanguageEnum.VI_VN.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*/
                }
                /*if (StringUtils.isNotBlank(pt_BR)) {
                    temp.put(LanguageEnum.PT_BR.getLang(), pt_BR);
                } else {
                    *//*String lang = LanguageEnum.PT_BR.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*//*
                }*/
                if (StringUtils.isNotBlank(ko_KR)) {
                    temp.put(LanguageEnum.KO_KR.getLang(), ko_KR);
                }
                if (StringUtils.isNotBlank(hi_IN)) {
                    temp.put(LanguageEnum.HI_IN.getLang(), hi_IN);
                }
                dataMapFinal.put(code, temp);

            }
        }
        // 打印结果
        // 找出excel没有，表里有
        if (flag) {
            String sql1 = "SELECT * FROM system_param";
            System.out.println("存在system_param.但是不存在excel的数据");
            List<String> sysDataList = queryDataSystem(sql1);
            for (String str : sysDataList) {
                if (!excelList.contains(str) && str.startsWith("LOOKUP_")) {
                    System.out.println("'" + str + "',");
                }
            }
        }
    }

    /**
     * 只添加印尼语言
     * @param sheet
     */
    public static void handleExcel2(Sheet sheet) {
        //lookup code 的excel集合
        List<String> excelList = new ArrayList<>();

        boolean flag = false;
        for (Row row : sheet) {
            // 跳过第一行标题
            if (row.getRowNum() == 0) continue;
            // 获取第一列和第二列的值 ,需要看excel的格式
            String code = getCellValueAsString(row.getCell(0)).trim();
            excelList.add(code);
            if (code.startsWith("LOOKUP_")) {
                flag = true;
            }
            /*if (!code.equals("LOOKUP_TRADE_TYPE_1")
                    && !code.equals("LOOKUP_TRADE_TYPE_3")
                    && !code.equals("LOOKUP_TRADE_TYPE_2")
                    && !code.equals("LOOKUP_TRADE_TYPE_8")
                    && !code.equals("LOOKUP_TRADE_TYPE_9")
                    && !code.equals("LOOKUP_TRADE_TYPE_10")
                    && !code.equals("LOOKUP_11104")
                    && !code.equals("PROMPT_98005")
                    && !code.equals("PROMPT_14011")
                    && !code.equals("PROMPT_20139")
                    && !code.equals("PROMPT_30133") &&
                    !code.equals("LOOKUP_11694")) {
                continue;
            }*/

            String zh_cn = getCellValueAsString(row.getCell(1));
            String id_id = getCellValueAsString(row.getCell(2));
            String zh_tw = null;
            String en_us = null;
            String vi_VN = null;
            //String pt_BR = getCellValueAsString(row.getCell(5));
            String ko_KR = null;
            String hi_IN = null;
            Map<String, String> temp = new HashMap<>();

            if (StringUtils.isNotBlank(code)) {
                if (StringUtils.isNotBlank(zh_cn)) {
                    temp.put(LanguageEnum.ZH_CN.getLang(), zh_cn);
                }
                if (StringUtils.isNotBlank(zh_tw)) {
                    temp.put(LanguageEnum.ZH_TW.getLang(), zh_tw);
                } else {
                    //  使用翻译
                    /*String lang = LanguageEnum.ZH_TW.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*/
                }
                if (StringUtils.isNotBlank(en_us)) {
                    temp.put(LanguageEnum.EN_US.getLang(), en_us);
                } else {
                    /*String lang = LanguageEnum.EN_US.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*/
                }
                if (StringUtils.isNotBlank(vi_VN)) {
                    temp.put(LanguageEnum.VI_VN.getLang(), vi_VN);
                } else {
                    /*String lang = LanguageEnum.VI_VN.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*/
                }
                /*if (StringUtils.isNotBlank(pt_BR)) {
                    temp.put(LanguageEnum.PT_BR.getLang(), pt_BR);
                } else {
                    *//*String lang = LanguageEnum.PT_BR.getLang();
                    String s = translateText(apiKey, zh_cn, lang);
                    temp.put(lang, s);*//*
                }*/
                if (StringUtils.isNotBlank(ko_KR)) {
                    temp.put(LanguageEnum.KO_KR.getLang(), ko_KR);
                }
                if (StringUtils.isNotBlank(hi_IN)) {
                    temp.put(LanguageEnum.HI_IN.getLang(), hi_IN);
                }
                if (StringUtils.isNotBlank(id_id)) {
                    temp.put(LanguageEnum.ID_ID.getLang(), id_id);
                }
                dataMapFinal.put(code, temp);

            }
        }
        // 打印结果
        // 找出excel没有，表里有
        if (flag) {
            String sql1 = "SELECT * FROM system_param";
            System.out.println("存在system_param.但是不存在excel的数据");
            List<String> sysDataList = queryDataSystem(sql1);
            for (String str : sysDataList) {
                if (!excelList.contains(str) && str.startsWith("LOOKUP_")) {
                    System.out.println("'" + str + "',");
                }
            }
        }
    }


    // 工具方法：将单元格值转换为字符串
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 如果是日期格式，转换为日期字符串
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return String.valueOf(cell.getNumericCellValue()); // 获取公式计算后的值
            default:
                return "";
        }
    }

    // Google Cloud 服务账号秘钥文件路径
    private static final String SERVICE_ACCOUNT_KEY_PATH = "/path/to/service_account_key.json";

    private static final String apiKey = "AIzaSyCL7N_ImqhIw3ODixYSOhlT0FMxOIhnRVA";

    /**
     * 人工翻译 key code  i18n_message 中message_key+lang values 是message
     */
    private static final Map<String, Map<String, String>> dataMapFinal = new LinkedHashMap<>();

    /**
     * key 是表 i18n_message 中message_key+lang values 是message
     */
    private static final Map<String, String> dataMap = new HashMap<>();

    /**
     * key 是表 i18n_message 中message_key+lang values 是message
     * 查询重复的
     */
    private static final Map<String, String> repactDataMap = new HashMap<>();

    // 需要翻译的文本 key = code , value = 中文文本
    /**
     * 20010 PROMPT_20010 会员更新标签审核处理中，不能删除该标签
     */
    private static final Map<String, String> textToTranslate = new LinkedHashMap<>();
    /**
     * 不存在的，就添加到i8n
     */
    private static final Map<String, String> partTextToTranslate = new LinkedHashMap<>();

    /**
     * 这些不翻译，重置数据
     */
    private static final List<String> resetDataType = new ArrayList<>();

    static {
        resetDataType.add("device_terminal");
        resetDataType.add("registry");
        resetDataType.add("deposit_phone_number");
        resetDataType.add("deposit_email_address");
        resetDataType.add("deposit_detailed_address");
        resetDataType.add("deposit_city");
        resetDataType.add("deposit_province");
        resetDataType.add("deposit_first_name");
        resetDataType.add("deposit_last_name");
        resetDataType.add("bank_card_code");
        resetDataType.add("digital_wallet_name");
        resetDataType.add("digital_wallet_account");
        resetDataType.add("blockchain_address");
        resetDataType.add("blockchain_protocol");
        resetDataType.add("card_holder_name");
        resetDataType.add("bank_name");
        resetDataType.add("bank_card_name");


    }


    // 添加其他需要翻译的语言
    //public static final List<String> language = Lists.newArrayList(Arrays.stream(LanguageEnum.values()).map(LanguageEnum::getLang).toList());
    public static final List<String> language = Lists.newArrayList(Arrays.asList("zh-CN", "zh-TW", "ko-KR", "vi-VN", "pt-BR", "en-US","hi-IN"));

    private static final String JDBC_URL = "jdbc:mysql:loadbalance://192.168.29.111:4000,192.168.29.112:4000/dev_bwintl?characterEncoding=UTF-8&serverTimezone=America/Bogota&useServerPrepStmts=true&cachePrepStmts=true&prepStmtCacheSize=1000&prepStmtCacheSqlLimit=20480&useConfigs=maxPerformance&loadBalanceStrategy=roundRobin";
    private static final String USER = "dev_bwintl";
    private static final String PASSWORD = "9Gd4Ojlyshq0Ybiw";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void queryDataToTranslateMap(String sql) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String messageKey = resultSet.getString("message_key");
                String language = resultSet.getString("language");
                String messageValue = resultSet.getString("message");
                if (StringUtils.isNotBlank(messageKey) && StringUtils.isNotBlank(language))
                    dataMap.put(messageKey + language, messageValue);
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 查询 system_param 的 value
     */
    public static List<String> queryDataSystem(String sql) {
        List<String> sysList = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                // String type = resultSet.getString("type");
                String value = resultSet.getString("value");
                //String messageValue = resultSet.getString("message");
                sysList.add(value);
            }
            statement.executeBatch();
            connection.commit();
            return sysList;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return sysList;
    }

    public static void reSetData() {
        if (CollectionUtils.isEmpty(resetDataType)) {
            return;
        }
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            StringBuilder sqlBuilder = new StringBuilder("select * from system_param where type in (");
            for (int i = 0; i < resetDataType.size(); i++) {
                sqlBuilder.append("'").append(resetDataType.get(i)).append("'");
                if (i < resetDataType.size() - 1) {
                    sqlBuilder.append(", ");
                }
            }
            sqlBuilder.append(")");
            String sql = sqlBuilder.toString();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String value = resultSet.getString("value");
                String valueDesc = resultSet.getString("value_desc");
                String insertSql = "update  `i18n_message` set message = '" + valueDesc + "' where  message_key =  '" + value + "' ";
                System.out.println(insertSql);
                statement.addBatch(insertSql);
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void checkDataToTranslateMap(String sql) {
        // 校验system 是否有重复的翻译值
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String messageKey = resultSet.getString("value");
                String countNum = resultSet.getString("countNum");
                if (StringUtils.isNotBlank(messageKey) && StringUtils.isNotBlank(countNum))
                    repactDataMap.put(messageKey, countNum);
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 这个是表字段需要翻译的 key
     *
     * @throws SQLException
     */
    public static void initTextToTranslateMapOnce() {
        Connection connection = null;
        Statement statement1 = null;
        Statement statement2 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            statement1 = connection.createStatement();
            statement2 = connection.createStatement();

            // 获取现有的 i18n_message 键
            String sql2 = "SELECT * FROM i18n_message WHERE message_key LIKE 'LOOKUP_SH_PLAY_TYPE%'";
            resultSet2 = statement2.executeQuery(sql2);
            Set<String> isExistSet = new HashSet<>();

            while (resultSet2.next()) {
                String key = resultSet2.getString("message_key");
                isExistSet.add(key);
            }

            // 获取 system_param 数据
            String sql1 = "SELECT * FROM system_param WHERE value LIKE 'LOOKUP_SH_PLAY_TYPE%'";
            resultSet1 = statement1.executeQuery(sql1);

            while (resultSet1.next()) {
                String key = resultSet1.getString("value");
                String valueDesc = resultSet1.getString("value_desc");
                if (textToTranslate.containsKey(key)) {
                    System.out.println("重复的可以:" + key);
                }
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(valueDesc) && key.startsWith("LOOKUP_SH_PLAY_TYPE")) {
                    textToTranslate.put(key, valueDesc);

                }
            }

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (resultSet1 != null) {
                try {
                    resultSet1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (resultSet2 != null) {
                try {
                    resultSet2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement1 != null) {
                try {
                    statement1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement2 != null) {
                try {
                    statement2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 这个是表字段需要翻译的key
     *
     * @throws SQLException
     */
    /**
     * 这个是表字段需要翻译的 key
     *
     * @throws SQLException
     */
    public static void initTextToTranslateMap() {
        Connection connection = null;
        Statement statement1 = null;
        Statement statement2 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            statement1 = connection.createStatement();
            statement2 = connection.createStatement();

            // 获取现有的 i18n_message 键
            String sql2 = "SELECT * FROM i18n_message WHERE message_key LIKE 'LOOKUP_%'";
            resultSet2 = statement2.executeQuery(sql2);
            Set<String> isExistSet = new HashSet<>();

            while (resultSet2.next()) {
                String key = resultSet2.getString("message_key");
                isExistSet.add(key);
            }

            // 获取 system_param 数据
            String sql1 = "SELECT * FROM system_param";
            resultSet1 = statement1.executeQuery(sql1);

            while (resultSet1.next()) {
                String key = resultSet1.getString("value");
                String valueDesc = resultSet1.getString("value_desc");

                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(valueDesc) && key.startsWith("LOOKUP_")) {
                    textToTranslate.put(key, valueDesc);

                    if (!isExistSet.contains(key)) {
                        partTextToTranslate.put(key, valueDesc);
                    }
                }
            }

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (resultSet1 != null) {
                try {
                    resultSet1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (resultSet2 != null) {
                try {
                    resultSet2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement1 != null) {
                try {
                    statement1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement2 != null) {
                try {
                    statement2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 这个是表字段需要翻译的 key,只增加，不修改
     *
     * @throws SQLException
     */
    public static void initTextToTranslateMapForAdd() {
        Connection connection = null;
        Statement statement1 = null;
        Statement statement2 = null;
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            statement1 = connection.createStatement();
            statement2 = connection.createStatement();

            // 获取现有的 i18n_message 键
            String sql2 = "SELECT * FROM i18n_message WHERE message_key LIKE 'LOOKUP_%'";
            resultSet2 = statement2.executeQuery(sql2);
            Set<String> isExistSet = new HashSet<>();

            while (resultSet2.next()) {
                String key = resultSet2.getString("message_key");
                isExistSet.add(key);
            }

            // 获取 system_param 数据
            String sql1 = "SELECT * FROM system_param";
            resultSet1 = statement1.executeQuery(sql1);

            while (resultSet1.next()) {
                String key = resultSet1.getString("value");
                String valueDesc = resultSet1.getString("value_desc");

                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(valueDesc) && key.startsWith("LOOKUP_")) {
                    //textToTranslate.put(key, valueDesc);

                    if (!isExistSet.contains(key)) {
                        partTextToTranslate.put(key, valueDesc);
                    }
                }
            }

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (resultSet1 != null) {
                try {
                    resultSet1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (resultSet2 != null) {
                try {
                    resultSet2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement1 != null) {
                try {
                    statement1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement2 != null) {
                try {
                    statement2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void operateMysql(List<String> sqlList) throws SQLException {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (String sql : sqlList) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件
     */
    public static Map<String, String> loadProperties(String filePath) {

        Map<String, String> propertiesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 忽略空行和以 # 或 ; 开头的注释行
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue;
                }

                // 分割 key 和 value
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    propertiesMap.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return propertiesMap;
    }

    /**
     * 读取ResultCode配置文件。初始化textToTranslate
     */
    static void getTextToTranslate() {

        String filePath = "./data-transfer/src/main/resources/messages.properties"; // 使用相对路径从类路径加载
        Map<String, String> propertiesMap = loadProperties(filePath);

        // 重复的map
        Map<String, String> repeatMap = new TreeMap<>();
        // 判断是否有重复的
        int count = 0;
        boolean repeatFlag = false;
        for (ResultCode codeDemo : ResultCode.values()) {
            count++;
            Integer code = codeDemo.getCode();
            String messageCode = codeDemo.getMessageCode();
            String desc = codeDemo.getDesc();
            if (!textToTranslate.containsKey(messageCode)) {
                // 判断是否符合添加
                if (isTranslate(messageCode)) {
                    // todo 如果注释，则不更新配置文件
                    textToTranslate.put(messageCode, desc);
                    if (!propertiesMap.containsKey(messageCode)) {
                        partTextToTranslate.put(messageCode, desc);
                    }
                }
            } else {
                repeatFlag = true;
                repeatMap.put(messageCode, desc);
            }
            if (count >= 10) {
                //break;
            }
        }
        //打印
        for (Map.Entry<String, String> entry : textToTranslate.entrySet()) {
            String code = entry.getKey();
            String text = entry.getValue();
            System.out.println(code + "=" + text);
        }
        if (repeatFlag) {
            System.out.println("重复的key");
            for (Map.Entry<String, String> entry : repeatMap.entrySet()) {
                String code = entry.getKey();
                String text = entry.getValue();
                System.out.println(code + "=" + text);
            }
        }

        // 判断如果有重复的key，就直接停止翻译，提示
        if (repeatFlag) {
            throw new RuntimeException("有重复的，请检查");
        }
        // 检验 system_param ，value需要唯一
        // repactDataMap
        checkDataToTranslateMap(" select  `value` ,count(*) as countNum from system_param where value like '%LOOKUP%' GROUP BY  `value` HAVING countNum>1 ");
        if (!CollectionUtils.isEmpty(repactDataMap)) {
            // 打印
            System.out.println("重复的value");
            repactDataMap.forEach((key, value) -> System.out.println(key));
            // throw new RuntimeException("有重复的，请检查");
        }


    }

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10, // 核心线程数
            50, // 最大线程数
            60, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingQueue<>(500), // 队列容量
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );

    // 全量更新
    public static void allUpdate() {
        try {
            clearFile("./data-transfer/src/main/resources/messages.properties");
            for (String lang : language) {
                lang = lang.replace(CommonConstant.CENTER_LINE, CommonConstant.UNDERLINE);
                clearFile("./data-transfer/src/main/resources/messages_" + lang + ".properties");
            }
            // 初始化
            initTextToTranslateMap();
            // todo 记得打开，
            getTextToTranslate();
            CountDownLatch latch = new CountDownLatch(textToTranslate.size());

            // 执行翻译操作，并将结果插入数据库并生成配置文件
            for (Map.Entry<String, String> entry : textToTranslate.entrySet()) {
                String code = entry.getKey();
                String text = entry.getValue();
                // 调用 Google 翻译引擎进行翻译
                threadPoolExecutor.submit(() -> {
                    // 多线程跑 -start
                    Map<String, String> translations = translateText(text);
                    try {
                        if (!code.startsWith("PROMPT")) {
                            // 将翻译结果新增到数据库
                            String sql = "select * from i18n_message where message_key = '" + code + "' ";

                            queryDataToTranslateMap(sql);
                            insertTranslationIntoDatabase(code, translations);
//                            log.info("Database新增messageKey:{},源文本:{},语言:{}", code, text, translations);
                        } else {
                            // 将翻译结果新增到配置文件
                            addToConfigurationFile(code, translations);
//                            log.info("Properties新增messageKey:{},源文本:{},语言:{}", code, text, translations);
                        }
                        // 多线程结束 -end
                    } catch (Exception e) {
                        System.err.println("Error translating text: " + e.getMessage());
                    } finally {
                        latch.countDown(); // 每个任务完成后计数器减1
                        log.info("Task for key: {} completed. Remaining tasks: {}", code, latch.getCount());

                    }

                });

            }

            latch.await(); // 阻塞，直到所有任务完成
            // 全部才用
            reSetData();
            System.out.println("翻译完成！");
            log.info("所有翻译完成!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Translation process interrupted: {}", e.getMessage());
        } finally {
            threadPoolExecutor.shutdown();
            try {
                if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPoolExecutor.shutdownNow();
            }
        }


    }

    // 全量更新
    public static void LocalAllUpdate() {
        try {
            clearFile("./data-transfer/src/main/resources/messages.properties");
            for (String lang : language) {
                lang = lang.replace(CommonConstant.CENTER_LINE, CommonConstant.UNDERLINE);
                clearFile("./data-transfer/src/main/resources/messages_" + lang + ".properties");
            }
            // 初始化
            initTextToTranslateMap();
            // todo 记得打开，
            getTextToTranslate();
            // 执行翻译操作，并将结果插入数据库并生成配置文件
            for (Map.Entry<String, String> entry : textToTranslate.entrySet()) {
                String code = entry.getKey();
                String text = entry.getValue();
                // 调用 Google 翻译引擎进行翻译

                Map<String, String> translations = translateText(text);

                if (!code.startsWith("PROMPT")) {
                    // 将翻译结果新增到数据库
                    /*String sql = "select * from i18n_message where message_key = '" + code + "' ";

                    queryDataToTranslateMap(sql);
                    insertTranslationIntoDatabase(code, translations);
                    log.info("Database新增messageKey:{},源文本:{},语言:{}", code, text, translations);*/
                } else {
                    // 将翻译结果新增到配置文件
                    addToConfigurationFile(code, translations);
                    log.info("Properties新增messageKey:{},源文本:{},语言:{}", code, text, translations);
                }
            }
            // 全部才用
            reSetData();
            System.out.println("Translation completed successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 只更新指定key
     * %LOOKUP_SH_PLAY_TYPE%
     */
    // 增量更新
    public static void IncrementalUpdateOnce() {
        try {

            // 初始化
            initTextToTranslateMapOnce();
            // 执行翻译操作，并将结果插入数据库并生成配置文件
            // 打印
            log.info("---------------------------------------------");
            log.info(JSON.toJSONString(textToTranslate));
            log.info(JSON.toJSONString(textToTranslate.size()));
            log.info("---------------------------------------------");
            for (Map.Entry<String, String> entry : textToTranslate.entrySet()) {
                String code = entry.getKey();
                String text = entry.getValue();

                // 调用 Google 翻译引擎进行翻译
                Map<String, String> translations = translateText(text);
                if (!code.startsWith("PROMPT")) {
                    // 将翻译结果新增到数据库
                    String sql = "select * from i18n_message where message_key = '" + code + "' ";

                    queryDataToTranslateMap(sql);
                    insertTranslationIntoDatabase(code, translations);
//                    log.info("Database新增messageKey:{},源文本:{},语言:{}", code, text, translations);
                }
            }

            System.out.println("Translation completed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * // 增量更新,只插入，不更新。 不直接操作数据库，只打印
     */
    public static void IncrementalUpdate() {
        try {

            // 初始化
            initTextToTranslateMap();
            // todo 记得打开，
            getTextToTranslate();
            // 执行翻译操作，并将结果插入数据库并生成配置文件
            // 打印
            log.info("---------------------------------------------");
            // log.info(JSON.toJSONString(partTextToTranslate));
            log.info(JSON.toJSONString(partTextToTranslate.size()));
            // log.info(JSON.toJSONString(textToTranslate));
            log.info(JSON.toJSONString(textToTranslate.size()));
            // partTextToTranslat 翻译值，只添加
            log.info("---------------------------------------------");
            for (Map.Entry<String, String> entry : partTextToTranslate.entrySet()) {
                String code = entry.getKey();
                String text = entry.getValue();

                // 调用 Google 翻译引擎进行翻译
                Map<String, String> translations = translateText(text);
                if (!code.startsWith("PROMPT")) {
                    // 将翻译结果新增到数据库
                    String sql = "select * from i18n_message where message_key = '" + code + "' ";

                    queryDataToTranslateMap(sql);
                    insertTranslationIntoDatabaseForAdd(code, translations);
//                    log.info("Database新增messageKey:{},源文本:{},语言:{}", code, text, translations);
                } else {
                    // 将翻译结果新增到配置文件
                    addToConfigurationFile(code, translations);
//                    log.info("Properties新增messageKey:{},源文本:{},语言:{}", code, text, translations);
                }
            }

            System.out.println("Translation completed successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 判断是否翻译
     *
     * @param messageCode code
     */
    private static boolean isTranslate(String messageCode) {

        return messageCode.startsWith("PROMPT_") || messageCode.startsWith("LOOKUP_") || messageCode.startsWith("EXCEL_") || messageCode.startsWith("BUSINESS_");
    }

    // 创建翻译服务
    /*private static Translate createTranslateService() throws IOException {
        FileInputStream credentialsStream = new FileInputStream(SERVICE_ACCOUNT_KEY_PATH);
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped("https://www.googleapis.com/auth/cloud-translation");
        return TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }*/

    public static String translateText(String apiKey, String text, String targetLanguage) {
        try {
            text = text.trim();
            String url = "https://translation.googleapis.com/language/translate/v2?key=" + apiKey + "&q=" + text + "&target=" + targetLanguage;
            log.info("*******{}", text);
            log.info("*******{}", apiKey);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                // Parse JSON response
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();
                JsonArray translations = jsonResponse.getAsJsonObject("data").getAsJsonArray("translations");

                // Extract the translated text from the JSON response
                if (translations.size() > 0) {
                    JsonObject translationObj = translations.get(0).getAsJsonObject();
                    return translationObj.get("translatedText").getAsString();
                } else {
                    log.error("No translation found in the response.");
                }
            } else {
                log.error("Failed to translate. HTTP error code: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 使用 Google 翻译引擎翻译文本
    private static Map<String, String> translateText(String text) {
        Map<String, String> map = new HashMap<>();
        language.forEach(lang -> {
            //Translation translate = translateService.translate(text, Translate.TranslateOption.sourceLanguage("zh-CN"), Translate.TranslateOption.targetLanguage(lang));
            // 如果是中文，则不需要翻译
            //log.info("lang11111:{}", lang);
            // 中文的不进行翻译
            if (LanguageEnum.ZH_CN.getLang().equals(lang)) {
                map.put(lang, text);
            } else {
                String s = Translator.translate(text, "zh-CN", lang);
                //String s = translateText(apiKey, text, lang);
                map.put(lang, s);
            }


        });
        return map;
    }

    // 将翻译结果新增到数据库
    private static void insertTranslationIntoDatabaseForAdd(String code, Map<String, String> translations) {
        // TODO: 实现将翻译结果插入数据库的逻辑

        // SQL 模板（只用于生成字符串，不执行）
        String insertTemplate = "INSERT IGNORE INTO `i18n_message` " +
                "(`message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) " +
                "VALUES ('BACK_END', '{code}', '{lang}', '{text}', 1, NOW(), NULL, NULL);";

        String updateSql = "UPDATE `i18n_message` SET `message` = ?, `updater` = 1, `updated_time` = NOW() " +
                "WHERE `message_key` = ? AND `language` = ?";
        try {
            List<String> sqlList = new ArrayList<>();
            /*translations.forEach((lang, text) -> {
                //判断是否是插入还是更新
                String key = code + lang;
                if (dataMap.containsKey(key)) {
                    //sqlList.add("update  `i18n_message` set message = '" + text + "' where  message_key =  '" + code + "' and language =  '" + lang + "' ");
                } else {
                    sqlList.add("INSERT IGNORE INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) " + "VALUES ('BACK_END', '" + code + "', '" + lang + "', '" + text.replace("'", "''") + "', 1, 1, NULL, NULL);");

                }
            });*/
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                String lang = entry.getKey();
                String text = entry.getValue();
                String key = code + lang;
                // 转义单引号，防止 SQL 错误
                String safeText = text == null ? "" : text.replace("'", "''");
                String sqlPreview;

                if (dataMap.containsKey(key)) {
                    // 已存在，执行更新

                } else {
                    // 不存在，执行插入
                    // 不存在，生成插入 SQL
                    sqlPreview = insertTemplate
                            .replace("{text}", safeText)
                            .replace("{code}", code)
                            .replace("{lang}", lang);

                    sqlList.add(sqlPreview);
                }
            }
            sqlList.forEach(System.out::println);
            //operateMysql(sqlList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 将翻译结果新增到数据库
    private static void insertTranslationIntoDatabase(String code, Map<String, String> translations) {
        // TODO: 实现将翻译结果插入数据库的逻辑
        try {
            List<String> sqlList = new ArrayList<>();
            translations.forEach((lang, text) -> {
                //判断是否是插入还是更新
                String key = code + lang;
                if (dataMap.containsKey(key)) {
                    sqlList.add("update  `i18n_message` set message = '" + text + "' where  message_key =  '" + code + "' and language =  '" + lang + "' ");
                } else {
                    sqlList.add("INSERT IGNORE INTO `i18n_message` ( `message_type`, `message_key`, `language`, `message`, `creator`, `created_time`, `updater`, `updated_time`) " + "VALUES ('BACK_END', '" + code + "', '" + lang + "', '" + text + "', 1, 1, NULL, NULL);");
                }
            });
            operateMysql(sqlList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void clearFile(String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false));
            writer.write(""); // 写入空字符串以清空文件内容
            writer.close();
            System.out.println("文件内容已清空：" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将翻译结果新增到配置文件
    private static void addToConfigurationFile(String code, Map<String, String> translations) throws IOException {


        translations.forEach((lang, text) -> {
            PrintWriter writer = null;
            try {
                lang = lang.replace(CommonConstant.CENTER_LINE, CommonConstant.UNDERLINE);
                // 默认语言设置
                if (lang.equals("en_US")) {
                    // 打开配置文件输出流，使用 UTF-8 编码
                    writer = new PrintWriter(new FileWriter("./data-transfer/src/main/resources/messages.properties", StandardCharsets.UTF_8, true));

                    PrintWriter finalWriter = writer;

                    // 写入翻译结果到配置文件
                    finalWriter.println(code + "=" + text);
                    finalWriter.close();
                }
                // 打开配置文件输出流，使用 UTF-8 编码
                writer = new PrintWriter(new FileWriter("./data-transfer/src/main/resources/messages_" + lang + ".properties", StandardCharsets.UTF_8, true));

                PrintWriter finalWriter = writer;

                // 写入翻译结果到配置文件
                finalWriter.println(code + "=" + text);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        });
    }
}

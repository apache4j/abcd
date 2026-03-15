package com.cloud.baowang.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.nacos.api.utils.StringUtils;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SpringBeanUtil;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.excel.handler.SimpleCellWriteHandler;
import com.cloud.baowang.common.excel.handler.SimpleSheetWriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.cloud.baowang.common.excel.ParallelUtil.DEF_PARALLEL_NUM;

@Slf4j
public class ExcelUtil {
    /**
     * minio的桶名bucket
     */
    public static final String BAOWANG_BUCKET = "baowang";
    /**
     * 单个sheet条数
     */
    public static final Integer EXCEL_SHEET_ROW_MAX_SIZE = 10000;
    /**
     * excel contentType
     */
    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /**
     * excel head
     */
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final int DEF_PAGE_SIZE = 1000;

    /**
     * 计算页数工具
     *
     * @param pageSize 页数据size
     * @param count    数据总数
     * @return
     */
    public static long getPages(Integer pageSize, Long count) {
        long pages;
        if (count % pageSize == 0) {
            pages = count / pageSize;
        } else {
            pages = count / pageSize + 1;
        }
        return pages;
    }

    /**
     * @param response         响应内容
     * @param fileName         文件名
     * @param head             excel头对象
     * @param param            查询入参
     * @param parallelNum      并发数阈值 到了这个阈值才会统一去写,达不到阈值会流式
     * @param totalPage        数据总页数
     * @param pageListFunction 实际查询数据方法 返回list
     * @param <T>              返回对象
     * @param <P>              请求入参
     */
    public static <T, P extends PageVO> void writeForParallel(HttpServletResponse response, String fileName, Class<T> head, P param, int parallelNum, long totalPage, PageFunction<List<T>, P> pageListFunction) {
        try {
            writeForParallel(response, null, fileName, head, param, parallelNum, totalPage, pageListFunction);
        } catch (Exception e) {
            log.error("导出文件异常,error:", e);
        }
    }

    /**
     * @param response         响应内容
     * @param fileName         文件名
     * @param head             excel头对象
     * @param param            查询入参
     * @param parallelNum      并发数阈值 到了这个阈值才会统一去写,达不到阈值会流式
     * @param totalPage        数据总页数
     * @param pageListFunction 实际查询数据方法 返回list
     * @param <T>              返回对象
     * @param <P>              请求入参
     */
    public static <T, P extends PageVO> void writeForParallel(HttpServletResponse response, List<String> includeColumnList, String fileName, Class<T> head, P param, int parallelNum, long totalPage, PageFunction<List<T>, P> pageListFunction) {
        // 导出10w条限制
        if (totalPage > 10) {
            throw new BaowangDefaultException(ResultCode.EXPORTED_NUM_LIMITED);
        }
        try {
            response.setContentType(CONTENT_TYPE);
            response.setHeader(CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8));
            pageExcelWriterParallel(EasyExcel.write(response.getOutputStream(), head).includeColumnFieldNames(includeColumnList).build(), param, parallelNum, totalPage, pageListFunction);
        } catch (Exception e) {
            log.error("导出文件异常,error:", e);
        }
    }

    /**
     * @param head             excel头对象
     * @param param            查询入参
     * @param parallelNum      并发数阈值 到了这个阈值才会统一去写,达不到阈值会流式
     * @param totalPage        数据总页数
     * @param pageListFunction 实际查询数据方法 返回list
     * @param <T>              返回对象
     * @param <P>              请求入参
     */
    public static <T, P extends PageVO> byte[] writeForParallel(Class<T> head, P param, int parallelNum, long totalPage, PageFunction<List<T>, P> pageListFunction) {
        if(param != null){
            if(null == param.getIncludeColumnList() || param.getIncludeColumnList().size() == 0){
                throw new BaowangDefaultException(ResultCode.EXPORT_FIELD_NOT_NULL);
            }
            return writeForParallel(head, param.getIncludeColumnList(), param, parallelNum, totalPage, pageListFunction);
        }
        return writeForParallel(head, null, param, parallelNum, totalPage, pageListFunction);
    }

    /**
     * @param head             excel头对象
     * @param includeColumnList 包含字段名称
     * @param param            查询入参
     * @param parallelNum      并发数阈值 到了这个阈值才会统一去写,达不到阈值会流式
     * @param totalPage        数据总页数
     * @param pageListFunction 实际查询数据方法 返回list
     * @param <T>              返回对象
     * @param <P>              请求入参
     */
    public static <T, P extends PageVO> byte[] writeForParallel(Class<T> head, List<String> includeColumnList, P param, int parallelNum, long totalPage, PageFunction<List<T>, P> pageListFunction) {
        // 导出10w条限制
        if (totalPage > 10) {
            throw new BaowangDefaultException(ResultCode.EXPORTED_NUM_LIMITED);
        }
        try {
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            pageExcelWriterParallel(EasyExcel.write(fileOutputStream, head)
                    .includeColumnFieldNames(includeColumnList).build(), param, parallelNum, totalPage, pageListFunction);
            byte[] bytes = convertOutputStream(fileName);
            // 删除文件
            File tempFile = new File(fileName);
            tempFile.delete();
            return bytes;
        } catch (Exception e) {
            log.error("生成导出文件,转换成字节数组,error:", e);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * @param response         响应内容
     * @param fileName         文件名
     * @param head             excel头对象
     * @param param            查询入参
     * @param totalPage        数据总页数
     * @param pageListFunction 实际查询数据方法 返回list
     * @param <T>              返回对象
     * @param <P>              请求入参
     */
    public static <T, P extends PageVO> void writeForParallel(HttpServletResponse response, String fileName, Class<T> head, P param, long totalPage, PageFunction<List<T>, P> pageListFunction) {
        try {
            // 写入默认系统核心数
            writeForParallel(response, fileName, head, param, DEF_PARALLEL_NUM, totalPage, pageListFunction);
        } catch (Exception e) {
            log.error("导出文件异常,error:", e);
        }
    }

    /**
     * @param response         响应内容
     * @param fileName         文件名
     * @param head             excel头对象
     * @param param            查询入参
     * @param totalPage        数据总页数
     * @param pageListFunction 实际查询数据方法 返回list
     * @param <T>              返回对象
     * @param <P>              请求入参
     */
    public static <T, P extends PageVO> void writeForParallelI18n(HttpServletResponse response, String fileName,
                                                                  Class<T> head, P param, int parallelNum, long totalPage,
                                                                  PageFunction<List<T>, P> pageListFunction) {
        try {
            pageExcelWriterParallelI18n(buildDownloadForHttpResponse(response, fileName, null).build(), head,
                    param, parallelNum, totalPage, pageListFunction);
        } catch (Exception e) {
            log.error("Error exporting file, error:", e);
        }
    }

    private static <T, P extends PageVO> void pageExcelWriterParallelI18n(ExcelWriter excelWriter, Class<T> head,
                                                                          P param, int parallelNum, long totalPage, PageFunction<List<T>, P> pageListFunction) throws InterruptedException {
        try (excelWriter) {
            if (totalPage <= 0) {
                excelWriter.write(Collections.emptyList(), EasyExcel.writerSheet().build());
                return;
            }
            AtomicLong count = new AtomicLong();
            WriteSheet writeSheet = EasyExcel.writerSheet().head(head).build();
            ParallelUtil.parallel(List.class, parallelNum, totalPage)
                    .param(param)
                    .asyncProducer(vo -> pageListFunction.apply((P) vo))
                    .syncConsumer(pageList -> {
                        long sheetNo = getPages(EXCEL_SHEET_ROW_MAX_SIZE, count.addAndGet(pageList.size()));
                        writeSheet.setSheetNo((int) sheetNo);
                        writeSheet.setSheetName("Sheet" + writeSheet.getSheetNo());
                        excelWriter.write(pageList, writeSheet);
                    }).start();
        }
    }

    private static <T, P extends PageVO> void pageExcelWriterParallel(ExcelWriter excelWriter, P param, int parallelNum, long totalPage, PageFunction<List<T>, P> pageListFunction) throws InterruptedException {
        // 自动关闭流
        try (excelWriter) {
            if (totalPage <= 0) { // 如果无待写入的数据则写入标题
                excelWriter.write(Collections.emptyList(), EasyExcel.writerSheet().build());
                return;
            }
            AtomicLong count = new AtomicLong();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            ParallelUtil.parallel(List.class, parallelNum, totalPage)
                    .param(param)
                    .asyncProducer(vo -> pageListFunction.apply((P) vo))
                    .syncConsumer(pageList -> {
                        // 计算sheetNo
                        long sheetNo = getPages(EXCEL_SHEET_ROW_MAX_SIZE, count.addAndGet(pageList.size()));
                        writeSheet.setSheetNo((int) sheetNo);
                        writeSheet.setSheetName("Sheet" + writeSheet.getSheetNo());
                        excelWriter.write(pageList, writeSheet);
                    }).start();
        }
    }

    public static ExcelReaderBuilder read(InputStream inputStream, Class head, ReadListener readListener) {
        ExcelReaderBuilder excelReaderBuilder = new ExcelReaderBuilder();
        excelReaderBuilder.file(inputStream);
        if (head != null) {
            excelReaderBuilder.head(head);
        }

        if (readListener != null) {
            excelReaderBuilder.registerReadListener(readListener);
        }

        return excelReaderBuilder;
    }

    public static ExcelReaderBuilder read(File file, Class head, ReadListener readListener) {
        ExcelReaderBuilder excelReaderBuilder = new ExcelReaderBuilder();
        excelReaderBuilder.file(file);
        if (head != null) {
            excelReaderBuilder.head(head);
        }

        if (readListener != null) {
            excelReaderBuilder.registerReadListener(readListener);
        }

        return excelReaderBuilder;
    }

    public static <T> ExcelReaderBuilder read(InputStream inputStream, Class<T> head, Consumer<List<T>> consumer) {
        return read(inputStream, head, new EasyExcelConsumerListener<>(DEF_PAGE_SIZE, consumer));
    }

    public static <T> ExcelReaderBuilder read(File file, Class<T> head, Consumer<List<T>> consumer) {
        return read(file, head, DEF_PAGE_SIZE, consumer);
    }

    public static <T> ExcelReaderBuilder read(File file, Class<T> head, Integer pageSize, Consumer<List<T>> consumer) {
        return read(file, head, new EasyExcelConsumerListener<>(pageSize, consumer));
    }

    /**
     * 导出初始化response
     *
     * @param response
     * @throws UnsupportedEncodingException
     */
    private static void initResponse(HttpServletResponse response, String fileName) {
        try {
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding("utf-8");
            //国际化 下载文件名直接使用英文
            //fileName = I18nMessageUtil.getI18NMessage(fileName);
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        } catch (Exception e) {
            log.error("导出文件异常,error:", e);
        }
    }

    /**
     * 构建Web下导出的Builder
     *
     * @param response
     * @param fileName
     * @param writeHandlerList
     * @return
     * @throws IOException
     */
    public static ExcelWriterBuilder buildDownloadForHttpResponse(HttpServletResponse response, String fileName, List<WriteHandler> writeHandlerList) {
        try {
            initResponse(response, fileName);
            ExcelWriterBuilder write = EasyExcel.write(response.getOutputStream());
            writeHandlerList = Optional.ofNullable(writeHandlerList).orElse(new ArrayList<>());
            writeHandlerList.stream().forEach(writeHandler -> write.registerWriteHandler(writeHandler));
            write
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .needHead(true);
            write.registerWriteHandler(SpringBeanUtil.getBean(SimpleCellWriteHandler.class))
                    .registerWriteHandler(SpringBeanUtil.getBean(SimpleSheetWriteHandler.class));

            return write;

        } catch (Exception e) {
            log.error("导出文件异常,error:", e);
        }
        return null;

    }

    /**
     * 导入重新构建头，
     *
     * @param analysisContext
     * @param headMap
     * @param clazz
     */
    public static void buildUpdateHeadAgain(AnalysisContext analysisContext, Map<Integer, ReadCellData<?>> headMap, Class clazz) {
        ExcelReadHeadProperty excelHeadPropertyData = analysisContext.readSheetHolder().excelReadHeadProperty();
        //获取导入的头
        Map<Integer, Head> nowHeadMapData = excelHeadPropertyData.getHeadMap();
        // 如果 nowHeadMapData 不为空，easyexcel能通过注解名字匹配上
        if (MapUtils.isNotEmpty(nowHeadMapData)) {
            return;
        }
        // 国际化处理将名字转换回中文重新匹配,originExcelHeadPropertyData由表头类产生
        ExcelReadHeadProperty originExcelHeadPropertyData = new ExcelReadHeadProperty(analysisContext.currentReadHolder(), clazz, null);
        //key下标，val列信息
        Map<Integer, Head> originHeadMapData = originExcelHeadPropertyData.getHeadMap();


        //excel实际列名
        Map<Integer, String> dataMap = ConverterUtils.convertToStringMap(headMap, analysisContext);
        Map<Integer, Head> tmpHeadMap = new HashMap<>(originHeadMapData.size() * 4 / 3 + 1);
        //临时类
        Map<Integer, ExcelContentProperty> tmpContentPropertyMap = new HashMap<>(originHeadMapData.size() * 4 / 3 + 1);
        //循环匹配
        for (Map.Entry<Integer, Head> entry : originHeadMapData.entrySet()) {
            Head headData = entry.getValue();
            String headName = String.format("%s.%s", headData.getField().getDeclaringClass().getSimpleName(), headData.getFieldName());
            headName = SpringBeanUtil.getBean(MessageSource.class).getMessage(headName, null, LocaleContextHolder.getLocale());
//            headData.setHeadNameList(Arrays.asList(headName));
            for (Map.Entry<Integer, String> stringEntry : dataMap.entrySet()) {
                if (stringEntry == null) {
                    continue;
                }
                String headString = stringEntry.getValue().trim();
                //下标
                Integer stringKey = stringEntry.getKey();
                if (StringUtils.isEmpty(headString)) {
                    continue;
                }

                if (StringUtils.equals(headName, headString)) {
                    headData.setColumnIndex(stringKey);
                    tmpHeadMap.put(stringKey, headData);
                    break;
                }
            }
        }
        excelHeadPropertyData.setHeadMap(tmpHeadMap);
    }

    /**
     * 文件输出流 转 字节数组
     * @param fileName 文件路径
     * @return 字节数组
     */
    public static byte[] convertOutputStream(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            // 定义缓冲区大小
            byte[] buffer = new byte[4096];
            int bytesRead;

            // 创建临时的byte数组
            byte[] tempArray;

            // 使用缓冲区从FileInputStream中读取数据到临时数组中
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                tempArray = new byte[bytesRead];
                System.arraycopy(buffer, 0, tempArray, 0, bytesRead);
                byteArrayOutputStream.write(tempArray);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("文件输出流convert字节数组 error:", e);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
    }
}



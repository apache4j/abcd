package com.cloud.baowang.system.util;

import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class JsonComparator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<JsonDifference> compareJson(Object strObj, Object str2Obj)throws Exception {
        Gson gson = new Gson();
        String str1=null;
        if (Objects.isNull(strObj)){
             str1="{}";
        }else{
             str1=gson.toJson(strObj);
        }
        String str2=gson.toJson(str2Obj);
        JsonNode json1 = mapper.readTree(str1);
        JsonNode json2 = mapper.readTree(str2);
        List<JsonDifference> differences = new ArrayList<>();
        compareJsonRecursive(json1, json2, differences, "");
        return differences;
    }

    private static void compareJsonRecursive(JsonNode node1, JsonNode node2,
                                             List<JsonDifference> differences, String path) {
        // 处理节点1不存在的情况（新增字段）
        if (node1 == null || node1.isNull()) {
            if (node2 != null && !node2.isNull()) {
                differences.add(new JsonDifference(
                        JsonDifference.DifferenceType.ADDED,
                        path,
                        null,
                        convertJsonNodeToValue(node2)
                ));
            }
            return;
        }

        // 处理节点2不存在的情况（删除字段）
        if (node2 == null || node2.isNull()) {
            differences.add(new JsonDifference(
                    JsonDifference.DifferenceType.REMOVED,
                    path,
                    convertJsonNodeToValue(node1),
                    null
            ));
            return;
        }

        // 处理节点类型不同的情况
        if (!node1.getNodeType().equals(node2.getNodeType())) {
            differences.add(new JsonDifference(
                    JsonDifference.DifferenceType.TYPE_CHANGED,
                    path,
                    node1.getNodeType().toString(),
                    node2.getNodeType().toString()
            ));
            return;
        }

        // 根据节点类型进行比较
        switch (node1.getNodeType()) {
            case OBJECT:
                compareObjects(node1, node2, differences, path);
                break;
            case ARRAY:
                compareArrays(node1, node2, differences, path);
                break;
            default:
                // 基本类型直接比较
                if (!node1.equals(node2)) {
                    differences.add(new JsonDifference(
                            JsonDifference.DifferenceType.MODIFIED,
                            path,
                            convertJsonNodeToValue(node1),
                            convertJsonNodeToValue(node2)
                    ));
                }
        }
    }

    private static void compareObjects(JsonNode obj1, JsonNode obj2,
                                       List<JsonDifference> differences, String path) {
        // 比较obj1中的字段
        Iterator<String> fieldNames = obj1.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            String newPath = path.isEmpty() ? field : path + "." + field;
            compareJsonRecursive(obj1.get(field), obj2.get(field), differences, newPath);
        }

        // 检查obj2中有而obj1中没有的字段（新增字段）
        fieldNames = obj2.fieldNames();
        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            if (!obj1.has(field)) {
                String newPath = path.isEmpty() ? field : path + "." + field;
                differences.add(new JsonDifference(
                        JsonDifference.DifferenceType.ADDED,
                        newPath,
                        null,
                        convertJsonNodeToValue(obj2.get(field))
                ));
            }
        }
    }

    private static void compareArrays(JsonNode arr1, JsonNode arr2,
                                      List<JsonDifference> differences, String path) {
        // 比较数组元素
        Map<String,String> arr1map= new HashMap<>();
        Map<String,String> arr2map= new HashMap<>();
        arr1.forEach(arr ->{
            arr1map.put(arr.asText(),arr.asText());
        });
        arr2.forEach(arr ->{
            arr2map.put(arr.asText(),arr.asText());
        });
        String valuea="["+arr1map.values().stream().map(Object::toString).collect(Collectors.joining(","))+"]";
        String valueb="["+arr2map.values().stream().map(Object::toString).collect(Collectors.joining(","))+"]";
        boolean checkFlag=true;
        for (Map.Entry<String, String> entry : arr1map.entrySet()) {
            String key = entry.getKey();
            if (!arr2map.containsKey(key)) {
                differences.add(new JsonDifference(
                        JsonDifference.DifferenceType.MODIFIED,
                        path,
                        valuea
                        ,
                        valueb
                ));
                checkFlag=false;
                break;
            }
        }
        if (checkFlag == true){
            for (Map.Entry<String, String> entry : arr2map.entrySet()) {
                String k = entry.getKey();
                if (!arr1map.containsKey(k)) {
                    differences.add(new JsonDifference(
                            JsonDifference.DifferenceType.MODIFIED,
                            path,
                            valuea
                            ,
                            valueb
                    ));
                    break;
                }
            }
        }
    }

    private static String convertJsonNodeToValue(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isTextual()) return node.asText();
        if (node.isNumber()) return node.numberValue().toString();
        if (node.isBoolean()) return node.asBoolean()+"";
        if (node.isArray()){
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (JsonNode element : node) {
                sb.append(element.asText()); // 获取文本值
                if (sb.length() > 0) {
                    sb.append(",");
                }
            }
            String result = "";
            if (node.size()>0){
                result=sb.substring(0,sb.length()-1);
            }else{
                result =sb.toString();
            }
            result=result+"]";
            return result;
        }
        return node.toString(); // 对于复杂对象，返回JSON字符串
    }

    public static  Map<String,String> extractSchemas(Class<?> clazz) {
        // 获取字段级别的@Schema注解
        Map<String,String> data= new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Schema fieldSchema = field.getAnnotation(Schema.class);
            if (fieldSchema != null) {
                data.put(field.getName(),fieldSchema.description());
            }
        }
        return data;
    }

    public static void main(String[] args) throws Exception {
//        String jsonStr1="{\"changeBeforeObj\":{\"人民币卡中1\":[\"234232\"],\"人民币电中\":[\"234232\"]},\"changeAfterObj\":{\"人民币卡中1\":[\"234232\",\"BANK_TRANSFER\",\"23123\"]},\"columnNameMap\":{\"人民币卡中1\":\"人民币\",\"人民币电中\":\"人民币\"},\"changeType\":\"存款授权\"}";

        String jsonStr1="{\"oldValue\":\"人民币:{324:[]}\",\"path\":\"324\",\"pathName\":\"提款授权\",\"changeType\":\"提款授权\"}";

//        {"changeBeforeObj":{"人TRC20取中":["3534534"]},"changeAfterObj":{"324":[]},"columnNameMap":{"人TRC20取中":"人民币","324":"人民币","baseClounm":"提款授权"},"changeType":"提款授权"}

        Gson gson =new Gson();
        JsonDifference obj= gson.fromJson(jsonStr1, JsonDifference.class);
//        SiteInfoChangeBodyVO obj1= gson.fromJson(jsonStr1, SiteInfoChangeBodyVO.class);
//        List<JsonDifference> result = compareJson(obj1.getChangeBeforeObj(), obj1.getChangeAfterObj());
        System.out.println("递归比较结果:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
    }
}

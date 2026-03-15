package com.cloud.baowang.common.core.autocode;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class Translator {

    /**
     * 翻译文本
     *
     * @param text       待翻译的文本
     * @param sourceLang 源语言
     * @param targetLang 目标语言
     * @return 翻译后的文本
     */
    public static String translate(String text, String sourceLang, String targetLang) {
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
                + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + encodedText;


        try (HttpResponse response = HttpRequest.get(url)
                .header(Header.CONTENT_TYPE, "application/json")
                .header(Header.CACHE_CONTROL, "no-cache")
                .execute()) {
            //System.out.println(response);
            if (response.isOk()) {
                //System.out.println(response.body());
                String result = response.body();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(result);
                // 取出译文
                String translatedText = root.get(0).get(0).get(0).asText();
                //System.out.println("译文: " + translatedText);
                return translatedText;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "";

    }

    public static void main(String[] args) throws IOException {
        String text = "简单提取翻译文本";
        String translated = translate(text, "zh-CN", "en-US");
        System.out.println("Translated: " + translated);
        String translated1 = translate(text, "zh-CN", "pt-BR");//pt-BR
        System.out.println("Translated: " + translated1);
        String translated2 = translate(text, "zh-CN", "vi-VN");//vi-VN
        System.out.println("Translated: " + translated2);
        String translated3 = translate(text, "zh-CN", "ko-KR");//vi-VN
        System.out.println("Translated: " + translated3);
        String translated4 = translate(text, "zh-CN", "zh-TW");
        System.out.println("Translated: " + translated4);
        String translated5 = translate(text, "zh-CN", "zh-CN");
        System.out.println("Translated: " + translated5);
    }
}
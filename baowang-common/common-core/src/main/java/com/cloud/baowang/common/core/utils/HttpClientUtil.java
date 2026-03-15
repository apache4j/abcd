package com.cloud.baowang.common.core.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HttpClientUtil {
    public static String get(String url) {
        log.info("HttpClient.get() 请求参数:{}", url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(35000).setConnectionRequestTimeout(60000)
                    .setSocketTimeout(60000).build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error("HttpClient.get() error 请求参数:{}", url, e);
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        log.debug("HttpClient.get() 返回结果:{}", result);
        return result;
    }


    public static String doGet(String path, Map<String, Object> param, Map<String, String> headers) {
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建uri
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(path);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key).toString());
                }
            }
            URI uri = builder.build();
            // 创建http GET请求
            httpGet = new HttpGet(uri);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            throw new RuntimeException("[发送Get请求错误：]" + e.getMessage());
        } finally {
            try {
                httpGet.releaseConnection();
                response.close();
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String specialGet(String urlStr) {
        log.info("HttpClient.specialGet() 请求参数:{}", urlStr);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            URL url = new URL(urlStr);
            URI uri = new URI("https", url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), null);
            HttpGet httpGet = new HttpGet(uri);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(35000).setConnectionRequestTimeout(35000)
                    .setSocketTimeout(60000).build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error("HttpClient.specialGet() error 请求参数:{}", urlStr, e);
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        log.info("HttpClient.specialGet() 返回结果:{}", result);
        return result;
    }

    public static String doPost(String url, Map<String, Object> paramMap) {
        log.info("doPost请求参数,url:{},paramMap:{}", url, JSONObject.toJSONString(paramMap));
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(60000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue() == null ? null : mapEntry.getValue().toString()));
            }

            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("doPost请求返回结果:{}", result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            log.error("http远程请求异常,msg={}", e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static String doPostJson(String url, String json, Map<String, String> headerMap) {
        log.info("PostJson请求,url:{},请求参数:{}", url, json);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(60000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/json");
        if (CollectionUtil.isNotEmpty(headerMap)) {
            headerMap.forEach(httpPost::addHeader);
        }
        HttpEntity entity = new ByteArrayEntity(json.getBytes(StandardCharsets.UTF_8));
        httpPost.setEntity(entity);

        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("PostJson请求返回结果:{}", result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            log.error("PostJson请求异常,msg={}", e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}

package com.cloud.baowang.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.entity.XxlJobInfo;
import com.cloud.baowang.entity.XxlJobRes;
import com.cloud.baowang.job.api.vo.JobUpdateAndStartVo;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/10 20:08
 * @Version: V1.0
 **/
@Component
public class XxlJobUtil {
    private static final Logger logger= LoggerFactory.getLogger(XxlJobUtil.class);
    /**
     * 调度中心地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.admin.userName}")
    private String userName;

    @Value("${xxl.job.admin.userPass}")
    private String userPass;
    private RestTemplate restTemplate = new RestTemplate();

    private static final String LOGIN_URL = "/login";
    private static final String ADD_URL = "/jobinfo/add";
    private static final String UPDATE_URL = "/jobinfo/update";
    private static final String REMOVE_URL = "/jobinfo/remove";
    private static final String STOP_URL = "/jobinfo/stop";
    private static final String START_URL = "/jobinfo/start";

    private static final String PAGE_URL = "/jobinfo/pageList";

    private static final String JOB_GROUP_PAGE_URL = "/jobgroup/pageList";
    private String COOKIE ="";

    private Long groupId=null;

    @PostConstruct
    public void initData(){
        login();
        groupId=findGroupId();
    }


    /**
     * 登录获取 cookie
     * @throws IOException
     */
    private void login() {
        String loginUrl=adminAddresses.concat(LOGIN_URL+"?userName="+userName+"&password="+userPass);
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(loginUrl, null, String.class);
        if (stringResponseEntity.getStatusCode()== HttpStatus.OK) {
            List<String> cookies = stringResponseEntity.getHeaders().get("Set-Cookie");
            String[] cookieArray=cookies.get(0).split(";");
            StringBuilder tmpCookies = new StringBuilder();
            //for (String c : cookies) {
                tmpCookies.append(cookieArray[0]);
            //}
            COOKIE = tmpCookies.toString();
            logger.info("xxlJob 登录成功:{}",COOKIE);
        }else {
            logger.info("xxlJob 登录失败:{}",stringResponseEntity.getStatusCode());
        }
    }


    /**
     * 新建任务
     * @param jobInfo 任务信息
     * @return {"code":200,"msg":null,"content":"3"} content:任务id
     */
    public XxlJobRes add(XxlJobInfo jobInfo) {
        login();
        jobInfo.setJobGroup(groupId);
        //新建任务
        XxlJobRes xxlJobRes = JSON.parseObject(doPost(adminAddresses + ADD_URL, jobInfo), XxlJobRes.class);
        if (xxlJobRes.isSuccess()) {
            return xxlJobRes;
        } else {
            xxlJobRes = JSON.parseObject(doPost(adminAddresses + ADD_URL, jobInfo), XxlJobRes.class);
        }
        return xxlJobRes;
    }

    /**
     * 修改任务
     * @param jobInfo 任务信息
     * @return {"code":200,"msg":null,"content":null}
     */
    public XxlJobRes update(XxlJobInfo jobInfo) {
        login();
        jobInfo.setJobGroup(groupId);
        XxlJobRes xxlJobRes = JSON.parseObject(doPost(adminAddresses + UPDATE_URL, jobInfo), XxlJobRes.class);
        if (xxlJobRes.isSuccess()) {
            return xxlJobRes;
        } else {
            xxlJobRes = JSON.parseObject(doPost(adminAddresses + UPDATE_URL, jobInfo), XxlJobRes.class);
        }
        return xxlJobRes;
    }

    /**
     * 修改并且启动
     * @param jobUpdateAndStartVo 修改参数
     * @return
     */

    public String updateAndStart(JobUpdateAndStartVo jobUpdateAndStartVo) {
        login();
        XxlJobInfo xxlJobInfo = findByHandler(jobUpdateAndStartVo.getExecutorHandler());
        xxlJobInfo.setScheduleConf(jobUpdateAndStartVo.getScheduleConf());
        doPost(adminAddresses + UPDATE_URL, xxlJobInfo);
        HashMap<String, String> map = Maps.newHashMap();
        map.put("id", xxlJobInfo.getId());
        doPost(adminAddresses + START_URL, map);
        return "SUCCESS";
    }

    public XxlJobInfo findByHandler(String executorHandler) {
        String resultResp=doPost(adminAddresses + PAGE_URL+"?jobGroup="+groupId+"&triggerStatus=-1&start=0&length=10&executorHandler="+executorHandler, null);
        JSONObject resultJson= JSON.parseObject(resultResp);
        if(resultJson!=null){
            JSONArray dataArray=resultJson.getJSONArray("data");
            if(dataArray==null){
                logger.info("通过executorHandler:{}查询为空",executorHandler);
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }
            if(dataArray.size()!=1){
                logger.info("通过executorHandler:{}查询为多条",executorHandler);
                throw new BaowangDefaultException(ResultCode.DATA_EXISTS_MORE);
            }
            JSONObject rowObject=dataArray.getJSONObject(0);
            XxlJobInfo xxlJobInfo=rowObject.to(XxlJobInfo.class);
            return xxlJobInfo;
        }
        throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
    }


    public Long findGroupId() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("start", 0);
        map.put("length", 10);
        String resultResp = doPost(adminAddresses + JOB_GROUP_PAGE_URL, map);
        JSONObject resultJson= JSON.parseObject(resultResp);
        if(resultJson!=null){
            JSONArray dataArray=resultJson.getJSONArray("data");
            if(dataArray==null){
                logger.info("分组查询为空");
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }
            for(int i=0;i<=dataArray.size()-1;i++){
                JSONObject rowObject=dataArray.getJSONObject(i);
                if("job-server".equals(rowObject.getString("appname"))){
                    return rowObject.getLong("id");
                }
            }
        }
        throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
    }

    /**
     * 删除任务
     * @param jobIds 任务id
     * @return {"code":200,"msg":null,"content":null}
     */
    public XxlJobRes remove(List<String> jobIds) {
        XxlJobRes xxlJobRes = null;
        if(CollectionUtils.isEmpty(jobIds)) {
            xxlJobRes.setCode(200);
            return xxlJobRes;
        }
        login();

        for (String jobId : jobIds) {
            HashMap<String, String> map = Maps.newHashMap();
            map.put("id", jobId);
            xxlJobRes = JSON.parseObject(doPost(adminAddresses + REMOVE_URL, map), XxlJobRes.class);
            if (!xxlJobRes.isSuccess()) {
                xxlJobRes = JSON.parseObject(doPost(adminAddresses + REMOVE_URL, map), XxlJobRes.class);
            }
        }
        return xxlJobRes;
    }

    /**
     * 停止任务
     * @param jobIds 任务ids
     * @return {"code":200,"msg":null,"content":null}
     */
    public XxlJobRes stop(List<String> jobIds) {
        XxlJobRes xxlJobRes = null;
        if(CollectionUtils.isEmpty(jobIds)) {
            xxlJobRes.setCode(200);
            return xxlJobRes;
        }

        login();
        XxlJobRes res = null;
        for (String jobId : jobIds) {
            HashMap<String, String> map = Maps.newHashMap();
            map.put("id", jobId);
            res = JSON.parseObject(doPost(adminAddresses + STOP_URL, map), XxlJobRes.class);
            if (!res.isSuccess()) {
                res = JSON.parseObject(doPost(adminAddresses + STOP_URL, map), XxlJobRes.class);
            }
        }
        return res;
    }

    /**
     * 启动任务
     * @param jobIds 任务id
     * @return {"code":200,"msg":null,"content":null}
     */
    public XxlJobRes start(List<String> jobIds) {
        XxlJobRes xxlJobRes = null;
        if(CollectionUtils.isEmpty(jobIds)) {
            xxlJobRes.setCode(200);
            return xxlJobRes;
        }

        login();
        XxlJobRes res = null;
        for (String jobId : jobIds) {
            HashMap<String, String> map = Maps.newHashMap();
            map.put("id", jobId);
            res = JSON.parseObject(doPost(adminAddresses + START_URL, map), XxlJobRes.class);
            if (!res.isSuccess()) {
                res = JSON.parseObject(doPost(adminAddresses + START_URL, map), XxlJobRes.class);
            }
        }
        return res;
    }

    /**
     * 调用任务调度中心接口
     * @param url 调度中心地址
     * @param obj 入参
     * @return
     */
    public String doPost(String url, Object obj) {
        logger.info("post cookie:{}",COOKIE);
        logger.info("jobUrl:{},reqParam:{}", url, obj);
        HttpResponse response = HttpRequest
                .post(url)
                .header(HttpHeaders.COOKIE, COOKIE)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.FORM_URLENCODED.getValue())
                .form(BeanUtil.beanToMap(obj))
                .execute();
        String body = response.body();
        logger.info("resp:{}", body);
        return body;
    }


}

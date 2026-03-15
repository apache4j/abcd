package com.cloud.baowang.user.runner;


import com.cloud.baowang.user.service.GenerateUserIdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化 system-business 模块对应业务数据,生成不重复的userId
 *
 * @author wade
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AutoUserIdApplicationRunner implements ApplicationRunner {


    @Autowired
    private GenerateUserIdService generateUserIdService;

    @Override
    public void run(ApplicationArguments args) {

        generateUserIdService.initUserId();
    }


}
package com.cloud.baowang.es.sync.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.cloud.baowang.es.sync.handler.MessageHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;


@Data
@Slf4j
public abstract class AbstractCanalClient implements CanalClient {

    protected volatile boolean flag;
    protected String filter = StringUtils.EMPTY;
    protected Integer batchSize = 1;
    protected Long timeout = 1L;
    protected TimeUnit unit = TimeUnit.SECONDS;
    private Thread workThread;
    private CanalConnector connector;
    private MessageHandler messageHandler;


    @Override
    public void start() {
        log.info("start canal client");
        workThread = new Thread(this::process);
        workThread.setName("canal-client-thread");
        flag = true;
        workThread.start();
    }

    @Override
    public void stop() {
        log.info("stop canal client");
        flag = false;
    }

    @Override
    public void process() {
        log.error("canal client process No implementation");
    }
}

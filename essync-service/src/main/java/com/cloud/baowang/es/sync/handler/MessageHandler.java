package com.cloud.baowang.es.sync.handler;


import java.util.List;

public interface MessageHandler<T> {


    void handleMessage(List<T> t);
}

package com.cloud.baowang.play.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.undertow.Undertow;
import io.undertow.server.ConnectorStatistics;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class UndertowMetricsBinder implements ApplicationListener<ServletWebServerInitializedEvent>, MeterBinder {

    private static final String OBJECT_NAME = "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\"";
    //Undertow 工作队列大小  当前在 Undertow 工作线程池中的任务队列长度，也就是还没被线程处理的请求数量。这个数值大可能说明请求堆积、线程不够。
    private static final String GAUGE_NAME_WORKER_QUEUE_SIZE = "undertow_worker_queue_size";
    //Undertow 工作线程池大小 当前 Undertow 工作线程池中实际分配的线程数（可变的）。和最大线程数对比可衡量线程使用程度。
    private static final String GAUGE_NAME_WORKER_POOL_SIZE = "undertow_worker_pool_size";
    //Undertow 最大线程池大小 Undertow 配置允许的最大工作线程数。如果 pool_size 接近这个值，说明服务器线程资源快满了。
    private static final String GAUGE_NAME_MAX_WORKER_POOL_SIZE = "undertow_worker_pool_max";
    //Undertow I/O 线程数 用于处理网络 I/O（如接收连接、读取数据）的线程数量。这个是固定配置的，不负责业务逻辑，只负责网络读写。
    private static final String GAUGE_NAME_IO_THREAD_COUNT = "undertow_io_thread_count";


    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    private final List<ConnectorStatistics> stats = new ArrayList<>();

    @Override
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        if (event.getWebServer() instanceof UndertowWebServer uws) {
            Undertow undertow = extractUndertow(uws);
            if (undertow != null) {
                undertow.getListenerInfo().forEach(li -> {
                    ConnectorStatistics cs = li.getConnectorStatistics();
                    if (cs != null) {
                        stats.add(cs);
                    }
                });
            }
        }
    }

    /**
     * | 指标名                               | 中文解释                   | 使用场景                                            |
     * | --------------------------------- | ---------------------- | ----------------------------------------------- |
     * | **undertow\_connections\_active** | 当前活跃连接数（正在使用的 TCP 连接数） | 反映瞬时并发连接压力；当数值过高时，说明客户端同时占用的连接多，可能需要调优线程池或负载均衡。 |
     * | **undertow\_connections\_peak**   | 历史峰值连接数（启动以来的最大活跃连接数）  | 用于容量规划和历史最大负载分析，帮助估算服务的最大承载能力。                  |
     * | **undertow\_requests\_total**     | 累计处理的 HTTP 请求总数        | 衡量服务累计吞吐量，可配合时间区间计算 QPS（请求数/秒）。                 |
     * | **undertow\_bytes\_sent**         | 累计发送给客户端的字节数           | 用于分析服务的输出流量，配合 `requests_total` 可以推算平均响应大小。     |
     * | **undertow\_bytes\_received**     | 累计从客户端接收的字节数           | 用于分析服务的输入流量，配合 API 类型可帮助定位大流量接口。                |
     * | **undertow\_errors\_total**       | 累计请求错误数（如返回 5xx 错误）    | 服务健康度监控的重要指标；当数值快速增加时，需排查应用或下游依赖问题。             |
     *
     * @param registry
     */
    @Override
    public void bindTo(MeterRegistry registry) {

        registerGauge(GAUGE_NAME_WORKER_QUEUE_SIZE, "WorkerQueueSize", "Undertow worker queue size", registry);
        registerGauge(GAUGE_NAME_WORKER_POOL_SIZE, "CoreWorkerPoolSize", "Undertow worker pool size", registry);
        registerGauge(GAUGE_NAME_MAX_WORKER_POOL_SIZE, "MaxWorkerPoolSize", "Undertow max worker pool size", registry);
        registerGauge(GAUGE_NAME_IO_THREAD_COUNT, "IoThreadCount", "Undertow IO thread count", registry);


        Gauge.builder("undertow_connections_active", stats, s -> s.stream().mapToLong(ConnectorStatistics::getActiveConnections).sum())
                .description("Current active connections").register(registry);

        Gauge.builder("undertow_connections_peak", stats, s -> s.stream().mapToLong(ConnectorStatistics::getMaxActiveConnections).sum())
                .description("Peak active connections since start").register(registry);

        Gauge.builder("undertow_requests_total", stats, s -> s.stream().mapToLong(ConnectorStatistics::getRequestCount).sum())
                .description("Total requests handled").register(registry);

        Gauge.builder("undertow_bytes_sent", stats, s -> s.stream().mapToLong(ConnectorStatistics::getBytesSent).sum())
                .description("Total bytes sent").register(registry);

        Gauge.builder("undertow_bytes_received", stats, s -> s.stream().mapToLong(ConnectorStatistics::getBytesReceived).sum())
                .description("Total bytes received").register(registry);

        Gauge.builder("undertow_errors_total", stats, s -> s.stream().mapToLong(ConnectorStatistics::getErrorCount).sum())
                .description("Total request errors").register(registry);


    }

    private Undertow extractUndertow(UndertowWebServer server) {
        try {
            Field f = UndertowWebServer.class.getDeclaredField("undertow");
            f.setAccessible(true);
            return (Undertow) f.get(server);
        } catch (Exception e) {
            return null;
        }
    }

    private void registerGauge(String metricName, String attributeName, String description, MeterRegistry registry) {
        Gauge.builder(metricName, mBeanServer,
                        server -> {
                            try {
                                Object value = server.getAttribute(workerObjectName(), attributeName);
                                return ((Number) value).doubleValue();
                            } catch (Exception e) {
                                return 0d;
                            }
                        })
                .description(description)
                .register(registry);
    }

    private ObjectName workerObjectName() throws MalformedObjectNameException {
        return new ObjectName(OBJECT_NAME);
    }
}

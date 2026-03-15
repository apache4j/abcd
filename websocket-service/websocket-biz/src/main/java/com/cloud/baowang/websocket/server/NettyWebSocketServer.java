package com.cloud.baowang.websocket.server;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.config.WsThreadPoolConfigProperties;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Properties;


@Slf4j
@Configuration
@DependsOn("redisson")
public class NettyWebSocketServer {

    @Value("${netty.port:8090}")
    private Integer PORT;
    @Value("${netty.application.name}")
    private String SERVER_NAME;
    @Value("${spring.cloud.client.ip-address}")
    private String ipAddress;
    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Resource
    private WsThreadPoolConfigProperties properties;
    public static final NettyWebSocketServerHandler NETTY_WEB_SOCKET_SERVER_HANDLER = new NettyWebSocketServerHandler();
    public static final HeartbeatHandler NETTY_HEARTBEAT_HANDLER = new HeartbeatHandler();
    // 创建线程池执行器
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
     * 启动 ws server
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        int cpuCoreNum=NettyRuntime.availableProcessors();
        bossGroup = new NioEventLoopGroup(cpuCoreNum);
        workerGroup = new NioEventLoopGroup(cpuCoreNum*2);
        run();
        log.info("启动 ws server 成功,cpu核数:{}",cpuCoreNum);

    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workerGroup.shutdownGracefully();
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("关闭 ws server 成功");
    }

    public void run() throws InterruptedException {
        // 服务器启动引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 65535)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO)) // 为 bossGroup 添加 日志处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //30秒客户端没有向服务器发送心跳则关闭连接
                        pipeline.addLast(new IdleStateHandler(30, 0, 0));
                        // 因为使用http协议，所以需要使用http的编码器，解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 以块方式写，添加 chunkedWriter 处理器
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * 说明：
                         *  1. http数据在传输过程中是分段的，HttpObjectAggregator可以把多个段聚合起来；
                         *  2. 这就是为什么当浏览器发送大量数据时，就会发出多次 http请求的原因
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        // http头处理
                        pipeline.addLast(new HttpAttrHandler());
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // 自定义handler ，处理业务逻辑
                        pipeline.addLast(NETTY_HEARTBEAT_HANDLER);
                        pipeline.addLast(NETTY_WEB_SOCKET_SERVER_HANDLER);
                    }
                });
        //
        registerNamingService(SERVER_NAME, PORT);
        // 启动服务器，监听端口，阻塞直到启动成功
        serverBootstrap.bind(PORT).sync();
    }

    /**
     * 将Netty服务注册进Nacos
     *
     * @param nettyName 服务名称
     * @param nettyPort 服务端口号
     */
    private void registerNamingService(String nettyName, Integer nettyPort) {
        try {
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
            properties.setProperty(PropertyKeyConst.NAMESPACE, nacosDiscoveryProperties.getNamespace());
            properties.setProperty(PropertyKeyConst.USERNAME, nacosDiscoveryProperties.getUsername());
            properties.setProperty(PropertyKeyConst.PASSWORD, nacosDiscoveryProperties.getPassword());
            NamingService namingService = NamingFactory.createNamingService(properties);
            namingService.registerInstance(nettyName, nacosDiscoveryProperties.getGroup(), ipAddress, nettyPort);
            log.info("netty-server => nacos 注册成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

package com.cloud.baowang.common.excel;


import cn.hutool.extra.spring.SpringUtil;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.data.transfer.i18n.I18nResponseAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 并发工具类（并发生产数据，串行有序消费数据）
 *
 * @param <R,P>
 */
@Slf4j
public class ParallelUtil<R, P extends PageVO> {
    public static final int DEF_PARALLEL_NUM = Runtime.getRuntime().availableProcessors(); // 默认线程数
    // i18n多语言
    public static I18nResponseAdvice i18nResponseAdvice;

    static {
        i18nResponseAdvice = SpringUtil.getBean(I18nResponseAdvice.class);
    }
    /**
     * 查询入参
     */
    private P param;
    private Class<P> paramClass;
    private int parallelNum; // 生产者并发线程数
    private long totalNum; // 总任务数
    private Consumer<R> resultConsumer; // 消费者函数
    private PageFunction<R, P> producerFunction; // 生产者函数
    private ThreadPoolExecutor threadPoolExecutor; // 生产者线程池

    private ArrayBlockingQueue<ParallelResult<R>> queue; // 生产者将任务放到此队列，消费者从此队列读数据
    private long timeout = 60; // 默认超时时间
    private TimeUnit timeoutTimeUnit = TimeUnit.SECONDS; // 默认超时时间单位

    /**
     * 并发数默认为核心数
     *
     * @param consumerClass
     * @param totalNum
     * @param <R>
     * @param <P>
     * @return
     */
    public static <R, P extends PageVO> ParallelUtil<R, P> parallel(Class<R> consumerClass, long totalNum) {
        return parallel(consumerClass, DEF_PARALLEL_NUM, totalNum);
    }

    /**
     * 初始化
     *
     * @param consumerClass 消费的类Class
     * @param parallelNum   并发线程数
     * @param totalNum      并发执行总数（触发asyncProducer函数次数）
     * @param <R,P>
     * @return
     */
    public static <R, P extends PageVO> ParallelUtil<R, P> parallel(Class<R> consumerClass, int parallelNum, long totalNum) {
        ParallelUtil<R, P> parallelUtil = new ParallelUtil<>();
        parallelUtil.parallelNum = (int) Math.max(1, Math.min(parallelNum, totalNum));
        parallelUtil.totalNum = totalNum;
        return parallelUtil;
    }

    /**
     * 异步并发生产者
     *
     * @param producerFunction 生产者函数，参数为1~totalNum，返回值为任意类型
     * @return
     */
    public ParallelUtil<R, P> asyncProducer(PageFunction<R, P> producerFunction) {
        this.producerFunction = producerFunction;
        return this;
    }

    /**
     * 异步并发生产者
     *
     * @param ，参数为1~totalNum，返回值为任意类型
     * @return
     */
    public ParallelUtil<R, P> param(P param) {
        this.param = param;
        this.paramClass = (Class<P>) param.getClass();
        return this;
    }

    /**
     * 消费者(串行有序消费生产者返回的数据)
     *
     * @param resultConsumer
     * @return
     */
    public ParallelUtil<R, P> syncConsumer(Consumer<R> resultConsumer) {
        this.resultConsumer = resultConsumer;
        return this;
    }

    /**
     * 开始执行
     *
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        try {
            if (totalNum <= 0) { // 如果无任务则直接返回
                return;
            }
            if (totalNum == 1) { // 如果只有一个任务，则串行执行，生产者生成的数据直接给到消费者
                param.setPageNumber(1);
                R apply = producerFunction.apply(param);
                i18nResponseAdvice.switchLanguage(apply, LocaleContextHolder.getLocale());
                resultConsumer.accept(apply);
                return;
            }
            // 初始化队列和线程池
            queue = new ArrayBlockingQueue<>(parallelNum);
            Locale locale = LocaleContextHolder.getLocale();
            RequestAttributes holder = CurrReqUtils.getHolder();
            threadPoolExecutor = new ThreadPoolExecutor(1, parallelNum, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
            // 生产者开始执行
            Thread producerThread = new Thread(() -> {
                try {
                    AtomicLong indexAtomicLong = new AtomicLong(1);
                    List<CompletableFuture<R>> futureList = new ArrayList<>(parallelNum);
                    for (long index = 1; index <= totalNum; index++) {
                        param.setPageNumber((int) index);
                        // 多线程入参必须每次实例化
                        P instance = paramClass.getDeclaredConstructor().newInstance();
                        BeanUtils.copyProperties(param, instance);
                        futureList.add(CompletableFuture.supplyAsync(() -> {
                            CurrReqUtils.setHolder(holder);
                            log.info("查询入参:pageNum:{},{}", instance.getPageNumber(), instance);
                            R apply = producerFunction.apply(instance);
                            // i18n多语言翻译
                            i18nResponseAdvice.switchLanguage(apply, locale);
//                            log.info("查询返回:{}", apply);
                            CurrReqUtils.resetHolder();
                            return apply;
                        }, threadPoolExecutor));
                        if (futureList.size() == parallelNum) {
                            for (CompletableFuture<R> future : futureList) {
                                queue.put(new ParallelResult<>(indexAtomicLong.getAndIncrement(), future.join()));
                            }
                            futureList.clear();
                        }
                    }
                    for (CompletableFuture<R> future : futureList) {
                        queue.put(new ParallelResult<>(indexAtomicLong.getAndIncrement(), future.join()));
                    }
                    futureList.clear();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    queue.offer(ParallelResult.empty()); // 添加一个空元素，防止queue.poll等待到超时
                    throw new RuntimeException(e);
                } catch (BaowangDefaultException e) {
                    queue.offer(ParallelResult.empty()); // 添加一个空元素，防止queue.poll等待到超时
                    throw e;
                } catch (Exception e) {
                    queue.offer(ParallelResult.empty()); // 添加一个空元素，防止queue.poll等待到超时
                    throw new RuntimeException(e);
                }
            });
            producerThread.setDaemon(true);
            producerThread.start();
            AtomicReference<Throwable> exception = new AtomicReference<>();
            producerThread.setUncaughtExceptionHandler((t, e) -> exception.set(e));
            // 消费者等待消费
            AtomicLong count = new AtomicLong();
            ParallelResult<R> parallelResult;
            while ((parallelResult = queue.poll(timeout, timeoutTimeUnit)) != null) { // 消费者等待消费
                if (parallelResult.isEmpty()) {
                    break;
                } // 异常时添加的空元素则直接return
                resultConsumer.accept(parallelResult.getData()); // 消费者消费生产者生产的数据
                count.incrementAndGet();
                if (parallelResult.getIndex() == totalNum) {
                    break;
                } // 已最后一条，直接结束，queue.poll等待问题
            }
            if (count.get() != totalNum) {
                log.info("导出:{},totalNum:{}", count.get(), totalNum);
                throw new BaowangDefaultException(exception.get() == null ? "timeout" : exception.get().getMessage());
            }
        } finally {
            if (threadPoolExecutor != null) {
                threadPoolExecutor.shutdown();
            }
        }
    }

}

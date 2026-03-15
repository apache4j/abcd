package com.cloud.baowang.common.core.utils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class ProbabilityGenerator<T> {
    private final NavigableMap<BigDecimal, T> map = new TreeMap<>();
    private final Random random;
    private BigDecimal totalProbability = BigDecimal.ZERO;

    public ProbabilityGenerator() {
        this(new Random());
    }

    public ProbabilityGenerator(Random random) {
        this.random = random;
    }

    /**
     * 添加一个带有概率的值
     *
     * @param value       值
     * @param probability 该值出现的概率，保留两位小数
     */
    public void addEntry(T value, BigDecimal probability) {
        if (probability.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("概率必须大于 0");
        }

        // 更新总概率并将条目添加到映射中
        totalProbability = totalProbability.add(probability);
        map.put(totalProbability, value);
    }

    /**
     * 根据概率生成一个值
     *
     * @return 生成的值
     */
    public T getRandomValue() {
        BigDecimal randomValue = BigDecimal.valueOf(random.nextDouble()).multiply(totalProbability);
        return map.higherEntry(randomValue).getValue();
    }

    /**
     * 保留两位小数的方法
     *
     * @param value 待处理的 BigDecimal 值
     * @return 保留两位小数的值
     */
    private BigDecimal roundToTwoDecimals(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}


package com.cloud.baowang.common.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class ProbabilityRangeGenerator {
    private final NavigableMap<BigDecimal, BigDecimal[]> map = new TreeMap<>();
    private final Random random;
    private BigDecimal totalProbability = BigDecimal.ZERO;

    public ProbabilityRangeGenerator() {
        this(new Random());
    }

    public ProbabilityRangeGenerator(Random random) {
        this.random = random;
    }

    /**
     * 添加一个带有概率的区间 [min, max]
     *
     * @param min         区间的最小值，保留两位小数
     * @param max         区间的最大值，保留两位小数
     * @param probability 该区间的生成概率，保留两位小数
     */
    public void addRange(BigDecimal min, BigDecimal max, BigDecimal probability) {
        if (probability.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("概率必须大于 0");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("区间最小值必须小于或等于最大值");
        }
        totalProbability = totalProbability.add(probability);
        map.put(totalProbability, new BigDecimal[]{min, max});
    }

    /**
     * 根据概率生成区间内的随机值，保留两位小数
     *
     * @return 生成的随机数
     */
    public BigDecimal getRandomValue() {
        BigDecimal randomValue = BigDecimal.valueOf(random.nextDouble()).multiply(totalProbability);
        BigDecimal[] range = map.higherEntry(randomValue).getValue();

        // 在指定的区间内生成随机数
        BigDecimal rangeMin = range[0];
        BigDecimal rangeMax = range[1];
        BigDecimal randomInRange = rangeMin.add(rangeMax.subtract(rangeMin)
                .multiply(BigDecimal.valueOf(random.nextDouble())));

        // 保留两位小数
        return randomInRange.setScale(2, RoundingMode.HALF_DOWN);
    }
}


package io.github.jacorycyjin.smartlibrary.backend.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 评分计算工具类
 * 
 * @author Jacory
 * @date 2025/03/03
 */
public class ScoreCalculator {

    /**
     * 计算综合评分（动态权重算法）
     * 
     * 算法说明：
     * 1. 当用户评分人数较少时（<10人），豆瓣评分权重更高
     * 2. 当用户评分人数较多时（>=100人），用户评分权重更高
     * 3. 权重动态调整，确保评分更合理
     * 
     * 权重计算公式：
     * - 用户评分权重 = min(0.7, 评分人数 / 150)
     * - 豆瓣评分权重 = 1 - 用户评分权重
     * 
     * @param sourceScore 豆瓣评分（0-10分）
     * @param userScore 用户平均评分（0-10分）
     * @param userScoreCount 用户评分人数
     * @return 综合评分（保留1位小数）
     */
    public static BigDecimal calculateFinalScore(BigDecimal sourceScore, BigDecimal userScore, Integer userScoreCount) {
        // 如果两个评分都为空或0，返回0
        if (isScoreEmpty(sourceScore) && isScoreEmpty(userScore)) {
            return BigDecimal.ZERO;
        }

        // 只有豆瓣评分
        if (!isScoreEmpty(sourceScore) && isScoreEmpty(userScore)) {
            return sourceScore.setScale(1, RoundingMode.HALF_UP);
        }

        // 只有用户评分
        if (isScoreEmpty(sourceScore) && !isScoreEmpty(userScore)) {
            return userScore.setScale(1, RoundingMode.HALF_UP);
        }

        // 两个评分都有，按动态权重计算
        // 计算用户评分权重（最大0.7，即用户评分最多占70%）
        int count = userScoreCount != null ? userScoreCount : 0;
        double userWeightValue = Math.min(0.7, count / 150.0);
        double sourceWeightValue = 1.0 - userWeightValue;

        BigDecimal userWeight = BigDecimal.valueOf(userWeightValue);
        BigDecimal sourceWeight = BigDecimal.valueOf(sourceWeightValue);

        BigDecimal sourceWeighted = sourceScore.multiply(sourceWeight);
        BigDecimal userWeighted = userScore.multiply(userWeight);
        
        return sourceWeighted.add(userWeighted).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 判断评分是否为空
     */
    private static boolean isScoreEmpty(BigDecimal score) {
        return score == null || score.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 验证评分范围（0-10分）
     * 
     * @param score 评分
     * @return 是否有效
     */
    public static boolean isValidScore(BigDecimal score) {
        if (score == null) {
            return false;
        }
        return score.compareTo(BigDecimal.ZERO) >= 0 
            && score.compareTo(BigDecimal.TEN) <= 0;
    }
}

package dev.bass631.spendy.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record StatisticsResponse(
        BigDecimal totalAmount,
        List<CategoryStat> categoryStats
) {
    public record CategoryStat(
            String categoryName,
            BigDecimal total,
            long count
    ) {
    }
}

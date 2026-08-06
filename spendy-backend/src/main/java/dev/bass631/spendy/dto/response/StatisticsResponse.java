package dev.bass631.spendy.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StatisticsResponse(
        BigDecimal totalAmount,
        List<CategoryStat> categoryStats
) {
    public record CategoryStat(
            UUID categoryId,
            String categoryName,
            BigDecimal total,
            long count
    ) {
    }
}

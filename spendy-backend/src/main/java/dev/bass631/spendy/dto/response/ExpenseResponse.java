package dev.bass631.spendy.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        String description,
        String createdBy,
        LocalDateTime createdAt
) {
}

package dev.bass631.spendy.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateExpenseRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @Size(max = 1000) String description
) {
}

package dev.bass631.spendy.dto.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        long usageCount
) {
}

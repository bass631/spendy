package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.response.StatisticsResponse;
import dev.bass631.spendy.repository.ExpenseRepository;
import dev.bass631.spendy.util.XlsxExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ExpenseRepository expenseRepository;
    private final XlsxExportUtil xlsxExportUtil;

    @Override
    public StatisticsResponse getStatistics(String period, LocalDate from, LocalDate to) {
        LocalDateTime periodFrom = resolveFrom(period, from);
        LocalDateTime periodTo = resolveTo(period, to);
        BigDecimal total = expenseRepository.getTotalByPeriod(periodFrom, periodTo);
        List<ExpenseRepository.CategoryStatProjection> stats =
                expenseRepository.getCategoryStatsByPeriod(periodFrom, periodTo);
        List<StatisticsResponse.CategoryStat> categoryStats = stats.stream()
                .map(s -> new StatisticsResponse.CategoryStat(s.getCategory(), s.getTotal(), s.getCount()))
                .toList();
        return new StatisticsResponse(total, categoryStats);
    }

    @Override
    public byte[] exportStatistics(String period, LocalDate from, LocalDate to) {
        StatisticsResponse stats = getStatistics(period, from, to);
        return xlsxExportUtil.exportStatistics(stats);
    }

    private LocalDateTime resolveFrom(String period, LocalDate from) {
        if ("custom".equals(period) && from != null) {
            return from.atStartOfDay();
        }
        return switch (period) {
            case "day" -> LocalDate.now().atStartOfDay();
            case "week" -> LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            case "month" -> LocalDate.now().withDayOfMonth(1).atStartOfDay();
            case "year" -> LocalDate.now().withDayOfYear(1).atStartOfDay();
            default -> LocalDateTime.of(1970, 1, 1, 0, 0);
        };
    }

    private LocalDateTime resolveTo(String period, LocalDate to) {
        if ("custom".equals(period) && to != null) {
            return to.atTime(LocalTime.MAX);
        }
        return LocalDateTime.now();
    }
}

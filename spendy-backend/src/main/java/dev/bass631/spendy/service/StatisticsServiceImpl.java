package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.response.StatisticsResponse;
import dev.bass631.spendy.repository.ExpenseRepository;
import dev.bass631.spendy.util.PeriodResolver;
import dev.bass631.spendy.util.XlsxExportUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final ExpenseRepository expenseRepository;
    private final XlsxExportUtil xlsxExportUtil;

    @Override
    public StatisticsResponse getStatistics(String period, LocalDate from, LocalDate to) {
        LocalDateTime periodFrom = PeriodResolver.resolveFrom(period, from);
        LocalDateTime periodTo = PeriodResolver.resolveTo(period, to);
        BigDecimal total = expenseRepository.getTotalByPeriod(periodFrom, periodTo);
        List<ExpenseRepository.CategoryStatProjection> stats =
                expenseRepository.getCategoryStatsByPeriod(periodFrom, periodTo);
        List<StatisticsResponse.CategoryStat> categoryStats = stats.stream()
                .map(s -> new StatisticsResponse.CategoryStat(s.getId(), s.getCategory(), s.getTotal(), s.getCount()))
                .toList();
        return new StatisticsResponse(total, categoryStats);
    }

    @Override
    public byte[] exportStatistics(String period, LocalDate from, LocalDate to) {
        StatisticsResponse stats = getStatistics(period, from, to);
        return xlsxExportUtil.exportStatistics(stats);
    }
}

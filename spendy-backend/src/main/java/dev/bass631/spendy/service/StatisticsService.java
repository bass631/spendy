package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.response.StatisticsResponse;

import java.time.LocalDate;

public interface StatisticsService {
    StatisticsResponse getStatistics(String period, LocalDate from, LocalDate to);
    byte[] exportStatistics(String period, LocalDate from, LocalDate to);
}

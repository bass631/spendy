package dev.bass631.spendy.controller;

import dev.bass631.spendy.dto.request.StatisticsQuery;
import dev.bass631.spendy.dto.response.StatisticsResponse;
import dev.bass631.spendy.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics(StatisticsQuery query) {
        LocalDate from = query.from();
        LocalDate to = query.to();
        StatisticsResponse response = statisticsService.getStatistics(query.period(), from, to);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStatistics(StatisticsQuery query) {
        LocalDate from = query.from();
        LocalDate to = query.to();
        byte[] data = statisticsService.exportStatistics(query.period(), from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistics.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}

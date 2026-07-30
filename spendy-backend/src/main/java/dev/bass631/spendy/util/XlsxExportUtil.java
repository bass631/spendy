package dev.bass631.spendy.util;

import dev.bass631.spendy.dto.response.StatisticsResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class XlsxExportUtil {

    public byte[] exportStatistics(StatisticsResponse statistics) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Statistics");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Category");
            headerRow.createCell(1).setCellValue("Total");
            headerRow.createCell(2).setCellValue("Count");

            int rowNum = 1;
            for (var stat : statistics.categoryStats()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat.categoryName());
                row.createCell(1).setCellValue(stat.total().doubleValue());
                row.createCell(2).setCellValue(stat.count());
            }

            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("Total");
            totalRow.createCell(1).setCellValue(statistics.totalAmount().doubleValue());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export statistics to xlsx", e);
        }
    }
}

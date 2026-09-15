package com.example.reportingapi.controller.report;

import com.example.common.entity.enums.TransactionType;
import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;
import com.example.common.mappers.responses.YearlyReportView;
import com.example.common.util.RestApiResponse;
import com.example.reportingapi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public RestApiResponse<MonthlySummaryView> getMonthlyReport(
            @RequestParam Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return new RestApiResponse<>("200", "Success",
                reportService.getMonthlySummary(userId, accountId, month, year));
    }

    @GetMapping("/summary")
    public RestApiResponse<MonthlySummaryView> getMonthlySummary(
            @RequestParam Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return getMonthlyReport(userId, accountId, month, year);
    }

    @GetMapping("/yearly")
    public RestApiResponse<YearlyReportView> getYearlyReport(
            @RequestParam Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Integer year) {
        return new RestApiResponse<>("200", "Success",
                reportService.getYearlyReport(userId, accountId, year));
    }

    @GetMapping("/category-breakdown")
    public RestApiResponse<CategoryBreakdownView> getCategoryBreakdown(
            @RequestParam Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return new RestApiResponse<>("200", "Success",
                reportService.getCategoryBreakdown(userId, accountId, type, month, year));
    }

}

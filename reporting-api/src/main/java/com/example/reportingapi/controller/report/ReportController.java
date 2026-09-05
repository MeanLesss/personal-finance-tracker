package com.example.reportingapi.controller.report;

import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;
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

    @GetMapping("/summary")
    public RestApiResponse<MonthlySummaryView> getMonthlySummary(
            @RequestParam Long userId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return new RestApiResponse<>("200", "Success", reportService.getMonthlySummary(userId, month, year));
    }

    @GetMapping("/category-breakdown")
    public RestApiResponse<CategoryBreakdownView> getCategoryBreakdown(
            @RequestParam Long userId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return new RestApiResponse<>("200", "Success", reportService.getCategoryBreakdown(userId, month, year));
    }

}

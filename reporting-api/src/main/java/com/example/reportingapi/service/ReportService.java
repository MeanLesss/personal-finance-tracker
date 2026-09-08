package com.example.reportingapi.service;

import com.example.common.entity.enums.TransactionType;
import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;
import com.example.common.mappers.responses.YearlyReportView;

public interface ReportService {

    MonthlySummaryView getMonthlySummary(Long userId, Integer month, Integer year);

    MonthlySummaryView getMonthlySummary(Long userId, Long accountId, Integer month, Integer year);

    YearlyReportView getYearlyReport(Long userId, Long accountId, Integer year);

    CategoryBreakdownView getCategoryBreakdown(Long userId, Integer month, Integer year);

    CategoryBreakdownView getCategoryBreakdown(Long userId, Long accountId, TransactionType type, Integer month, Integer year);

}

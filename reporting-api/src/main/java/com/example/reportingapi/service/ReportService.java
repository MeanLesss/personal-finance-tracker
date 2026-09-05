package com.example.reportingapi.service;

import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;

public interface ReportService {

    MonthlySummaryView getMonthlySummary(Long userId, Integer month, Integer year);

    CategoryBreakdownView getCategoryBreakdown(Long userId, Integer month, Integer year);

}

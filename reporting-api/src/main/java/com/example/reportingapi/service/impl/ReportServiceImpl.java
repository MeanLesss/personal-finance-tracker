package com.example.reportingapi.service.impl;

import com.example.common.entity.enums.TransactionType;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.responses.CategoryBreakdownItem;
import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;
import com.example.common.repository.UserRepository;
import com.example.reportingapi.repository.ReportingTransactionRepository;
import com.example.reportingapi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportingTransactionRepository reportingTransactionRepository;
    private final UserRepository userRepository;

    @Override
    public MonthlySummaryView getMonthlySummary(Long userId, Integer month, Integer year) {
        verifyUser(userId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal totalIncome = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, startDate, endDate);
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }

        BigDecimal totalExpense = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startDate, endDate);
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        double savingsRate = (totalIncome.compareTo(BigDecimal.ZERO) > 0)
                ? netSavings.multiply(BigDecimal.valueOf(100))
                        .divide(totalIncome, 2, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        return new MonthlySummaryView(userId, month, year, totalIncome, totalExpense, netSavings, savingsRate);
    }

    @Override
    public CategoryBreakdownView getCategoryBreakdown(Long userId, Integer month, Integer year) {
        verifyUser(userId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal totalExpense = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, startDate, endDate);
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        List<Object[]> rows = reportingTransactionRepository.sumExpenseGroupedByCategory(userId, startDate, endDate);
        List<CategoryBreakdownItem> items = new ArrayList<>();

        for (Object[] row : rows) {
            Long categoryId = ((Number) row[0]).longValue();
            String categoryName = (String) row[1];
            BigDecimal totalSpent = (BigDecimal) row[2];

            double percentage = (totalExpense.compareTo(BigDecimal.ZERO) > 0)
                    ? totalSpent.multiply(BigDecimal.valueOf(100))
                            .divide(totalExpense, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            items.add(new CategoryBreakdownItem(categoryId, categoryName, totalSpent, percentage));
        }

        return new CategoryBreakdownView(userId, month, year, totalExpense, items);
    }

    private void verifyUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }

}

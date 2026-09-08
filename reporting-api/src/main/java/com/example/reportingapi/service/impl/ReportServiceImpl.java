package com.example.reportingapi.service.impl;

import com.example.common.entity.Account;
import com.example.common.entity.enums.TransactionType;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.responses.CategoryBreakdownItem;
import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;
import com.example.common.mappers.responses.MonthlyTrendItem;
import com.example.common.mappers.responses.YearlyReportView;
import com.example.common.repository.UserRepository;
import com.example.reportingapi.repository.ReportingAccountRepository;
import com.example.reportingapi.repository.ReportingTransactionRepository;
import com.example.reportingapi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportingTransactionRepository reportingTransactionRepository;
    private final ReportingAccountRepository reportingAccountRepository;
    private final UserRepository userRepository;

    @Override
    public MonthlySummaryView getMonthlySummary(Long userId, Integer month, Integer year) {
        return getMonthlySummary(userId, null, month, year);
    }

    @Override
    public MonthlySummaryView getMonthlySummary(Long userId, Long accountId, Integer month, Integer year) {
        if (userId != null) {
            verifyUser(userId);
        }
        Account account = null;
        if (accountId != null) {
            account = verifyAccount(accountId);
        }

        LocalDate now = LocalDate.now();
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        LocalDate startDate = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        String monthName = Month.of(targetMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        BigDecimal totalIncome;
        BigDecimal totalExpense;
        Integer totalTx;
        Integer incomeTx;
        Integer expenseTx;

        if (accountId != null) {
            totalIncome = reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                    accountId, TransactionType.INCOME, startDate, endDate);
            totalExpense = reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                    accountId, TransactionType.EXPENSE, startDate, endDate);
            totalTx = reportingTransactionRepository.countByAccountIdAndDateBetween(
                    accountId, startDate, endDate);
            incomeTx = reportingTransactionRepository.countByAccountIdAndTypeAndDateBetween(
                    accountId, TransactionType.INCOME, startDate, endDate);
            expenseTx = reportingTransactionRepository.countByAccountIdAndTypeAndDateBetween(
                    accountId, TransactionType.EXPENSE, startDate, endDate);
        } else {
            totalIncome = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.INCOME, startDate, endDate);
            totalExpense = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.EXPENSE, startDate, endDate);
            totalTx = reportingTransactionRepository.countByUserIdAndDateBetween(
                    userId, startDate, endDate);
            incomeTx = reportingTransactionRepository.countByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.INCOME, startDate, endDate);
            expenseTx = reportingTransactionRepository.countByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.EXPENSE, startDate, endDate);
        }

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        double savingsRate = calculateRate(netSavings, totalIncome);

        String accountName = (account != null) ? account.getAccountName() : "All Accounts";
        String currency = (account != null && account.getCurrency() != null) ? account.getCurrency() : "USD";

        return MonthlySummaryView.builder()
                .month(targetMonth)
                .monthName(monthName)
                .year(targetYear)
                .startDate(startDate)
                .endDate(endDate)
                .userId(userId)
                .accountId(accountId)
                .accountName(accountName)
                .currency(currency)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .totalTransactions(totalTx != null ? totalTx : 0)
                .incomeTransactions(incomeTx != null ? incomeTx : 0)
                .expenseTransactions(expenseTx != null ? expenseTx : 0)
                .build();
    }

    @Override
    public YearlyReportView getYearlyReport(Long userId, Long accountId, Integer year) {
        if (userId != null) {
            verifyUser(userId);
        }
        Account account = null;
        if (accountId != null) {
            account = verifyAccount(accountId);
        }
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        BigDecimal annualIncome = BigDecimal.ZERO;
        BigDecimal annualExpense = BigDecimal.ZERO;
        int totalTransactions = 0;

        List<MonthlyTrendItem> monthlyTrends = new ArrayList<>();
        String highestSpendingMonth = "None";
        BigDecimal maxExpense = BigDecimal.ZERO;
        String highestIncomeMonth = "None";
        BigDecimal maxIncome = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            LocalDate mStart = LocalDate.of(targetYear, m, 1);
            LocalDate mEnd = mStart.withDayOfMonth(mStart.lengthOfMonth());

            BigDecimal mIncome;
            BigDecimal mExpense;
            Integer mCount;

            if (accountId != null) {
                mIncome = reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                        accountId, TransactionType.INCOME, mStart, mEnd);
                mExpense = reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                        accountId, TransactionType.EXPENSE, mStart, mEnd);
                mCount = reportingTransactionRepository.countByAccountIdAndDateBetween(
                        accountId, mStart, mEnd);
            } else {
                mIncome = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                        userId, TransactionType.INCOME, mStart, mEnd);
                mExpense = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                        userId, TransactionType.EXPENSE, mStart, mEnd);
                mCount = reportingTransactionRepository.countByUserIdAndDateBetween(
                        userId, mStart, mEnd);
            }

            mIncome = mIncome != null ? mIncome : BigDecimal.ZERO;
            mExpense = mExpense != null ? mExpense : BigDecimal.ZERO;
            int count = mCount != null ? mCount : 0;

            BigDecimal mNet = mIncome.subtract(mExpense);
            double mRate = calculateRate(mNet, mIncome);

            String monthName = Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            if (mExpense.compareTo(maxExpense) > 0) {
                maxExpense = mExpense;
                highestSpendingMonth = monthName;
            }
            if (mIncome.compareTo(maxIncome) > 0) {
                maxIncome = mIncome;
                highestIncomeMonth = monthName;
            }

            annualIncome = annualIncome.add(mIncome);
            annualExpense = annualExpense.add(mExpense);
            totalTransactions += count;

            monthlyTrends.add(MonthlyTrendItem.builder()
                    .month(m)
                    .monthName(monthName)
                    .income(mIncome)
                    .expense(mExpense)
                    .netSavings(mNet)
                    .savingsRate(mRate)
                    .transactionCount(count)
                    .build());
        }

        BigDecimal annualNet = annualIncome.subtract(annualExpense);
        double annualSavingsRate = calculateRate(annualNet, annualIncome);

        LocalDate yearStart = LocalDate.of(targetYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(targetYear, 12, 31);

        List<CategoryBreakdownItem> topExpenses = extractTopCategories(userId, accountId, TransactionType.EXPENSE, yearStart, yearEnd, annualExpense, 5);
        List<CategoryBreakdownItem> topIncomes = extractTopCategories(userId, accountId, TransactionType.INCOME, yearStart, yearEnd, annualIncome, 5);

        String accountName = (account != null) ? account.getAccountName() : "All Accounts";
        String currency = (account != null && account.getCurrency() != null) ? account.getCurrency() : "USD";

        return YearlyReportView.builder()
                .year(targetYear)
                .userId(userId)
                .accountId(accountId)
                .accountName(accountName)
                .currency(currency)
                .totalIncome(annualIncome)
                .totalExpense(annualExpense)
                .netSavings(annualNet)
                .savingsRate(annualSavingsRate)
                .totalTransactions(totalTransactions)
                .highestSpendingMonth(highestSpendingMonth)
                .highestIncomeMonth(highestIncomeMonth)
                .monthlyBreakdown(monthlyTrends)
                .topExpenseCategories(topExpenses)
                .topIncomeCategories(topIncomes)
                .build();
    }

    @Override
    public CategoryBreakdownView getCategoryBreakdown(Long userId, Integer month, Integer year) {
        return getCategoryBreakdown(userId, null, TransactionType.EXPENSE, month, year);
    }

    @Override
    public CategoryBreakdownView getCategoryBreakdown(Long userId, Long accountId, TransactionType type, Integer month, Integer year) {
        if (userId != null) {
            verifyUser(userId);
        }
        Account account = null;
        if (accountId != null) {
            account = verifyAccount(accountId);
        }

        TransactionType targetType = (type != null) ? type : TransactionType.EXPENSE;
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        LocalDate startDate;
        LocalDate endDate;
        String periodLabel;

        if (month != null) {
            startDate = LocalDate.of(targetYear, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            periodLabel = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + targetYear;
        } else {
            startDate = LocalDate.of(targetYear, 1, 1);
            endDate = LocalDate.of(targetYear, 12, 31);
            periodLabel = "Full Year " + targetYear;
        }

        BigDecimal totalAmount;
        List<Object[]> rows;

        if (accountId != null) {
            totalAmount = reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                    accountId, targetType, startDate, endDate);
            rows = reportingTransactionRepository.sumGroupedByCategoryAndAccount(
                    accountId, targetType, startDate, endDate);
        } else {
            totalAmount = reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, targetType, startDate, endDate);
            rows = reportingTransactionRepository.sumGroupedByCategory(
                    userId, targetType, startDate, endDate);
        }

        totalAmount = (totalAmount != null) ? totalAmount : BigDecimal.ZERO;
        List<CategoryBreakdownItem> items = new ArrayList<>();

        for (Object[] row : rows) {
            Long catId = ((Number) row[0]).longValue();
            String catName = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            int count = (row.length > 3 && row[3] != null) ? ((Number) row[3]).intValue() : 0;

            double pct = calculateRate(amount, totalAmount);
            items.add(CategoryBreakdownItem.builder()
                    .categoryId(catId)
                    .categoryName(catName)
                    .totalAmount(amount)
                    .percentageOfTotal(pct)
                    .transactionCount(count)
                    .build());
        }

        String accountName = (account != null) ? account.getAccountName() : "All Accounts";

        return CategoryBreakdownView.builder()
                .period(periodLabel)
                .userId(userId)
                .accountId(accountId)
                .accountName(accountName)
                .type(targetType)
                .startDate(startDate)
                .endDate(endDate)
                .totalAmount(totalAmount)
                .categories(items)
                .build();
    }

    private List<CategoryBreakdownItem> extractTopCategories(Long userId, Long accountId, TransactionType type, LocalDate start, LocalDate end, BigDecimal total, int limit) {
        List<Object[]> rows = (accountId != null)
                ? reportingTransactionRepository.sumGroupedByCategoryAndAccount(accountId, type, start, end)
                : reportingTransactionRepository.sumGroupedByCategory(userId, type, start, end);

        List<CategoryBreakdownItem> items = new ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (count++ >= limit) break;
            Long catId = ((Number) row[0]).longValue();
            String catName = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            int txCount = (row.length > 3 && row[3] != null) ? ((Number) row[3]).intValue() : 0;
            double pct = calculateRate(amount, total);
            items.add(CategoryBreakdownItem.builder()
                    .categoryId(catId)
                    .categoryName(catName)
                    .totalAmount(amount)
                    .percentageOfTotal(pct)
                    .transactionCount(txCount)
                    .build());
        }
        return items;
    }

    private double calculateRate(BigDecimal numerator, BigDecimal denominator) {
        if (denominator != null && denominator.compareTo(BigDecimal.ZERO) > 0 && numerator != null) {
            return numerator.multiply(BigDecimal.valueOf(100))
                    .divide(denominator, 2, RoundingMode.HALF_UP).doubleValue();
        }
        return 0.0;
    }

    private void verifyUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }

    private Account verifyAccount(Long accountId) {
        return reportingAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
    }

}

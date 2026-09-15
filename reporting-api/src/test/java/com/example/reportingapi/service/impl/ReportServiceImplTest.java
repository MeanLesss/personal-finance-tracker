package com.example.reportingapi.service.impl;

import com.example.common.entity.Account;
import com.example.common.entity.User;
import com.example.common.entity.enums.AccountType;
import com.example.common.entity.enums.TransactionType;
import com.example.common.mappers.responses.CategoryBreakdownView;
import com.example.common.mappers.responses.MonthlySummaryView;
import com.example.common.mappers.responses.YearlyReportView;
import com.example.common.repository.UserRepository;
import com.example.reportingapi.repository.ReportingAccountRepository;
import com.example.reportingapi.repository.ReportingTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportingTransactionRepository reportingTransactionRepository;

    @Mock
    private ReportingAccountRepository reportingAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Koemsran");
        testUser.setEmail("koemsran@example.com");

        testAccount = new Account();
        testAccount.setId(10L);
        testAccount.setAccountName("ABA Bank Account");
        testAccount.setAccountType(AccountType.BANK);
        testAccount.setCurrency("USD");
        testAccount.setBalance(new BigDecimal("2800.00"));
        testAccount.setUser(testUser);
    }

    @Test
    void testMonthlySummaryWithAccount() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(reportingAccountRepository.findById(10L)).thenReturn(Optional.of(testAccount));

        when(reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                10L, TransactionType.INCOME, start, end)).thenReturn(new BigDecimal("2000.00"));
        when(reportingTransactionRepository.sumAmountByAccountIdAndTypeAndDateBetween(
                10L, TransactionType.EXPENSE, start, end)).thenReturn(new BigDecimal("450.00"));
        when(reportingTransactionRepository.countByAccountIdAndDateBetween(
                10L, start, end)).thenReturn(3);
        when(reportingTransactionRepository.countByAccountIdAndTypeAndDateBetween(
                10L, TransactionType.INCOME, start, end)).thenReturn(1);
        when(reportingTransactionRepository.countByAccountIdAndTypeAndDateBetween(
                10L, TransactionType.EXPENSE, start, end)).thenReturn(2);

        MonthlySummaryView view = reportService.getMonthlySummary(1L, 10L, 3, 2026);

        assertNotNull(view);
        assertEquals(3, view.getMonth());
        assertEquals("March", view.getMonthName());
        assertEquals(2026, view.getYear());
        assertEquals("ABA Bank Account", view.getAccountName());
        assertEquals("USD", view.getCurrency());
        assertEquals(new BigDecimal("2000.00"), view.getTotalIncome());
        assertEquals(new BigDecimal("450.00"), view.getTotalExpense());
        assertEquals(new BigDecimal("1550.00"), view.getNetSavings());
        assertEquals(77.5, view.getSavingsRate());
        assertEquals(3, view.getTotalTransactions());
        assertEquals(1, view.getIncomeTransactions());
        assertEquals(2, view.getExpenseTransactions());
    }

    @Test
    void testYearlyReportBreakdown() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                eq(1L), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00"));
        when(reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                eq(1L), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("400.00"));

        YearlyReportView report = reportService.getYearlyReport(1L, null, 2026);

        assertNotNull(report);
        assertEquals(2026, report.getYear());
        assertEquals("All Accounts", report.getAccountName());
        assertEquals(12, report.getMonthlyBreakdown().size());
        assertEquals("January", report.getMonthlyBreakdown().get(0).getMonthName());
        assertEquals(new BigDecimal("12000.00"), report.getTotalIncome());
        assertEquals(new BigDecimal("4800.00"), report.getTotalExpense());
        assertEquals(new BigDecimal("7200.00"), report.getNetSavings());
        assertEquals(60.0, report.getSavingsRate());
    }

    @Test
    void testCategoryBreakdown() {
        when(userRepository.existsById(1L)).thenReturn(true);
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);

        when(reportingTransactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                1L, TransactionType.EXPENSE, start, end)).thenReturn(new BigDecimal("500.00"));

        List<Object[]> rows = List.of(
                new Object[]{1L, "Dining", new BigDecimal("300.00"), 3},
                new Object[]{2L, "Transport", new BigDecimal("200.00"), 2}
        );
        when(reportingTransactionRepository.sumGroupedByCategory(1L, TransactionType.EXPENSE, start, end))
                .thenReturn(rows);

        CategoryBreakdownView view = reportService.getCategoryBreakdown(1L, null, TransactionType.EXPENSE, 3, 2026);

        assertNotNull(view);
        assertEquals("March 2026", view.getPeriod());
        assertEquals(new BigDecimal("500.00"), view.getTotalAmount());
        assertEquals(2, view.getCategories().size());
        assertEquals("Dining", view.getCategories().get(0).getCategoryName());
        assertEquals(60.0, view.getCategories().get(0).getPercentageOfTotal());
        assertEquals(3, view.getCategories().get(0).getTransactionCount());
    }

}

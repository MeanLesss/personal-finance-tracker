package com.example.reportingapi.service.impl;

import com.example.common.entity.Budget;
import com.example.common.entity.Category;
import com.example.common.entity.User;
import com.example.common.entity.enums.TransactionType;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.BudgetMapper;
import com.example.common.mappers.responses.BudgetStatusView;
import com.example.common.mappers.responses.BudgetView;
import com.example.common.payload.request.BudgetRequest;
import com.example.common.repository.UserRepository;
import com.example.common.util.Pagination;
import com.example.reportingapi.repository.BudgetRepository;
import com.example.reportingapi.repository.CategoryRepository;
import com.example.reportingapi.repository.ReportingTransactionRepository;
import com.example.reportingapi.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReportingTransactionRepository reportingTransactionRepository;

    @Override
    @Transactional
    public BudgetView save(BudgetRequest request) {
        Budget budget = new Budget();
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        if (request.getUserId() != null) {
            budget.setUser(findUser(request.getUserId()));
        }
        if (request.getCategoryId() != null) {
            budget.setCategory(findCategory(request.getCategoryId()));
        }
        return budgetMapper.mapFrom(budgetRepository.save(budget));
    }

    @Override
    public BudgetView findById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        return budgetMapper.mapFrom(budget);
    }

    @Override
    public List<BudgetView> findAll(Pagination pagination) {
        Page<Budget> page = budgetRepository.findAll(pagination.getJPAPageRequest());
        pagination.setTotalCounts(page.getTotalElements());
        return page.getContent().stream()
                .map(budgetMapper::mapFromForList)
                .toList();
    }

    @Override
    @Transactional
    public BudgetView update(Long id, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        if (request.getUserId() != null) {
            budget.setUser(findUser(request.getUserId()));
        }
        if (request.getCategoryId() != null) {
            budget.setCategory(findCategory(request.getCategoryId()));
        }
        return budgetMapper.mapFrom(budgetRepository.save(budget));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        budgetRepository.delete(budget);
    }

    @Override
    public List<BudgetStatusView> getBudgetStatus(Long userId, Integer month, Integer year) {
        findUser(userId);
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<BudgetStatusView> statusList = new ArrayList<>();
        for (Budget budget : budgets) {
            Long categoryId = budget.getCategory().getId();
            String categoryName = budget.getCategory().getName();
            BigDecimal limit = budget.getMonthlyLimit();

            BigDecimal actualSpend = reportingTransactionRepository.sumAmountByUserIdAndCategoryAndTypeAndDateBetween(
                    userId, categoryId, TransactionType.EXPENSE, startDate, endDate);
            if (actualSpend == null) {
                actualSpend = BigDecimal.ZERO;
            }

            BigDecimal remaining = limit.subtract(actualSpend);
            double percent = (limit.compareTo(BigDecimal.ZERO) > 0)
                    ? actualSpend.multiply(BigDecimal.valueOf(100))
                            .divide(limit, 2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            boolean isOverBudget = actualSpend.compareTo(limit) > 0;

            statusList.add(new BudgetStatusView(
                    budget.getId(),
                    categoryId,
                    categoryName,
                    month,
                    year,
                    limit,
                    actualSpend,
                    remaining,
                    percent,
                    isOverBudget
            ));
        }

        return statusList;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

}
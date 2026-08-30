package com.example.reportingapi.service.impl;

import com.example.common.entity.Budget;
import com.example.common.entity.Category;
import com.example.common.entity.User;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.BudgetMapper;
import com.example.common.mappers.responses.BudgetView;
import com.example.common.payload.request.BudgetRequest;
import com.example.common.repository.UserRepository;
import com.example.common.util.Pagination;
import com.example.reportingapi.repository.BudgetRepository;
import com.example.reportingapi.repository.CategoryRepository;
import com.example.reportingapi.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

}
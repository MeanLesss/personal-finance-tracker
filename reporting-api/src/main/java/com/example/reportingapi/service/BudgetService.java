package com.example.reportingapi.service;

import com.example.common.mappers.responses.BudgetStatusView;
import com.example.common.mappers.responses.BudgetView;
import com.example.common.payload.request.BudgetRequest;
import com.example.common.util.Pagination;

import java.util.List;

public interface BudgetService {

    BudgetView save(BudgetRequest request);

    BudgetView findById(Long id);

    List<BudgetView> findAll(Pagination pagination);

    BudgetView update(Long id, BudgetRequest request);

    void delete(Long id);

    List<BudgetStatusView> getBudgetStatus(Long userId, Integer month, Integer year);

}
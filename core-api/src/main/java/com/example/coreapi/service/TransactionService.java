package com.example.coreapi.service;

import com.example.common.entity.enums.TransactionType;
import com.example.common.mappers.responses.TransactionView;
import com.example.common.payload.request.TransactionRequest;
import com.example.common.util.Pagination;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionView save(TransactionRequest request);

    TransactionView findById(Long id);

    List<TransactionView> findAll(Pagination pagination);

    List<TransactionView> findAll(Long accountId, Long categoryId, LocalDate startDate, LocalDate endDate, TransactionType type, Pagination pagination);

    TransactionView update(Long id, TransactionRequest request);

    void delete(Long id);

}
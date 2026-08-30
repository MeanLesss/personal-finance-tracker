package com.example.coreapi.service;

import com.example.common.mappers.responses.TransactionView;
import com.example.common.payload.request.TransactionRequest;
import com.example.common.util.Pagination;

import java.util.List;

public interface TransactionService {

    TransactionView save(TransactionRequest request);

    TransactionView findById(Long id);

    List<TransactionView> findAll(Pagination pagination);

    TransactionView update(Long id, TransactionRequest request);

    void delete(Long id);

}
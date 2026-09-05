package com.example.coreapi.service;

import com.example.common.mappers.responses.AccountBalanceView;
import com.example.common.mappers.responses.AccountView;
import com.example.common.mappers.responses.NetWorthView;
import com.example.common.mappers.responses.TransactionView;
import com.example.common.payload.request.AccountRequest;
import com.example.common.payload.request.TransferRequest;
import com.example.common.util.Pagination;

import java.util.List;

public interface AccountService {

    AccountView save(AccountRequest request);

    AccountView findById(Long id);

    List<AccountView> findAll(Pagination pagination);

    AccountView update(Long id, AccountRequest request);

    void delete(Long id);

    AccountBalanceView getBalance(Long id);

    NetWorthView getNetWorth(Long userId);

    void transfer(TransferRequest request);

    List<TransactionView> getAccountTransactions(Long accountId);

}
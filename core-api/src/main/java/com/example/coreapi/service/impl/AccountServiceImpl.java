package com.example.coreapi.service.impl;

import com.example.common.entity.Account;
import com.example.common.entity.User;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.AccountMapper;
import com.example.common.mappers.responses.AccountView;
import com.example.common.payload.request.AccountRequest;
import com.example.common.repository.UserRepository;
import com.example.common.util.Pagination;
import com.example.coreapi.repository.AccountRepository;
import com.example.coreapi.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AccountView save(AccountRequest request) {
        Account account = new Account();
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);
        account.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        if (request.getUserId() != null) {
            account.setUser(findUser(request.getUserId()));
        }
        return accountMapper.mapFrom(accountRepository.save(account));
    }

    @Override
    public AccountView findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return accountMapper.mapFrom(account);
    }

    @Override
    public List<AccountView> findAll(Pagination pagination) {
        Page<Account> page = accountRepository.findAll(pagination.getJPAPageRequest());
        pagination.setTotalCounts(page.getTotalElements());
        return page.getContent().stream()
                .map(accountMapper::mapFromForList)
                .toList();
    }

    @Override
    @Transactional
    public AccountView update(Long id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        if (request.getBalance() != null) {
            account.setBalance(request.getBalance());
        }
        if (request.getCurrency() != null) {
            account.setCurrency(request.getCurrency());
        }
        if (request.getUserId() != null) {
            account.setUser(findUser(request.getUserId()));
        }
        return accountMapper.mapFrom(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        accountRepository.delete(account);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

}
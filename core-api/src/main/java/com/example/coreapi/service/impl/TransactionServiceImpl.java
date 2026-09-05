package com.example.coreapi.service.impl;

import com.example.common.entity.Account;
import com.example.common.entity.Category;
import com.example.common.entity.Transaction;
import com.example.common.entity.enums.TransactionType;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.TransactionMapper;
import com.example.common.mappers.responses.TransactionView;
import com.example.common.payload.request.TransactionRequest;
import com.example.common.util.Pagination;
import com.example.coreapi.repository.AccountRepository;
import com.example.coreapi.repository.TransactionRepository;
import com.example.coreapi.service.TransactionService;
import com.example.reportingapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TransactionView save(TransactionRequest request) {
        Transaction transaction = new Transaction();
        applyRequest(transaction, request);
        Account account = transaction.getAccount();
        account.setBalance(account.getBalance().add(balanceEffect(transaction)));
        return transactionMapper.mapFrom(transactionRepository.save(transaction));
    }

    @Override
    public TransactionView findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return transactionMapper.mapFrom(transaction);
    }

    @Override
    public List<TransactionView> findAll(Pagination pagination) {
        return findAll(null, null, null, null, null, pagination);
    }

    @Override
    public List<TransactionView> findAll(Long accountId, Long categoryId, LocalDate startDate, LocalDate endDate, TransactionType type, Pagination pagination) {
        Specification<Transaction> spec = (root, query, cb) -> cb.conjunction();

        if (accountId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("account").get("id"), accountId));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        }
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        Page<Transaction> page = transactionRepository.findAll(spec, pagination.getJPAPageRequest());
        pagination.setTotalCounts(page.getTotalElements());
        return page.getContent().stream()
                .map(transactionMapper::mapFromForList)
                .toList();
    }

    @Override
    @Transactional
    public TransactionView update(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        Account oldAccount = transaction.getAccount();
        BigDecimal oldEffect = balanceEffect(transaction);

        applyRequest(transaction, request);
        Account newAccount = transaction.getAccount();

        oldAccount.setBalance(oldAccount.getBalance().subtract(oldEffect));
        newAccount.setBalance(newAccount.getBalance().add(balanceEffect(transaction)));

        return transactionMapper.mapFrom(transaction);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        Account account = transaction.getAccount();
        account.setBalance(account.getBalance().subtract(balanceEffect(transaction)));
        transactionRepository.delete(transaction);
    }

    private void applyRequest(Transaction transaction, TransactionRequest request) {
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));
        transaction.setAccount(account);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        transaction.setCategory(category);
    }

    private BigDecimal balanceEffect(Transaction transaction) {
        return switch (transaction.getType()) {
            case INCOME -> transaction.getAmount();
            case EXPENSE -> transaction.getAmount().negate();
            case TRANSFER -> BigDecimal.ZERO;
        };
    }

}
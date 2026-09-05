package com.example.coreapi.controller.transaction;

import com.example.common.entity.enums.TransactionType;
import com.example.common.mappers.responses.TransactionView;
import com.example.common.payload.request.TransactionRequest;
import com.example.common.util.Pagination;
import com.example.common.util.RestApiResponse;
import com.example.coreapi.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public RestApiResponse<TransactionView> save(@RequestBody TransactionRequest request) {
        return new RestApiResponse<>("200", "Success", transactionService.save(request));
    }

    @GetMapping("/{id}")
    public RestApiResponse<TransactionView> findById(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", transactionService.findById(id));
    }

    @GetMapping
    public RestApiResponse<List<TransactionView>> findAll(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType type,
            Pagination pagination) {
        return new RestApiResponse<>("200", "Success",
                transactionService.findAll(accountId, categoryId, startDate, endDate, type, pagination), pagination);
    }

    @PutMapping("/{id}")
    public RestApiResponse<TransactionView> update(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return new RestApiResponse<>("200", "Success", transactionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return new RestApiResponse<>("200", "Success", null);
    }

}
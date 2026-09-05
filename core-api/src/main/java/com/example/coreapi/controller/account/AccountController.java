package com.example.coreapi.controller.account;

import com.example.common.mappers.responses.AccountBalanceView;
import com.example.common.mappers.responses.AccountView;
import com.example.common.mappers.responses.NetWorthView;
import com.example.common.mappers.responses.TransactionView;
import com.example.common.payload.request.AccountRequest;
import com.example.common.payload.request.TransferRequest;
import com.example.common.util.Pagination;
import com.example.common.util.RestApiResponse;
import com.example.coreapi.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public RestApiResponse<AccountView> save(@RequestBody AccountRequest request) {
        return new RestApiResponse<>("200", "Success", accountService.save(request));
    }

    @GetMapping("/{id}")
    public RestApiResponse<AccountView> findById(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", accountService.findById(id));
    }

    @GetMapping
    public RestApiResponse<List<AccountView>> findAll(Pagination pagination) {
        return new RestApiResponse<>("200", "Success", accountService.findAll(pagination), pagination);
    }

    @PutMapping("/{id}")
    public RestApiResponse<AccountView> update(@PathVariable Long id, @RequestBody AccountRequest request) {
        return new RestApiResponse<>("200", "Success", accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return new RestApiResponse<>("200", "Success", null);
    }

    @GetMapping("/{id}/balance")
    public RestApiResponse<AccountBalanceView> getBalance(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", accountService.getBalance(id));
    }

    @GetMapping("/{id}/transactions")
    public RestApiResponse<List<TransactionView>> getAccountTransactions(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", accountService.getAccountTransactions(id));
    }

    @PostMapping("/transfer")
    public RestApiResponse<Void> transfer(@RequestBody TransferRequest request) {
        accountService.transfer(request);
        return new RestApiResponse<>("200", "Transfer completed successfully", null);
    }

    @GetMapping("/net-worth")
    public RestApiResponse<NetWorthView> getNetWorth(@RequestParam Long userId) {
        return new RestApiResponse<>("200", "Success", accountService.getNetWorth(userId));
    }

}
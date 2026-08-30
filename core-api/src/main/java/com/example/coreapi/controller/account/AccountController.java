package com.example.coreapi.controller.account;

import com.example.common.mappers.responses.AccountView;
import com.example.common.payload.request.AccountRequest;
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

}
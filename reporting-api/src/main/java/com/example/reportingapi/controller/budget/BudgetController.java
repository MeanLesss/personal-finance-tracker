package com.example.reportingapi.controller.budget;

import com.example.common.mappers.responses.BudgetView;
import com.example.common.payload.request.BudgetRequest;
import com.example.common.util.Pagination;
import com.example.common.util.RestApiResponse;
import com.example.reportingapi.service.BudgetService;
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
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public RestApiResponse<BudgetView> save(@RequestBody BudgetRequest request) {
        return new RestApiResponse<>("200", "Success", budgetService.save(request));
    }

    @GetMapping("/{id}")
    public RestApiResponse<BudgetView> findById(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", budgetService.findById(id));
    }

    @GetMapping
    public RestApiResponse<List<BudgetView>> findAll(Pagination pagination) {
        return new RestApiResponse<>("200", "Success", budgetService.findAll(pagination), pagination);
    }

    @PutMapping("/{id}")
    public RestApiResponse<BudgetView> update(@PathVariable Long id, @RequestBody BudgetRequest request) {
        return new RestApiResponse<>("200", "Success", budgetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return new RestApiResponse<>("200", "Success", null);
    }

}
package com.example.reportingapi.controller.category;

import com.example.common.mappers.responses.CategoryView;
import com.example.common.payload.request.CategoryRequest;
import com.example.common.util.Pagination;
import com.example.common.util.RestApiResponse;
import com.example.reportingapi.service.CategoryService;
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
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public RestApiResponse<CategoryView> save(@RequestBody CategoryRequest request) {
        return new RestApiResponse<>("200", "Success", categoryService.save(request));
    }

    @GetMapping("/{id}")
    public RestApiResponse<CategoryView> findById(@PathVariable Long id) {
        return new RestApiResponse<>("200", "Success", categoryService.findById(id));
    }

    @GetMapping
    public RestApiResponse<List<CategoryView>> findAll(Pagination pagination) {
        return new RestApiResponse<>("200", "Success", categoryService.findAll(pagination), pagination);
    }

    @PutMapping("/{id}")
    public RestApiResponse<CategoryView> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return new RestApiResponse<>("200", "Success", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return new RestApiResponse<>("200", "Success", null);
    }

}
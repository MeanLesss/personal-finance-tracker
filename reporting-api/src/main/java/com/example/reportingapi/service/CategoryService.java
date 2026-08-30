package com.example.reportingapi.service;

import com.example.common.mappers.responses.CategoryView;
import com.example.common.payload.request.CategoryRequest;
import com.example.common.util.Pagination;

import java.util.List;

public interface CategoryService {

    CategoryView save(CategoryRequest request);

    CategoryView findById(Long id);

    List<CategoryView> findAll(Pagination pagination);

    CategoryView update(Long id, CategoryRequest request);

    void delete(Long id);

}
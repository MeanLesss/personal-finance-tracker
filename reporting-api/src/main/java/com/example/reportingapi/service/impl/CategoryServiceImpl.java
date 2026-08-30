package com.example.reportingapi.service.impl;

import com.example.common.entity.Category;
import com.example.common.entity.User;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.mappers.CategoryMapper;
import com.example.common.mappers.responses.CategoryView;
import com.example.common.payload.request.CategoryRequest;
import com.example.common.repository.UserRepository;
import com.example.common.util.Pagination;
import com.example.reportingapi.repository.CategoryRepository;
import com.example.reportingapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryView save(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        if (request.getUserId() != null) {
            category.setUser(findUser(request.getUserId()));
        }
        return categoryMapper.mapFrom(categoryRepository.save(category));
    }

    @Override
    public CategoryView findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.mapFrom(category);
    }

    @Override
    public List<CategoryView> findAll(Pagination pagination) {
        Page<Category> page = categoryRepository.findAll(pagination.getJPAPageRequest());
        pagination.setTotalCounts(page.getTotalElements());
        return page.getContent().stream()
                .map(categoryMapper::mapFromForList)
                .toList();
    }

    @Override
    @Transactional
    public CategoryView update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(request.getName());
        category.setType(request.getType());
        if (request.getUserId() != null) {
            category.setUser(findUser(request.getUserId()));
        }
        return categoryMapper.mapFrom(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

}
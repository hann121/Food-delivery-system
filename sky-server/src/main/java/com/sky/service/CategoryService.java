package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {


    void add(CategoryDTO categoryDTO);

    PageResult FindByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void StartOrStop(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    void delete(Long id);

    List<Category> findByType(Integer type);
}

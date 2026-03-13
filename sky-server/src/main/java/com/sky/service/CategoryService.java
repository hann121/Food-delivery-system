package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {


    void add(CategoryDTO categoryDTO);

    PageResult FindByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    void StartOrStop(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    void delete(Long id);
}

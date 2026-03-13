package com.sky.mapper;

import org.apache.ibatis.annotations.Select;

public interface SetmealMapper {

    //    根据分类id统计数量
    @Select("select count(id) from setmeal where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);
}

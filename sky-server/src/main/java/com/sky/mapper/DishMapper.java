package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    //  根据分类id统计数量
    @Select("select count(id) from dish where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);

    //  新增菜品
    void add(Dish dish);

    //  分页查询
    Page<DishVO> findByPage(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    List<Dish> getByids(List<Long> ids);

    void update(Dish dish);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

//    根据分类id查询菜品
    List<Dish> list(Dish dish);

}

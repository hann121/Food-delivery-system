package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void addFlavors(List<DishFlavor> flavors);

    void deleteByDishId(List<Long> ids);

    List<DishFlavor> getByDishId(Long dishId);

    void update(List<DishFlavor> flavors);
}

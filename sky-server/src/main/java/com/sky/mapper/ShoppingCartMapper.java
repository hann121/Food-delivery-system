package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCart find(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);
}

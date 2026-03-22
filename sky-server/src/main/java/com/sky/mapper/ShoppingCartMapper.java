package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCart find(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    List<ShoppingCart> getByUserId(Long userId);

    void deleteOne(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteAll(Long userId);

    void updateByUserId(ShoppingCart cart);
}

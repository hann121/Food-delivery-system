package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /*
    * 添加进购物车
    * */
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加的商品id信息:{}",shoppingCartDTO.toString());

        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /*
    * 查看购物车
    * */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> showShoppingCart(){
        log.info("查看购物车的用户id:{}", BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartService.showShoppingCart();
        return Result.success(shoppingCarts);
    }

    /*
    * 删除购物车中的一个商品
    * */
    @PostMapping("/sub")
    public Result deleteOne(@RequestBody ShoppingCartDTO shoppingCartDTO){
            log.info("要删除的商品信息:{}",shoppingCartDTO.toString());
            shoppingCartService.deleteOne(shoppingCartDTO);
            return Result.success();
    }

    /*
    * 清空购物车
    * */
    @DeleteMapping("/clean")
    public Result deleteAll(){
        log.info("清空购物车");
        shoppingCartService.deleteAll();
        return Result.success();
    }
}

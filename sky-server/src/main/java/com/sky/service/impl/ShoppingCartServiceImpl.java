package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    //    添加至购物车
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //创建shoppingCart对象，并赋值
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //先判断购物车中是否存在一样的物品
        ShoppingCart cart = shoppingCartMapper.find(shoppingCart);
        //如果存在
        if(cart!=null){
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateByUserId(cart);
        }else{
            //不存在的话，先判断是套餐还是菜品
            Long dishId=shoppingCart.getDishId();
            //传入的菜品
            if(dishId!=null){
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                //传入的是套餐
                Setmeal setmeal=setmealMapper.getBySetmealId(shoppingCart.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);

            shoppingCartMapper.insert(shoppingCart);
        }
    }

//    查看购物车
    @Override
    public List<ShoppingCart> showShoppingCart() {
        List<ShoppingCart> shoppingCarts =shoppingCartMapper.getByUserId(BaseContext.getCurrentId());
        return shoppingCarts;
    }
//    删除一个商品
    @Override
    public void deleteOne(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart =new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //删除同种多件的一个
        ShoppingCart curShoppingCart = shoppingCartMapper.find(shoppingCart);
        if(curShoppingCart.getNumber()>1){
            curShoppingCart.setNumber(curShoppingCart.getNumber()-1);
            shoppingCartMapper.updateByUserId(curShoppingCart);
        }else{
            //删除只有单件的商品
            shoppingCartMapper.deleteOne(shoppingCart);
        }
    }
//      清空购物车
    @Override
    public void deleteAll() {
        shoppingCartMapper.deleteAll(BaseContext.getCurrentId());
    }
}
